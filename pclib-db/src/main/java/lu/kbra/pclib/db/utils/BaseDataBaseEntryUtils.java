package lu.kbra.pclib.db.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.pair.Pair;
import lu.kbra.pclib.datastructure.pair.Pairs;
import lu.kbra.pclib.db.annotations.entry.Insert;
import lu.kbra.pclib.db.annotations.entry.Load;
import lu.kbra.pclib.db.annotations.entry.Update;
import lu.kbra.pclib.db.annotations.view.DB_View;
import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Check;
import lu.kbra.pclib.db.autobuild.column.Checks;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.column.DefaultValue;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.Generated;
import lu.kbra.pclib.db.autobuild.column.GeneratedColumnData;
import lu.kbra.pclib.db.autobuild.column.Nullable;
import lu.kbra.pclib.db.autobuild.column.OnUpdate;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.BinaryType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.BlobType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.VarbinaryType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BooleanType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.DecimalType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.DoubleType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.FloatType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BigIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BitType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.IntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.SmallIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.TinyIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.CharType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.JsonType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.TextType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.VarcharType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.DateType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.TimestampType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.IntegerType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.RealType;
import lu.kbra.pclib.db.autobuild.table.CharacterSet;
import lu.kbra.pclib.db.autobuild.table.CheckData;
import lu.kbra.pclib.db.autobuild.table.Collation;
import lu.kbra.pclib.db.autobuild.table.ConstraintData;
import lu.kbra.pclib.db.autobuild.table.Engine;
import lu.kbra.pclib.db.autobuild.table.Factory;
import lu.kbra.pclib.db.autobuild.table.ForeignKeyData;
import lu.kbra.pclib.db.autobuild.table.PrimaryKeyData;
import lu.kbra.pclib.db.autobuild.table.TableName;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.autobuild.table.UniqueData;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;

public class BaseDataBaseEntryUtils implements DataBaseEntryUtils {

	@Column
	private static final Object columnType = null;

