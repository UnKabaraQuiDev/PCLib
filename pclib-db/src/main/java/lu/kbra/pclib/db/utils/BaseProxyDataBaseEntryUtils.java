package lu.kbra.pclib.db.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import lu.kbra.pclib.db.utils.SimpleTransformingQuery.MapSimpleTransformingQuery;
import lu.kbra.pclib.impl.ThrowingFunction;

public class BaseProxyDataBaseEntryUtils extends BaseDataBaseEntryUtils implements ProxyDataBaseEntryUtils {

	@Override
	public <T extends DataBaseEntry> void initQueries(final SQLQueryable<T> instance) {
		final Class<? extends SQLQueryable<T>> tableClazz = instance.getTargetClass();
		final Field[] tableFields = tableClazz.getDeclaredFields();
		final String tableName = this.getQueryableName(tableClazz);

		// scan the table itself
		for (final Field field : tableFields) {
			if (Modifier.isStatic(field.getModifiers()) || !field.isAnnotationPresent(Query.class)) {
				continue;
			}

			field.setAccessible(true);

			final Type fieldType = field.getGenericType();
			if (!(fieldType instanceof ParameterizedType)) {
				throw new IllegalArgumentException("Invalid query type: " + fieldType.getTypeName() + " for: " + field);
			}

			try {
				final Object value = this
						.buildTableQueryFunction(tableClazz, tableName, instance, fieldType, field.getAnnotation(Query.class));

				if (field != null) {
					field.set(instance, value);
				}
			} catch (final Exception e) {
				throw new RuntimeException("Failed to initialize @Query field: " + field.getName() + ", from: " + tableClazz.getName(), e);
			}
		}
	}

	@Override
	public <T extends DataBaseEntry> Object buildTableQueryFunction(
			final Class<? extends SQLQueryable<T>> tableClazz,
			final String tableName,
			final SQLQueryable<T> instance,
			final Type type,
			final Query query) {
		final String queryText = query.value().replace(Query.TABLE_NAME, PCUtils.sqlEscapeIdentifier(tableName));

		if (query.limit() > query.offset() && !(query.offset() == -1 || query.limit() == -1)) {
			throw new IllegalArgumentException("Invalid order: (offset) -> " + query.offset() + " (limit) -> " + query.limit()
					+ ", should be in this order: <others> <limit> <offset>");
		}

		final ParameterizedType pt = (ParameterizedType) type;
		final Type raw = pt.getRawType();

		if (!NextTask.class.equals(raw)) {
			throw new IllegalArgumentException("Unsupported query field type: " + raw);
		}

		return null;
		// throw new UnsupportedOperationException("Assigning a NextTask in tables isn't
		// supported yet.");
	}

