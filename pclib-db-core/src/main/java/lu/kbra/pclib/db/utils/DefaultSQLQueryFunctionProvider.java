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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.query.QueryParameter;
import lu.kbra.pclib.db.query.ReorderingTransformingQuery.ListReorderingTransformingQuery;
import lu.kbra.pclib.db.query.ReorderingTransformingQuery.ScalarListReorderingTransformingQuery;
import lu.kbra.pclib.db.query.SimpleTransformingQuery.ListSimpleTransformingQuery;
import lu.kbra.pclib.db.query.SimpleTransformingQuery.ScalarListSimpleTransformingQuery;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
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
	public static final class ParameterQueryPart {

		private final int index;
		private final String column;
		private final String comparator;
		private final boolean ignoreNull;
		private final ColumnType<?, ?> type;

	}

	@Data
	private static final class ParameterQueryPlan {

		private final String sql;
		private final List<ParameterQueryPart> whereParts;
		private final ParameterQueryPart limitPart;
		private final ParameterQueryPart offsetPart;

		private List<ColumnType<?, ?>> types(final List<Object> args) {
			final List<ColumnType<?, ?>> types = new ArrayList<>();

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
	public static final class ReturnMapping {

		private final AnnotatedType actualType;
		private final boolean entryReturn;
		private final ColumnType<?, ?> columnType;

	}

	protected DatabaseEntryUtils databaseEntryUtils;
	protected SQLStructureVisitor structureVisitor;
	protected SQLColumnTypeProvider columnTypeProvider;

	public DefaultSQLQueryFunctionProvider(final DatabaseEntryUtils databaseEntryUtils) {
		this.databaseEntryUtils = databaseEntryUtils;
		this.structureVisitor = databaseEntryUtils.getStructureVisitor();
		this.columnTypeProvider = databaseEntryUtils.getColumnTypeProvider();
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
	protected <T extends DatabaseEntry, V> Function<List<Object>, V> buildFunctionForParameterMethod(
			final Method method,
			final AnnotatedType returnType,
			final SQLQueryable<T> instance,
			final Query query) {
		final Query.Type type = query.strategy().isAuto() ? this.detectDefaultStrategy(returnType, method) : query.strategy();
		final ReturnMapping returnMapping = this.buildReturnMapping(method);
		final ParameterQueryPlan plan = this.buildParameterQueryPlan(instance, method, query.orderBy(), returnMapping);
		final Class<?> returnTypeClass = PCUtils.wrapPrimitiveClass(PCUtils.getRawClass(returnType.getType()));

		if (returnMapping.entryReturn) {
			if (returnTypeClass == Optional.class) {
				return (Function<List<Object>, V>) obj -> {
					final Object d = instance
							.query(new ListSimpleTransformingQuery<>(plan.sql, toQueryParameters(plan.types(obj), plan.values(obj)), type));
					return (V) returnTypeClass.cast(type.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
				};
			} else {
				return (Function<List<Object>, V>) obj -> (V) returnTypeClass.cast(instance
						.query(new ListSimpleTransformingQuery<>(plan.sql, toQueryParameters(plan.types(obj), plan.values(obj)), type)));
			}
		} else if (returnTypeClass == Optional.class) {
			return (Function<List<Object>, V>) obj -> {
				final Object d = instance.query(new ScalarListSimpleTransformingQuery<>(plan.sql,
						toQueryParameters(plan.types(obj), plan.values(obj)),
						type,
						returnMapping.columnType,
						returnMapping.actualType.getType()));
				return (V) returnTypeClass.cast(type.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
			};
		} else {
			return (Function<List<Object>, V>) obj -> (V) returnTypeClass
					.cast(instance.query(new ScalarListSimpleTransformingQuery<>(plan.sql,
							toQueryParameters(plan.types(obj), plan.values(obj)),
							type,
							returnMapping.columnType,
							returnMapping.actualType.getType())));
		}
	}

	protected ParameterQueryPlan buildParameterQueryPlan(
			final SQLQueryable<?> instance,
			final Method method,
			final OrderBy[] orderBy,
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

			final ColumnType<?, ?> type = this.databaseEntryUtils.getTypeFor(parameter.getAnnotatedType());

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
				final String column = this.resolveParameterColumnName(instance, parameter, method);
				final String comparator = this.normalizeComparator(annotation == null ? "=" : annotation.comparator(), method);
				final boolean ignoreNull = annotation.ignoreNull();

				whereParts.add(new ParameterQueryPart(i, column, comparator, ignoreNull, type));
			}
		}

		final List<String> orderByParts = Arrays.stream(orderBy)
				.map(order -> this.buildOrderByPart(order, method))
				.collect(Collectors.toList());

		final String sql = databaseEntryUtils.getStructureVisitor()
				.buildParameterQuerySql(instance, whereParts, orderByParts, limitPart, offsetPart, returnMapping);
		return new ParameterQueryPlan(sql, whereParts, limitPart, offsetPart);
	}

	@Override
	public <T extends DatabaseEntry, V> Function<List<Object>, V>
			buildMethodQueryFunction(final SQLQueryable<T> instance, final Method method) {
		try {
			if (!method.isAnnotationPresent(Query.class)) {
				throw new IllegalArgumentException("No @Query found on method: " + method);
			}

			final Query query = method.getAnnotation(Query.class);

			if (query.limit() > query.offset() && !(query.offset() == -1 || query.limit() == -1)) {
				throw new IllegalArgumentException("Invalid order: (offset) -> " + query.offset() + " (limit) -> " + query.limit()
						+ ", should be in this order: <others> <limit> <offset>");
			}

			final AnnotatedType returnType = method.getAnnotatedReturnType();
			final AnnotatedType[] argTypes = method.getAnnotatedParameterTypes();

			final String queryText = query.value() == null || query.value().trim().isEmpty() ? null : query.value();

			if (queryText == null && (query.columns().length != 0 || query.limit() != -1 || query.offset() != -1)) {
				// for manual queries (by declared @Query columns)
				final String sql = this.structureVisitor.safeSelect(instance, query.columns(), query.limit() != -1, query.offset() != -1);
				return this.buildFunctionForMethod(method, returnType, argTypes, instance, sql, query);
			} else if (queryText == null) {
				// for automatic queries driven by method parameters
				return this.buildFunctionForParameterMethod(method, returnType, instance, query);
			} else {
				// for manual queries (with sql)
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
	private <T extends DatabaseEntry, B> Function<List<Object>, B> buildFunctionForMethod(
			final Method method,
			final AnnotatedType returnType,
			final AnnotatedType[] argTypes,
			final SQLQueryable<T> instance,
			final String querySql,
			final Query query) {
		final Query.Type type = query.strategy().isAuto() ? this.detectDefaultStrategy(returnType, method) : query.strategy();
		final ReturnMapping returnMapping = this.buildReturnMapping(method);
		final Class<?> returnTypeClass = PCUtils.wrapPrimitiveClass(PCUtils.getRawClass(returnType.getType()));
		final Map<String, String> paramNameToColumnName = new HashMap<>();
		final Map<String, Integer> paramNameToIndex = new HashMap<>();
		for (int i = 0; i < method.getParameters().length; i++) {
			final Parameter parameter = method.getParameters()[i];
			try {
				final String columnName = this.resolveParameterColumnName(instance, parameter, method);
				paramNameToColumnName.put(Integer.toString(i), columnName);
				paramNameToColumnName.put(parameter.getName(), columnName);
			} catch (Exception e) {
				// ignore
			}
			paramNameToIndex.put(Integer.toString(i), i);
			paramNameToIndex.put(parameter.getName(), i);
		}
		final List<ColumnType<?, ?>> types = Arrays.stream(argTypes).map(this.databaseEntryUtils::getTypeFor).collect(Collectors.toList());
		final List<Integer> paramOrder = new ArrayList<>(method.getParameterCount());

		final String sql = this.databaseEntryUtils.resolveSQLQualifiers(instance, querySql, new HashMap<>(), in -> {
			if (in.startsWith(DatabaseEntryUtils.PARAMETER_COLUMN_KEY)) {
				final String[] tokens = in.split(":");
				if (paramNameToColumnName.containsKey(tokens[1])) {
					return Optional.ofNullable(paramNameToColumnName.get(tokens[1]));
				} else {
					throw new IllegalArgumentException("Parameter named: '" + tokens[1] + "' not found on " + method
							+ "\nYou may need to enable parameter name retention during compilation.");
				}
			} else if (in.startsWith(DatabaseEntryUtils.PARAMETER_VALUE_KEY)) {
				final String[] tokens = in.split(":");
				if (paramNameToIndex.containsKey(tokens[1])) {
					paramOrder.add(paramNameToIndex.get(tokens[1]));
					return Optional.of("?");
				} else {
					throw new IllegalArgumentException("Parameter named: '" + tokens[1] + "' not found on " + method
							+ "\nYou may need to enable parameter name retention during compilation.");
				}
			}

			return Optional.empty();
		});

		if (paramOrder.isEmpty()) {

			if (returnMapping.entryReturn) {
				if (returnTypeClass == Optional.class) {
					return (Function<List<Object>, B>) objs -> {
						final Object d = instance.query(new ListSimpleTransformingQuery<>(sql, toQueryParameters(types, objs), type));
						return (B) returnTypeClass.cast(type.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
					};
				} else {
					return (Function<List<Object>, B>) objs -> (B) returnTypeClass
							.cast(instance.query(new ListSimpleTransformingQuery<>(sql, toQueryParameters(types, objs), type)));
				}
			} else if (returnTypeClass == Optional.class) {
				return (Function<List<Object>, B>) objs -> {
					final Object d = instance.query(new ScalarListSimpleTransformingQuery<>(sql,
							toQueryParameters(types, objs),
							type,
							returnMapping.columnType,
							returnMapping.actualType.getType()));
					return (B) returnTypeClass.cast(type.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
				};
			} else {
				return (Function<List<Object>, B>) objs -> (B) returnTypeClass
						.cast(instance.query(new ScalarListSimpleTransformingQuery<>(sql,
								toQueryParameters(types, objs),
								type,
								returnMapping.columnType,
								returnMapping.actualType.getType())));
			}

		} else {

			final int[] reordering = paramOrder.stream().mapToInt(Integer::valueOf).toArray();

			if (returnMapping.entryReturn) {

				if (returnTypeClass == Optional.class) {
					return (Function<List<Object>, B>) objs -> {
						final Object d = instance
								.query(new ListReorderingTransformingQuery<>(sql, toQueryParameters(types, objs), type, reordering));
						return (B) returnTypeClass.cast(type.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
					};
				} else {
					return (Function<List<Object>, B>) objs -> (B) returnTypeClass.cast(
							instance.query(new ListReorderingTransformingQuery<>(sql, toQueryParameters(types, objs), type, reordering)));
				}
			} else if (returnTypeClass == Optional.class) {
				return (Function<List<Object>, B>) objs -> {
					final Object d = instance.query(new ScalarListReorderingTransformingQuery<>(sql,
							toQueryParameters(types, objs),
							type,
							returnMapping.columnType,
							returnMapping.actualType.getType(),
							reordering));
					return (B) returnTypeClass.cast(type.isNullable() ? Optional.ofNullable(d) : Optional.of(d));
				};
			} else {
				return (Function<List<Object>, B>) objs -> (B) returnTypeClass
						.cast(instance.query(new ScalarListReorderingTransformingQuery<>(sql,
								toQueryParameters(types, objs),
								type,
								returnMapping.columnType,
								returnMapping.actualType.getType(),
								reordering)));
			}
		}
	}

	private List<QueryParameter<?>> toQueryParameters(List<ColumnType<?, ?>> types, List<Object> objs) {
		if (types.size() != objs.size()) {
			throw new IllegalArgumentException("Number of arguments not matching, expected: " + types.size() + " but got: " + objs.size());
		}
		if (types.isEmpty()) {
			return Collections.emptyList();
		}
		return IntStream.range(0, types.size()).mapToObj(i -> toQueryParameter(types.get(i), objs.get(i))).collect(Collectors.toList());
	}

	private <T> QueryParameter<?> toQueryParameter(ColumnType<?, ?> columnType, Object object) {
		return new QueryParameter<T>((ColumnType<T, ?>) columnType, (T) object);
	}

	private static List<Object> reorder(final List<Object> obj, final List<Integer> paramOrder) {
		final List<Object> result = new ArrayList<>(paramOrder.size());

		for (final Integer index : paramOrder) {
			result.add(obj.get(index));
		}

		return result;
	}

	private String buildOrderByPart(final OrderBy order, final Method method) {
		if (order.column() == null || order.column().trim().isEmpty()) {
			throw new IllegalArgumentException("@OrderBy column must not be empty on method " + method);
		}
		return this.structureVisitor.qualifiedName(order.column().trim()) + " " + order.type().name();
	}

	private ReturnMapping buildReturnMapping(final Method method) {
		final AnnotatedType annotatedType = this.getActualReturnType(method.getAnnotatedReturnType());
		final Class<?> actualRawType = PCUtils.getRawClass(annotatedType.getType());
		final boolean entryReturn = DatabaseEntry.class.isAssignableFrom(actualRawType);
		return new ReturnMapping(annotatedType, entryReturn, entryReturn ? null : this.databaseEntryUtils.getTypeFor(annotatedType));
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

	private String resolveParameterColumnName(final SQLQueryable<?> table, final Parameter parameter, final Method method) {
		final Param annotation = parameter.getAnnotation(Param.class);

		if (annotation != null && annotation.value() != null && !annotation.value().trim().isEmpty()) {
			return annotation.value().trim();
		}

		if (annotation != null && annotation.field() != null && !annotation.field().trim().isEmpty()) {
			return this.databaseEntryUtils.getColumnForField(table, annotation.field()).getLocalQualifiedName();
		}

		final String name = parameter.getName();
		if (!parameter.isNamePresent() || name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Could not resolve query column name for parameter " + parameter + " on method " + method
					+ ". Add @Param(\"column_name\") to the parameter.");
		}

		return structureVisitor.fieldToColumnName(name.trim());
	}

	protected boolean isListType(final Type type) {
		if (type instanceof ParameterizedType) {
			final Type raw = ((ParameterizedType) type).getRawType();
			if (raw instanceof Class<?>) {
				return Collection.class.isAssignableFrom((Class<?>) raw);
			}
		}
		if (type instanceof Class<?>) {
			return Collection.class.isAssignableFrom((Class<?>) type);
		}
		return false;
	}

}
