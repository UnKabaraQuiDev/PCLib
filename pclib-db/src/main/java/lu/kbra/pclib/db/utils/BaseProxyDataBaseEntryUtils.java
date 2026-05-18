package lu.kbra.pclib.db.utils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
import lu.kbra.pclib.db.autobuild.query.Query;
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
				// for automatic queries (by column only)
				final String[] cols = query.columns();

				final String sql = SQLBuilder.safeSelect(tableName, cols, query.limit() != -1, query.offset() != -1);

				return this.getFunctionForMethod(method, returnType, argTypes, instance, sql, query);
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
