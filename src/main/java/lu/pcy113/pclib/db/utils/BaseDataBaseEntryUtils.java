package lu.pcy113.pclib.db.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.builder.SQLBuilder;
import lu.pcy113.pclib.datastructure.tuple.Tuple;
import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.entry.Insert;
import lu.pcy113.pclib.db.annotations.entry.Load;
import lu.pcy113.pclib.db.annotations.entry.Update;
import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.Column;
import lu.pcy113.pclib.db.autobuild.column.ColumnData;
import lu.pcy113.pclib.db.autobuild.column.ColumnType;
import lu.pcy113.pclib.db.autobuild.column.DefaultValue;
import lu.pcy113.pclib.db.autobuild.column.ForeignKey;
import lu.pcy113.pclib.db.autobuild.column.Generated;
import lu.pcy113.pclib.db.autobuild.column.Nullable;
import lu.pcy113.pclib.db.autobuild.column.OnUpdate;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.autobuild.column.Unique;
import lu.pcy113.pclib.db.autobuild.column.type.BinaryTypes.BinaryType;
import lu.pcy113.pclib.db.autobuild.column.type.BinaryTypes.BlobType;
import lu.pcy113.pclib.db.autobuild.column.type.BinaryTypes.VarbinaryType;
import lu.pcy113.pclib.db.autobuild.column.type.BooleanType;
import lu.pcy113.pclib.db.autobuild.column.type.DecimalTypes.DecimalType;
import lu.pcy113.pclib.db.autobuild.column.type.DecimalTypes.DoubleType;
import lu.pcy113.pclib.db.autobuild.column.type.DecimalTypes.FloatType;
import lu.pcy113.pclib.db.autobuild.column.type.IntTypes.BigIntType;
import lu.pcy113.pclib.db.autobuild.column.type.IntTypes.BitType;
import lu.pcy113.pclib.db.autobuild.column.type.IntTypes.IntType;
import lu.pcy113.pclib.db.autobuild.column.type.IntTypes.SmallIntType;
import lu.pcy113.pclib.db.autobuild.column.type.IntTypes.TinyIntType;
import lu.pcy113.pclib.db.autobuild.column.type.TextTypes.CharType;
import lu.pcy113.pclib.db.autobuild.column.type.TextTypes.JsonType;
import lu.pcy113.pclib.db.autobuild.column.type.TextTypes.TextType;
import lu.pcy113.pclib.db.autobuild.column.type.TextTypes.VarcharType;
import lu.pcy113.pclib.db.autobuild.column.type.TimeTypes.DateType;
import lu.pcy113.pclib.db.autobuild.column.type.TimeTypes.TimestampType;
import lu.pcy113.pclib.db.autobuild.query.Query;
import lu.pcy113.pclib.db.autobuild.table.CharacterSet;
import lu.pcy113.pclib.db.autobuild.table.Collation;
import lu.pcy113.pclib.db.autobuild.table.ConstraintData;
import lu.pcy113.pclib.db.autobuild.table.Engine;
import lu.pcy113.pclib.db.autobuild.table.Factory;
import lu.pcy113.pclib.db.autobuild.table.ForeignKeyData;
import lu.pcy113.pclib.db.autobuild.table.PrimaryKeyData;
import lu.pcy113.pclib.db.autobuild.table.TableName;
import lu.pcy113.pclib.db.autobuild.table.TableStructure;
import lu.pcy113.pclib.db.autobuild.table.UniqueData;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;
import lu.pcy113.pclib.db.utils.SimpleTransformingQuery.ListSimpleTransformingQuery;
import lu.pcy113.pclib.db.utils.SimpleTransformingQuery.MapSimpleTransformingQuery;
import lu.pcy113.pclib.impl.ExceptionFunction;

@SuppressWarnings("serial")
public class BaseDataBaseEntryUtils implements DataBaseEntryUtils {