	protected final Map<Predicate<Class<?>>, Function<Column, ColumnType>> classTypeMap = new HashMap<Predicate<Class<?>>, Function<Column, ColumnType>>() {
		{
			this.put(Class::isEnum, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
		}
	};

	protected final Map<Class<?>, Function<Column, ColumnType>> typeMap = new HashMap<Class<?>, Function<Column, ColumnType>>() {
		{
			// -- java types
			this.put(String.class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
			this.put(CharSequence.class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
			this.put(char[].class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());

			this.put(byte[].class, col -> col.length() != -1 ? new VarbinaryType(col.length()) : new BlobType());
			this.put(ByteBuffer.class, col -> col.length() != -1 ? new VarbinaryType(col.length()) : new BlobType());

			this.put(Byte.class, col -> new TinyIntType());
			this.put(byte.class, col -> new TinyIntType());
			this.put(Short.class, col -> new SmallIntType());
			this.put(short.class, col -> new SmallIntType());
			this.put(Integer.class, col -> new IntType());
			this.put(int.class, col -> new IntType());
			this.put(Long.class, col -> new BigIntType());
			this.put(long.class, col -> new BigIntType());
			this.put(BigInteger.class, col -> new BigIntType());

			this.put(Double.class, col -> new DoubleType());
			this.put(double.class, col -> new DoubleType());
			this.put(Float.class, col -> new FloatType());
			this.put(float.class, col -> new FloatType());

			this.put(Boolean.class, col -> new BooleanType());
			this.put(boolean.class, col -> new BooleanType());

			this.put(Timestamp.class, col -> new TimestampType());
			this.put(LocalDateTime.class, col -> new TimestampType());
			this.put(Date.class, col -> new DateType());
			this.put(LocalDate.class, col -> new DateType());

			this.put(JSONObject.class, col -> new JsonType());
			this.put(JSONArray.class, col -> new JsonType());

			// -- native types
			this.put(TextType.class, col -> new TextType());
			this.put(CharType.class, col -> new CharType(col.length()));
			this.put(VarcharType.class, col -> new VarcharType(col.length()));

			this.put(BinaryType.class, col -> new BinaryType(col.length()));
			this.put(VarbinaryType.class, col -> new BinaryType(col.length()));
			this.put(BlobType.class, col -> new BlobType());

			this.put(BitType.class, col -> new BitType());
			this.put(SmallIntType.class, col -> new SmallIntType());
			this.put(IntType.class, col -> new IntType());
			this.put(BigIntType.class, col -> new BigIntType());

			this.put(DoubleType.class, col -> new DoubleType());
			this.put(FloatType.class, col -> new FloatType());
			this.put(DecimalType.class, col -> new DecimalType(col.length(), col.params()));

			this.put(TimestampType.class, col -> new TimestampType());
			this.put(DateType.class, col -> new DateType());

			this.put(JsonType.class, col -> new JsonType());
		}
	};

	@Override
	public ColumnType getTypeFor(final Field field) {
		final Column colAnno = field.getAnnotation(Column.class);
		final Class<?> fieldType = colAnno.type().equals(Class.class) ? field.getType() : colAnno.type();
		return this.getTypeFor(fieldType, colAnno);
	}

	@Override
	public ColumnType getTypeFor(final Class<?> clazz, final Column col) {
		if (this.typeMap.containsKey(clazz)) {
			return this.typeMap.get(clazz).apply(col);
		} else {
			return this.classTypeMap.entrySet()
					.stream()
					.filter(entry -> entry.getKey().test(clazz))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException(
							"Unsupported type: " + clazz.getName() + " for column: " + (col.name().isEmpty() ? "<empty>" : col.name())))
					.getValue()
					.apply(col);
		}
	}

	@Override
	public <T extends DataBaseEntry> TableStructure scanTable(final Class<? extends AbstractDBTable<T>> tableClazz) {
		final TableStructure ts = this.scanEntry(this.getEntryType(tableClazz));

		if (tableClazz.isAnnotationPresent(CharacterSet.class)) {
			final CharacterSet charsetAnno = tableClazz.getAnnotation(CharacterSet.class);
			ts.setCharacterSet(charsetAnno.value());
		}

		if (tableClazz.isAnnotationPresent(Engine.class)) {
			final Engine engineAnno = tableClazz.getAnnotation(Engine.class);
			ts.setEngine(engineAnno.value());
		}

		if (tableClazz.isAnnotationPresent(Collation.class)) {
			final Collation engineAnno = tableClazz.getAnnotation(Collation.class);
			ts.setCollation(engineAnno.value());
		}

		if (tableClazz.isAnnotationPresent(TableName.class)) {
			final TableName tableAnno = tableClazz.getAnnotation(TableName.class);
			if (!tableAnno.value().isEmpty()) {
				ts.setName(tableAnno.value());
			}
		}

		return ts;
	}

	public List<Field> sortFields(final Field[] fields) {
		final List<Field> pkFields = new ArrayList<>();
		final List<Field> fkFields = new ArrayList<>();
		final List<Field> otherFields = new ArrayList<>();

		for (final Field field : fields) {
			if (field.isAnnotationPresent(PrimaryKey.class)) {
				pkFields.add(field);
			} else if (field.isAnnotationPresent(ForeignKey.class)) {
				fkFields.add(field);
			} else {
				otherFields.add(field);
			}
		}

		final Comparator<Field> byName = Comparator.comparing(Field::getName);

		pkFields.sort(byName);
		otherFields.sort(byName);
		fkFields.sort(byName);

		final List<Field> sorted = new ArrayList<>();
		sorted.addAll(pkFields);
		sorted.addAll(otherFields);
		sorted.addAll(fkFields);

		return sorted;
	}

	@Override
	public <T extends DataBaseEntry> TableStructure scanEntry(final Class<T> entryClazz) {
		final List<ColumnData> columns = new LinkedList<>();
		final List<ConstraintData> constraints = new LinkedList<>();
		final Set<String> primaryKeys = new LinkedHashSet<>();
		final Map<Integer, Set<String>> uniqueGroups = new LinkedHashMap<>();
		final Set<Pair<String, Check>> checks = new HashSet<>();
		final Map<Class<? extends SQLQueryable<? extends DataBaseEntry>>, Map<ColumnData, ForeignKey>> foreignKeys = new LinkedHashMap<>();

		for (final Field field : this.sortFields(PCUtils.getAllFields(entryClazz))) {
			field.setAccessible(true);

			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}

			final Column colAnno = field.getAnnotation(Column.class);
			final String columnName = this.fieldToColumnName(field);

			final ColumnType columnType = this.getTypeFor(colAnno.type().equals(Class.class) ? field.getType() : colAnno.type(), colAnno);

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
				final int group = field.getAnnotation(Unique.class).value();
				uniqueGroups.computeIfAbsent(group, k -> new LinkedHashSet<>()).add(columnName);
			}

			// FOREIGN KEY
			if (field.isAnnotationPresent(ForeignKey.class)) {
				final ForeignKey fk = field.getAnnotation(ForeignKey.class);
				foreignKeys.computeIfAbsent(fk.table(), k -> new LinkedHashMap<>()).put(columnData, fk);
			}

			// CHECK
			if (field.isAnnotationPresent(Check.class) || field.isAnnotationPresent(Checks.class)) {
				final Check[] check = field.getAnnotationsByType(Check.class);
				Arrays.stream(check).forEach(c -> checks.add(Pairs.readOnly(columnName, c)));
			}

			// GENERATED
			if (field.isAnnotationPresent(Generated.class)) {
				final Generated gen = field.getAnnotation(Generated.class);

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
			/*
			 * if (primaryKeys.size() > 1) { throw new
			 * UnsupportedOperationException("Only one primary key is supported atm."); }
			 */
			constraints.add(new PrimaryKeyData(ts, primaryKeys.toArray(new String[0])));
		}

		for (final Set<String> groupCols : uniqueGroups.values()) {
			constraints.add(new UniqueData(ts, groupCols.toArray(new String[0])));
		}
		for (final Pair<String, Check> pair : checks) {
			constraints.add(new CheckData(ts, pair.getValue().value().replace(Check.FIELD_NAME_PLACEHOLDER, pair.getKey())));
		}

		// we go through the foreign keys and group them by referenced table
		for (final Map.Entry<Class<? extends SQLQueryable<? extends DataBaseEntry>>, Map<ColumnData, ForeignKey>> entry : foreignKeys
				.entrySet()) {
			final Class<? extends SQLQueryable<? extends DataBaseEntry>> foreignQueryable = entry.getKey();
			final String refTableName = this.getQueryableName(foreignQueryable);
			final Map<ColumnData, ForeignKey> colMap = entry.getValue();

			final Map<Integer, List<Map.Entry<ColumnData, ForeignKey>>> grouped = new HashMap<>();

			for (final Map.Entry<ColumnData, ForeignKey> colEntry : colMap.entrySet()) {
				final int groupIndex = colEntry.getValue().groupId();
				grouped.computeIfAbsent(groupIndex, k -> new ArrayList<>()).add(colEntry);
			}

			for (final List<Map.Entry<ColumnData, ForeignKey>> group : grouped.values()) {
				final String[] colNames = group.stream().map(e -> e.getKey().getName()).toArray(String[]::new);
				final String[] refCols = group.stream().map(e -> this.getReferencedColumnName(e.getValue())).toArray(String[]::new);

				if (PCUtils.duplicates(refCols)) {
					throw new IllegalArgumentException(
							"Foreign key references duplicate columns: " + String.join(", ", refCols) + " to table: " + refTableName);
				}

				constraints.add(new ForeignKeyData(ts, colNames, refTableName, refCols));
			}
		}

		ts.setConstraints(constraints.toArray(new ConstraintData[0]));

		return ts;
	}

	protected Type findSQLQueryInterface(final Type type) {
		if (!(type instanceof ParameterizedType))
			return null;

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

	@Override
	public String getReferencedColumnName(final ForeignKey fk) {
		if (!fk.column().isEmpty()) {
			return fk.column();
		}
		final Class<? extends SQLQueryable<? extends DataBaseEntry>> refQueryable = fk.table();
		final Class<? extends DataBaseEntry> refType = this.getEntryType(refQueryable);
		final ColumnData[] refPks = this.getPrimaryKeys(refType);

		if (refPks.length > 1) {
			throw new IllegalArgumentException(
					"Foreign key references multiple primary keys in " + refQueryable.getSimpleName() + ". Specify the column explicitly.");
		} else if (refPks.length == 1) {
			return refPks[0].getName();
		} else {
			throw new IllegalArgumentException(
					"Foreign key references no primary key in " + refQueryable.getSimpleName() + ". Specify the column explicitly.");
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Class<T> getEntryType(final Class<? extends SQLQueryable<? extends DataBaseEntry>> tableClass) {
		final Type genericSuperclass = tableClass.getGenericSuperclass();

		if (genericSuperclass instanceof ParameterizedType) {
			final ParameterizedType pt = (ParameterizedType) genericSuperclass;
			final Type[] typeArgs = pt.getActualTypeArguments();

			if (typeArgs.length == 1 && typeArgs[0] instanceof Class<?>) {
				return (Class<T>) typeArgs[0];
			}
		}

		throw new IllegalArgumentException("Could not determine DataBaseEntry type from " + tableClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> ColumnData[] getPrimaryKeys(final T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get primary keys for null object.", new NullPointerException("data is null."));
		}
		return this.getPrimaryKeys((Class<T>) data.getClass());
	}

	@Override
	public <T extends DataBaseEntry> ColumnData[] getPrimaryKeys(final Class<? extends T> entryType) {
		final List<ColumnData> primaryKeys = new ArrayList<>();

		for (final Field f : this.sortFields(this.getAllFields(entryType))) {
			if (f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(PrimaryKey.class)) {
				final Column nCol = f.getAnnotation(Column.class);
				final ColumnData colData = new ColumnData();
				colData.setName(nCol.name().isEmpty() ? this.fieldToColumnName(f) : nCol.name());
				colData.setType(this.getTypeFor(nCol.type().equals(Class.class) ? f.getType() : nCol.type(), nCol));
				primaryKeys.add(colData);
			}
		}
		return primaryKeys.toArray(new ColumnData[0]);
	}

	@Override
	public <T extends DataBaseEntry> ColumnData[] getGeneratedKeys(final T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get primary keys for null object.", new NullPointerException("data is null."));
		}
		return this.getGeneratedKeys((Class<T>) data.getClass());
	}

	@Override
	public <T extends DataBaseEntry> ColumnData[] getGeneratedKeys(final Class<? extends T> entryType) {
		final List<ColumnData> primaryKeys = new ArrayList<>();

		for (final Field f : this.sortFields(this.getAllFields(entryType))) {
			if (f.isAnnotationPresent(Column.class)
					&& (f.isAnnotationPresent(Generated.class) || f.isAnnotationPresent(AutoIncrement.class))) {
				final Column nCol = f.getAnnotation(Column.class);
				final ColumnData colData = new ColumnData();
				colData.setName(nCol.name().isEmpty() ? f.getName() : nCol.name());
				colData.setType(this.getTypeFor(nCol.type().equals(Class.class) ? f.getType() : nCol.type(), nCol));
				primaryKeys.add(colData);
			}
		}
		return primaryKeys.toArray(new ColumnData[0]);
	}

	@Override
	public String getQueryableName(final Class<? extends SQLQueryable<? extends DataBaseEntry>> tableClass) {
		if (tableClass.isAnnotationPresent(TableName.class)) {
			final TableName tableAnno = tableClass.getAnnotation(TableName.class);
			if (!tableAnno.value().isEmpty()) {
				return tableAnno.value();
			}
		}
		if (tableClass.isAnnotationPresent(DB_View.class)) {
			final DB_View tableAnno = tableClass.getAnnotation(DB_View.class);
			if (!tableAnno.name().isEmpty()) {
				return tableAnno.name();
			}
		}
		return PCUtils.camelCaseToSnakeCase(tableClass.getSimpleName().replaceAll("Table$", ""));
	}

	@Override
	public String fieldToColumnName(final String name) {
		return PCUtils.camelCaseToSnakeCase(name);
	}

	@Override
	public String fieldToColumnName(final Field field) {
		if (!field.isAnnotationPresent(Column.class)) {
			throw new IllegalArgumentException("Field " + field.getName() + " is not annotated with @Column");
		}
		final Column colAnno = field.getAnnotation(Column.class);
		return colAnno.name().isEmpty() ? this.fieldToColumnName(field.getName()) : colAnno.name();
	}

	@Override
	public <T extends DataBaseEntry> Field getFieldFor(final Class<T> entryClazz, final String sqlName) {
		try {
			final Field field = this.findField(entryClazz, sqlName);
			if (field != null && field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).name().equals(sqlName)) {
				return field;
			}
		} catch (final NoSuchFieldException e) {
			// ignore
		}

		for (final Field field : PCUtils.getAllFields(entryClazz)) {
			if (field.isAnnotationPresent(Column.class)
					&& (field.getAnnotation(Column.class).name().equals(sqlName) || this.fieldToColumnName(field).equals(sqlName))) {
				return field;
			}
		}

		throw new IllegalArgumentException("No field for column named: '" + sqlName + "' in class: [" + entryClazz.getName() + "]");
	}

	@Override
	public <T extends DataBaseEntry> void fillInsert(final T data, final ResultSet rs) throws SQLException {
		final Class<?> entryClazz = data.getClass();

		try {
			for (final Field field : this.sortFields(PCUtils.getAllFields(entryClazz))) {
				field.setAccessible(true);

				if (!field.isAnnotationPresent(PrimaryKey.class)) {
					continue;
				}

				final String columnName = this.fieldToColumnName(field);
				final Column column = field.getAnnotation(Column.class);

				final ColumnType type = this.getTypeFor(field);

				try {
					final Object value = type.load(rs, 1, field.getGenericType());
					field.set(data, rs.wasNull() ? null : value);
				} catch (final Exception e) {
					throw new RuntimeException(
							"Failed to decode value/update field for: " + field.getName() + " as " + columnName + " with value '"
									+ rs.getObject(columnName) + "'",
							e);
				}
			}

			final Method insertMethod = this.getInsertMethod(data);
			if (insertMethod != null) {
				try {
					insertMethod.invoke(data);
				} catch (final Exception e) {
					throw new RuntimeException("Exception while invoking insert method.", e);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException("Failed to update fields on " + entryClazz + " for input: " + PCUtils.asMap(rs), e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void fillLoad(final T data, final ResultSet rs) throws SQLException {
		final Class<?> entryClazz = data.getClass();

		try {
			for (final Field field : this.sortFields(PCUtils.getAllFields(entryClazz))) {
				field.setAccessible(true);

				if (!field.isAnnotationPresent(Column.class)) {
					continue;
				}

				final String columnName = this.fieldToColumnName(field);
				final Column column = field.getAnnotation(Column.class);

				final ColumnType type = this.getTypeFor(field);

				try {
					final Object value = type.load(rs, columnName, field.getGenericType());
					field.set(data, rs.wasNull() ? null : value);
				} catch (final Exception e) {
					throw new RuntimeException(
							"Failed to decode value/update field for: " + field.getName() + " as " + columnName + " with value '"
									+ rs.getObject(columnName) + "'",
							e);
				}
			}

			final Method loadMethod = this.getLoadMethod(data);
			if (loadMethod != null) {
				try {
					loadMethod.invoke(data);
				} catch (final Exception e) {
					throw new RuntimeException("Exception while invoking load method.", e);
				}
			}
		} catch (final Exception e) {
			throw new RuntimeException("Failed to update fields on " + entryClazz + " for input: " + PCUtils.asMap(rs), e);
		}
	}

	@Override
	public <T extends DataBaseEntry> T fillLoadCopy(final T data, final ResultSet rs) throws SQLException {
		final T new_ = this.instance(data);
		this.fillLoad(new_, rs);
		return new_;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> T instance(final T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot instance null object.", new NullPointerException("data is null."));
		}
		return this.<T>instance((Class<T>) data.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> T instance(final Class<T> clazz) {
		final Method factoryMethod = this.getStaticFactoryMethod(clazz);
		if (factoryMethod != null) {
			try {
				factoryMethod.setAccessible(true);
				return (T) factoryMethod.invoke(null);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(
						"Failed to instantiate " + clazz.getName() + " through factory method: " + factoryMethod.getName(),
						e);
			}
		} else {
			try {
				final Constructor<T> ctor = clazz.getDeclaredConstructor();
				ctor.setAccessible(true);
				return ctor.newInstance();
			} catch (final NoSuchMethodException e) {
				throw new RuntimeException("No empty constructor nor factory method found " + clazz.getName(), e);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException("Failed to instantiate " + clazz.getName(), e);
			}
		}
	}

	public Method getStaticFactoryMethod(final Class<?> clazz) {
		for (final Method method : clazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Factory.class) && Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 0) {
				if (!method.getReturnType().equals(clazz)) {
					throw new IllegalArgumentException(
							"Factory method returns wrong type: " + clazz.getName() + " returns " + method.getReturnType().getName());
				}
				return method;
			}
		}
		return null;
	}

	@Override
	public <T extends DataBaseEntry> void fillLoadAll(final T data, final ResultSet result, final Consumer<T> listExporter)
			throws SQLException {
		if (data == null || result == null || listExporter == null) {
			throw new IllegalArgumentException("Null argument provided to fillAll.");
		}

		while (result.next()) {
			final T copy = this.fillLoadCopy(data, result);
			listExporter.accept(copy);
		}
	}

	@Override
	public <T extends DataBaseEntry> void fillLoadAllTable(
			final Class<? extends SQLQueryable<T>> tableClazz,
			final SQLQuery<T, ?> query,
			final ResultSet result,
			final Consumer<T> listExporter) throws SQLException {
		if (query == null || result == null || listExporter == null) {
			throw new IllegalArgumentException("Null argument provided to fillAll.");
		}

		final Class<T> entryClazz = (Class<T>) this.getEntryType(tableClazz);

		while (result.next()) {
			final T copy = this.instance(entryClazz);
			this.fillLoad(copy, result);
			listExporter.accept(copy);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Map<String, Object>[] getUniqueValues(final ConstraintData[] allConstraints, final T data) {
		if (allConstraints == null || allConstraints.length == 0 || data == null) {
			return (Map<String, Object>[]) new Map[0];
		}

		final List<UniqueData> uniqueConstraints = Arrays.stream(allConstraints)
				.filter(c -> c instanceof UniqueData)
				.map(PCUtils::<UniqueData>cast)
				.collect(Collectors.toList());

		final Map<String, Object>[] result = (Map<String, Object>[]) new Map[uniqueConstraints.size()];

		for (int i = 0; i < uniqueConstraints.size(); i++) {
			final UniqueData unique = uniqueConstraints.get(i);
			final String[] columns = unique.getColumns();

			final Map<String, Object> keyMap = new LinkedHashMap<>();

			for (final String colName : columns) {
				try {
					final Field field = this.getFieldFor(data.getClass(), colName);

					field.setAccessible(true);
					final Object value = field.get(data);
					keyMap.put(colName, value);
				} catch (final IllegalAccessException e) {
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

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> List<String>[] getUniqueKeys(final ConstraintData[] allConstraints, final T data) {
		if (allConstraints == null || allConstraints.length == 0 || data == null) {
			return (List<String>[]) new List[0];
		}

		return Arrays.stream(this.getUniqueValues(allConstraints, data))
				.map(map -> map.keySet().stream().collect(Collectors.toList()))
				.collect(Collectors.toList())
				.toArray(new List[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Map<String, Object> getNotNullValues(final T data) {
		final Class<T> entryClazz = (Class<T>) data.getClass();
		final Map<String, Object> result = new HashMap<>();

		for (final Field field : PCUtils.getAllFields(entryClazz)) {
			if (!field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Generated.class)
					|| field.isAnnotationPresent(OnUpdate.class))
				continue;

			try {
				field.setAccessible(true);
				final Object value = field.get(data);

				if (value == null)
					continue;

				result.put(this.fieldToColumnName(field), value);
			} catch (final IllegalAccessException e) {
				PCUtils.throwRuntime(e);
				return null;
			}
		}

		return result;
	}

	@Override
	public <T extends DataBaseEntry> List<String> getNotNullKeys(final T data) {
		return this.getNotNullValues(data).keySet().stream().collect(Collectors.toList());
	}

	@Override
	public <T extends DataBaseEntry> void fillUpdate(final T data, final ResultSet rs) throws SQLException {
		final Class<?> entryClazz = data.getClass();

		try {
			for (final Field field : this.sortFields(PCUtils.getAllFields(entryClazz))) {
				field.setAccessible(true);

				if (!field.isAnnotationPresent(OnUpdate.class) || !field.isAnnotationPresent(Generated.class)) {
					continue;
				}

				final String columnName = this.fieldToColumnName(field);
				final Column column = field.getAnnotation(Column.class);

				final ColumnType type = this.getTypeFor(field);

				final Object value = type.load(rs, columnName, field.getGenericType());
				field.set(data, rs.wasNull() ? null : value);
			}

			final Method updateMethod = this.getUpdateMethod(data);
			if (updateMethod != null) {
				try {
					updateMethod.invoke(data);
				} catch (final Exception e) {
					throw new RuntimeException("Exception while invoking update method.", e);
				}
			}
		} catch (final IllegalAccessException e) {
			throw new RuntimeException("Failed to update update keys on " + entryClazz, e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Method getUpdateMethod(final T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get update method for null object.", new NullPointerException("data is null."));
		}
		return this.getUpdateMethod((Class<T>) data.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Method getLoadMethod(final T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get load method for null object.", new NullPointerException("data is null."));
		}
		return this.getLoadMethod((Class<T>) data.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Method getInsertMethod(final T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get insert method for null object.", new NullPointerException("data is null."));
		}
		return this.getInsertMethod((Class<T>) data.getClass());
	}

	@Override
	public <T extends DataBaseEntry> Method getInsertMethod(final Class<T> data) {
		for (final Method m : data.getDeclaredMethods()) {
			if (m.isAnnotationPresent(Insert.class)) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	@Override
	public <T extends DataBaseEntry> Method getUpdateMethod(final Class<T> data) {
		for (final Method m : data.getDeclaredMethods()) {
			if (m.isAnnotationPresent(Update.class)) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	@Override
	public <T extends DataBaseEntry> Method getLoadMethod(final Class<T> data) {
		for (final Method m : data.getDeclaredMethods()) {
			if (m.isAnnotationPresent(Load.class)) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedInsertSQL(final AbstractDBTable<T> table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<?> entryClazz = data.getClass();
		final String tableName = table.getQualifiedName();

		final List<String> columns = this.sortFields(PCUtils.getAllFields(entryClazz))
				.stream()
				.filter(f -> f.isAnnotationPresent(Column.class))
				.filter(f -> !f.isAnnotationPresent(Generated.class))
				.filter(f -> !f.isAnnotationPresent(AutoIncrement.class))
				.filter(f -> {
					f.setAccessible(true);
					try {
						final Object value = f.get(data);

						if (value == null && f.isAnnotationPresent(DefaultValue.class)) {
							return false;
						}
						return true;
					} catch (final IllegalAccessException e) {
						throw new RuntimeException("Failed to access field value for field: " + f.getName(), e);
					}
				})
				.map(f -> PCUtils.sqlEscapeIdentifier(this.fieldToColumnName(f)))
				.collect(Collectors.toList());

		final String placeholders = columns.stream().map(col -> "?").collect(Collectors.joining(", "));

		final String columnList = String.join(", ", columns);

		return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnList, placeholders);
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedUpdateSQL(final AbstractDBTable<T> table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<?> entryClazz = data.getClass();
		final String tableName = table.getQualifiedName();

		final List<String> setColumns = this.sortFields(PCUtils.getAllFields(entryClazz))
				.stream()
				.filter(f -> f.isAnnotationPresent(Column.class))
				.filter(f -> !f.isAnnotationPresent(Generated.class))
				.filter(f -> !f.isAnnotationPresent(PrimaryKey.class))
				.filter(f -> !f.isAnnotationPresent(OnUpdate.class))
				.filter(f -> {
					f.setAccessible(true);
					try {
						final Object value = f.get(data);
						if (value == null && f.isAnnotationPresent(DefaultValue.class)) {
							return false;
						}
						return true;
					} catch (final IllegalAccessException e) {
						throw new RuntimeException("Failed to access field value for field: " + f.getName(), e);
					}
				})
				.map(f -> PCUtils.sqlEscapeIdentifier(this.fieldToColumnName(f)) + " = ?")
				.collect(Collectors.toList());

		if (setColumns.isEmpty()) {
			throw new IllegalArgumentException("No columns to update.");
		}

		final List<String> whereColumns = this.sortFields(PCUtils.getAllFields(entryClazz))
				.stream()
				.filter(f -> f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(PrimaryKey.class))
				.map(f -> PCUtils.sqlEscapeIdentifier(this.fieldToColumnName(f)) + " = ?")
				.collect(Collectors.toList());

		if (whereColumns.isEmpty()) {
			throw new IllegalArgumentException("No primary key defined on " + entryClazz.getSimpleName());
		}

		final String setClause = String.join(", ", setColumns);
		final String whereClause = String.join(" AND ", whereColumns);

		return String.format("UPDATE %s SET %s WHERE %s", tableName, setClause, whereClause);
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedDeleteSQL(final AbstractDBTable<T> table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<?> entryClazz = data.getClass();
		final String tableName = table.getQualifiedName();

		final List<String> whereColumns = this.sortFields(PCUtils.getAllFields(entryClazz))
				.stream()
				.filter(f -> f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(PrimaryKey.class))
				.map(f -> PCUtils.sqlEscapeIdentifier(this.fieldToColumnName(f)) + " = ?")
				.collect(Collectors.toList());

		if (whereColumns.isEmpty()) {
			throw new IllegalArgumentException("No primary key defined on " + entryClazz.getSimpleName());
		}

		final String whereClause = String.join(" AND ", whereColumns);

		return String.format("DELETE FROM %s WHERE %s", tableName, whereClause);
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedSelectSQL(final SQLQueryable<T> table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<?> entryClazz = data.getClass();
		final String tableName = table.getQualifiedName();

		final List<String> whereColumns = this.sortFields(PCUtils.getAllFields(entryClazz))
				.stream()
				.filter(f -> f.isAnnotationPresent(Column.class) && f.isAnnotationPresent(PrimaryKey.class))
				.map(f -> PCUtils.sqlEscapeIdentifier(this.fieldToColumnName(f)) + " = ?")
				.collect(Collectors.toList());

		if (whereColumns.isEmpty()) {
			throw new IllegalArgumentException("No primary key defined on " + entryClazz.getSimpleName());
		}

		final String whereClause = String.join(" AND ", whereColumns);

		return String.format("SELECT * FROM %s WHERE %s", tableName, whereClause);
	}

	@Override
	public <T extends DataBaseEntry> void prepareInsertSQL(final PreparedStatement stmt, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<?> entryClazz = data.getClass();

		final List<Field> fieldsToInsert = this.sortFields(PCUtils.getAllFields(entryClazz))
				.stream()
				.filter(f -> f.isAnnotationPresent(Column.class))
				.filter(f -> !f.isAnnotationPresent(Generated.class))
				.filter(f -> !f.isAnnotationPresent(AutoIncrement.class))
				.filter(f -> {
					f.setAccessible(true);
					try {
						final Object value = f.get(data);

						if (value == null && f.isAnnotationPresent(DefaultValue.class)) {
							return false;
						}
						return true;
					} catch (final IllegalAccessException e) {
						throw new RuntimeException("Failed to access field value for field: " + f.getName(), e);
					}
				})
				.collect(Collectors.toList());

		int index = 1;
		for (final Field field : fieldsToInsert) {
			field.setAccessible(true);
			try {
				final Object value = field.get(data);
				final ColumnType type = this.getTypeFor(field);

				type.store(stmt, index++, value);
			} catch (final IllegalAccessException e) {
				throw new RuntimeException("Failed to access field value", e);
			} catch (final Exception e) {
				throw new RuntimeException("Failed to store field value (" + field + ")", e);
			}
		}
	}

	@Override
	public <T extends DataBaseEntry> void prepareUpdateSQL(final PreparedStatement stmt, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<?> entryClazz = data.getClass();

		final List<Field> setFields = this.sortFields(PCUtils.getAllFields(entryClazz))
				.stream()
				.filter(f -> f.isAnnotationPresent(Column.class))
				.filter(f -> !f.isAnnotationPresent(Generated.class))
				.filter(f -> !f.isAnnotationPresent(PrimaryKey.class))
				.filter(f -> !f.isAnnotationPresent(OnUpdate.class))
				.filter(f -> {
					f.setAccessible(true);
					try {
						final Object value = f.get(data);
						if (value == null && f.isAnnotationPresent(DefaultValue.class)) {
							return false;
						}
						return true;
					} catch (final IllegalAccessException e) {
						throw new RuntimeException("Failed to access field value for field: " + f.getName(), e);
					}
				})
				.collect(Collectors.toList());

		final List<Field> pkFields = this.sortFields(PCUtils.getAllFields(entryClazz))
				.stream()
				.filter(f -> f.isAnnotationPresent(Column.class))
				.filter(f -> f.isAnnotationPresent(PrimaryKey.class))
				.collect(Collectors.toList());

		int index = 1;
		try {
			for (final Field field : setFields) {
				field.setAccessible(true);
				final Object value = field.get(data);
				final ColumnType type = this.getTypeFor(field);

				type.store(stmt, index++, value);
			}

			for (final Field field : pkFields) {
				field.setAccessible(true);
				final Object value = field.get(data);
				final ColumnType type = this.getTypeFor(field);

				type.store(stmt, index++, value);
			}
		} catch (final IllegalAccessException e) {
			throw new RuntimeException("Failed to access field value", e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void prepareDeleteSQL(final PreparedStatement stmt, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<?> entryClazz = data.getClass();

		final List<Field> pkFields = this.sortFields(PCUtils.getAllFields(entryClazz))
				.stream()
				.filter(f -> f.isAnnotationPresent(Column.class))
				.filter(f -> f.isAnnotationPresent(PrimaryKey.class))
				.collect(Collectors.toList());

		int index = 1;
		try {
			for (final Field field : pkFields) {
				field.setAccessible(true);
				final Object value = field.get(data);

				final ColumnType type = this.getTypeFor(field);
				type.store(stmt, index++, value);
			}
		} catch (final IllegalAccessException e) {
			throw new RuntimeException("Failed to access field value", e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void prepareSelectSQL(final PreparedStatement stmt, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<?> entryClazz = data.getClass();

		final List<Field> pkFields = this.sortFields(PCUtils.getAllFields(entryClazz))
				.stream()
				.filter(f -> f.isAnnotationPresent(Column.class))
				.filter(f -> f.isAnnotationPresent(PrimaryKey.class))
				.collect(Collectors.toList());

		int index = 1;
		try {
			for (final Field field : pkFields) {
				field.setAccessible(true);
				final Object value = field.get(data);

				final ColumnType type = this.getTypeFor(field);
				type.store(stmt, index++, value);
			}
		} catch (final IllegalAccessException e) {
			throw new RuntimeException("Failed to access field value", e);
		}
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedSelectCountUniqueSQL(
			final SQLQueryable<? extends T> instance,
			final List<String>[] uniqueKeys,
			final T data) {

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		final String sqlQuery = SQLBuilder.safeSelectCountUniqueCollision(instance, Arrays.asList(uniqueKeys));

		return sqlQuery;
	}

	@Override
	public <T extends DataBaseEntry> void prepareSelectCountUniqueSQL(
			final PreparedStatement stmt,
			final List<String>[] uniqueKeys,
			final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		final Class<? extends DataBaseEntry> entryClazz = data.getClass();

		try {
			int index = 1;
			for (final List<String> list : uniqueKeys) {
				for (final String column : list) {
					final Field field = this.getFieldFor(entryClazz, column);
					field.setAccessible(true);

					final ColumnType type = this.getTypeFor(field);
					type.store(stmt, index++, field.get(data));
				}
			}
		} catch (final IllegalAccessException e) {
			PCUtils.throwRuntime(e);
		}
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedSelectCountNotNullSQL(
			final SQLQueryable<? extends T> instance,
			final List<String> notNullKeys,
			final T data) {
		if (notNullKeys.size() == 0) {
			throw new IllegalArgumentException("No non-null keys found for " + data.getClass().getName());
		}

		return SQLBuilder.safeSelectUniqueCollision(instance, Collections.singletonList(notNullKeys));
	}

	@Override
	public <T extends DataBaseEntry> void prepareSelectCountNotNullSQL(
			final PreparedStatement stmt,
			final List<String> notNullKeys,
			final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		if (notNullKeys.size() == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		final Class<? extends DataBaseEntry> entryClazz = data.getClass();

		try {
			int index = 1;
			for (final String column : notNullKeys) {
				final Field field = this.getFieldFor(entryClazz, column);
				field.setAccessible(true);

				final ColumnType type = this.getTypeFor(field);
				type.store(stmt, index++, field.get(data));
			}
		} catch (final IllegalAccessException e) {
			PCUtils.throwRuntime(e);
		}
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedSelectUniqueSQL(
			final AbstractDBTable<T> instance,
			final List<String>[] uniqueKeys,
			final T data) {
		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		return SQLBuilder.safeSelectUniqueCollision(instance, Arrays.asList(uniqueKeys));
	}

	@Override
	public <T extends DataBaseEntry> void prepareSelectUniqueSQL(
			final PreparedStatement stmt,
			final List<String>[] uniqueKeys,
			final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		final Class<? extends DataBaseEntry> entryClazz = data.getClass();

		try {
			int index = 1;
			for (final List<String> list : uniqueKeys) {
				for (final String column : list) {
					final Field field = this.getFieldFor(entryClazz, column);
					field.setAccessible(true);

					final ColumnType type = this.getTypeFor(field);
					type.store(stmt, index++, field.get(data));
				}
			}
		} catch (final IllegalAccessException e) {
			PCUtils.throwRuntime(e);
		}
	}

	protected Field[] getAllFields(final Class<?> type) {
		final List<Field> fields = new ArrayList<>();
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
		}
		return fields.toArray(new Field[fields.size()]);
	}

	protected Field findField(final Class<?> type, final String name) throws NoSuchFieldException {
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			try {
				return c.getDeclaredField(name);
			} catch (final NoSuchFieldException e) {
				// keep going
			}
		}
		throw new NoSuchFieldException(name);
	}

	protected Column getFallbackColumnAnnotation() {
		try {
			return BaseDataBaseEntryUtils.class.getDeclaredField("columnType").getAnnotation(Column.class);
		} catch (NoSuchFieldException | SecurityException e) {
			PCUtils.throwRuntime(e);
			return null;
		}
	}

	public BaseDataBaseEntryUtils loadMySQLTypes() {
		typeMap.clear();
		
		typeMap.put(String.class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
		typeMap.put(CharSequence.class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
		typeMap.put(char[].class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());

		typeMap.put(byte[].class, col -> col.length() != -1 ? new VarbinaryType(col.length()) : new BlobType());
		typeMap.put(ByteBuffer.class, col -> col.length() != -1 ? new VarbinaryType(col.length()) : new BlobType());

		typeMap.put(Byte.class, col -> new TinyIntType());
		typeMap.put(byte.class, col -> new TinyIntType());
		typeMap.put(Short.class, col -> new SmallIntType());
		typeMap.put(short.class, col -> new SmallIntType());
		typeMap.put(Integer.class, col -> new IntType());
		typeMap.put(int.class, col -> new IntType());
		typeMap.put(Long.class, col -> new BigIntType());
		typeMap.put(long.class, col -> new BigIntType());
		typeMap.put(BigInteger.class, col -> new BigIntType());

		typeMap.put(Double.class, col -> new DoubleType());
		typeMap.put(double.class, col -> new DoubleType());
		typeMap.put(Float.class, col -> new FloatType());
		typeMap.put(float.class, col -> new FloatType());

		typeMap.put(Boolean.class, col -> new BooleanType());
		typeMap.put(boolean.class, col -> new BooleanType());

		typeMap.put(Timestamp.class, col -> new TimestampType());
		typeMap.put(LocalDateTime.class, col -> new TimestampType());
		typeMap.put(Date.class, col -> new DateType());
		typeMap.put(LocalDate.class, col -> new DateType());

		typeMap.put(JSONObject.class, col -> new JsonType());
		typeMap.put(JSONArray.class, col -> new JsonType());

		// -- native types
		typeMap.put(TextType.class, col -> new TextType());
		typeMap.put(CharType.class, col -> new CharType(col.length()));
		typeMap.put(VarcharType.class, col -> new VarcharType(col.length()));

		typeMap.put(BinaryType.class, col -> new BinaryType(col.length()));
		typeMap.put(VarbinaryType.class, col -> new BinaryType(col.length()));
		typeMap.put(BlobType.class, col -> new BlobType());

		typeMap.put(BitType.class, col -> new BitType());
		typeMap.put(SmallIntType.class, col -> new SmallIntType());
		typeMap.put(IntType.class, col -> new IntType());
		typeMap.put(BigIntType.class, col -> new BigIntType());

		typeMap.put(DoubleType.class, col -> new DoubleType());
		typeMap.put(FloatType.class, col -> new FloatType());
		typeMap.put(DecimalType.class, col -> new DecimalType(col.length(), col.params()));

		typeMap.put(TimestampType.class, col -> new TimestampType());
		typeMap.put(DateType.class, col -> new DateType());

		typeMap.put(JsonType.class, col -> new JsonType());

		return this;
	}
	
	public BaseDataBaseEntryUtils loadSQLiteTypes() {
		typeMap.clear();
		
		typeMap.put(String.class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
		typeMap.put(CharSequence.class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());
		typeMap.put(char[].class, col -> col.length() != -1 ? new VarcharType(col.length()) : new TextType());

		typeMap.put(byte[].class, col -> col.length() != -1 ? new VarbinaryType(col.length()) : new BlobType());
		typeMap.put(ByteBuffer.class, col -> col.length() != -1 ? new VarbinaryType(col.length()) : new BlobType());

		typeMap.put(Byte.class, col -> new IntegerType());
		typeMap.put(byte.class, col -> new IntegerType());
		typeMap.put(Short.class, col -> new IntegerType());
		typeMap.put(short.class, col -> new IntegerType());
		typeMap.put(Integer.class, col -> new IntegerType());
		typeMap.put(int.class, col -> new IntegerType());
		typeMap.put(Long.class, col -> new IntegerType());
		typeMap.put(long.class, col -> new IntegerType());
		typeMap.put(BigInteger.class, col -> new IntegerType());

		typeMap.put(Double.class, col -> new RealType());
		typeMap.put(double.class, col -> new RealType());
		typeMap.put(Float.class, col -> new RealType());
		typeMap.put(float.class, col -> new RealType());

		typeMap.put(Boolean.class, col -> new BooleanType());
		typeMap.put(boolean.class, col -> new BooleanType());

		typeMap.put(Timestamp.class, col -> new TimestampType());
		typeMap.put(LocalDateTime.class, col -> new TimestampType());
		typeMap.put(Date.class, col -> new DateType());
		typeMap.put(LocalDate.class, col -> new DateType());

		typeMap.put(JSONObject.class, col -> new JsonType());
		typeMap.put(JSONArray.class, col -> new JsonType());

		// -- native types
		typeMap.put(TextType.class, col -> new TextType());
		typeMap.put(CharType.class, col -> new CharType(col.length()));
		typeMap.put(VarcharType.class, col -> new VarcharType(col.length()));

		typeMap.put(BinaryType.class, col -> new BinaryType(col.length()));
		typeMap.put(VarbinaryType.class, col -> new BinaryType(col.length()));
		typeMap.put(BlobType.class, col -> new BlobType());

		typeMap.put(BitType.class, col -> new BitType());
		typeMap.put(SmallIntType.class, col -> new SmallIntType());
		typeMap.put(IntType.class, col -> new IntType());
		typeMap.put(BigIntType.class, col -> new BigIntType());

		typeMap.put(DoubleType.class, col -> new DoubleType());
		typeMap.put(FloatType.class, col -> new FloatType());
		typeMap.put(DecimalType.class, col -> new DecimalType(col.length(), col.params()));

		typeMap.put(TimestampType.class, col -> new TimestampType());
		typeMap.put(DateType.class, col -> new DateType());

		typeMap.put(JsonType.class, col -> new JsonType());

		return this;
	}

}
