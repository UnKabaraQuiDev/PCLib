package lu.kbra.pclib.db.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.tuple.Pair;
import lu.kbra.pclib.datastructure.tuple.Pairs;
import lu.kbra.pclib.datastructure.tuple.Quadruples;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyPair;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyQuadruple;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Check;
import lu.kbra.pclib.db.annotations.entry.Checks;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.DefaultValues;
import lu.kbra.pclib.db.annotations.entry.Factory;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Generated;
import lu.kbra.pclib.db.annotations.entry.Insert;
import lu.kbra.pclib.db.annotations.entry.Load;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.OnUpdate;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.TypeHint;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.Uniques;
import lu.kbra.pclib.db.annotations.entry.Update;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.dbms.DbmsProviders;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.GeneratedColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolvers;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitors;
import lu.kbra.pclib.db.domain.table.CheckData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.table.ForeignKeyData;
import lu.kbra.pclib.db.domain.table.PrimaryKeyData;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;
import lu.kbra.pclib.db.domain.table.meta.DefaultTableHints;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtilsOptionsOwner;
import lu.kbra.pclib.db.utils.registry.ColumnTypeFactory;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.impl.function.ThrowingFunction;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

@ToString
@EqualsAndHashCode
public class BaseDataBaseEntryUtils implements DataBaseEntryUtils {

	private static final Set<String> EMPTY_SET = Collections.unmodifiableSet(new HashSet<String>(0));
	private static final Object[] EMPTY_ARRAY = new Object[0];

	@Getter
	protected final List<ColumnTypeFactory> columnTypeFactories = new ArrayList<>();

	@Getter
	protected String dbmsQualifierName;
	@Getter
	protected SQLStructureVisitor structureVisitor;
	@Getter
	protected SQLFunctionResolver functionResolver;

	protected final Map<Field, ColumnType> fieldColumnTypeCache = new ConcurrentHashMap<>();
	protected final Map<Class<? extends DataBaseEntry>, ColumnData[]> columnsCache = new ConcurrentHashMap<>();
	protected final Map<Class<? extends DataBaseEntry>, ColumnData[]> primaryKeysCache = new ConcurrentHashMap<>();
	protected final Map<Class<? extends DataBaseEntry>, String[]> primaryKeysNamesCache = new ConcurrentHashMap<>();
	protected final Map<Class<? extends DataBaseEntry>, ColumnData[]> generatedKeysCache = new ConcurrentHashMap<>();
	protected final Map<Class<? extends DataBaseEntry>, String[]> updateColumnsNamesCache = new ConcurrentHashMap<>();
	protected final Map<Class<? extends DataBaseEntry>, ColumnData[]> nonNullColumnsCache = new ConcurrentHashMap<>();
	protected final Map<Class<? extends DataBaseEntry>, ColumnData[]> insertColumnsCache = new ConcurrentHashMap<>();
	protected final Map<Class<? extends DataBaseEntry>, ColumnData[]> updateColumnsCache = new ConcurrentHashMap<>();
	protected final Map<Class<?>, Method> insertMethodCache = new ConcurrentHashMap<>();
	protected final Map<Class<?>, Method> updateMethodCache = new ConcurrentHashMap<>();
	protected final Map<Class<?>, Method> loadMethodCache = new ConcurrentHashMap<>();
	protected final Map<Class<? extends SQLQueryable<?>>, TableStructure> tableStructureCache = new ConcurrentHashMap<>();
	protected final Map<Field, String> fieldToColumnNameCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, String>, Field> fieldCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends AbstractDBTable<? extends DataBaseEntry>>, Class<? extends DataBaseEntry>>, String> updateSqlCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends AbstractDBTable<? extends DataBaseEntry>>, Class<? extends DataBaseEntry>>, String> deleteSqlCache = new ConcurrentHashMap<>();
	protected final Map<AnnotatedType, Map<String, Object>> typeHints = new ConcurrentHashMap<>();
	protected final Map<Class<?>, Map<String, Object>> queryableHints = new ConcurrentHashMap<>();
	protected final Map<String, Object> options = new ConcurrentHashMap<>();
	protected final Map<Class<? extends DataBaseEntry>, Map<Set<String>, ReadOnlyPair<List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>>, ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>>>> argInstanceFactoryCache = new ConcurrentHashMap<>();
	protected final Map<Class<? extends DataBaseEntry>, Map<String, ColumnData>> columnsNamesCache = new ConcurrentHashMap<>();

	public BaseDataBaseEntryUtils(final ColumnTypeRegistry typeRegistry, final String protocolName) {
		this.loadTypes(typeRegistry);
		this.dbmsQualifierName = protocolName;
		this.structureVisitor = SQLStructureVisitors.forProtocol(protocolName);
		this.functionResolver = SQLFunctionResolvers.forProtocol(protocolName);
	}

	public BaseDataBaseEntryUtils(
			final ColumnTypeRegistry typeRegistry,
			final String protocol,
			final SQLStructureVisitor structureVisitor,
			final SQLFunctionResolver functionResolver) {
		this.loadTypes(typeRegistry);
		this.dbmsQualifierName = protocol;
		this.structureVisitor = structureVisitor;
		this.functionResolver = functionResolver;
	}

	public BaseDataBaseEntryUtils(final String protocol) {
		this(DbmsProviders.columnTypeRegistryFor(protocol), protocol);
	}

	protected BaseDataBaseEntryUtils() {
	}

	public void appendTypes(final ColumnTypeRegistry addColumnTypeRegistry) {
		addColumnTypeRegistry.registerTypes(this.columnTypeFactories);
	}

	@Override
	public Stream<ColumnTypeFactory> computeType(final Class<?> rawType, final Map<String, Object> typeHints) {
		return this.columnTypeFactories.stream()
				.map(entry -> new Pair<>(entry.eval(rawType, typeHints), entry))
				.filter(entry -> !Objects.equals(entry.getKey(), ColumnTypeRegistry.EXCLUDE))
				.sorted(Comparator.comparingInt(e -> -e.getKey()))
				.map(Pair::getValue);
	}

	@Override
	public String fieldToColumnName(final Field field) {
		return this.fieldToColumnNameCache.computeIfAbsent(field, this::computeFieldToColumnName);
	}

	@Override
	public String fieldToColumnName(final String name) {
		return this.structureVisitor.fieldToColumnName(name);
	}