	private final Map<Class<?>, Function<Column, ColumnType>> typeMap = new HashMap<Class<?>, Function<Column, ColumnType>>() {
		{
			// -- java types
			put(String.class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
			put(CharSequence.class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
			put(char[].class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());

			put(byte[].class, col -> col.length() != -1 ? new VarbinaryType(col.length()) : new BlobType());
			put(ByteBuffer.class, col -> col.length() != -1 ? new VarbinaryType(col.length()) : new BlobType());

			put(Byte.class, col -> new TinyIntType());
			put(byte.class, col -> new TinyIntType());
			put(Short.class, col -> new SmallIntType());
			put(short.class, col -> new SmallIntType());
			put(Integer.class, col -> new IntType());
			put(int.class, col -> new IntType());
			put(Long.class, col -> new BigIntType());
			put(long.class, col -> new BigIntType());

			put(Double.class, col -> new DoubleType());
			put(double.class, col -> new DoubleType());
			put(Float.class, col -> new FloatType());
			put(float.class, col -> new FloatType());

			put(Boolean.class, col -> new BooleanType());
			put(boolean.class, col -> new BooleanType());

			put(Timestamp.class, col -> new TimestampType());
			put(Date.class, col -> new DateType());

			put(JSONObject.class, col -> new JsonType());
			put(JSONArray.class, col -> new JsonType());

			// -- native types
			put(TextType.class, col -> new TextType());
			put(CharType.class, col -> new CharType(col.length()));
			put(VarcharType.class, col -> new VarcharType(col.length()));

			put(BinaryType.class, col -> new BinaryType(col.length()));
			put(VarbinaryType.class, col -> new BinaryType(col.length()));
			put(BlobType.class, col -> new BlobType());

			put(TimestampType.class, col -> new TimestampType());
			put(DateType.class, col -> new DateType());

			put(BitType.class, col -> new BitType());
			put(SmallIntType.class, col -> new SmallIntType());
			put(IntType.class, col -> new IntType());
			put(BigIntType.class, col -> new BigIntType());

			put(DoubleType.class, col -> new DoubleType());
			put(FloatType.class, col -> new FloatType());
			put(DecimalType.class, col -> new DecimalType(col.length(), Integer.parseInt(col.params()[0])));

			put(TimestampType.class, col -> new TimestampType());
			put(DateType.class, col -> new DateType());

			put(JsonType.class, col -> new DateType());
		}
	};

	/*
	 * private final Map<Class<?>, ExceptionBiFunction<ResultSet, Integer, Object>>
	 * resultSetExtractors = new HashMap<Class<?>, ExceptionBiFunction<ResultSet,
	 * Integer, Object>>() { { put(String.class, (rs, column) ->
	 * rs.getString(column)); put(CharSequence.class, (rs, column) ->
	 * rs.getString(column)); put(JSONObject.class, (rs, column) -> new
	 * JSONObject(rs.getString(column))); put(JSONArray.class, (rs, column) -> new
	 * JSONArray(rs.getString(column)));
	 * 
	 * put(short.class, (rs, column) -> rs.getShort(column)); put(Short.class, (rs,
	 * column) -> rs.getShort(column));
	 * 
	 * put(int.class, (rs, column) -> rs.getInt(column)); put(Integer.class, (rs,
	 * column) -> rs.getInt(column));
	 * 
	 * put(long.class, (rs, column) -> rs.getLong(column)); put(Long.class, (rs,
	 * column) -> rs.getLong(column));
	 * 
	 * put(double.class, (rs, column) -> rs.getDouble(column)); put(Double.class,
	 * (rs, column) -> rs.getDouble(column));
	 * 
	 * put(float.class, (rs, column) -> rs.getFloat(column)); put(Float.class, (rs,
	 * column) -> rs.getFloat(column));
	 * 
	 * put(boolean.class, (rs, column) -> rs.getBoolean(column)); put(Boolean.class,
	 * (rs, column) -> rs.getBoolean(column));
	 * 
	 * put(Timestamp.class, (rs, column) -> rs.getTimestamp(column));
	 * put(Date.class, (rs, column) -> rs.getDate(column)); } };
	 */

	@Override
	public ColumnType getTypeFor(Field field) {
		final Column colAnno = field.getAnnotation(Column.class);
		Class<?> fieldType = colAnno.type().equals(Class.class) ? field.getType() : colAnno.type();
		return getTypeFor(fieldType, colAnno);
	}

	@Override
	public ColumnType getTypeFor(Class<?> clazz, Column col) {
		if (typeMap.containsKey(clazz)) {
			return typeMap.get(clazz).apply(col);
		} else {
			throw new IllegalArgumentException("Unsupported type: " + clazz.getName() + " for column: " + col.name());
		}
	}

	@Override
	public <T extends DataBaseEntry> TableStructure scanTable(Class<? extends DataBaseTable<T>> tableClazz) {
		final TableStructure ts = scanEntry(getEntryType(tableClazz));

		if (tableClazz.isAnnotationPresent(CharacterSet.class)) {
			CharacterSet charsetAnno = tableClazz.getAnnotation(CharacterSet.class);
			ts.setCharacterSet(charsetAnno.value());
		}

		if (tableClazz.isAnnotationPresent(Engine.class)) {
			Engine engineAnno = tableClazz.getAnnotation(Engine.class);
			ts.setEngine(engineAnno.value());
		}

		if (tableClazz.isAnnotationPresent(Collation.class)) {
			Collation engineAnno = tableClazz.getAnnotation(Collation.class);
			ts.setCollation(engineAnno.value());
		}

		if (tableClazz.isAnnotationPresent(TableName.class)) {
			TableName tableAnno = tableClazz.getAnnotation(TableName.class);
			if (!tableAnno.value().isEmpty()) {
				ts.setName(tableAnno.value());
			}
		}

		return ts;
	}

	public List<Field> sortFields(Field[] fields) {
		List<Field> pkFields = new ArrayList<>();
		List<Field> fkFields = new ArrayList<>();
		List<Field> otherFields = new ArrayList<>();

		for (Field field : fields) {
			if (field.isAnnotationPresent(PrimaryKey.class)) {
				pkFields.add(field);
			} else if (field.isAnnotationPresent(ForeignKey.class)) {
				fkFields.add(field);
			} else {
				otherFields.add(field);
			}
		}

		Comparator<Field> byName = Comparator.comparing(Field::getName);

		pkFields.sort(byName);
		otherFields.sort(byName);
		fkFields.sort(byName);

		List<Field> sorted = new ArrayList<>();
		sorted.addAll(pkFields);
		sorted.addAll(otherFields);
		sorted.addAll(fkFields);

		return sorted;
	}

	@Override
	public <T extends DataBaseEntry> TableStructure scanEntry(Class<T> entryClazz) {
		final List<ColumnData> columns = new LinkedList<>();
		final List<ConstraintData> constraints = new LinkedList<>();
		final Set<String> primaryKeys = new LinkedHashSet<>();
		final Map<Integer, Set<String>> uniqueGroups = new LinkedHashMap<>();
		final Map<Class<? extends SQLQueryable<?>>, Map<ColumnData, ForeignKey>> foreignKeys = new LinkedHashMap<>();

		for (Field field : sortFields(entryClazz.getDeclaredFields())) {
			field.setAccessible(true);

			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}

			final Column colAnno = field.getAnnotation(Column.class);
			final String columnName = fieldToColumnName(field);

			ColumnType columnType = getTypeFor(colAnno.type().equals(Class.class) ? field.getType() : colAnno.type(), colAnno);

			ColumnData columnData = new ColumnData();
			columnData.setName(columnName);
			columnData.setType(columnType);

			if (field.isAnnotationPresent(AutoIncrement.class)) {
				columnData.setAutoIncrement(true);
			}

			if (field.isAnnotationPresent(DefaultValue.class)) {
				columnData.setDefaultValue(field.getAnnotation(DefaultValue.class).value());
			}

			if (field.isAnnotationPresent(OnUpdate.class)) {
				columnData.setOnUpdate(field.getAnnotation(OnUpdate.class).value());
			}

			if (field.isAnnotationPresent(Nullable.class)) {
				columnData.setNullable(field.getAnnotation(Nullable.class).value());
			} else {
				columnData.setNullable(false); // Default to true if not specified
			}

			// PRIMARY KEY
			if (field.isAnnotationPresent(PrimaryKey.class)) {
				primaryKeys.add(columnName);
			}

			// UNIQUE
			if (field.isAnnotationPresent(Unique.class)) {
				int group = field.getAnnotation(Unique.class).value();
				uniqueGroups.computeIfAbsent(group, k -> new LinkedHashSet<>()).add(columnName);
			}

			// FOREIGN KEY
			if (field.isAnnotationPresent(ForeignKey.class)) {
				ForeignKey fk = field.getAnnotation(ForeignKey.class);
				foreignKeys.computeIfAbsent(fk.table(), k -> new LinkedHashMap<>()).put(columnData, fk);
			}

			// GENERATED
			if (field.isAnnotationPresent(Generated.class)) {
				Generated gen = field.getAnnotation(Generated.class);
				
				columnData = new GeneratedColumnData(columnData, gen);
				
				columns.add(columnData);
			} else {
				columns.add(columnData);
			}
		}

		final TableStructure ts = new TableStructure(entryClazz);
		ts.setColumns(columns.toArray(new ColumnData[0]));

		// CONSTRAINTS
		if (!primaryKeys.isEmpty()) {
			if (primaryKeys.size() > 1) {
				throw new UnsupportedOperationException("Only one primary key is supported atm.");
			}
			constraints.add(new PrimaryKeyData(ts, primaryKeys.toArray(new String[0])));
		}

		for (Set<String> groupCols : uniqueGroups.values()) {
			constraints.add(new UniqueData(ts, groupCols.toArray(new String[0])));
		}

		for (Map.Entry<Class<? extends SQLQueryable<?>>, Map<ColumnData, ForeignKey>> entry : foreignKeys.entrySet()) {
			final Class<? extends SQLQueryable<?>> foreignQueryable = entry.getKey();
			final String refTableName = getQueryableName(foreignQueryable);
			final Map<ColumnData, ForeignKey> colMap = entry.getValue();

			final Map<Integer, List<Map.Entry<ColumnData, ForeignKey>>> grouped = new HashMap<>();

			for (Map.Entry<ColumnData, ForeignKey> colEntry : colMap.entrySet()) {
				int groupIndex = colEntry.getValue().groupId();
				grouped.computeIfAbsent(groupIndex, k -> new ArrayList<>()).add(colEntry);
			}

			for (List<Map.Entry<ColumnData, ForeignKey>> group : grouped.values()) {
				final String[] colNames = group.stream().map(e -> e.getKey().getName()).toArray(String[]::new);
				final String[] refCols = group.stream().map(e -> getReferencedColumnName(e.getValue())).toArray(String[]::new);

				if (PCUtils.duplicates(refCols)) {
					throw new IllegalArgumentException("Foreign key references duplicate columns: " + String.join(", ", refCols) + " to table: " + refTableName);
				}

				constraints.add(new ForeignKeyData(ts, colNames, refTableName, refCols));
			}
		}

		ts.setConstraints(constraints.toArray(new ConstraintData[0]));

		return ts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> void initQueries(SQLQueryable<T> instance) {
		final Class<? extends SQLQueryable<T>> tableClazz = (Class<? extends SQLQueryable<T>>) instance.getClass();
		final Field[] tableFields = tableClazz.getDeclaredFields();
		final String tableName = getQueryableName(tableClazz);

		// scan the table itself
		for (Field field : tableFields) {
			if (Modifier.isStatic(field.getModifiers()))
				continue;
			if (!field.isAnnotationPresent(Query.class))
				continue;

			field.setAccessible(true);

			final Type fieldType = field.getGenericType();
			if (!(fieldType instanceof ParameterizedType))
				throw new IllegalArgumentException("Invalid query type: " + fieldType.getTypeName() + " for: " + field);

			try {
				final Object value = buildTableQueryFunction(tableClazz, tableName, instance, fieldType, field.getAnnotation(Query.class));

				if (value != null) {
					field.set(instance, value);
				} else {
					throw new IllegalArgumentException("Unsupported field type for @Query: " + field.getName());
				}
			} catch (Exception e) {
				throw new RuntimeException("Failed to initialize @Query field: " + field.getName() + ", from: " + tableClazz.getName(), e);
			}
		}

		// scan the entry
		/*
		 * final Class<T> entryClazz = (Class<T>) getEntryType(tableClazz); final
		 * Field[] entryFields = entryClazz.getDeclaredFields(); for (Field field :
		 * entryFields) { if (!Modifier.isStatic(field.getModifiers())) continue; if
		 * (!field.isAnnotationPresent(Query.class)) continue;
		 * 
		 * field.setAccessible(true);
		 * 
		 * final Type fieldType = field.getGenericType(); if (!(fieldType instanceof
		 * ParameterizedType)) throw new IllegalArgumentException("Invalid query type: "
		 * + fieldType.getTypeName() + " for: " + field);
		 * 
		 * try { final Object value = buildEntryQueryFunction(entryClazz, tableName,
		 * fieldType, field.getAnnotation(Query.class));
		 * 
		 * if (value != null) { field.set(null, value); } else { throw new
		 * IllegalArgumentException("Unsupported field type for @Query: " +
		 * field.getName()); }
		 * 
		 * } catch (Exception e) { throw new
		 * RuntimeException("Failed to initialize @Query field: " + field.getName() +
		 * ", from: " + entryClazz.getName(), e); } }
		 */
	}

	@Override
	public <T extends DataBaseEntry> Object buildTableQueryFunction(Class<? extends SQLQueryable<T>> tableClazz, String tableName, SQLQueryable<T> instance, Type type, Query query) {
		final String queryText = query.value().replace(Query.TABLE_NAME, PCUtils.sqlEscapeIdentifier(tableName));

		if (query.limit() < query.offset() && !(query.offset() == -1 || query.limit() == -1)) {
			throw new IllegalArgumentException("Invalid order: (offset) -> " + query.offset() + " (limit) -> " + query.limit() + ", should be in this order: <others> <offset> <limit>");
		}

		final ParameterizedType pt = (ParameterizedType) type;
		final Type raw = pt.getRawType();

		if (!NextTask.class.equals(raw))
			throw new IllegalArgumentException("Unsupported query field type: " + raw);

		if (queryText == null || queryText.isEmpty()) {
			final String[] cols = query.columns();

			final String sql = SQLBuilder.safeSelect(PCUtils.sqlEscapeIdentifier(tableName), cols, query.limit() != -1, query.offset() != -1);

			final Object fun = getObjectForTable(pt, instance, cols, sql, query);

			return fun;
		} else {
			final Object fun = getObjectForTable(pt, instance, queryText, query);

			return fun;
		}
	}

	private <T extends DataBaseEntry> Object getObjectForTable(ParameterizedType pt, SQLQueryable<T> table, String sql, Query query) {
		final Query.Type type = query.strategy().equals(Query.Type.AUTO) ? detectDefaultTableStrategy(pt) : query.strategy();

		Type argType = pt.getActualTypeArguments()[0];

		Class<?> rawClass;
		if (argType instanceof ParameterizedType) {
			rawClass = (Class<?>) ((ParameterizedType) argType).getRawType();
		} else if (argType instanceof Class<?>) {
			rawClass = (Class<?>) argType;
		} else {
			throw new IllegalArgumentException("Unsupported type argument: " + argType);
		}

		// map
		if (List.class.isAssignableFrom(rawClass)) {
			return NextTask.withArg((ExceptionFunction<List<Object>, ?>) obj -> table.query(new ListSimpleTransformingQuery(sql, obj, type)).runThrow());
		}

		// tuple (2, 3)
		if (Tuple.class.isAssignableFrom(rawClass)) {
			return NextTask.withArg((ExceptionFunction<Tuple, ?>) obj -> table.query(new ListSimpleTransformingQuery(sql, Arrays.asList(obj.asArray()), type)).runThrow());
		}

		// simple object (1)
		return NextTask.withArg((ExceptionFunction<Object, ?>) obj -> table.query(new ListSimpleTransformingQuery(sql, Arrays.asList(obj), type)).runThrow());
	}

	private <T extends DataBaseEntry> Object getObjectForTable(ParameterizedType pt, SQLQueryable<T> table, String[] cols, String sql, Query query) {
		cols = query.offset() == -1 ? cols : PCUtils.<String>insert(cols, query.offset(), Query.OFFSET_KEY);
		final String[] insCols = query.limit() == -1 ? cols : PCUtils.<String>insert(cols, query.limit(), Query.LIMIT_KEY);

		final Query.Type type = query.strategy().equals(Query.Type.AUTO) ? detectDefaultTableStrategy(pt) : query.strategy();

		Type argType = pt.getActualTypeArguments()[0];

		Class<?> rawClass;
		if (argType instanceof ParameterizedType) {
			rawClass = (Class<?>) ((ParameterizedType) argType).getRawType();
		} else if (argType instanceof Class<?>) {
			rawClass = (Class<?>) argType;
		} else {
			throw new IllegalArgumentException("Unsupported type argument: " + argType);
		}

		// map
		if (Map.class.isAssignableFrom(rawClass)) {
			return NextTask.withArg((ExceptionFunction<Map<String, Object>, ?>) obj -> table.query(new MapSimpleTransformingQuery(sql, insCols, obj, type)).runThrow());
		}

		// tuple (2, 3)
		if (Tuple.class.isAssignableFrom(rawClass)) {
			return NextTask.withArg((ExceptionFunction<Tuple, ?>) obj -> table.query(new MapSimpleTransformingQuery(sql, insCols, mapTupleToColumns(insCols, obj), type)).runThrow());
		}

		// simple object (1)
		return NextTask.withArg((ExceptionFunction<Object, ?>) obj -> table.query(new MapSimpleTransformingQuery(sql, insCols, PCUtils.hashMap(insCols[0], obj), type)).runThrow());
	}

	private Query.Type detectDefaultTableStrategy(ParameterizedType pt) {
		if (isListType(pt.getActualTypeArguments()[1])) {
			return Query.Type.LIST_EMPTY;
		}

		return Query.Type.FIRST_NULL;
	}

	public Map<String, Object> mapTupleToColumns(String[] columns, Tuple tuple) {
		if (tuple.elementCount() != columns.length) {
			throw new IllegalArgumentException("Tuple element count does not match columns length");
		}
		Map<String, Object> map = new HashMap<>();
		for (int i = 0; i < columns.length; i++) {
			map.put(columns[i], tuple.get(i));
		}
		return map;
	}

	@Override
	public <T extends DataBaseEntry> Function<List<Object>, ?> buildMethodQueryFunction(String tableName, SQLQueryable<T> instance, Method method) {
		if (!method.isAnnotationPresent(Query.class)) {
			throw new IllegalArgumentException("No @Query found on method: " + method);
		}

		final Query query = method.getAnnotation(Query.class);

		tableName = PCUtils.sqlEscapeIdentifier(tableName);

		final String queryText = query.value().replace(Query.TABLE_NAME, tableName);

		if (query.limit() < query.offset() && !(query.offset() == -1 || query.limit() == -1)) {
			throw new IllegalArgumentException("Invalid order: (offset) -> " + query.offset() + " (limit) -> " + query.limit() + ", should be in this order: <others> <offset> <limit>");
		}

		final Type returnType = method.getGenericReturnType();

		if (queryText == null || queryText.isEmpty()) {
			final String[] cols = query.columns();

			final String sql = SQLBuilder.safeSelect(tableName, cols, query.limit() != -1, query.offset() != -1);

			final Function<List<Object>, ?> fun = getObjectForMethod(returnType, instance, sql, query);

			return fun;
		} else {
			final Function<List<Object>, ?> fun = getObjectForMethod(returnType, instance, queryText, query);

			return fun;
		}
	}

	private <T extends DataBaseEntry> Function<List<Object>, ?> getObjectForMethod(Type returnType, SQLQueryable<T> instance, String sql, Query query) {
		final Query.Type type = query.strategy().equals(Query.Type.AUTO) ? detectDefaultMethodStrategy(returnType) : query.strategy();

		return (Function<List<Object>, ?>) obj -> instance.query(new ListSimpleTransformingQuery(sql, obj, type)).run();
	}

	private Query.Type detectDefaultMethodStrategy(Type returnType) {
		if (returnType instanceof ParameterizedType && isListType(returnType)) {
			return Query.Type.LIST_EMPTY;
		} else if (returnType instanceof Class) {
			return Query.Type.FIRST_NULL;
		}
		throw new IllegalArgumentException("Unsupported return type: " + returnType);
	}

	/*
	 * @Override public <T extends DataBaseEntry> Object
	 * buildEntryQueryFunction(Class<T> entryClazz, String tableName, Type type,
	 * Query query) { final String queryText =
	 * query.value().replace(Query.TABLE_NAME,
	 * PCUtils.sqlEscapeIdentifier(tableName));
	 * 
	 * if (query.limit() < query.offset() && !(query.offset() == -1 || query.limit()
	 * == -1)) { throw new IllegalArgumentException("Invalid order: (offset) -> " +
	 * query.offset() + " (limit) -> " + query.limit() +
	 * ", should be in this order: <others> <offset> <limit>"); }
	 * 
	 * final ParameterizedType pt = (ParameterizedType) type;
	 * 
	 * // autogen via the columns if (queryText == null || queryText.isEmpty()) {
	 * final String[] cols = query.columns();
	 * 
	 * final String sql =
	 * SQLBuilder.safeSelect(PCUtils.sqlEscapeIdentifier(tableName), cols,
	 * query.limit() != -1, query.offset() != -1);
	 * 
	 * final Object fun = getObjectForEntry(pt, cols, sql, query);
	 * 
	 * return fun; } else { final Object fun = getObjectForEntry(pt, queryText,
	 * query);
	 * 
	 * return fun; } }
	 */

	// autogen
	/*
	 * private <T extends DataBaseEntry> Object getObjectForEntry(ParameterizedType
	 * pt, String[] cols, String sql, Query query) { final Type raw =
	 * pt.getRawType();
	 * 
	 * cols = query.offset() == -1 ? cols : PCUtils.<String>insert(cols,
	 * query.offset(), Query.OFFSET_KEY); final String[] insCols = query.limit() ==
	 * -1 ? cols : PCUtils.<String>insert(cols, query.limit(), Query.LIMIT_KEY);
	 * 
	 * // fixed query if (raw instanceof Class<?> &&
	 * SQLQuery.class.isAssignableFrom((Class<?>) raw)) { final Query.Type type =
	 * query.strategy().equals(Query.Type.AUTO) ? detectDefaultEntryStrategy(pt) :
	 * query.strategy();
	 * 
	 * if (PreparedQuery.class.isAssignableFrom((Class<?>) raw)) { return
	 * ((Class<?>) raw).cast(new MapSimplePreparedQuery(sql, new String[0],
	 * PCUtils.hashMap())); }
	 * 
	 * return ((Class<?>) raw).cast(new MapSimpleTransformingQuery(sql, new
	 * String[0], PCUtils.hashMap(), type)); }
	 * 
	 * final ParameterizedType returnType = (ParameterizedType)
	 * pt.getActualTypeArguments()[pt.getActualTypeArguments().length - 1]; final
	 * Query.Type type = query.strategy().equals(Query.Type.AUTO) ?
	 * detectDefaultEntryStrategy(returnType) : query.strategy();
	 * 
	 * // else transforming query if (raw == Function.class &&
	 * pt.getActualTypeArguments().length == 2 && pt.getActualTypeArguments()[0]
	 * instanceof Class<?> && Map.class.isAssignableFrom((Class<?>)
	 * pt.getActualTypeArguments()[0])) { return (Function<Map<String, Object>,
	 * SQLQuery<T, ?>>) input -> new MapSimpleTransformingQuery(sql, insCols, input,
	 * type); }
	 * 
	 * if (raw == Function.class && pt.getActualTypeArguments().length == 2) {
	 * return (Function<Object, SQLQuery<T, ?>>) obj -> new
	 * MapSimpleTransformingQuery(sql, insCols, PCUtils.hashMap(insCols[0], obj),
	 * type); }
	 * 
	 * if (raw == BiFunction.class && pt.getActualTypeArguments().length == 3) {
	 * return (BiFunction<Object, Object, SQLQuery<T, ?>>) (a, b) -> new
	 * MapSimpleTransformingQuery(sql, insCols, PCUtils.hashMap(insCols[0], a,
	 * insCols[1], b), type); }
	 * 
	 * if (raw == TriFunction.class && pt.getActualTypeArguments().length == 4) {
	 * return (TriFunction<Object, Object, Object, SQLQuery<T, ?>>) (a, b, c) -> new
	 * MapSimpleTransformingQuery(sql, insCols, PCUtils.hashMap(insCols[0], a,
	 * insCols[1], b, insCols[2], c), type); }
	 * 
	 * throw new IllegalArgumentException("Type doesn't match any query function: "
	 * + raw + ", with: " + pt.getActualTypeArguments().length +
	 * " arguments for query: " + sql + ", with: " + cols.length + " (" +
	 * insCols.length + ") arguments."); }
	 */

	public Query.Type detectDefaultEntryStrategy(ParameterizedType returnType) {
		Type sqlQueryType = findSQLQueryInterface(returnType);
		if (!(sqlQueryType instanceof ParameterizedType)) {
			return Query.Type.FIRST_NULL;
		}

		Type[] typeArgs = ((ParameterizedType) sqlQueryType).getActualTypeArguments();
		if (typeArgs.length != 2) {
			return Query.Type.FIRST_NULL;
		}

		Type resultType = typeArgs[1];
		if (isListType(resultType)) {
			return Query.Type.LIST_EMPTY;
		}

		return Query.Type.FIRST_NULL;
	}

	private Type findSQLQueryInterface(Type type) {
		if (!(type instanceof ParameterizedType))
			return null;

		ParameterizedType pt = (ParameterizedType) type;
		Class<?> rawClass = (Class<?>) pt.getRawType();

		for (Type iface : rawClass.getGenericInterfaces()) {
			if (iface instanceof ParameterizedType) {
				ParameterizedType ipt = (ParameterizedType) iface;
				Type rawIface = ipt.getRawType();
				if (rawIface instanceof Class<?> && SQLQuery.class.isAssignableFrom((Class<?>) rawIface)) {
					return ipt;
				}
			}
		}

		Type superType = rawClass.getGenericSuperclass();
		if (superType != null) {
			return findSQLQueryInterface(superType);
		}

		return null;
	}

	private boolean isListType(Type type) {
		if (type instanceof ParameterizedType) {
			Type raw = ((ParameterizedType) type).getRawType();
			if (raw instanceof Class<?>) {
				return List.class.isAssignableFrom((Class<?>) raw);
			}
		}
		if (type instanceof Class<?>) {
			return List.class.isAssignableFrom((Class<?>) type);
		}
		return false;
	}

	// manual sql
	/*
	 * private <T extends DataBaseEntry> Object getObjectForEntry(ParameterizedType
	 * pt, String sql, Query query) { final Type raw = pt.getRawType();
	 * 
	 * final ParameterizedType returnType = (ParameterizedType)
	 * pt.getActualTypeArguments()[pt.getActualTypeArguments().length - 1]; final
	 * Query.Type type = query.strategy().equals(Query.Type.AUTO) ?
	 * detectDefaultEntryStrategy(returnType) : query.strategy();
	 * 
	 * if (raw == Function.class && pt.getActualTypeArguments().length == 2 &&
	 * pt.getActualTypeArguments()[0] instanceof Class<?> &&
	 * Map.class.isAssignableFrom((Class<?>) pt.getActualTypeArguments()[0])) {
	 * return (Function<List<Object>, SQLQuery<T, ?>>) input -> new
	 * ListSimpleTransformingQuery(sql, input, type); }
	 * 
	 * if (raw == Function.class && pt.getActualTypeArguments().length == 2) {
	 * return (Function<Object, SQLQuery<T, ?>>) obj -> new
	 * ListSimpleTransformingQuery(sql, Arrays.asList(obj), type); }
	 * 
	 * if (raw == BiFunction.class && pt.getActualTypeArguments().length == 3) {
	 * return (BiFunction<Object, Object, SQLQuery<T, ?>>) (a, b) -> new
	 * ListSimpleTransformingQuery(sql, Arrays.asList(a, b), type); }
	 * 
	 * if (raw == TriFunction.class && pt.getActualTypeArguments().length == 4) {
	 * return (TriFunction<Object, Object, Object, SQLQuery<T, ?>>) (a, b, c) -> new
	 * ListSimpleTransformingQuery(sql, Arrays.asList(a, b, c), type); }
	 * 
	 * throw new IllegalArgumentException("Type doesn't match any query function: "
	 * + raw + ", with: " + pt.getActualTypeArguments().length +
	 * " arguments for query: " + sql); }
	 */

	@Override
	public String getReferencedColumnName(ForeignKey fk) {
		if (!fk.column().isEmpty()) {
			return fk.column();
		}
		final Class<? extends SQLQueryable<? extends DataBaseEntry>> refQueryable = fk.table();
		final Class<? extends DataBaseEntry> refType = getEntryType(refQueryable);
		final ColumnData[] refPks = getPrimaryKeys(refType);

		if (refPks.length > 1) {
			throw new IllegalArgumentException("Foreign key references multiple primary keys in " + refQueryable.getSimpleName() + ". Specify the column explicitly.");
		} else if (refPks.length == 1) {
			return refPks[0].getName();
		} else {
			throw new IllegalArgumentException("Foreign key references no primary key in " + refQueryable.getSimpleName() + ". Specify the column explicitly.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Class<T> getEntryType(Class<? extends SQLQueryable<T>> tableClass) {
		Type genericSuperclass = tableClass.getGenericSuperclass();

		if (genericSuperclass instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) genericSuperclass;
			Type[] typeArgs = pt.getActualTypeArguments();

			if (typeArgs.length == 1 && typeArgs[0] instanceof Class<?>) {
				return (Class<T>) typeArgs[0];
			}
		}

		throw new IllegalArgumentException("Could not determine DataBaseEntry type from " + tableClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> ColumnData[] getPrimaryKeys(T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get primary keys for null object.", new NullPointerException("data is null."));
		}
		return getPrimaryKeys((Class<T>) data.getClass());
	}

	@Override
	public <T extends DataBaseEntry> ColumnData[] getPrimaryKeys(Class<? extends T> entryType) {
		final List<ColumnData> primaryKeys = new ArrayList<>();

		for (Field f : sortFields(entryType.getDeclaredFields())) {
			if (f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(PrimaryKey.class)) {
				Column nCol = f.getAnnotation(Column.class);
				ColumnData colData = new ColumnData();
				colData.setName(nCol.name().isEmpty() ? f.getName() : nCol.name());
				colData.setType(getTypeFor(nCol.type().equals(Class.class) ? f.getType() : nCol.type(), nCol));
				primaryKeys.add(colData);
			}
		}
		return primaryKeys.toArray(new ColumnData[0]);
	}

	@Override
	public <T extends DataBaseEntry> String getQueryableName(Class<? extends SQLQueryable<T>> tableClass) {
		if (tableClass.isAnnotationPresent(TableName.class)) {
			TableName tableAnno = tableClass.getAnnotation(TableName.class);
			if (!tableAnno.value().isEmpty()) {
				return tableAnno.value();
			}
		}
		return PCUtils.camelToSnake(tableClass.getSimpleName().replaceAll("Table$", ""));
	}

	@Override
	public String fieldToColumnName(String name) {
		return PCUtils.camelToSnake(name);
	}

	@Override
	public String fieldToColumnName(Field field) {
		if (!field.isAnnotationPresent(Column.class)) {
			throw new IllegalArgumentException("Field " + field.getName() + " is not annotated with @Column");
		}
		final Column colAnno = field.getAnnotation(Column.class);
		return colAnno.name().isEmpty() ? fieldToColumnName(field.getName()) : colAnno.name();
	}

	@Override
	public <T extends DataBaseEntry> void fillInsert(T data, ResultSet rs) throws SQLException {
		final Class<?> clazz = data.getClass();

		try {
			for (Field field : sortFields(clazz.getDeclaredFields())) {
				field.setAccessible(true);

				if (!field.isAnnotationPresent(PrimaryKey.class)) {
					continue;
				}

				final String columnName = fieldToColumnName(field);
				final Column column = field.getAnnotation(Column.class);

				final ColumnType type = getTypeFor(field);

				final Object value = type.load(rs, 1, field.getType()); // getResultSetValue(rs, 1, field.getType());
				if (value != null) {
					field.set(data, value);
				}
			}

			final Method insertMethod = getInsertMethod(data);
			if (insertMethod != null) {
				try {
					insertMethod.invoke(data);
				} catch (Exception e) {
					throw new RuntimeException("Exception while invoking insert method.", e);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to update generated keys on " + data.getClass(), e);
		}
	}

	/*
	 * private Object getResultSetValue(ResultSet rs, int columnIndex, Class<?>
	 * targetType) throws SQLException { if
	 * (resultSetExtractors.containsKey(targetType)) { try { return
	 * resultSetExtractors.get(targetType).apply(rs, columnIndex); } catch
	 * (SQLException e) { throw e; } catch (Exception e) { throw new
	 * RuntimeException("Exception while getting value for column: " + columnIndex,
	 * e); } } else { // throw new IllegalArgumentException("Unsupported type: " +
	 * clazz.getName() + " // for column: " + columnName);
	 * 
	 * // fallback: try getObject() Object obj = rs.getObject(columnIndex); if (obj
	 * != null && !targetType.isAssignableFrom(obj.getClass())) { throw new
	 * IllegalArgumentException("Cannot assign value of type " + obj.getClass() +
	 * " to " + targetType); } return obj; } }
	 */

	/*
	 * private Object getResultSetValue(ResultSet rs, String columnName, Class<?>
	 * targetType) throws SQLException { if
	 * (resultSetExtractors.containsKey(targetType)) { try { if
	 * (PCUtils.hasColumn(rs, columnName)) { try { return
	 * resultSetExtractors.get(targetType).apply(rs, PCUtils.getColumnIndex(rs,
	 * columnName)); } catch (Exception e) { throw new
	 * RuntimeException("Exception while getting value for column: " + columnName,
	 * e); } } else { throw new
	 * IllegalArgumentException("No column found for name: " + columnName); } }
	 * catch (SQLException e) { throw e; } } else { // throw new
	 * IllegalArgumentException("Unsupported type: " + clazz.getName() + " // for
	 * column: " + columnName);
	 * 
	 * // fallback: try getObject() Object obj = rs.getObject(columnName); if (obj
	 * != null && !targetType.isAssignableFrom(obj.getClass())) { throw new
	 * IllegalArgumentException("Cannot assign value of type " + obj.getClass() +
	 * " to " + targetType); } return obj; } }
	 */

	@Override
	public <T extends DataBaseEntry> void fillLoad(T data, ResultSet rs) throws SQLException {
		final Class<?> clazz = data.getClass();

		try {
			for (Field field : sortFields(clazz.getDeclaredFields())) {
				field.setAccessible(true);

				if (!field.isAnnotationPresent(Column.class)) {
					continue;
				}

				final String columnName = fieldToColumnName(field);
				final Column column = field.getAnnotation(Column.class);

				final ColumnType type = getTypeFor(field);

				final Object value = type.load(rs, columnName, field.getType());
				if (value != null) {
					field.set(data, value);
				}
			}

			final Method loadMethod = getLoadMethod(data);
			if (loadMethod != null) {
				try {
					loadMethod.invoke(data);
				} catch (Exception e) {
					throw new RuntimeException("Exception while invoking load method.", e);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to update generated keys on " + data.getClass(), e);
		}
	}

	@Override
	public <T extends DataBaseEntry> T fillLoadCopy(T data, ResultSet rs) throws SQLException {
		final T new_ = instance(data);
		fillLoad(new_, rs);
		return new_;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> T instance(T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot instance null object.", new NullPointerException("data is null."));
		}
		return this.<T>instance((Class<T>) data.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> T instance(Class<T> clazz) {
		final Method factoryMethod = getStaticFactoryMethod(clazz);
		if (factoryMethod != null) {
			try {
				factoryMethod.setAccessible(true);
				return (T) factoryMethod.invoke(null);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Failed to instantiate " + clazz.getName() + " through factory method: " + factoryMethod.getName(), e);
			}
		} else {
			try {
				final Constructor<T> ctor = clazz.getDeclaredConstructor();
				ctor.setAccessible(true);
				return ctor.newInstance();
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("No empty constructor nor factory method found " + clazz.getName(), e);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
			}
		}
	}

	public Method getStaticFactoryMethod(Class<?> clazz) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Factory.class) && Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 0) {
				if (!method.getReturnType().equals(clazz)) {
					throw new IllegalArgumentException("Factory method returns wrong type: " + clazz.getName() + " returns " + method.getReturnType().getName());
				}
				return method;
			}
		}
		return null;
	}

	@Override
	public <T extends DataBaseEntry> void fillLoadAll(T data, ResultSet result, Consumer<T> listExporter) throws SQLException {
		if (data == null || result == null || listExporter == null) {
			throw new IllegalArgumentException("Null argument provided to fillAll.");
		}

		while (result.next()) {
			T copy = fillLoadCopy(data, result);
			listExporter.accept(copy);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> void fillLoadAllTable(Class<? extends SQLQueryable<T>> tableClazz, SQLQuery<T, ?> query, ResultSet result, Consumer<T> listExporter) throws SQLException {
		if (query == null || result == null || listExporter == null) {
			throw new IllegalArgumentException("Null argument provided to fillAll.");
		}

		final Class<T> entryClazz = (Class<T>) getEntryType(tableClazz);

		while (result.next()) {
			final T copy = instance(entryClazz);
			fillLoad(copy, result);
			listExporter.accept(copy);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Map<String, Object>[] getUniqueKeys(ConstraintData[] allConstraints, T data) {
		if (allConstraints == null || allConstraints.length == 0 || data == null) {
			return (Map<String, Object>[]) new Map[0];
		}

		final List<UniqueData> uniqueConstraints = new ArrayList<>();
		for (ConstraintData constraint : allConstraints) {
			if (constraint instanceof UniqueData) {
				uniqueConstraints.add((UniqueData) constraint);
			}
		}

		final Map<String, Object>[] result = (Map<String, Object>[]) new Map[uniqueConstraints.size()];

		for (int i = 0; i < uniqueConstraints.size(); i++) {
			final UniqueData unique = uniqueConstraints.get(i);
			final String[] columns = unique.getColumns();

			final Map<String, Object> keyMap = new LinkedHashMap<>();

			for (String colName : columns) {
				try {
					final Field field = getColumnField(data.getClass(), colName);

					field.setAccessible(true);
					final Object value = field.get(data);
					keyMap.put(colName, value);
				} catch (IllegalAccessException e) {
					PCUtils.throwRuntime(e);
					return null;
				}
			}

			result[i] = keyMap;
		}

		final List<Map<String, Object>> cleanedUniques = Arrays.stream(result).map(map -> {
			map.entrySet().removeIf(entry -> entry.getValue() == null);
			return map;
		}).filter(map -> !map.isEmpty()).collect(Collectors.toList());

		return cleanedUniques.toArray(new HashMap[0]);
	}

	private Field getColumnField(Class<? extends DataBaseEntry> class1, String colName) {
		for (Field f : sortFields(class1.getDeclaredFields())) {
			if (!f.isAnnotationPresent(Column.class))
				continue;

			final String fieldName = fieldToColumnName(f);
			if (fieldName.equals(colName))
				return f;
		}

		throw new IllegalArgumentException("No field found for column: " + colName + " in class:" + class1.getName());
	}

	@Override
	public <T extends DataBaseEntry> void fillUpdate(T data, ResultSet rs) throws SQLException {
		final Class<?> clazz = data.getClass();

		try {
			for (Field field : sortFields(clazz.getDeclaredFields())) {
				field.setAccessible(true);

				if (!field.isAnnotationPresent(OnUpdate.class)) {
					continue;
				}

				final String columnName = fieldToColumnName(field);
				final Column column = field.getAnnotation(Column.class);

				final ColumnType type = getTypeFor(field);

				final Object value = type.load(rs, columnName, field.getType());
				if (value != null) {
					field.set(data, value);
				}
			}

			final Method updateMethod = getUpdateMethod(data);
			if (updateMethod != null) {
				try {
					updateMethod.invoke(data);
				} catch (Exception e) {
					throw new RuntimeException("Exception while invoking update method.", e);
				}
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to update update keys on " + data.getClass(), e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Method getUpdateMethod(T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get update method for null object.", new NullPointerException("data is null."));
		}
		return getUpdateMethod((Class<T>) data.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Method getLoadMethod(T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get load method for null object.", new NullPointerException("data is null."));
		}
		return getLoadMethod((Class<T>) data.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Method getInsertMethod(T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get insert method for null object.", new NullPointerException("data is null."));
		}
		return getInsertMethod((Class<T>) data.getClass());
	}

	@Override
	public <T extends DataBaseEntry> Method getInsertMethod(Class<T> data) {
		for (Method m : data.getDeclaredMethods()) {
			if (m.isAnnotationPresent(Insert.class)) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	@Override
	public <T extends DataBaseEntry> Method getUpdateMethod(Class<T> data) {
		for (Method m : data.getDeclaredMethods()) {
			if (m.isAnnotationPresent(Update.class)) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	@Override
	public <T extends DataBaseEntry> Method getLoadMethod(Class<T> data) {
		for (Method m : data.getDeclaredMethods()) {
			if (m.isAnnotationPresent(Load.class)) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedInsertSQL(DataBaseTable<T> table, T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<?> clazz = data.getClass();
		final String tableName = table.getQualifiedName();

		final List<String> columns = sortFields(clazz.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(Column.class)).filter(f -> {
			f.setAccessible(true);
			try {
				final Object value = f.get(data);

				if (value == null && (f.isAnnotationPresent(DefaultValue.class) || f.isAnnotationPresent(DefaultValue.class))) {
					return false;
				}
				return true;
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Failed to access field value for field: " + f.getName(), e);
			}
		}).map(f -> {
			final String columnName = fieldToColumnName(f);
			return PCUtils.sqlEscapeIdentifier(columnName);
		}).collect(Collectors.toList());

		final String placeholders = columns.stream().map(col -> "?").collect(Collectors.joining(", "));

		final String columnList = String.join(", ", columns);

		return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnList, placeholders);
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedUpdateSQL(DataBaseTable<T> table, T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<?> clazz = data.getClass();
		final String tableName = table.getQualifiedName();

		final List<String> setColumns = sortFields(clazz.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(Column.class)).filter(f -> !f.isAnnotationPresent(PrimaryKey.class)).filter(f -> !f.isAnnotationPresent(OnUpdate.class))
				.filter(f -> {
					f.setAccessible(true);
					try {
						Object value = f.get(data);
						if (value == null && (f.isAnnotationPresent(DefaultValue.class) || f.isAnnotationPresent(DefaultValue.class))) {
							return false;
						}
						return true;
					} catch (IllegalAccessException e) {
						throw new RuntimeException("Failed to access field value for field: " + f.getName(), e);
					}
				}).map(f -> PCUtils.sqlEscapeIdentifier(fieldToColumnName(f)) + " = ?").collect(Collectors.toList());

		if (setColumns.isEmpty()) {
			throw new IllegalArgumentException("No columns to update.");
		}

		final List<String> whereColumns = sortFields(clazz.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(PrimaryKey.class)).map(f -> PCUtils.sqlEscapeIdentifier(fieldToColumnName(f)) + " = ?")
				.collect(Collectors.toList());

		if (whereColumns.isEmpty()) {
			throw new IllegalArgumentException("No primary key defined on " + clazz.getSimpleName());
		}

		final String setClause = String.join(", ", setColumns);
		final String whereClause = String.join(" AND ", whereColumns);

		return String.format("UPDATE %s SET %s WHERE %s", tableName, setClause, whereClause);
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedDeleteSQL(DataBaseTable<T> table, T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<?> clazz = data.getClass();
		final String tableName = table.getQualifiedName();

		final List<String> whereColumns = sortFields(clazz.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(PrimaryKey.class)).map(f -> PCUtils.sqlEscapeIdentifier(fieldToColumnName(f)) + " = ?")
				.collect(Collectors.toList());

		if (whereColumns.isEmpty()) {
			throw new IllegalArgumentException("No primary key defined on " + clazz.getSimpleName());
		}

		final String whereClause = String.join(" AND ", whereColumns);

		return String.format("DELETE FROM %s WHERE %s", tableName, whereClause);
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedSelectSQL(SQLQueryable<T> table, T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<?> clazz = data.getClass();
		final String tableName = table.getQualifiedName();

		final List<String> whereColumns = sortFields(clazz.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(PrimaryKey.class)).map(f -> PCUtils.sqlEscapeIdentifier(fieldToColumnName(f)) + " = ?")
				.collect(Collectors.toList());

		if (whereColumns.isEmpty()) {
			throw new IllegalArgumentException("No primary key defined on " + clazz.getSimpleName());
		}

		final String whereClause = String.join(" AND ", whereColumns);

		return String.format("SELECT * FROM %s WHERE %s", tableName, whereClause);
	}

	@Override
	public <T extends DataBaseEntry> void prepareInsertSQL(PreparedStatement stmt, T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<?> clazz = data.getClass();

		final List<Field> fieldsToInsert = sortFields(clazz.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(Column.class)).filter(f -> {
			f.setAccessible(true);
			try {
				Object value = f.get(data);
				if (value == null && (f.isAnnotationPresent(DefaultValue.class) || f.isAnnotationPresent(DefaultValue.class))) {
					return false;
				}
				return true;
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Failed to access field value", e);
			}
		}).collect(Collectors.toList());

		int index = 1;
		for (Field field : fieldsToInsert) {
			field.setAccessible(true);
			try {
				final Object value = field.get(data);
				final ColumnType type = getTypeFor(field);

				type.store(stmt, index++, value);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Failed to access field value", e);
			}
		}
	}

	@Override
	public <T extends DataBaseEntry> void prepareUpdateSQL(PreparedStatement stmt, T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<?> clazz = data.getClass();

		List<Field> setFields = sortFields(clazz.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(Column.class)).filter(f -> !f.isAnnotationPresent(PrimaryKey.class)).filter(f -> !f.isAnnotationPresent(OnUpdate.class)).filter(f -> {
			f.setAccessible(true);
			try {
				Object value = f.get(data);
				if (value == null && (f.isAnnotationPresent(DefaultValue.class) || f.isAnnotationPresent(DefaultValue.class))) {
					return false;
				}
				return true;
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Failed to access field value", e);
			}
		}).collect(Collectors.toList());

		final List<Field> pkFields = sortFields(clazz.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(Column.class)).filter(f -> f.isAnnotationPresent(PrimaryKey.class)).collect(Collectors.toList());

		int index = 1;
		try {
			for (Field field : setFields) {
				field.setAccessible(true);
				final Object value = field.get(data);
				final ColumnType type = getTypeFor(field);

				type.store(stmt, index++, value);
			}

			for (Field field : pkFields) {
				field.setAccessible(true);
				final Object value = field.get(data);
				final ColumnType type = getTypeFor(field);

				type.store(stmt, index++, value);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to access field value", e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void prepareDeleteSQL(PreparedStatement stmt, T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<?> clazz = data.getClass();

		final List<Field> pkFields = sortFields(clazz.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(Column.class)).filter(f -> f.isAnnotationPresent(PrimaryKey.class)).collect(Collectors.toList());

		int index = 1;
		try {
			for (Field field : pkFields) {
				field.setAccessible(true);
				final Object value = field.get(data);

				final ColumnType type = getTypeFor(field);
				type.store(stmt, index++, value);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to access field value", e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void prepareSelectSQL(PreparedStatement stmt, T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<?> clazz = data.getClass();

		final List<Field> pkFields = sortFields(clazz.getDeclaredFields()).stream().filter(f -> f.isAnnotationPresent(Column.class)).filter(f -> f.isAnnotationPresent(PrimaryKey.class)).collect(Collectors.toList());

		int index = 1;
		try {
			for (Field field : pkFields) {
				field.setAccessible(true);
				final Object value = field.get(data);

				final ColumnType type = getTypeFor(field);
				type.store(stmt, index++, value);
			}
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Failed to access field value", e);
		}
	}

}
