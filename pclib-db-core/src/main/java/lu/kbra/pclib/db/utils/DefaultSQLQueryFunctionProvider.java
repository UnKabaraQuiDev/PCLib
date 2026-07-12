package lu.kbra.pclib.db.utils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
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
import lu.kbra.pclib.db.annotations.query.Limit;
import lu.kbra.pclib.db.annotations.query.Offset;
import lu.kbra.pclib.db.annotations.query.Param;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.query.SimpleTransformingQuery.ListSimpleTransformingQuery;
import lu.kbra.pclib.db.query.SimpleTransformingQuery.ScalarListTransformingQuery;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.SQLColumnTypeProvider;
import lu.kbra.pclib.db.utils.impl.SQLQueryFunctionProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DefaultSQLQueryFunctionProvider implements SQLQueryFunctionProvider {

	@Data
	private static final class ParameterQueryPart {

		private final int index;
		private final String column;
		private final String comparator;
		private final boolean ignoreNull;
		private final ColumnType type;

	}

	@Data
	private static final class ParameterQueryPlan {

		private final String sql;
		private final List<ParameterQueryPart> whereParts;
		private final ParameterQueryPart limitPart;
		private final ParameterQueryPart offsetPart;

		private List<ColumnType> types(final List<Object> args) {
			final List<ColumnType> types = new ArrayList<>();

			for (final ParameterQueryPart part : this.whereParts) {
				if (part.ignoreNull) {
					types.add(part.type);
				}
				types.add(part.type);
			}

			if (this.limitPart != null) {
				types.add(this.limitPart.type);
			}

			if (this.offsetPart != null) {
				types.add(this.offsetPart.type);
			}

			return types;
		}

		private List<Object> values(final List<Object> args) {
			final List<Object> values = new ArrayList<>();

			for (final ParameterQueryPart part : this.whereParts) {
				final Object value = args.get(part.index);
				if (part.ignoreNull) {
					values.add(value);
				}
				values.add(value);
			}

			if (this.limitPart != null) {
				values.add(args.get(this.limitPart.index));
			}

			if (this.offsetPart != null) {
				values.add(args.get(this.offsetPart.index));
			}

			return values;
		}

	}

	@Data
	private static final class ReturnMapping {

		private final AnnotatedType actualType;
		private final boolean entryReturn;
		private final ColumnType columnType;

	}

	protected DataBaseEntryUtils dataBaseEntryUtils;
	protected SQLStructureVisitor structureVisitor;
	protected SQLColumnTypeProvider columnTypeProvider;

	public DefaultSQLQueryFunctionProvider(final DataBaseEntryUtils dataBaseEntryUtils) {
		this.dataBaseEntryUtils = dataBaseEntryUtils;
		this.structureVisitor = dataBaseEntryUtils.getStructureVisitor();
		this.columnTypeProvider = dataBaseEntryUtils.getColumnTypeProvider();
	}

	public Query.Type detectDefaultStrategy(final AnnotatedType returnType) {
		Type effectiveType = returnType.getType();

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

		final Class<?> effectiveClazz = PCUtils.getRawClass(effectiveType);

		// primitives cannot be null
		if (effectiveClazz.isPrimitive()) {
			return Query.Type.FIRST_THROW;
		}

		// Optional<?> -> FIRST_NULL
		// Nullable annotations -> FIRST_NULL
		if (Optional.class == effectiveClazz || this.isNullable(returnType)) {
			return Query.Type.FIRST_NULL;
		}

		// Non-null annotations -> FIRST_THROW
		if (this.isNonNull(returnType)) {
			return Query.Type.FIRST_THROW;
		}

		return Query.Type.FIRST_NULL;
	}

	/*
	 * this is needed because if the method doesn't have a visibility modifier, the annotations get
	 * applied to the return type
	 */
	public Query.Type detectDefaultStrategy(final AnnotatedType returnType, final AnnotatedElement parentElement) {
		Type effectiveType = returnType.getType();

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

		final Class<?> effectiveClazz = PCUtils.getRawClass(effectiveType);

		// primitives cannot be null
		if (effectiveClazz.isPrimitive()) {
			return Query.Type.FIRST_THROW;
		}

		// Optional<?> -> FIRST_NULL
		// Nullable annotations -> FIRST_NULL
		if (Optional.class == effectiveClazz || this.isNullable(returnType) || this.isNullable(parentElement)) {
			return Query.Type.FIRST_NULL;
		}

		// Non-null annotations -> FIRST_THROW
		if (this.isNonNull(returnType) || this.isNonNull(parentElement)) {
			return Query.Type.FIRST_THROW;
		}

		return Query.Type.FIRST_NULL;
	}

	@Deprecated
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

	/**
	 * Builds a <code>Function&lt;List&lt;Object&gt;, <i>ReturnType</i>&gt;</code> for the given method
	 * with no custom SQL.
	 */
	@SuppressWarnings("unchecked")
	protected <T extends DataBaseEntry, V> Function<List<Object>, V> buildFunctionForParameterMethod(
			final Method method,
			final AnnotatedType returnType,
			final SQLQueryable<T> instance,
			final Query query) {
		final Query.Type type = query.strategy().isAuto() ? this.detectDefaultStrategy(returnType, method) : query.strategy();
		final ReturnMapping returnMapping = this.buildReturnMapping(method);
		final ParameterQueryPlan plan = this.buildParameterQueryPlan(method, query.orderBy(), instance.getQualifiedName(), returnMapping);
		final Class<?> returnTypeClass = PCUtils.wrapPrimitiveClass(PCUtils.getRawClass(returnType.getType()));

		if (returnMapping.entryReturn) {
			if (returnTypeClass == Optional.class) {
				return (Function<List<Object>, V>) obj -> {
					final Object d = instance.query(new ListSimpleTransformingQuery<>(plan.sql, plan.values(obj), plan.types(obj), type));
					return (V) returnTypeClass.cast(type.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
				};
			} else {
				return (Function<List<Object>, V>) obj -> (V) returnTypeClass
						.cast(instance.query(new ListSimpleTransformingQuery<>(plan.sql, plan.values(obj), plan.types(obj), type)));
			}
		} else if (returnTypeClass == Optional.class) {
			return (Function<List<Object>, V>) obj -> {
				final Object d = instance.query(new ScalarListTransformingQuery<>(plan.sql,
						plan.values(obj),
						plan.types(obj),
						type,
						returnMapping.columnType,
						returnMapping.actualType.getType()));
				return (V) returnTypeClass.cast(type.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
			};
		} else {
			return (Function<List<Object>, V>) obj -> (V) returnTypeClass.cast(instance.query(new ScalarListTransformingQuery<>(plan.sql,
					plan.values(obj),
					plan.types(obj),
					type,
					returnMapping.columnType,
					returnMapping.actualType.getType())));
		}
	}

	protected ParameterQueryPlan buildParameterQueryPlan(
			final Method method,
			final OrderBy[] orderBy,
			final String tableName,
			final ReturnMapping returnMapping) {
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
				throw new IllegalArgumentException("A @Query method parameter can only use one of @Param, @Limit or @Offset: " + parameter
						+ " (parameter " + i + " of " + method + ")");
			}

			final ColumnType type = this.dataBaseEntryUtils.getTypeFor(parameter.getAnnotatedType());

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

		final String sql = this.buildParameterQuerySql(tableName, whereParts, orderByParts, limitPart, offsetPart, returnMapping);
		return new ParameterQueryPlan(sql, whereParts, limitPart, offsetPart);

	}

	@Override
	public <T extends DataBaseEntry, V> Function<List<Object>, V>
			buildMethodQueryFunction(final SQLQueryable<T> instance, final Method method) {
		try {
			if (!method.isAnnotationPresent(Query.class)) {
				throw new IllegalArgumentException("No @Query found on method: " + method);
			}

			final Query query = method.getAnnotation(Query.class);

			final String queryText = this.dataBaseEntryUtils.replaceSQLQualifiers(instance, query.value());

			if (query.limit() > query.offset() && !(query.offset() == -1 || query.limit() == -1)) {
				throw new IllegalArgumentException("Invalid order: (offset) -> " + query.offset() + " (limit) -> " + query.limit()
						+ ", should be in this order: <others> <limit> <offset>");
			}

			final AnnotatedType returnType = method.getAnnotatedReturnType();
			final AnnotatedType[] argTypes = method.getAnnotatedParameterTypes();

			if (queryText == null || queryText.isEmpty()) {
				// for manual queries (by declared @Query columns)
				final String[] cols = query.columns();
				if (cols.length != 0 || query.limit() != -1 || query.offset() != -1) {
					final String sql = this.structureVisitor.safeSelect(instance, cols, query.limit() != -1, query.offset() != -1);
					return this.buildFunctionForMethod(method, returnType, argTypes, instance, sql, query);
				}

				// for automatic queries driven by method parameters
				return this.buildFunctionForParameterMethod(method, returnType, instance, query);
			} else { // for manual queries (with sql)
				return this.buildFunctionForMethod(method, returnType, argTypes, instance, queryText, query);
			}
		} catch (final Exception e) {
			throw new RuntimeException(
					"Exception when building method query function for: " + method + " on [" + instance.getClass().getName() + "]",
					e);
		}
	}

	protected Type findSQLQueryInterface(final Type type) {
		if (!(type instanceof ParameterizedType)) {
			return null;
		}

		final ParameterizedType pt = (ParameterizedType) type;
		final Class<?> rawClass = (Class<?>) pt.getRawType();

		for (final Type iface : rawClass.getGenericInterfaces()) {
			if (iface instanceof ParameterizedType) {
				final ParameterizedType ipt = (ParameterizedType) iface;
				final Type rawIface = ipt.getRawType();
				if (rawIface instanceof Class<?> && SQLQuery.class.isAssignableFrom((Class<?>) rawIface)) {
					return ipt;
				}
			}
		}

		final Type superType = rawClass.getGenericSuperclass();
		if (superType != null) {
			return this.findSQLQueryInterface(superType);
		}

		return null;
	}

	/**
	 * for automatic & manual but with direct return
	 */
	@SuppressWarnings("unchecked")
	private <T extends DataBaseEntry, B> Function<List<Object>, B> buildFunctionForMethod(
			final Method method,
			final AnnotatedType returnType,
			final AnnotatedType[] argTypes,
			final SQLQueryable<T> instance,
			final String sql,
			final Query query) {
		final Query.Type type = query.strategy().isAuto() ? this.detectDefaultStrategy(returnType, method) : query.strategy();
		final ReturnMapping returnMapping = this.buildReturnMapping(method);
		final Class<?> returnTypeClass = PCUtils.wrapPrimitiveClass(PCUtils.getRawClass(returnType.getType()));
		final List<ColumnType> types = Arrays.stream(argTypes).map(this.dataBaseEntryUtils::getTypeFor).collect(Collectors.toList());

		if (returnMapping.entryReturn) {
			if (returnTypeClass == Optional.class) {
				return (Function<List<Object>, B>) obj -> {
					final Object d = instance.query(new ListSimpleTransformingQuery<>(sql, obj, types, type));
					return (B) returnTypeClass.cast(type.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
				};
			} else {
				return (Function<List<Object>, B>) obj -> (B) returnTypeClass
						.cast(instance.query(new ListSimpleTransformingQuery<>(sql, obj, types, type)));
			}
		} else if (returnTypeClass == Optional.class) {
			return (Function<List<Object>, B>) obj -> {
				final Object d = instance.query(new ScalarListTransformingQuery<>(sql,
						obj,
						types,
						type,
						returnMapping.columnType,
						returnMapping.actualType.getType()));
				return (B) returnTypeClass.cast(type.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
			};
		} else {
			return (Function<List<Object>, B>) obj -> (B) returnTypeClass.cast(instance.query(new ScalarListTransformingQuery<>(sql,
					obj,
					types,
					type,
					returnMapping.columnType,
					returnMapping.actualType.getType())));
		}
	}

	private String buildOrderByPart(final OrderBy order, final Method method) {
		if (order.column() == null || order.column().trim().isEmpty()) {
			throw new IllegalArgumentException("@OrderBy column must not be empty on method " + method);
		}
		return this.structureVisitor.qualifiedName(order.column().trim()) + " " + order.type().name();
	}

	private String buildParameterQuerySql(
			final String tableName,
			final List<ParameterQueryPart> whereParts,
			final List<String> orderByParts,
			final ParameterQueryPart limitPart,
			final ParameterQueryPart offsetPart,
			final ReturnMapping returnMapping) {
		final String select = returnMapping.entryReturn ? "*" : "*";
		final StringBuilder sql = new StringBuilder(
				"SELECT " + this.structureVisitor.qualifiedName(select) + " FROM " + this.structureVisitor.qualifiedName(tableName));
		final List<String> where = new ArrayList<>();

		for (final ParameterQueryPart part : whereParts) {
			if (part.ignoreNull) {
				where.add("(? IS NULL OR " + this.structureVisitor.qualifiedName(part.column) + " " + part.comparator + " ?)");
			} else {
				where.add(this.structureVisitor.qualifiedName(part.column) + " " + part.comparator + " ?");
			}
		}

		if (!where.isEmpty()) {
			sql.append(" WHERE ").append(where.stream().collect(Collectors.joining(" AND ")));
		}

		if (!orderByParts.isEmpty()) {
			sql.append(" ORDER BY ").append(orderByParts.stream().collect(Collectors.joining(", ")));
		}

		if (limitPart != null) {
			sql.append(" LIMIT ?");
		}

		if (offsetPart != null) {
			sql.append(" OFFSET ?");
		}

		sql.append(";");
		return sql.toString();
	}

	private ReturnMapping buildReturnMapping(final Method method) {
		final AnnotatedType annotatedType = this.getActualReturnType(method.getAnnotatedReturnType());
		final Class<?> actualRawType = PCUtils.getRawClass(annotatedType.getType());
		final boolean entryReturn = DataBaseEntry.class.isAssignableFrom(actualRawType);
		return new ReturnMapping(annotatedType, entryReturn, entryReturn ? null : this.dataBaseEntryUtils.getTypeFor(annotatedType));
	}

	private AnnotatedType getActualReturnType(final AnnotatedType type) {
		if (type instanceof AnnotatedParameterizedType) {
			final Type rawType = ((ParameterizedType) type.getType()).getRawType();
			final AnnotatedType[] args = ((AnnotatedParameterizedType) type).getAnnotatedActualTypeArguments();

			if ((Optional.class.equals(rawType) || List.class.equals(rawType)) && args.length == 1) {
				return this.getActualReturnType(args[0]);
			}

			if (NextTask.class.equals(rawType) && args.length > 0) {
				return this.getActualReturnType(args[args.length - 1]);
			}
		}
		return type;
	}

	private boolean isNonNull(final AnnotatedElement annotatedElement) {
		return Arrays.stream(annotatedElement.getAnnotations())
				.anyMatch(
						c -> "NotNull".equals(c.annotationType().getSimpleName()) || "NonNull".equals(c.annotationType().getSimpleName()));
	}

	private boolean isNullable(final AnnotatedElement annotatedElement) {
		return Arrays.stream(annotatedElement.getAnnotations()).anyMatch(c -> "Nullable".equals(c.annotationType().getSimpleName()));
	}

	private String normalizeComparator(final String comparator, final Method method) {
		final String normalized = comparator == null ? "=" : comparator.trim().toUpperCase();
		switch (normalized) {
		case "=":
		case "<":
		case "<=":
		case ">":
		case ">=":
		case "LIKE":
		case "<>":
			return normalized;
		default:
			throw new IllegalArgumentException("Unsupported @Param comparator '" + comparator + "' on method " + method
					+ ".\nSupported comparators are: =, <, <=, >, >=, <>, !=, LIKE.");
		}
	}

	private String resolveParameterColumnName(final Parameter parameter, final Param annotation, final Method method) {
		if (annotation != null && annotation.value() != null && !annotation.value().trim().isEmpty()) {
			return annotation.value().trim();
		}

		final String name = parameter.getName();
		if (!parameter.isNamePresent() || name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Could not resolve query column name for parameter " + parameter + " on method " + method
					+ ". Add @Param(\"column_name\") to the parameter.");
		}

		return name.trim();
	}

	protected boolean isListType(final Type type) {
		if (type instanceof ParameterizedType) {
			final Type raw = ((ParameterizedType) type).getRawType();
			if (raw instanceof Class<?>) {
				return List.class.isAssignableFrom((Class<?>) raw);
			}
		}
		if (type instanceof Class<?>) {
			return List.class.isAssignableFrom((Class<?>) type);
		}
		return false;
	}

}