	@Override
	public <T extends DataBaseEntry> void fillInsert(final T data, final ResultSet rs) throws SQLException {
		final Class<T> entryClazz = (Class<T>) data.getClass();

		final ColumnData[] primaryKeys = this.getPrimaryKeys(entryClazz);

		try {
			for (final ColumnData columnData : primaryKeys) {
				if (!columnData.isPrimaryKey()) {
					continue;
				}

				final Field field = this.getFieldFor(entryClazz, columnData);
				field.setAccessible(true);
				final String columnName = columnData.getName();
				final ColumnType type = columnData.getType();

				try {
					final Object value = type.load(rs, 1, field.getGenericType());
					field.set(data, rs.wasNull() ? null : value);
				} catch (final Exception e) {
					throw new DBException(
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
					throw new DBException("Exception while invoking insert method.", e);
				}
			}
		} catch (final Exception e) {
			throw new DBException("Failed to update fields on " + entryClazz + " for input: " + PCUtils.asMap(rs), e);
		}
	}

	public <T extends DataBaseEntry> T fillLoad(
			final Class<T> entryClazz,
			final ResultSet rs,
			final List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>> mapping,
			final ThrowingFunction<Object[], ? extends DataBaseEntry, DBException> factory)
			throws SQLException {
		try {
			final Object[] params = new Object[mapping.size()];
			for (final ReadOnlyQuadruple<String, ColumnData, Type, Integer> pair : mapping) {
				final String columnName = pair.getFirst();
				final ColumnData columnData = pair.getSecond();
				final ColumnType type = columnData.getType();

				try {
					final Object value = type.load(rs, columnName, pair.getThird());
					params[pair.getFourth()] = rs.wasNull() ? null : value;
				} catch (final Exception e) {
					throw new DBException(
							"Failed to decode value/update field for: " + columnData.getName() + " as " + columnName + " with value '"
									+ rs.getObject(columnName) + "'",
							e);
				}
			}

			final T data = (T) factory.apply(params);

			final Method loadMethod = this.getLoadMethod(data);
			if (loadMethod != null) {
				try {
					loadMethod.invoke(data);
				} catch (final Exception e) {
					throw new DBException("Exception while invoking load method.", e);
				}
			}
			return data;
		} catch (final Exception e) {
			throw new DBException("Failed to update fields on " + entryClazz + " for input: " + PCUtils.asMap(rs), e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void fillLoad(final T data, final ResultSet rs) throws SQLException {
		final Class<T> entryClazz = (Class<T>) data.getClass();

		final ColumnData[] columns = this.getColumnsFor(entryClazz);

		try {
			for (final ColumnData columnData : columns) {
				final Field field = this.getFieldFor(entryClazz, columnData);
				field.setAccessible(true);

				final String columnName = columnData.getName();
				final ColumnType type = columnData.getType();

				try {
					final Object value = type.load(rs, columnName, field.getGenericType());
					field.set(data, rs.wasNull() ? null : value);
				} catch (final Exception e) {
					throw new DBException(
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
					throw new DBException("Exception while invoking load method.", e);
				}
			}
		} catch (final Exception e) {
			throw new DBException("Failed to update fields on " + entryClazz + " for input: " + PCUtils.asMap(rs), e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void fillLoadAll(final Class<T> entryClazz, final ResultSet result, final Consumer<T> listExporter)
			throws SQLException {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(result, "result is null.");
		Objects.requireNonNull(listExporter, "listExporter is null.");

		final Map<Set<String>, ReadOnlyPair<List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>>, ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>>> factories = this.argInstanceFactoryCache
				.computeIfAbsent(entryClazz, this::computeInstanceFactories);

		final ResultSetMetaData resultMetaData = result.getMetaData();
		final int columnCount = resultMetaData.getColumnCount();
		final String[] columns = new String[columnCount];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = resultMetaData.getColumnLabel(i + 1);
		}

		final Set<String> args = new HashSet<>(Arrays.asList(columns));
		if (factories.containsKey(args)) {

			final ReadOnlyPair<List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>>, ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>> pair = factories
					.get(args);
			while (result.next()) {
				final T copy = this.fillLoad(entryClazz, result, pair.getKey(), pair.getValue());
				listExporter.accept(copy);
			}

		} else {

			while (result.next()) {
				final T copy = this.instance(entryClazz);
				this.fillLoad(copy, result);
				listExporter.accept(copy);
			}

		}
	}

	@Override
	public <T extends DataBaseEntry> void fillUpdate(final T data, final ResultSet rs) throws SQLException {
		final Class<T> entryClazz = (Class<T>) data.getClass();

		final ColumnData[] columns = this.getColumnsFor(entryClazz);

		try {
			for (final ColumnData columnData : columns) {
				if (!columnData.hasOnUpdate() || !(columnData instanceof GeneratedColumnData)) {
					continue;
				}

				final Field field = this.getFieldFor(entryClazz, columnData);
				field.setAccessible(true);
				final String columnName = columnData.getName();
				final ColumnType type = columnData.getType();

				final Object value = type.load(rs, columnName, field.getGenericType());
				field.set(data, rs.wasNull() ? null : value);
			}

			final Method updateMethod = this.getUpdateMethod(data);
			if (updateMethod != null) {
				try {
					updateMethod.invoke(data);
				} catch (final Exception e) {
					throw new DBException("Exception while invoking update method.", e);
				}
			}
		} catch (final IllegalAccessException e) {
			throw new DBException("Failed to update update keys on " + entryClazz, e);
		}
	}

	public <T extends DataBaseEntry> ColumnData getColumnFor(final Class<T> entryClazz, final String name) {
		return this.columnsNamesCache.computeIfAbsent(entryClazz, this::computeColumnNames).get(name);
	}

	public <T extends DataBaseEntry> ColumnData[] getColumnsFor(final Class<T> entryClazz) {
		Objects.requireNonNull(entryClazz, "entry class is null");
		return this.columnsCache.computeIfAbsent(entryClazz, this::computeColumnsFor);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Class<T> getEntryType(final Class<? extends SQLQueryable<?>> tableClass) {
		final Type genericSuperclass = tableClass.getGenericSuperclass();

		if (genericSuperclass instanceof ParameterizedType) {
			final ParameterizedType pt = (ParameterizedType) genericSuperclass;
			final Type[] typeArgs = pt.getActualTypeArguments();

			if (SQLQueryable.class.isAssignableFrom(PCUtils.getRawClass(pt.getRawType()))) {
				for (final Type typeArg : typeArgs) {
					if (DataBaseEntry.class.isAssignableFrom(PCUtils.getRawClass(typeArg))) {
						return (Class<T>) typeArg;
					}
				}
			}
		}

		throw new IllegalArgumentException("Could not determine DataBaseEntry type from " + tableClass);
	}

	public <T extends DataBaseEntry> Field getFieldFor(final Class<T> entryClazz, final ColumnData columnData) {
		return this.fieldCache.computeIfAbsent(Pairs.readOnly(entryClazz, columnData.getName()),
				key -> columnData.getField().orElseGet(() -> {
					for (final Field f : PCUtils.getAllFields(entryClazz)) {
						if (this.fieldToColumnName(f).equals(columnData.getName())) {
							return f;
						}
					}

					return null;
				}));
	}

	@Override
	public <T extends DataBaseEntry> Field getFieldFor(final Class<T> entryClazz, final String sqlName) {
		return this.fieldCache.computeIfAbsent(Pairs.readOnly(entryClazz, sqlName), key -> {
			try {
				final Field field = this.findField(entryClazz, sqlName);
				if (field != null && field.isAnnotationPresent(Column.class) && this.fieldToColumnName(field).equals(sqlName)) {
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
		});
	}

	@Override
	public <T extends DataBaseEntry> ColumnData[] getGeneratedKeys(final Class<T> entryClazz) {
		Objects.requireNonNull(entryClazz, "entry class is null");
		return this.generatedKeysCache.computeIfAbsent(entryClazz, this::computeGeneratedKeys);
	}

	@Override
	public <T extends DataBaseEntry> ColumnData[] getGeneratedKeys(final T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get primary keys for null object.", new NullPointerException("Data is null."));
		}
		return this.getGeneratedKeys((Class<T>) data.getClass());
	}

	@Override
	public <T extends DataBaseEntry> Method getInsertMethod(final Class<T> dataClazz) {
		return this.insertMethodCache.computeIfAbsent(dataClazz, dc -> {
			for (final Method m : dataClazz.getDeclaredMethods()) {
				if (m.isAnnotationPresent(Insert.class)) {
					m.setAccessible(true);
					return m;
				}
			}
			return null;
		});
	}

	@Override
	public <T extends DataBaseEntry> Method getInsertMethod(final T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get insert method for null object.", new NullPointerException("data is null."));
		}
		return this.getInsertMethod((Class<T>) data.getClass());
	}

	@Override
	public <T extends DataBaseEntry> Method getLoadMethod(final Class<T> dataClazz) {
		return this.loadMethodCache.computeIfAbsent(dataClazz, dc -> {
			for (final Method m : dc.getDeclaredMethods()) {
				if (m.isAnnotationPresent(Load.class)) {
					m.setAccessible(true);
					return m;
				}
			}
			return null;
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Method getLoadMethod(final T data) {
		if (data == null) {
			throw new IllegalArgumentException("Cannot get load method for null object.", new NullPointerException("data is null."));
		}
		return this.getLoadMethod((Class<T>) data.getClass());
	}

	@Override
	public <T extends DataBaseEntry> String[] getNonNullKeys(final T data) {
		return this.getNonNullValues(data).keySet().toArray(new String[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Map<String, Object> getNonNullValues(final T data) {
		final Class<T> entryClazz = (Class<T>) data.getClass();
		final Map<String, Object> result = new HashMap<>();

		for (final ColumnData columnData : this.getNonNullColumns(entryClazz)) {
			if (columnData.isGenerated() || columnData.isPrimaryKey() || columnData.hasOnUpdate()) {
				continue;
			}

			try {
				final Field field = this.getFieldFor(entryClazz, columnData);
				field.setAccessible(true);
				final Object value = field.get(data);

				if (value == null) {
					continue;
				}

				result.put(columnData.getName(), value);
			} catch (final IllegalAccessException e) {
				throw new DBException("Exception while getting non-null values from: " + entryClazz, e);
			}
		}

		return result;
	}

	@Override
	public Map<String, Object> getOptions() {
		return this.options;
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> String getPreparedDeleteSQL(final B table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<B> tableClazz = (Class<B>) table.getTargetClass();
		final Class<T> entryClazz = table.getEntryClass();

		return this.deleteSqlCache.computeIfAbsent(Pairs.readOnly(tableClazz, entryClazz),
				key -> this.computePreparedDeleteSql(table, entryClazz));
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedInsertSQL(final AbstractDBTable<T> table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<T> entryClazz = (Class<T>) data.getClass();

		final String[] columns = Arrays.stream(this.getInsertColumns(entryClazz)).filter(c -> {
			final Field f = this.getFieldFor(entryClazz, c);
			f.setAccessible(true);
			try {
				final Object value = f.get(data);

				if (value == null && c.hasDefaultValue()) {
					return false;
				}
				return true;
			} catch (final IllegalAccessException e) {
				throw new DBException("Failed to access field value for field: " + f, e);
			}
		}).map(ColumnData::getName).toArray(String[]::new);

		return this.structureVisitor.safeInsert(table, columns);
	}

	@Override
	public <T extends DataBaseEntry> String
			getPreparedSelectCountNotNullSQL(final SQLQueryable<? extends T> instance, final String[] notNullKeys, final T data) {
		if (notNullKeys.length == 0) {
			throw new IllegalArgumentException("No non-null keys found for " + data.getClass().getName());
		}

		return this.structureVisitor.safeSelectCountUniqueCollision(instance, new String[][] { notNullKeys });
	}

	@Override
	public <T extends DataBaseEntry> String
			getPreparedSelectCountUniqueSQL(final SQLQueryable<? extends T> instance, final String[][] uniqueKeys, final T data) {
		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		return this.structureVisitor.safeSelectCountUniqueCollision(instance, uniqueKeys);
	}

	@Override
	public <T extends DataBaseEntry> String getPreparedSelectSQL(final SQLQueryable<T> table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<T> entryClazz = (Class<T>) data.getClass();

		final ColumnData[] whereColumns = this.getPrimaryKeys(entryClazz);

		if (whereColumns.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + entryClazz.getSimpleName());
		}

		return this.structureVisitor.safeSelect(table, Arrays.stream(whereColumns).map(ColumnData::getName).toArray(String[]::new));
	}

	@Override
	public <T extends DataBaseEntry> String
			getPreparedSelectUniqueSQL(final AbstractDBTable<T> instance, final String[][] uniqueKeys, final T data) {
		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		return this.structureVisitor.safeSelectUniqueCollision(instance, uniqueKeys);
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> String getPreparedUpdateSQL(final B table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final Class<B> tableClazz = (Class<B>) table.getTargetClass();
		final Class<T> entryClazz = table.getEntryClass();

		return this.updateSqlCache.computeIfAbsent(Pairs.readOnly(tableClazz, entryClazz),
				key -> this.computePreparedUpdateSQL(table, entryClazz));
	}

	@Override
	public <T extends DataBaseEntry> ColumnData[] getPrimaryKeys(final Class<T> entryType) {
		return this.primaryKeysCache.computeIfAbsent(entryType, this::computePrimaryKeys);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> ColumnData[] getPrimaryKeys(final T data) {
		Objects.requireNonNull(data, "Entry is null.");
		return this.getPrimaryKeys((Class<T>) data.getClass());
	}

	@Override
	public <T extends DataBaseEntry> String[] getPrimaryKeysNames(final Class<T> entryClazz) {
		return this.primaryKeysNamesCache.computeIfAbsent(entryClazz, this::computePrimaryKeyNames);
	}

	@Override
	public Map<String, Object> getQueryableHints(final Class<?> tableClazz) {
		return this.queryableHints.computeIfAbsent(tableClazz, c -> Collections.unmodifiableMap(this.computeQueryableHints(tableClazz)));
	}

	@Override
	public <V extends SQLQueryable<T>, T extends DataBaseEntry> String getQueryableName(final Class<V> tableClass) {
		final String name = (String) this.getQueryableHints(tableClass).get(DefaultTableHints.NAME_OVERRIDE);
		return name == null || name.trim().isEmpty()
				? PCUtils.camelCaseToSnakeCase(tableClass.getSimpleName().replaceAll("(Table|View)$", ""))
				: name;
	}

	@Override
	public String getReferencedColumnName(final ForeignKey fk) {
		if (!fk.column().isEmpty()) {
			return fk.column();
		}
		final Class<? extends SQLQueryable<?>> refQueryable = fk.table();
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

	@Override
	public ColumnType getTypeFor(final AnnotatedType annotatedType) {
		final Map<String, Object> map = this.getTypeHints(annotatedType);
		return this.getTypeFor(annotatedType, map);
	}

	@Override
	public ColumnType getTypeFor(final AnnotatedType annotatedType, final Map<String, Object> typeHints) {
		if (!typeHints.containsKey(DefaultTypeHints.TYPE_OVERRIDE)) {
			return this.getTypeFor(PCUtils.getRawClass(annotatedType.getType()), Optional.of(annotatedType), typeHints);
		}

		try {
			final Object typeOverride = typeHints.get(DefaultTypeHints.TYPE_OVERRIDE);
			final Class<?> clazz = typeOverride instanceof Class ? (Class<?>) typeOverride : Class.forName(Objects.toString(typeOverride));
			return this.getTypeFor(clazz, Optional.of(annotatedType), typeHints);
		} catch (final ClassNotFoundException e) {
			throw new DBException(e);
		}
	}

	@Override
	public ColumnType getTypeFor(final Class<?> clazz, final Optional<AnnotatedType> type, final Map<String, Object> typeHints) {
		return this.computeType(clazz, typeHints)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No suitable type found: " + clazz.getName() + "\n" + typeHints))
				.get(type, typeHints);
	}

	@Override
	public ColumnType getTypeFor(final Field field) {
		return this.fieldColumnTypeCache.computeIfAbsent(field, f -> {
			final AnnotatedType annotatedType = f.getAnnotatedType();
			final Map<String, Object> typeHints = this.getTypeHints(annotatedType);
			return this.getTypeFor(annotatedType, typeHints);
		});
	}

	@Override
	public Map<String, Object> getTypeHints(final AnnotatedType annotatedType) {
		return this.typeHints.computeIfAbsent(annotatedType, c -> Collections.unmodifiableMap(this.computeTypeHints(c)));
	}

	@Override
	public <T extends DataBaseEntry> String[][] getUniqueKeys(final ConstraintData[] allConstraints, final T data) {
		if (allConstraints == null || allConstraints.length == 0 || data == null) {
			return new String[0][0];
		}

		return Arrays.stream(this.getUniqueValues(allConstraints, data))
				.map(map -> map.keySet().stream().toArray(String[]::new))
				.toArray(String[][]::new);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Map<String, Object>[] getUniqueValues(final ConstraintData[] allConstraints, final T data) {
		if (allConstraints == null || allConstraints.length == 0 || data == null) {
			return new Map[0];
		}

		final List<UniqueData> uniqueConstraints = Arrays.stream(allConstraints)
				.filter(UniqueData.class::isInstance)
				.map(PCUtils::<UniqueData>cast)
				.collect(Collectors.toList());

		final Map<String, Object>[] result = new Map[uniqueConstraints.size()];

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
					throw new DBException(e);
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

	@Override
	public <T extends DataBaseEntry> String[] getUpdateColumnsNames(final Class<T> entryClazz) {
		return this.updateColumnsNamesCache.computeIfAbsent(entryClazz, this::computeUpdateColumnsNames);
	}

	@Override
	public <T extends DataBaseEntry> Method getUpdateMethod(final Class<T> dataClazz) {
		return this.updateMethodCache.computeIfAbsent(dataClazz, dc -> {
			for (final Method m : dc.getDeclaredMethods()) {
				if (m.isAnnotationPresent(Update.class)) {
					m.setAccessible(true);
					return m;
				}
			}
			return null;
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Method getUpdateMethod(final T data) {
		Objects.requireNonNull(data, "Entry is null.");
		return this.getUpdateMethod((Class<T>) data.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> T instance(final Class<T> clazz) {
		return (T) this.argInstanceFactoryCache.computeIfAbsent(clazz, this::computeInstanceFactories)
				.get(BaseDataBaseEntryUtils.EMPTY_SET)
				.getValue()
				.apply(BaseDataBaseEntryUtils.EMPTY_ARRAY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> T instance(final T data) {
		Objects.requireNonNull(data, "Entry is null.");
		return this.<T>instance((Class<T>) data.getClass());
	}

	public DataBaseEntryUtils loadTypes(final ColumnTypeRegistry registry) {
		if (registry == null) {
			return this;
		}
		this.columnTypeFactories.clear();
		registry.registerTypes(this.columnTypeFactories);
		return this;
	}

	@Override
	public <T extends DataBaseEntry> void prepareDeleteSQL(final PreparedStatement stmt, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<T> entryClazz = (Class<T>) data.getClass();

		int index = 1;
		try {
			for (final ColumnData columnData : this.getPrimaryKeys(entryClazz)) {
				final Field field = this.getFieldFor(entryClazz, columnData);

				field.setAccessible(true);
				final Object value = field.get(data);

				final ColumnType type = this.getTypeFor(field);
				type.store(stmt, index++, value);
			}
		} catch (final IllegalAccessException e) {
			throw new DBException("Failed to access field value", e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void prepareInsertSQL(final PreparedStatement stmt, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<T> entryClazz = (Class<T>) data.getClass();

		int index = 1;
		for (final ColumnData columnData : this.getInsertColumns(entryClazz)) {
			final Field field = this.getFieldFor(entryClazz, columnData);
			field.setAccessible(true);

			try {
				final Object value = field.get(data);

				if (value == null && field.isAnnotationPresent(DefaultValue.class)) {
					continue;
				}
			} catch (final IllegalAccessException e) {
				throw new DBException("Failed to access field value for field: " + field.getName(), e);
			}

			try {
				final Object value = field.get(data);
				final ColumnType type = this.getTypeFor(field);

				type.store(stmt, index, value);
				index++;
			} catch (final IllegalAccessException e) {
				throw new DBException("Failed to access field value", e);
			} catch (final Exception e) {
				throw new DBException("Failed to store field value (" + field + ")", e);
			}
		}
	}

	@Override
	public <T extends DataBaseEntry> void
			prepareSelectCountNotNullSQL(final PreparedStatement stmt, final String[] notNullKeys, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		if (notNullKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		final Class<T> entryClazz = (Class<T>) data.getClass();

		try {
			int index = 1;
			for (final String column : notNullKeys) {
				final Field field = this.getFieldFor(entryClazz, column);
				field.setAccessible(true);

				final ColumnType type = this.getTypeFor(field);
				type.store(stmt, index++, field.get(data));
			}
		} catch (final IllegalAccessException e) {
			throw new DBException(e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void
			prepareSelectCountUniqueSQL(final PreparedStatement stmt, final String[][] uniqueKeys, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		final Class<? extends DataBaseEntry> entryClazz = data.getClass();

		try {
			int index = 1;
			for (final String[] list : uniqueKeys) {
				for (final String column : list) {
					final Field field = this.getFieldFor(entryClazz, column);
					field.setAccessible(true);

					final ColumnType type = this.getTypeFor(field);
					type.store(stmt, index++, field.get(data));
				}
			}
		} catch (final IllegalAccessException e) {
			throw new DBException(e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void prepareSelectSQL(final PreparedStatement stmt, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<T> entryClazz = (Class<T>) data.getClass();

		int index = 1;
		try {
			for (final ColumnData columnData : this.getPrimaryKeys(entryClazz)) {
				final Field field = this.getFieldFor(entryClazz, columnData);

				field.setAccessible(true);
				final Object value = field.get(data);

				final ColumnType type = this.getTypeFor(field);
				type.store(stmt, index++, value);
			}
		} catch (final IllegalAccessException e) {
			throw new DBException("Failed to access field value", e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void prepareSelectUniqueSQL(final PreparedStatement stmt, final String[][] uniqueKeys, final T data)
			throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		final Class<T> entryClazz = (Class<T>) data.getClass();

		try {
			int index = 1;
			for (final String[] list : uniqueKeys) {
				for (final String column : list) {
					final Field field = this.getFieldFor(entryClazz, column);
					field.setAccessible(true);

					final ColumnType type = this.getTypeFor(field);
					type.store(stmt, index++, field.get(data));
				}
			}
		} catch (final IllegalAccessException e) {
			throw new DBException(e);
		}
	}

	@Override
	public <T extends DataBaseEntry> void prepareUpdateSQL(final PreparedStatement stmt, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "PreparedStatement is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<T> entryClazz = (Class<T>) data.getClass();

		int index = 1;
		try {
			for (final ColumnData columnData : this.getUpdateColumns(entryClazz)) {
				final Field field = this.getFieldFor(entryClazz, columnData);
				field.setAccessible(true);

				final Object value = field.get(data);
				final ColumnType type = this.getTypeFor(field);

				type.store(stmt, index++, value);
			}

			for (final ColumnData columnData : this.getPrimaryKeys(entryClazz)) {
				final Field field = this.getFieldFor(entryClazz, columnData);

				field.setAccessible(true);
				final Object value = field.get(data);
				final ColumnType type = this.getTypeFor(field);

				type.store(stmt, index++, value);
			}
		} catch (final IllegalAccessException e) {
			throw new DBException("Failed to access field value", e);
		}
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String replaceQualifiers(final String input, final B instance) {
		final Pattern pattern = Pattern.compile("\\{([^}]+)}");

		final Matcher matcher = pattern.matcher(input);
		final StringBuilder result = new StringBuilder();

		while (matcher.find()) {
			final String token = matcher.group(1);

			String replacement = matcher.group(0);

			if (Query.TABLE_NAME_KEY.equals(token)) {
				replacement = this.structureVisitor.qualifiedName(instance);
			} else if (token.startsWith(Query.QUALIFIER_KEY)) {
				final String value = token.substring(Query.QUALIFIER_KEY.length());
				replacement = this.structureVisitor.qualifiedName(value);
			} else if (token.startsWith(Query.FUNCTION_KEY)) {
				final String value = token.substring(Query.FUNCTION_KEY.length());
				replacement = this.functionResolver.apply(value);
			}

			matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
		}

		matcher.appendTail(result);
		return result.toString();
	}

	@Override
	public DataBaseStructure scanDataBase(final DataBase dataBase, final Map<String, Object> baseHints) {
		return new DataBaseStructure(dataBase.getDataBaseName(), this.getQueryableHints(dataBase.getClass(), baseHints));
	}

	@Override
	public <T extends DataBaseEntry> TableStructure
			scanEntry(final Class<? extends AbstractDBTable<T>> tableClazz, final Class<T> entryClazz) {
		if (this.tableStructureCache.containsKey(tableClazz)) {
			return this.tableStructureCache.get(tableClazz);
		}

		final List<ConstraintData> constraints = new LinkedList<>();
		final Set<String> primaryKeys = new LinkedHashSet<>();
		final Map<Integer, Set<String>> uniqueGroups = new LinkedHashMap<>();
		final Set<Pair<String, Check>> checks = new HashSet<>();
		final Map<Class<? extends SQLQueryable<?>>, Map<ColumnData, ForeignKey>> foreignKeys = new LinkedHashMap<>();

		final List<ColumnData> columns = Arrays.asList(this.getColumnsFor(entryClazz));

		for (final ColumnData columnData : columns) {
			final String columnName = columnData.getName();
			final Field field = this.getFieldFor(entryClazz, columnData);

			// PRIMARY KEY
			if (columnData.isPrimaryKey()) {
				primaryKeys.add(columnName);
			}

			// UNIQUE
			if (columnData.isUnique()) {
				for (final Unique unique : field.getAnnotationsByType(Unique.class)) {
					final int group = unique.value();
					uniqueGroups.computeIfAbsent(group, k -> new LinkedHashSet<>()).add(columnName);
				}
			}

			// FOREIGN KEY
			if (columnData.isForeignKey()) {
				final ForeignKey fk = field.getAnnotation(ForeignKey.class);
				foreignKeys.computeIfAbsent(fk.table(), k -> new LinkedHashMap<>()).put(columnData, fk);
			}

			// CHECK
			if (field.isAnnotationPresent(Check.class) || field.isAnnotationPresent(Checks.class)) {
				final Check[] check = field.getAnnotationsByType(Check.class);
				Arrays.stream(check).forEach(c -> checks.add(Pairs.readOnly(columnName, c)));
			}
		}

		final Map<String, Object> tableHints = this.getQueryableHints(tableClazz);

		final TableStructure ts = new TableStructure(this.getQueryableName(tableClazz), tableClazz, entryClazz, tableHints);
		ts.setColumns(columns.toArray(new ColumnData[0]));

		// CONSTRAINTS
		if (!primaryKeys.isEmpty()) {
			constraints.add(new PrimaryKeyData(ts, primaryKeys.toArray(new String[0])));
		}

		for (final Set<String> groupCols : uniqueGroups.values()) {
			constraints.add(new UniqueData(ts, groupCols.toArray(new String[0])));
		}

		// CHECK ON ENTRY
		if (entryClazz.isAnnotationPresent(Check.class) || entryClazz.isAnnotationPresent(Checks.class)) {
			final Check[] check = entryClazz.getAnnotationsByType(Check.class);
			Arrays.stream(check).forEach(c -> checks.add(Pairs.readOnly(null, c)));
		}

		// CHECK ON TABLE
		if (tableClazz.isAnnotationPresent(Check.class) || tableClazz.isAnnotationPresent(Checks.class)) {
			final Check[] check = tableClazz.getAnnotationsByType(Check.class);
			Arrays.stream(check).forEach(c -> checks.add(Pairs.readOnly(null, c)));
		}

		for (final Pair<String, Check> pair : checks) {
			final Check check = pair.getValue();
			if (check.value().contains(Check.FIELD_NAME_PLACEHOLDER)) {
				throw new DBException("Invalid '" + Check.FIELD_NAME_PLACEHOLDER + "' in: " + check + " on class: " + entryClazz);
			}
			final String expr = pair.getValue()
					.value()
					.replace(Check.FIELD_NAME_PLACEHOLDER, pair.getKey())
					.replace(Check.TABLE_NAME_PLACEHOLDER, ts.getName());
			if (check.name() != null && !check.name().trim().isEmpty()) {
				constraints.add(new CheckData(check.name(), expr));
			} else {
				constraints.add(new CheckData(ts, expr));
			}
		}

		// we go through the foreign keys and group them by referenced table
		for (final Map.Entry<Class<? extends SQLQueryable<?>>, Map<ColumnData, ForeignKey>> entry : foreignKeys.entrySet()) {
			final Class<? extends SQLQueryable<? extends DataBaseEntry>> foreignQueryable = entry.getKey();
			final String refTableName = this.getQueryableName((Class) foreignQueryable);
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

		this.tableStructureCache.put(tableClazz, ts);

		return ts;
	}

	@Override
	public <T extends DataBaseEntry> TableStructure scanTable(final Class<? extends AbstractDBTable<T>> tableClazz) {
		return this.scanEntry(tableClazz, this.getEntryType(tableClazz));
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

		final List<Field> sorted = new ArrayList<>(pkFields);
		sorted.addAll(otherFields);
		sorted.addAll(fkFields);

		return sorted;
	}

	protected <T extends DataBaseEntry> Map<String, ColumnData> computeColumnNames(final Class<T> entryClazz) {
		final Map<String, ColumnData> columns = new HashMap<>();
		for (final ColumnData cd : this.getColumnsFor(entryClazz)) {
			columns.put(cd.getName(), cd);
		}
		return Collections.unmodifiableMap(columns);
	}

	protected <T extends DataBaseEntry> ColumnData[] computeColumnsFor(final Class<T> entryClazz) {
		final List<ColumnData> columns = new ArrayList<>();

		for (final Field field : this.sortFields(PCUtils.getAllFields(entryClazz))) {
			field.setAccessible(true);

			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}

			final String columnName = this.fieldToColumnName(field);
			final Map<String, Object> typeHints = this.getTypeHints(field.getAnnotatedType());
			final ColumnType columnType = this.getTypeFor(field.getAnnotatedType(), typeHints);

			ColumnData columnData = new ColumnData();
			columnData.setField(Optional.of(field));
			columnData.setName(columnName);
			columnData.setTypeHints(typeHints);
			columnData.setType(columnType);

			if (field.isAnnotationPresent(AutoIncrement.class)) {
				columnData.setAutoIncrement(true);
			}

			final Optional<Annotation> nullable = Arrays.stream(field.getAnnotations())
					.filter(c -> "Nullable".equals(c.annotationType().getSimpleName()))
					.findAny();
			final Optional<Annotation> notnull = Arrays.stream(field.getAnnotations())
					.filter(c -> "NotNull".equals(c.annotationType().getSimpleName())
							|| "NonNull".equals(c.annotationType().getSimpleName()))
					.findAny();
			if (nullable.isPresent() && nullable.get() instanceof Nullable) {
				columnData.setNullable(((Nullable) nullable.get()).value());
			} else if (notnull.isPresent()) {
				columnData.setNullable(false);
			} else {
				columnData.setNullable(false); // Default to NOT NULL if not specified
			}

			if (columnData.isNullable() && field.getType().isPrimitive()) {
				throw new DBException("Column: '" + columnName + "' defined by " + field + " is a nullable of primitive type.");
			}

			final String defaultValue = this.computeDefaultValue(field);
			if (defaultValue == null && !columnData.isNullable() && this.isForceDefaultValueOnNonNull()) {
				throw new DBException("Column: '" + columnName + "' defined by " + field
						+ " isn't nullable and defines no default value for '" + this.dbmsQualifierName + "'.\n"
						+ "Add @DefaultValue(DefaultValue.I_KNOW) to disable this error locally or set the option '"
						+ DataBaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY
						+ "' to false to disable this check globally, you'll need to make sure that this field actually has a value on insertion/update.");
			} else if (DefaultValue.I_KNOW.equals(defaultValue)) {
				columnData.setDefaultValue(null);
			} else if (defaultValue != null) {
				columnData.setDefaultValue(defaultValue);
			}

			if (field.isAnnotationPresent(OnUpdate.class)) {
				columnData.setOnUpdate(field.getAnnotation(OnUpdate.class).value());
			}

			// PRIMARY KEY
			columnData.setPrimaryKey(field.isAnnotationPresent(PrimaryKey.class));

			// UNIQUE
			columnData.setUnique(field.isAnnotationPresent(Unique.class) || field.isAnnotationPresent(Uniques.class));

			// FOREIGN KEY
			columnData.setForeignKey(field.isAnnotationPresent(ForeignKey.class));

			// GENERATED
			if (field.isAnnotationPresent(Generated.class)) {
				final Generated gen = field.getAnnotation(Generated.class);

				columnData = new GeneratedColumnData(columnData, gen);
			}

			columns.add(columnData);
		}

		return columns.toArray(new ColumnData[0]);
	}

	protected String computeDefaultValue(final Field field) {
		final List<ReadOnlyPair<DefaultValue, Annotation>> defaultValues = new ArrayList<>();
		Arrays.stream(field.getAnnotationsByType(DefaultValue.class))
				.map(defaultValue -> Pairs.<DefaultValue, Annotation>readOnly(defaultValue, null))
				.forEach(defaultValues::add);
		for (final Annotation annotation : field.getAnnotations()) {
			final Class<? extends Annotation> annotationClazz = annotation.annotationType();
			if (!annotationClazz.isAnnotationPresent(DefaultValue.class) && !annotationClazz.isAnnotationPresent(DefaultValues.class)) {
				continue;
			}
			Arrays.stream(annotationClazz.getAnnotationsByType(DefaultValue.class))
					.map(defaultValue -> Pairs.readOnly(defaultValue, annotation))
					.forEach(defaultValues::add);
		}

		if (defaultValues.size() == 0) {
			return null;
		}

		final List<ReadOnlyPair<DefaultValue, Annotation>> candidates = defaultValues.stream()
				.filter(c -> c.getKey().dbms().trim().isEmpty() || this.matchesDbmsQualifier(c.getKey().dbms()))
				.sorted(Comparator.comparing((final ReadOnlyPair<DefaultValue, Annotation> e) -> e.getValue() != null)
						.thenComparing(e -> e.getKey().dbms().trim().isBlank()))
				.toList();

		if (candidates.size() == 0) {
			throw new DBException("Found " + defaultValues.size() + " @DefaultValue on " + field + " but none matched '"
					+ this.dbmsQualifierName + "'.\nIf this is intended, add @DefaultValue(DefaultValue.NONE) as catch-all.");
		}

		final List<ReadOnlyPair<DefaultValue, Annotation>> specificCandidates = candidates.stream()
				.filter(c -> !c.getKey().dbms().trim().isEmpty())
				.toList();

		if (specificCandidates.size() > 1) {
			final List<ReadOnlyPair<DefaultValue, Annotation>> localSpecificCandidates = specificCandidates.stream()
					.filter(c -> !c.hasValue())
					.toList();
			if (localSpecificCandidates.size() == 1) {
				final String val = localSpecificCandidates.get(0).getKey().value();
				return DefaultValue.NONE.equals(val) ? null : val;
			}
			throw new DBException("Found " + specificCandidates.size() + " specific candidates @DefaultValue on " + field
					+ " that matched '" + this.dbmsQualifierName + "'. Defined:\n"
					+ defaultValues.stream()
							.map(c -> (c.getKey().dbms().trim().isEmpty() ? "[ALL]" : c.getKey().dbms().trim()) + ": " + c.getKey().value()
									+ (c.getValue() != null ? " from: " + c.getValue() : ""))
							.collect(Collectors.joining("\n")));
		} else if (specificCandidates.size() == 1) {
			final String val = specificCandidates.get(0).getKey().value();
			return DefaultValue.NONE.equals(val) ? null : val;
		}

		if (candidates.size() > 1) {
			throw new DBException("Found " + candidates.size() + " candidates @DefaultValue on " + field + " that matched '"
					+ this.dbmsQualifierName + "'. Defined:\n"
					+ defaultValues.stream()
							.map(c -> (c.getKey().dbms().trim().isEmpty() ? "[ALL]" : c.getKey().dbms().trim()) + ": " + c.getKey().value()
									+ (c.getValue() != null ? " from: " + c.getValue() : ""))
							.collect(Collectors.joining("\n")));
		}

		final String val = candidates.get(0).getKey().value();
		return DefaultValue.NONE.equals(val) ? null : val;
	}

	protected Method computeFactoryMethod(final Class<?> clazz) {
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

	protected String computeFieldToColumnName(final Field field) {
		if (!field.isAnnotationPresent(Column.class)) {
			throw new IllegalArgumentException("Field " + field.getName() + " is not annotated with @Column");
		}
		final Column colAnno = field.getAnnotation(Column.class);
		return colAnno.name().isEmpty() ? this.fieldToColumnName(field.getName()) : colAnno.name();

	}

	protected ColumnData[] computeGeneratedKeys(final Class<? extends DataBaseEntry> entryClazz) {
		Objects.requireNonNull(entryClazz, "entry class is null");

		final List<ColumnData> generatedKeys = new ArrayList<>();

		for (final ColumnData columnData : this.getColumnsFor(entryClazz)) {
			if (columnData.isAutoIncrement() || columnData.hasDefaultValue() && columnData.isPrimaryKey()) {
				generatedKeys.add(columnData);
			}
		}
		return generatedKeys.toArray(new ColumnData[0]);
	}

	protected <T extends DataBaseEntry>
			Map<Set<String>, ReadOnlyPair<List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>>, ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>>>
			computeInstanceFactories(final Class<T> entryClazz) {
		final Map<Set<String>, ReadOnlyPair<List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>>, ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>>> factories = new HashMap<>();

		for (final Constructor<?> constructor : entryClazz.getConstructors()) {
			final Set<String> args = new HashSet<>(constructor.getParameterCount());
			final List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>> mapping = new ArrayList<>(constructor.getParameterCount());
			for (int i = 0; i < constructor.getParameterCount(); i++) {
				final Parameter p = constructor.getParameters()[i];
				final String name = this.parameterToColumnName(p);
				args.add(name);
				mapping.add(Quadruples.readOnly(name, this.getColumnFor(entryClazz, name), constructor.getGenericParameterTypes()[i], i));
			}
			constructor.setAccessible(true);

			factories.put(Collections.unmodifiableSet(args),
					Pairs.readOnly(Collections.unmodifiableList(mapping),
							(ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>) (final Object[] params) -> {
								try {
									return (DataBaseEntry) constructor.newInstance(params);
								} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
										| InvocationTargetException e) {
									throw new DBException(
											"Failed to instantiate " + entryClazz.getName() + " through constructor: " + constructor,
											e);
								}
							}));
		}

		for (final Method method : entryClazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Factory.class)) {
				if (!Modifier.isStatic(method.getModifiers())) {
					throw new IllegalArgumentException("Factory method not static: " + method);
				}
			} else {
				continue;
			}
			if (!method.getReturnType().equals(entryClazz)) {
				throw new IllegalArgumentException(
						"Factory method returns wrong type: " + entryClazz.getName() + " returns " + method.getReturnType().getName());
			}

			final Set<String> args = new HashSet<>(method.getParameterCount());
			final List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>> mapping = new ArrayList<>(method.getParameterCount());
			for (int i = 0; i < method.getParameterCount(); i++) {
				final Parameter p = method.getParameters()[i];
				final String name = this.parameterToColumnName(p);
				args.add(name);
				mapping.add(Quadruples.readOnly(name, this.getColumnFor(entryClazz, name), method.getGenericParameterTypes()[i], i));
			}
			method.setAccessible(true);

			if (factories.containsKey(args)) {
				if (this.isFailOnDuplicateFactoryMethod()) {
					throw new DBException("Method with parameters: " + args + " registered at least twice on: " + entryClazz);
				} else if (this.isWarnOnDuplicateFactoryMethod()) {
					System.out.println("Method with parameters: " + args + " registered at least twice on: " + entryClazz);
				}
				// prefer constructor instead of factory
				continue;
			}
			factories.put(Collections.unmodifiableSet(args),
					Pairs.readOnly(Collections.unmodifiableList(mapping),
							(ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>) (final Object[] params) -> {
								try {
									return (DataBaseEntry) method.invoke(null, params);
								} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
									throw new DBException(
											"Failed to instantiate " + entryClazz.getName() + " through factory method: " + method,
											e);
								}
							}));
		}

		return Collections.unmodifiableMap(factories);
	}

	protected <T> ThrowingSupplier<T, DBException> computeInstanceMethod(final Class<T> clazz) {
		final Method factoryMethod = this.computeFactoryMethod(clazz);
		if (factoryMethod != null) {
			factoryMethod.setAccessible(true);
			return () -> {
				try {
					return (T) factoryMethod.invoke(null);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new DBException("Failed to instantiate " + clazz.getName() + " through factory method: " + factoryMethod, e);
				}
			};
		} else {
			try {
				final Constructor<T> constructor = clazz.getDeclaredConstructor();
				constructor.setAccessible(true);
				return () -> {
					try {
						return constructor.newInstance();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new DBException("Failed to instantiate " + clazz.getName() + " through no-arg constructor: " + constructor,
								e);
					}
				};
			} catch (final NoSuchMethodException e) {
				throw new DBException("No empty constructor nor factory method found " + clazz.getName(), e);
			}
		}
	}

	protected <T extends DataBaseEntry> ColumnData[] computeNonNullColumns(final Class<T> ec) {
		return Arrays.stream(this.getColumnsFor(ec))
				.filter(c -> !c.hasOnUpdate())
				.filter(c -> !c.isPrimaryKey())
				.filter(c -> !c.isGenerated())
				.toArray(ColumnData[]::new);
	}

	protected <B extends AbstractDBTable<T>, T extends DataBaseEntry> String
			computePreparedDeleteSql(final B table, final Class<T> entryClazz) {
		final String[] pkNames = this.getPrimaryKeysNames(entryClazz);
		if (pkNames.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + entryClazz.getSimpleName());
		}

		return this.structureVisitor.safeDelete(table, pkNames);
	}

	protected <B extends AbstractDBTable<T>, T extends DataBaseEntry> String
			computePreparedUpdateSQL(final B table, final Class<T> entryClazz) {
		final String[] setColumns = this.getUpdateColumnsNames(entryClazz);
		if (setColumns.length == 0) {
			throw new IllegalArgumentException("No columns to update.");
		}

		final String[] whereColumns = this.getPrimaryKeysNames(entryClazz);
		if (whereColumns.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + entryClazz.getSimpleName());
		}

		return this.structureVisitor.safeUpdate(table, setColumns, whereColumns);
	}

	protected <T extends DataBaseEntry> String[] computePrimaryKeyNames(final Class<T> ec) {
		return Arrays.stream(this.getPrimaryKeys(ec)).map(ColumnData::getName).toArray(String[]::new);
	}

	protected <T extends DataBaseEntry> ColumnData[] computePrimaryKeys(final Class<T> entryClazz) {
		return Arrays.stream(this.getColumnsFor(entryClazz))
				.filter(ColumnData::isPrimaryKey)
				.collect(Collectors.toList())
				.toArray(new ColumnData[0]);
	}

	protected Map<String, Object> computeQueryableHints(final Class<?> tableClazz) {
		final Map<String, Object> map = new HashMap<>();
		Arrays.stream(tableClazz.getAnnotationsByType(QueryableHint.class))
				.filter(tableHint -> this.matchesDbmsQualifier(tableHint.dbms()))
				.forEach(tableHint -> map.put(tableHint.type(), tableHint.value()));

		for (final Annotation a : tableClazz.getAnnotations()) {
			final Class<? extends Annotation> annotationClass = a.annotationType();
			for (final Method method : annotationClass.getMethods()) {
				final QueryableHint[] tableHints = PCUtils.combineArrays(method.getAnnotationsByType(QueryableHint.class),
						method.getAnnotatedReturnType().getAnnotationsByType(QueryableHint.class));

				if (tableHints != null && tableHints.length != 0) {
					try {
						final Object value = method.invoke(a);
						Arrays.stream(tableHints)
								.filter(typeHint -> this.matchesDbmsQualifier(typeHint.dbms()))
								.forEach(typeHint -> map.put(typeHint.type(), value));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new DBException("Couldn't retrieve type hint value for: " + method + " with " + Arrays.toString(tableHints),
								e);
					}
				}
			}

		}

		return map;
	}

	protected Map<String, Object> computeTypeHints(final AnnotatedType annotatedType) {
		final Map<String, Object> map = new HashMap<>();

		Arrays.stream(annotatedType.getAnnotationsByType(TypeHint.class))
				.filter(typeHint -> this.matchesDbmsQualifier(typeHint.dbms()))
				.forEach(typeHint -> map.put(typeHint.type(), typeHint.value()));

		for (final Annotation a : annotatedType.getAnnotations()) {

			final Class<? extends Annotation> annotationClass = a.annotationType();
			for (final Method method : annotationClass.getMethods()) {
				final TypeHint[] typeHints = PCUtils.combineArrays(method.getAnnotationsByType(TypeHint.class),
						method.getAnnotatedReturnType().getAnnotationsByType(TypeHint.class));
				if (typeHints != null && typeHints.length != 0) {
					try {
						final Object value = method.invoke(a);
						Arrays.stream(typeHints)
								.filter(typeHint -> this.matchesDbmsQualifier(typeHint.dbms()))
								.forEach(typeHint -> map.put(typeHint.type(), value));
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						throw new DBException("Couldn't retrieve type hint value for: " + method + " with " + Arrays.toString(typeHints),
								e);
					}
				}
			}

		}

		return map;
	}

	protected <T extends DataBaseEntry> String[] computeUpdateColumnsNames(final Class<T> ec) {
		return Arrays.stream(this.getUpdateColumns(ec)).map(ColumnData::getName).toArray(String[]::new);
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

	protected Field[] getAllFields(final Class<?> type) {
		final List<Field> fields = new ArrayList<>();
		for (Class<?> c = type; c != null; c = c.getSuperclass()) {
			fields.addAll(Arrays.asList(c.getDeclaredFields()));
		}
		return fields.toArray(new Field[fields.size()]);
	}

	protected <T extends DataBaseEntry> ColumnData[] getInsertColumns(final Class<T> entryClazz) {
		return this.insertColumnsCache.computeIfAbsent(entryClazz, this::computeInsertColumns);
	}

	/**
	 * NOT OnUpdate, NOT PK, NOT Generated
	 */
	protected <T extends DataBaseEntry> ColumnData[] getNonNullColumns(final Class<T> entryClazz) {
		return this.nonNullColumnsCache.computeIfAbsent(entryClazz, this::computeNonNullColumns);
	}

	protected <T extends DataBaseEntry> ColumnData[] getUpdateColumns(final Class<T> entryClazz) {
		return this.updateColumnsCache.computeIfAbsent(entryClazz, this::computeUpdateColumns);
	}

	protected final String globToRegex(final String trim) {
		return trim.replace(".", "\\.").replace("?", ".").replace("*", ".*");
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

	protected boolean matchesDbmsQualifier(final String dbms) {
		final String trimmed = dbms.trim();
		if (trimmed.isEmpty()) {
			return true;
		}
		return this.dbmsQualifierName.matches(this.globToRegex(trimmed));
	}

	protected String parameterToColumnName(final Parameter p) {
		if (!p.isAnnotationPresent(Column.class)) {
			if (p.isNamePresent()) {
				return this.fieldToColumnName(p.getName());
			} else {
				throw new DBException("No name present on: " + p + ", add @Column or keep parameter names during compilation.");
			}
		} else {
			final Column colAnno = p.getAnnotation(Column.class);
			if (colAnno.name().isEmpty()) {
				if (p.isNamePresent()) {
					return this.fieldToColumnName(p.getName());
				} else {
					throw new DBException("No name present on: " + p + ", add @Column or keep parameter names during compilation.");
				}
			} else {
				return colAnno.name();
			}
		}
	}

	private ColumnData[] computeInsertColumns(final Class<? extends DataBaseEntry> ec) {
		return Arrays.stream(this.getColumnsFor(ec))
				.filter(c -> !c.isGenerated())
				.filter(c -> !c.isAutoIncrement())
				.toArray(ColumnData[]::new);
	}

	private ColumnData[] computeUpdateColumns(final Class<? extends DataBaseEntry> ec) {
		return Arrays.stream(this.getColumnsFor(ec))
				.filter(c -> !c.isGenerated())
				.filter(c -> !c.isAutoIncrement())
				.filter(c -> !c.hasOnUpdate())
				.toArray(ColumnData[]::new);
	}

	private Map<String, Object> getQueryableHints(final Class<?> tableClazz, final Map<String, Object> baseHints) {
		final Map<String, Object> map = new HashMap<>(this.getQueryableHints(tableClazz));
		map.putAll(baseHints);
		return Collections.unmodifiableMap(map);
	}

}
