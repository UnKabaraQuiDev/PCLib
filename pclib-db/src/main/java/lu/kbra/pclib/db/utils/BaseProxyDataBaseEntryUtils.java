package lu.kbra.pclib.db.utils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.datastructure.tuple.Tuple;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.query.Limit;
import lu.kbra.pclib.db.autobuild.query.Offset;
import lu.kbra.pclib.db.autobuild.query.Param;
import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.SimpleTransformingQuery.ListSimpleTransformingQuery;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public class BaseProxyDataBaseEntryUtils extends BaseDataBaseEntryUtils implements ProxyDataBaseEntryUtils {

	public BaseProxyDataBaseEntryUtils() {
	}

	public BaseProxyDataBaseEntryUtils(final ColumnTypeRegistry typeRegistry) {
		super(typeRegistry);
	}

	public BaseProxyDataBaseEntryUtils(final String protocol) {
		super(protocol);
	}

	public Map<String, Object> mapTupleToColumns(final String[] columns, final Tuple tuple) {
		if (tuple.elementCount() != columns.length) {
			throw new IllegalArgumentException("Tuple element count does not match columns length");
		}
		final Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < columns.length; i++) {
			map.put(columns[i], tuple.get(i));
		}
		return map;
	}

	@Override
	public <T extends DataBaseEntry> Function<List<Object>, ?> buildMethodQueryFunction(
			String tableName,
			final SQLQueryable<T> instance,
			final Method method) {

		try {
			if (!method.isAnnotationPresent(Query.class)) {
				throw new IllegalArgumentException("No @Query found on method: " + method);
			}

			final Query query = method.getAnnotation(Query.class);

			tableName = PCUtils.sqlEscapeIdentifier(tableName);

			final String queryText = query.value().replace(Query.TABLE_NAME, tableName);

			if (query.limit() > query.offset() && !(query.offset() == -1 || query.limit() == -1)) {
				throw new IllegalArgumentException("Invalid order: (offset) -> " + query.offset() + " (limit) -> " + query.limit()
						+ ", should be in this order: <others> <limit> <offset>");
			}

			final Type returnType = method.getGenericReturnType();
			final Type[] argTypes = method.getGenericParameterTypes();

			if (queryText == null || queryText.isEmpty()) {
				// for automatic queries (by declared @Query columns)
				final String[] cols = query.columns();
				if (cols.length != 0 || query.limit() != -1 || query.offset() != -1) {
					final String sql = SQLBuilder.safeSelect(tableName, cols, query.limit() != -1, query.offset() != -1);
					return this.getFunctionForMethod(method, returnType, argTypes, instance, sql, query);
				}

				// for automatic queries driven by method parameters
				return this.getFunctionForParameterMethod(method, returnType, argTypes, instance, tableName, query);
			} else { // for manual queries (with sql)
				return this.getFunctionForMethod(method, returnType, argTypes, instance, queryText, query);
			}
		} catch (final Exception e) {
			throw new RuntimeException(
					"Exception when building method query function for: " + method + " on [" + instance.getClass().getName() + "]",
					e);
		}
	}

	protected ColumnType getColumnType(final Class<? extends DataBaseEntry> entryClazz, final String col) {
		if (Query.LIMIT_KEY.equals(col) || Query.OFFSET_KEY.equals(col)) {
			return this.typeMap.get(Long.class).apply(this.getFallbackColumnAnnotation());
		}
		return this.getTypeFor(this.getFieldFor(entryClazz, col));
	}

	private <T extends DataBaseEntry> Function<List<Object>, ?> getFunctionForParameterMethod(
			final Method method,
			final Type ptReturnType,
			final Type[] argTypes,
			final SQLQueryable<T> instance,
			final String tableName,
			final Query query) {
		final Query.Type type = Query.Type.AUTO.equals(query.strategy()) ? this.detectDefaultStrategy(ptReturnType, method)
				: query.strategy();
		final ParameterQueryPlan plan = this.buildParameterQueryPlan(method, argTypes, query.orderBy());

		if (ptReturnType instanceof ParameterizedType && NextTask.class.equals(((ParameterizedType) ptReturnType).getRawType())) {
			return (Function<List<Object>, ?>) obj -> {
				final PreparedParameterQuery prepared = plan.prepare(tableName, obj);
				return instance.query(new ListSimpleTransformingQuery(prepared.sql, prepared.values, prepared.types, type));
			};
		} else if (ptReturnType instanceof ParameterizedType && Optional.class.equals(((ParameterizedType) ptReturnType).getRawType())) {
			return (Function<List<Object>, ?>) obj -> {
				final PreparedParameterQuery prepared = plan.prepare(tableName, obj);
				final Object d = instance.query(new ListSimpleTransformingQuery(prepared.sql, prepared.values, prepared.types, type));
				return type.isNullable() ? Optional.ofNullable(d) : Optional.of(d);
			};
		} else {
			return (Function<List<Object>, ?>) obj -> {
				final PreparedParameterQuery prepared = plan.prepare(tableName, obj);
				return instance.query(new ListSimpleTransformingQuery(prepared.sql, prepared.values, prepared.types, type));
			};
		}
	}

	private ParameterQueryPlan buildParameterQueryPlan(final Method method, final Type[] argTypes, final OrderBy[] orderBy) {
		final Parameter[] parameters = method.getParameters();
		final List<ParameterQueryPart> whereParts = new ArrayList<>();
		ParameterQueryPart limitPart = null;
		ParameterQueryPart offsetPart = null;

		for (int i = 0; i < parameters.length; i++) {
			final Parameter parameter = parameters[i];
			final boolean limit = parameter.isAnnotationPresent(Limit.class);
			final boolean offset = parameter.isAnnotationPresent(Offset.class);
			final boolean param = parameter.isAnnotationPresent(Param.class);

			if ((limit ? 1 : 0) + (offset ? 1 : 0) + (param ? 1 : 0) > 1) {
				throw new IllegalArgumentException("A @Query method parameter can only use one of @Param, @Limit or @Offset: " + method);
			}

			final ColumnType type = this.getTypeFor(PCUtils.getRawClass(argTypes[i]), this.getFallbackField());

			if (limit) {
				if (limitPart != null) {
					throw new IllegalArgumentException("Only one @Limit parameter is allowed: " + method);
				}
				limitPart = new ParameterQueryPart(i, null, null, false, type);
			} else if (offset) {
				if (offsetPart != null) {
					throw new IllegalArgumentException("Only one @Offset parameter is allowed: " + method);
				}
				offsetPart = new ParameterQueryPart(i, null, null, false, type);
			} else {
				final Param annotation = parameter.getAnnotation(Param.class);
				final String column = this.resolveParameterColumnName(parameter, annotation, method);
				final String comparator = this.normalizeComparator(annotation == null ? "=" : annotation.comparator(), method);
				final boolean ignoreNull = annotation != null && annotation.ignoreNull();

				whereParts.add(new ParameterQueryPart(i, column, comparator, ignoreNull, type));
			}
		}

		final List<String> orderByParts = Arrays.stream(orderBy)
				.map(order -> this.buildOrderByPart(order, method))
				.collect(Collectors.toList());

		return new ParameterQueryPlan(whereParts, orderByParts, limitPart, offsetPart);
	}

	private String buildOrderByPart(final OrderBy order, final Method method) {
		if (order == null) {
			throw new IllegalArgumentException("@Query orderBy cannot contain null values: " + method);
		}

		final String column = order.column();
		if (column == null || column.trim().isEmpty()) {
			throw new IllegalArgumentException("@Query orderBy column cannot be empty: " + method);
		}

		return PCUtils.sqlEscapeIdentifier(this.fieldToColumnName(column.trim())) + " " + order.type().name();
	}

	private String resolveParameterColumnName(final Parameter parameter, final Param annotation, final Method method) {
		String column = annotation == null ? "" : annotation.value();
		if (column == null || column.trim().isEmpty()) {
			if (!parameter.isNamePresent()) {
				throw new IllegalArgumentException(
						"Missing column name for @Query parameter. Add @Param(\"columnName\") or compile with -parameters: " + method);
			}
			column = parameter.getName();
		}
		return this.fieldToColumnName(column.trim());
	}

	private String normalizeComparator(final String comparator, final Method method) {
		if (comparator == null) {
			throw new IllegalArgumentException("Comparator cannot be null: " + method);
		}

		final String normalized = comparator.trim().toUpperCase();
		if ("=".equals(normalized) || "<".equals(normalized) || "<=".equals(normalized) || ">".equals(normalized) || ">=".equals(normalized)
				|| "LIKE".equals(normalized)) {
			return normalized;
		}

		throw new IllegalArgumentException(
				"Unsupported @Param comparator '" + comparator + "' on " + method + ". Supported: =, <, <=, >, >=, LIKE");
	}

	private static final class ParameterQueryPlan {

		private final List<ParameterQueryPart> whereParts;
		private final List<String> orderByParts;
		private final ParameterQueryPart limitPart;
		private final ParameterQueryPart offsetPart;

		private ParameterQueryPlan(
				final List<ParameterQueryPart> whereParts,
				final List<String> orderByParts,
				final ParameterQueryPart limitPart,
				final ParameterQueryPart offsetPart) {
			this.whereParts = whereParts;
			this.orderByParts = orderByParts;
			this.limitPart = limitPart;
			this.offsetPart = offsetPart;
		}

		private PreparedParameterQuery prepare(final String tableName, final List<Object> args) {
			final StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName);
			final List<String> where = new ArrayList<>();
			final List<Object> values = new ArrayList<>();
			final List<ColumnType> types = new ArrayList<>();

			for (final ParameterQueryPart part : this.whereParts) {
				final Object value = args.get(part.index);
				if (value == null && part.ignoreNull) {
					continue;
				}

				where.add(PCUtils.sqlEscapeIdentifier(part.column) + " " + part.comparator + " ?");
				values.add(value);
				types.add(part.type);
			}

			if (!where.isEmpty()) {
				sql.append(" WHERE ").append(where.stream().collect(Collectors.joining(" AND ")));
			}

			if (!this.orderByParts.isEmpty()) {
				sql.append(" ORDER BY ").append(this.orderByParts.stream().collect(Collectors.joining(", ")));
			}

			if (this.limitPart != null) {
				sql.append(" LIMIT ?");
				values.add(args.get(this.limitPart.index));
				types.add(this.limitPart.type);
			}

			if (this.offsetPart != null) {
				sql.append(" OFFSET ?");
				values.add(args.get(this.offsetPart.index));
				types.add(this.offsetPart.type);
			}

			sql.append(";");
			return new PreparedParameterQuery(sql.toString(), values, types);
		}

	}

	private static final class ParameterQueryPart {

		private final int index;
		private final String column;
		private final String comparator;
		private final boolean ignoreNull;
		private final ColumnType type;

		private ParameterQueryPart(
				final int index,
				final String column,
				final String comparator,
				final boolean ignoreNull,
				final ColumnType type) {
			this.index = index;
			this.column = column;
			this.comparator = comparator;
			this.ignoreNull = ignoreNull;
			this.type = type;
		}

	}

	private static final class PreparedParameterQuery {

		private final String sql;
		private final List<Object> values;
		private final List<ColumnType> types;

		private PreparedParameterQuery(final String sql, final List<Object> values, final List<ColumnType> types) {
			this.sql = sql;
			this.values = values;
			this.types = types;
		}

	}

	/**
	 * for automatic & manual but with direct return
	 */
	private <T extends DataBaseEntry> Function<List<Object>, ?> getFunctionForMethod(
			final Method method,
			final Type ptReturnType,
			final Type[] argTypes,
			final SQLQueryable<T> instance,
			final String sql,
			final Query query) {
		final Query.Type type = Query.Type.AUTO.equals(query.strategy()) ? this.detectDefaultStrategy(ptReturnType, method)
				: query.strategy();

		final List<ColumnType> types = Arrays.stream(argTypes)
				.map(col -> this.getTypeFor(PCUtils.getRawClass(col), this.getFallbackField()))
				.collect(Collectors.toList());

		if (ptReturnType instanceof ParameterizedType && NextTask.class.equals(((ParameterizedType) ptReturnType).getRawType())) {
			return (Function<List<Object>, ?>) obj -> instance.query(new ListSimpleTransformingQuery(sql, obj, types, type));
		} else if (ptReturnType instanceof ParameterizedType && Optional.class.equals(((ParameterizedType) ptReturnType).getRawType())) {
			return (Function<List<Object>, ?>) obj -> {
				final Object d = instance.query(new ListSimpleTransformingQuery(sql, obj, types, type));
				return type.isNullable() ? Optional.ofNullable(d) : Optional.of(d);
			};
		} else {
			return (Function<List<Object>, ?>) obj -> instance.query(new ListSimpleTransformingQuery(sql, obj, types, type));
		}
	}

	public Query.Type detectDefaultStrategy(final Type returnType, final AnnotatedElement annotatedElement) {
		Type effectiveType = returnType;
		final ParameterizedType parameterizedType = effectiveType instanceof ParameterizedType ? (ParameterizedType) effectiveType : null;

		/** @deprecated */
		// Unwrap NextTask<?, ?, T>
		/*
		 * while (parameterizedType != null && NextTask.class.equals(parameterizedType.getRawType())) {
		 * effectiveType = parameterizedType.getActualTypeArguments()[2]; }
		 */

		// Resolve SQLQuery<?, T>
		final Type sqlQueryType = this.findSQLQueryInterface(effectiveType);
		if (sqlQueryType instanceof ParameterizedType) {
			final ParameterizedType sqlParameterizedType = (ParameterizedType) sqlQueryType;
			final Type[] typeArgs = sqlParameterizedType.getActualTypeArguments();
			if (typeArgs.length == 2) {
				effectiveType = typeArgs[1];
			}
		}

		// List<?> -> always LIST_EMPTY
		if (this.isListType(effectiveType)) {
			return Query.Type.LIST_EMPTY;
		}

		// Optional<?> -> FIRST_NULL
		// Nullable annotations -> FIRST_NULL
		if (parameterizedType != null && Optional.class.equals(parameterizedType.getRawType()) || this.isNullable(annotatedElement)) {
			return Query.Type.FIRST_NULL;
		}

		// Non-null annotations -> FIRST_THROW
		if (this.isNonNull(annotatedElement)) {
			return Query.Type.FIRST_THROW;
		}

		return Query.Type.FIRST_NULL;
	}

	private boolean isNullable(final AnnotatedElement annotatedElement) {
		return Arrays.stream(annotatedElement.getAnnotations()).anyMatch(c -> "Nullable".equals(c.annotationType().getSimpleName()));
	}

	private boolean isNonNull(final AnnotatedElement annotatedElement) {
		return Arrays.stream(annotatedElement.getAnnotations())
				.anyMatch(
						c -> "NotNull".equals(c.annotationType().getSimpleName()) || "NonNull".equals(c.annotationType().getSimpleName()));
	}

}