	private Query.Type detectDefaultTableStrategy(final ParameterizedType pt) {
		if (this.isListType(pt.getActualTypeArguments()[1])) {
			return Query.Type.LIST_EMPTY;
		}

		return Query.Type.FIRST_NULL;
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
			if (returnType instanceof ParameterizedType && NextTask.class.equals(((ParameterizedType) returnType).getRawType())) { // for
																																	// NextTask

				final ParameterizedType ptReturnType = (ParameterizedType) returnType;

				if (queryText == null || queryText.isEmpty()) {// for automatic queries (by column only)
					final String[] cols = query.columns();

					final String sql = SQLBuilder.safeSelect(tableName, cols, query.limit() != -1, query.offset() != -1);

					return this.getSupplierForMethod(ptReturnType, instance, cols, sql, query);
				} else { // for manual queries (with sql)
					return this.getSupplierForMethod(ptReturnType, instance, queryText, query);
				}

			} else { // for direct return

				final Type[] argTypes = method.getGenericParameterTypes();

				if (queryText == null || queryText.isEmpty()) {// for automatic queries (by column only)
					final String[] cols = query.columns();

					final String sql = SQLBuilder.safeSelect(tableName, cols, query.limit() != -1, query.offset() != -1);

					return this.getFunctionForMethod(returnType, argTypes, instance, sql, query);
				} else { // for manual queries (with sql)
					return this.getFunctionForMethod(returnType, argTypes, instance, queryText, query);
				}

			}
		} catch (final Exception e) {
			throw new RuntimeException(
					"Exception when building method query function for: " + method + " on [" + instance.getClass().getName() + "]",
					e);
		}
	}

	/** by manual sql */
	private <T extends DataBaseEntry> Function<List<Object>, ?> getSupplierForMethod(
			final ParameterizedType pt,
			final SQLQueryable<T> instance,
			final String sql,
			final Query query) {
		final Query.Type type = Query.Type.AUTO.equals(query.strategy()) ? this.detectDefaultTableStrategy(pt) : query.strategy();
		final Type argType = pt.getActualTypeArguments()[0];

		final Class<?> rawClass;
		if (argType instanceof ParameterizedType) {
			rawClass = (Class<?>) ((ParameterizedType) argType).getRawType();
		} else if (argType instanceof Class<?>) {
			rawClass = (Class<?>) argType;
		} else {
			throw new IllegalArgumentException("Unsupported type argument: " + argType);
		}

		// list
		if (List.class.isAssignableFrom(rawClass)) {
			final Type[] typeArgs = ((ParameterizedType) argType).getActualTypeArguments();
			final List<ColumnType> types = Arrays.stream(typeArgs)
					.map(col -> this.getTypeFor(PCUtils.getRawClass(col), this.getFallbackField()))
					.collect(Collectors.toList());

			return v -> NextTask.withArg((ThrowingFunction<List<Object>, ?, Throwable>) obj -> instance
					.query(new ListSimpleTransformingQuery(sql, obj, types, type)));
		}

		// tuple (2, 3)
		if (Tuple.class.isAssignableFrom(rawClass)) {
			final Type[] typeArgs = ((ParameterizedType) argType).getActualTypeArguments();
			final List<ColumnType> types = Arrays.stream(typeArgs)
					.map(col -> this.getTypeFor(PCUtils.getRawClass(col), this.getFallbackField()))
					.collect(Collectors.toList());

			return v -> NextTask.withArg((ThrowingFunction<Tuple, ?, Throwable>) obj -> instance
					.query(new ListSimpleTransformingQuery(sql, Arrays.asList(obj.asArray()), types, type)));
		}

		// simple object (1)
		return v -> NextTask.withArg((ThrowingFunction<Object, ?, Throwable>) obj -> instance.query(new ListSimpleTransformingQuery(sql,
				Arrays.asList(obj),
				Arrays.asList(this.getTypeFor(PCUtils.getRawClass(argType), this.getFallbackField())),
				type)));
	}

	/** by columns (automatic) */
	private <T extends DataBaseEntry> Function<List<Object>, ?> getSupplierForMethod(
			final ParameterizedType ptReturnType,
			final SQLQueryable<T> instance,
			String[] cols,
			final String sql,
			final Query query) {
		cols = query.limit() == -1 ? cols : PCUtils.<String>insert(cols, query.limit(), Query.LIMIT_KEY);
		final String[] insCols = query.offset() == -1 ? cols : PCUtils.<String>insert(cols, query.offset(), Query.OFFSET_KEY);
		final Query.Type type = Query.Type.AUTO.equals(query.strategy()) ? this.detectDefaultTableStrategy(ptReturnType) : query.strategy();

		final Type returnTypeGeneric = ptReturnType.getActualTypeArguments()[0];

		final Class<?> argRawClass;
		if (returnTypeGeneric instanceof ParameterizedType) {
			argRawClass = (Class<?>) ((ParameterizedType) returnTypeGeneric).getRawType();
		} else if (returnTypeGeneric instanceof Class<?>) {
			argRawClass = (Class<?>) returnTypeGeneric;
		} else {
			throw new IllegalArgumentException("Unsupported type argument: " + returnTypeGeneric);
		}

		final Class<? extends DataBaseEntry> entryClazz = this.getEntryType(instance.getTargetClass());

		// map
		if (Map.class.isAssignableFrom(argRawClass)) {
			final HashMap<String, ColumnType> types = Arrays.stream(cols)
					.collect(Collectors.toMap(col -> col, col -> this.getColumnType(entryClazz, col), (u, v) -> u, HashMap::new));

			return v -> NextTask.withArg((ThrowingFunction<Map<String, Object>, ?, Throwable>) obj -> instance
					.query(new MapSimpleTransformingQuery(sql, insCols, obj, types, type)));
		}

		// list
		if (List.class.isAssignableFrom(argRawClass)) {
			final List<ColumnType> types = Arrays.stream(cols).map(col -> this.getColumnType(entryClazz, col)).collect(Collectors.toList());

			return v -> NextTask.withArg((ThrowingFunction<List<Object>, ?, Throwable>) obj -> instance
					.query(new ListSimpleTransformingQuery(sql, obj, types, type)));
		}

		// tuple (2, 3)
		if (Tuple.class.isAssignableFrom(argRawClass)) {
			final List<ColumnType> types = Arrays.stream(cols).map(col -> this.getColumnType(entryClazz, col)).collect(Collectors.toList());

			return v -> NextTask.withArg((ThrowingFunction<Tuple, ?, Throwable>) obj -> instance
					.query(new ListSimpleTransformingQuery(sql, Arrays.asList(obj.asArray()), types, type)));
		}

		final HashMap<String, ColumnType> types = Arrays.stream(cols)
				.collect(Collectors.toMap(col -> col, col -> this.getColumnType(entryClazz, col), (u, v) -> u, HashMap::new));

		// simple object (1)
		return v -> NextTask.withArg((ThrowingFunction<Object, ?, Throwable>) obj -> instance
				.query(new MapSimpleTransformingQuery(sql, insCols, PCUtils.hashMap(insCols[0], obj), types, type)));
	}

	protected ColumnType getColumnType(final Class<? extends DataBaseEntry> entryClazz, final String col) {
		if (Query.LIMIT_KEY.equals(col) || Query.OFFSET_KEY.equals(col)) {
			return this.typeMap.get(Long.class).apply(this.getFallbackColumnAnnotation());
		}
		return this.getTypeFor(this.getFieldFor(entryClazz, col));
	}

	/** for automatic & manual but with direct return */
	private <T extends DataBaseEntry> Function<List<Object>, ?> getFunctionForMethod(
			final Type ptReturnType,
			final Type[] argTypes,
			final SQLQueryable<T> instance,
			final String sql,
			final Query query) {
		final Query.Type type = Query.Type.AUTO.equals(query.strategy()) ? this.detectDefaultMethodStrategy(ptReturnType)
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

	private Query.Type detectDefaultMethodStrategy(final Type returnType) {
		if (returnType instanceof ParameterizedType && this.isListType(returnType)) {
			return Query.Type.LIST_EMPTY;
		} else if (returnType instanceof Class) {
			return Query.Type.FIRST_NULL;
		} else if (returnType instanceof ParameterizedType && NextTask.class.equals(((ParameterizedType) returnType).getRawType())) {
			return this.detectDefaultMethodStrategy(((ParameterizedType) returnType).getActualTypeArguments()[1]);
		}
		return Query.Type.FIRST_NULL;
		// throw new IllegalArgumentException("Unsupported return type: " + returnType);
	}

	public Query.Type detectDefaultEntryStrategy(final ParameterizedType returnType) {
		final Type sqlQueryType = this.findSQLQueryInterface(returnType);
		if (!(sqlQueryType instanceof ParameterizedType)) {
			return Query.Type.FIRST_NULL;
		}

		final Type[] typeArgs = ((ParameterizedType) sqlQueryType).getActualTypeArguments();
		if (typeArgs.length != 2) {
			return Query.Type.FIRST_NULL;
		}

		final Type resultType = typeArgs[1];
		if (this.isListType(resultType)) {
			return Query.Type.LIST_EMPTY;
		}

		return Query.Type.FIRST_NULL;
	}

}
