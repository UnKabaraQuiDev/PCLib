package lu.kbra.pclib.db.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.tuple.Pair;
import lu.kbra.pclib.datastructure.tuple.Pairs;
import lu.kbra.pclib.datastructure.tuple.Quadruples;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyPair;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyQuadruple;
import lu.kbra.pclib.datastructure.tuple.Triplet;
import lu.kbra.pclib.datastructure.tuple.Triplets;
import lu.kbra.pclib.db.annotations.entry.Check;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ColumnHint;
import lu.kbra.pclib.db.annotations.entry.DbmsFilter;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.Factory;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Generated;
import lu.kbra.pclib.db.annotations.entry.Grouped;
import lu.kbra.pclib.db.annotations.entry.Insert;
import lu.kbra.pclib.db.annotations.entry.Load;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.RepeatableHint;
import lu.kbra.pclib.db.annotations.entry.TypeHint;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.Update;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.queryable.QueryableHint;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.dbms.DbmsProviders;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.GeneratedColumnData;
import lu.kbra.pclib.db.domain.column.meta.DefaultColumnHints;
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
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.impl.function.ThrowingFunction;

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
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, ColumnData[]> columnsCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, ColumnData[]> primaryKeysCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, String[]> primaryKeysNamesCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, ColumnData[]> generatedKeysCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, String[]> generatedColumnNamesCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, String[]> updateColumnsNamesCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, String[]> updateGeneratedColumnsNamesCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, ColumnData[]> nonNullColumnsCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, ColumnData[]> insertColumnsCache = new ConcurrentHashMap<>();
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, ColumnData[]> updateColumnsCache = new ConcurrentHashMap<>();
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
	protected final Map<ReadOnlyPair<Class<? extends DataBaseEntry>, Class<? extends SQLQueryable<? extends DataBaseEntry>>>, Map<String, ColumnData>> columnsNamesCache = new ConcurrentHashMap<>();
	protected final Map<String, Class<? extends SQLQueryable<? extends DataBaseEntry>>> simpleNameCache = new ConcurrentHashMap<>();

	protected List<SQLQueryable<? extends DataBaseEntry>> forScan = new ArrayList<>();

	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void registerScan(final B instance) {
		this.forScan.add(instance);
	}

	public void doScan() {
		this.scanSelfStructure();
		this.scanLinks();
	}

	@Override
	public TableStructure getTableStructure(Class<? extends SQLQueryable<?>> clazz) {
		return tableStructureCache.get(clazz);
	}

	@SuppressWarnings("unchecked")
	protected void scanLinks() {
		for (final SQLQueryable<?> instance : this.forScan) {
			final Class<? extends SQLQueryable<?>> tableClazz = instance.getTargetClass();
			if (!AbstractDBTable.class.isAssignableFrom(tableClazz)) {
				continue;
			}

			final TableStructure tableStructure = ((AbstractDBTable<?>) instance).getTableStructure();
			final Class<? extends DataBaseEntry> entryClazz = tableStructure.getEntryClass();

			final List<ConstraintData> constraints = new LinkedList<>();
			final Set<String> primaryKeys = new LinkedHashSet<>();
			final Map<Integer, Set<String>> uniqueGroups = new LinkedHashMap<>();
			final Set<Triplet<String, String, String>> checks = new HashSet<>();
			final Map<Class<? extends SQLQueryable<?>>, Map<Integer, Set<ColumnData>>> foreignKeys = new LinkedHashMap<>();

			for (final ColumnData columnData : tableStructure.getColumns()) {
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
					foreignKeys.computeIfAbsent(columnData.getHint(DefaultColumnHints.FOREIGN_KEY_TABLE), k -> new LinkedHashMap<>())
							.computeIfAbsent(columnData.getHint(DefaultColumnHints.FOREIGN_KEY_GROUP_ID), k -> new LinkedHashSet<>())
							.add(columnData);
				}

				// CHECK
				if (columnData.hasHint(DefaultColumnHints.CHECK)) {
					for (final Map<String, Object> forEach : columnData.<List<Map<String, Object>>>getHint(DefaultColumnHints.CHECK)) {
						checks.add(Triplets.readOnly(columnName,
								(String) forEach.get(DefaultColumnHints.CHECK_NAME),
								(String) forEach.get(DefaultColumnHints.CHECK_VALUE)));
					}
				}
			}

			// CONSTRAINTS
			if (!primaryKeys.isEmpty()) {
				constraints.add(new PrimaryKeyData(tableStructure, primaryKeys.toArray(new String[0])));
			}

			for (final Set<String> groupCols : uniqueGroups.values()) {
				constraints.add(new UniqueData(tableStructure, groupCols.toArray(new String[0])));
			}

			// CHECK ON ENTRY
			final Map<String, Object> entryHints = tableStructure.getEntryHints();
			if (entryHints.containsKey(DefaultColumnHints.CHECK)) {
				for (final Map<String, Object> forEach : (List<Map<String, Object>>) entryHints.get(DefaultColumnHints.CHECK)) {
					checks.add(Triplets.readOnly(null,
							(String) forEach.get(DefaultColumnHints.CHECK_NAME),
							(String) forEach.get(DefaultColumnHints.CHECK_VALUE)));
				}
			}

			// CHECK ON TABLE
			if (tableStructure.hasHint(DefaultColumnHints.CHECK)) {
				for (final Map<String, Object> forEach : tableStructure.<List<Map<String, Object>>>getHint(DefaultColumnHints.CHECK)) {
					checks.add(Triplets.readOnly(null,
							(String) forEach.get(DefaultColumnHints.CHECK_NAME),
							(String) forEach.get(DefaultColumnHints.CHECK_VALUE)));
				}
			}

			final Map<String, String> map = new HashMap<>();
			map.put(DataBaseEntryUtils.TABLE_NAME_KEY, this.structureVisitor.qualifiedName(this.getQueryableName(tableClazz)));
			for (final Triplet<String, String, String> pair : checks) {
				final String columnName = pair.getFirst();
				final String name = pair.getSecond();
				String expr = pair.getThird();
				if (expr.contains(Check.FIELD_NAME)) {
					throw new DBException(
							"Invalid '" + Check.FIELD_NAME + "' in check '" + name + "': " + expr + " on class: " + entryClazz);
				}
				if (columnName != null) {
					map.put(DataBaseEntryUtils.FIELD_NAME_KEY, this.structureVisitor.qualifiedName(name));
				} else {
					map.remove(DataBaseEntryUtils.FIELD_NAME_KEY);
				}
				expr = this.replaceSQLQualifiers(tableClazz, expr, map);
				if (name != null && !name.trim().isEmpty()) {
					constraints.add(new CheckData(name, expr));
				} else {
					constraints.add(new CheckData(tableStructure, expr));
				}
			}

			// TODO: scan all tables first, then second pass create the links, then actually generate everything
			// we go through the foreign keys and group them by referenced table
			for (final Map.Entry<Class<? extends SQLQueryable<?>>, Map<Integer, Set<ColumnData>>> entry : foreignKeys.entrySet()) {
				final Class<? extends SQLQueryable<? extends DataBaseEntry>> foreignQueryable = entry.getKey();
				final Class<? extends DataBaseEntry> foreignEntryClazz = this.getEntryType(foreignQueryable);
				final String refTableName = this.getQueryableName(foreignQueryable);
				final Map<Integer, Set<ColumnData>> grouped = entry.getValue();

				for (final Set<ColumnData> group : grouped.values()) {
					final String[] colNames = group.stream().map(ColumnData::getName).toArray(String[]::new);
					final String[] refCols = group.stream().map(this::getReferencedColumnName).toArray(String[]::new);

					if (PCUtils.duplicates(refCols)) {
						throw new IllegalArgumentException(
								"Foreign key references duplicate columns: " + String.join(", ", refCols) + " to table: " + refTableName);
					}

					constraints.add(new ForeignKeyData(tableStructure, colNames, refTableName, refCols));
				}

				final ColumnData[] pks = this.getPrimaryKeys((Class) foreignEntryClazz, (Class) foreignQueryable);
				for (final Map.Entry<Integer, Set<ColumnData>> group : grouped.entrySet()) {
					if (pks.length != group.getValue().size()) {
						throw new IllegalArgumentException("Invalid number of foreign keys to table: " + refTableName + ". Expected "
								+ pks.length + " but got: " + group.getValue().size() + " for id: " + group.getKey());
					}
				}
			}

			tableStructure.setConstraints(constraints.toArray(new ConstraintData[0]));
		}
	}

	protected void scanSelfStructure() {
		for (final SQLQueryable<?> instance : this.forScan) {
			final Class<? extends SQLQueryable<?>> tableClazz = instance.getTargetClass();
			if (AbstractDBTable.class.isAssignableFrom(tableClazz)) {
				final TableStructure tableStructure = this.scanSelfTableStructure((AbstractDBTable<?>) instance,
						tableClazz.asSubclass(AbstractDBTable.class));
				((AbstractDBTable<?>) instance).setTableStructure(tableStructure);
			} else if (AbstractDBView.class.isAssignableFrom(tableClazz)) {
				this.scanSelfViewStructure((AbstractDBView<?>) instance, tableClazz.asSubclass(AbstractDBView.class));
			}
		}
	}

	protected <T extends DataBaseEntry> TableStructure
			scanSelfTableStructure(final AbstractDBTable<T> instance, final Class<? extends AbstractDBTable<T>> tableClazz) {
		final Class<T> entryClazz = this.getEntryType(tableClazz);
		final Map<String, Object> queryableHints = this.getQueryableHints(tableClazz);
		final String queryableName = this.getQueryableName(tableClazz);
		final Map<String, Object> entryHints = this.getQueryableHints(entryClazz);

		final TableStructure tableStructure = new TableStructure(queryableName, tableClazz, entryClazz, queryableHints, entryHints);

		final ColumnData[] columnDatas = this.computeColumnsFor(entryClazz, tableClazz);
		tableStructure.setColumns(columnDatas);

		if (queryableHints.containsKey(DefaultTableHints.DEFINED_NAME)) {
			this.simpleNameCache.put((String) queryableHints.get(DefaultTableHints.DEFINED_NAME), tableClazz);
		}
		this.simpleNameCache.put(tableClazz.getSimpleName(), tableClazz);

		return tableStructure;
	}

	protected <T extends DataBaseEntry> void
			scanSelfViewStructure(final AbstractDBView<T> instance, final Class<? extends AbstractDBView<T>> tableClazz) {
		final Class<? extends DataBaseEntry> entryClazz = this.getEntryType(tableClazz);
	}

	public BaseDataBaseEntryUtils(final ColumnTypeRegistry typeRegistry, final String protocolName) {
		Objects.requireNonNull(protocolName, "protocolName is null.");
		this.loadTypes(typeRegistry);
		this.dbmsQualifierName = protocolName;
		this.structureVisitor = SQLStructureVisitors.forProtocol(protocolName);
		this.functionResolver = SQLFunctionResolvers.forProtocol(protocolName);
	}

	public BaseDataBaseEntryUtils(
			final ColumnTypeRegistry typeRegistry,
			final String protocolName,
			final SQLStructureVisitor structureVisitor,
			final SQLFunctionResolver functionResolver) {
		Objects.requireNonNull(protocolName, "protocolName is null.");
		Objects.requireNonNull(structureVisitor, "structureVisitor is null.");
		Objects.requireNonNull(functionResolver, "functionResolver is null.");
		this.loadTypes(typeRegistry);
		this.dbmsQualifierName = protocolName;
		this.structureVisitor = structureVisitor;
		this.functionResolver = functionResolver;
	}

	public BaseDataBaseEntryUtils(final String protocolName) {
		this(DbmsProviders.columnTypeRegistryFor(protocolName), protocolName);
	}

	protected BaseDataBaseEntryUtils() {
	}

	public void appendTypes(final ColumnTypeRegistry addColumnTypeRegistry) {
		addColumnTypeRegistry.registerTypes(this.columnTypeFactories);
	}

	@Override
	public Stream<ColumnTypeFactory> computeType(final Class<?> rawType, final Map<String, Object> typeHints) {
		Objects.requireNonNull(rawType, "rawType is null.");
		Objects.requireNonNull(typeHints, "typeHints is null.");
		return this.columnTypeFactories.stream()
				.map(entry -> new Pair<>(entry.eval(rawType, typeHints), entry))
				.filter(entry -> !Objects.equals(entry.getKey(), ColumnTypeRegistry.EXCLUDE))
				.sorted(Comparator.comparingInt(e -> -e.getKey()))
				.map(Pair::getValue);
	}

	@Override
	public String fieldToColumnName(final Field field) {
		Objects.requireNonNull(field, "field is null.");
		return this.fieldToColumnNameCache.computeIfAbsent(field, this::computeFieldToColumnName);
	}

	@Override
	public String fieldToColumnName(final String name) {
		Objects.requireNonNull(name, "name is null.");
		return this.structureVisitor.fieldToColumnName(name);
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void fillInsert(final B table, final T data, final ResultSet rs)
			throws SQLException {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(rs, "rs is null.");

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) table.getTargetClass();

		final ColumnData[] primaryKeys = this.getPrimaryKeys(entryClazz, tableClazz);

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

			final Method insertMethod = this.getInsertMethod(entryClazz);
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

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void fillLoad(final B table, final T data, final ResultSet rs)
			throws SQLException {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(rs, "rs is null.");

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) table.getTargetClass();

		final ColumnData[] columns = this.getColumnsFor(entryClazz, tableClazz);

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

			final Method loadMethod = this.getLoadMethod(entryClazz);
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
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void
			fillLoadAll(final B table, final Class<T> entryClazz, final ResultSet rs, final Consumer<T> listExporter) throws SQLException {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(rs, "rs is null.");
		Objects.requireNonNull(listExporter, "listExporter is null.");

		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) table.getTargetClass();
		final Map<Set<String>, ReadOnlyPair<List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>>, ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>>> factories = this.argInstanceFactoryCache
				.computeIfAbsent(entryClazz, k -> this.computeInstanceFactories(entryClazz, tableClazz));

		final ResultSetMetaData resultMetaData = rs.getMetaData();
		final int columnCount = resultMetaData.getColumnCount();
		final String[] columns = new String[columnCount];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = resultMetaData.getColumnLabel(i + 1);
		}

		final Set<String> args = new HashSet<>(Arrays.asList(columns));
		if (factories.containsKey(args)) {

			final ReadOnlyPair<List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>>, ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>> pair = factories
					.get(args);
			while (rs.next()) {
				final T copy = this.fillLoad(entryClazz, rs, pair.getKey(), pair.getValue());
				listExporter.accept(copy);
			}

		} else {

			while (rs.next()) {
				final T copy = this.instance(entryClazz, tableClazz);
				this.fillLoad(table, copy, rs);
				listExporter.accept(copy);
			}

		}
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void fillUpdate(final B table, final T data, final ResultSet rs)
			throws SQLException {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(rs, "rs is null.");

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) table.getTargetClass();

		final ColumnData[] columns = this.getColumnsFor(entryClazz, tableClazz);

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

	public <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData
			getColumnFor(final Class<T> entryClazz, final Class<B> tableClazz, final String name) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(tableClazz, "tableClazz is null.");
		Objects.requireNonNull(name, "name is null.");

		return this.columnsNamesCache
				.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz), k -> this.computeColumnNames(tableClazz, entryClazz))
				.get(name);
	}

	public <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			getColumnsFor(final Class<T> entryClazz, final Class<B> tableClazz) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(tableClazz, "tableClazz is null.");

		return this.columnsCache.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz),
				k -> this.computeColumnsFor(entryClazz, tableClazz));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Class<T> getEntryType(final Class<? extends SQLQueryable<?>> tableClass) {
		Objects.requireNonNull(tableClass, "tableClass is null.");

		final Class<?> result = this.findEntryType(tableClass);

		if (result != null) {
			return (Class<T>) result;
		}

		throw new IllegalArgumentException("Could not determine DataBaseEntry type from " + tableClass.getName());
	}

	private Class<?> findEntryType(final Class<?> type) {
		if (type == null || type == Object.class) {
			return null;
		}

		for (final Type genericInterface : type.getGenericInterfaces()) {
			final Class<?> result = this.findEntryType(genericInterface);

			if (result != null) {
				return result;
			}
		}

		final Class<?> resultFromSuperclass = this.findEntryType(type.getGenericSuperclass());

		if (resultFromSuperclass != null) {
			return resultFromSuperclass;
		}

		for (final Class<?> iface : type.getInterfaces()) {
			final Class<?> result = this.findEntryType(iface);

			if (result != null) {
				return result;
			}
		}

		return this.findEntryType(type.getSuperclass());
	}

	private Class<?> findEntryType(final Type type) {
		if (type == null) {
			return null;
		}

		if (type instanceof ParameterizedType) {
			final ParameterizedType pt = (ParameterizedType) type;
			final Class<?> rawType = PCUtils.getRawClass(pt.getRawType());

			if (rawType != null && SQLQueryable.class.isAssignableFrom(rawType)) {
				for (final Type typeArg : pt.getActualTypeArguments()) {
					final Class<?> rawArg = PCUtils.getRawClass(typeArg);

					if (rawArg != null && DataBaseEntry.class.isAssignableFrom(rawArg)) {
						return rawArg;
					}
				}
			}

			return this.findEntryType(rawType);
		}

		if (type instanceof Class<?>) {
			return this.findEntryType((Class) type);
		}

		return null;
	}

	public <T extends DataBaseEntry> Field getFieldFor(final Class<T> entryClazz, final ColumnData columnData) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(columnData, "columnData is null.");

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
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(sqlName, "sqlName is null.");

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
				if (field.isAnnotationPresent(Column.class) && this.fieldToColumnName(field).equals(sqlName)) {
					return field;
				}
			}

			throw new IllegalArgumentException("No field for column named: '" + sqlName + "' in class: [" + entryClazz.getName() + "]");
		});
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			getGeneratedKeys(final Class<T> entryClazz, final Class<B> tableClazz) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(tableClazz, "tableClazz is null.");

		return this.generatedKeysCache.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz),
				k -> this.computeGeneratedKeys(entryClazz, tableClazz));
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String[]
			getGeneratedColumnNames(final Class<T> entryClazz, final Class<B> tableClazz) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(tableClazz, "tableClazz is null.");

		return this.generatedColumnNamesCache.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz),
				k -> this.computeGeneratedColumnNames(entryClazz, tableClazz));
	}

	@Override
	public <T extends DataBaseEntry> Method getInsertMethod(final Class<T> dataClazz) {
		Objects.requireNonNull(dataClazz, "dataClazz is null.");

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
	public <T extends DataBaseEntry> Method getLoadMethod(final Class<T> dataClazz) {
		Objects.requireNonNull(dataClazz, "dataClazz is null.");

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

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String[] getNonNullKeys(final B instance, final T data) {
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(data, "data is null.");

		return this.getNonNullValues(instance, data).keySet().toArray(new String[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> Map<String, Object> getNonNullValues(final B instance, final T data) {
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(data, "data is null.");

		final Class<T> entryClazz = (Class<T>) data.getClass();
		final Class<B> tableClazz = (Class<B>) instance.getTargetClass();
		final Map<String, Object> result = new HashMap<>();

		for (final ColumnData columnData : this.getNonNullColumns(entryClazz, tableClazz)) {
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

		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) table.getTargetClass();
		final Class<T> entryClazz = table.getEntryClass();

		return this.deleteSqlCache.computeIfAbsent(Pairs.readOnly(tableClazz, entryClazz),
				key -> this.computePreparedDeleteSql(table, entryClazz));
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> String getPreparedInsertSQL(final B table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) table.getTargetClass();

		final String[] columns = Arrays.stream(this.getInsertColumns(entryClazz, tableClazz)).filter(c -> {
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
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String
			getPreparedSelectCountNotNullSQL(final B instance, final String[] notNullKeys, final T data) {
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(notNullKeys, "notNullKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (notNullKeys.length == 0) {
			throw new IllegalArgumentException("No non-null keys found for " + data.getClass().getName());
		}

		return this.structureVisitor.safeSelectCountUniqueCollision(instance, new String[][] { notNullKeys });
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String
			getPreparedSelectCountUniqueSQL(final B instance, final String[][] uniqueKeys, final T data) {
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(uniqueKeys, "uniqueKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		return this.structureVisitor.safeSelectCountUniqueCollision(instance, uniqueKeys);
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String getPreparedSelectSQL(final B table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) table.getTargetClass();

		final ColumnData[] whereColumns = this.getPrimaryKeys(entryClazz, tableClazz);

		if (whereColumns.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + entryClazz.getSimpleName());
		}

		return this.structureVisitor.safeSelect(table, Arrays.stream(whereColumns).map(ColumnData::getName).toArray(String[]::new));
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String
			getPreparedSelectUniqueSQL(final B instance, final String[][] uniqueKeys, final T data) {
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(uniqueKeys, "uniqueKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		return this.structureVisitor.safeSelectUniqueCollision(instance, uniqueKeys);
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> String getPreparedUpdateSQL(final B table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) table.getTargetClass();
		final Class<T> entryClazz = table.getEntryClass();

		return this.updateSqlCache.computeIfAbsent(Pairs.readOnly(tableClazz, entryClazz),
				key -> this.computePreparedUpdateSQL(table, entryClazz));
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			getPrimaryKeys(final Class<T> entryClazz, final Class<B> tableClazz) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(tableClazz, "tableClazz is null.");

		return this.primaryKeysCache.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz),
				k -> this.computePrimaryKeys(entryClazz, tableClazz));
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String[]
			getPrimaryKeysNames(final Class<T> entryClazz, final Class<B> tableClazz) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(tableClazz, "tableClazz is null.");

		return this.primaryKeysNamesCache.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz),
				k -> this.computePrimaryKeyNames(entryClazz, tableClazz));
	}

	@Override
	public Map<String, Object> getQueryableHints(final Class<?> tableClazz) {
		Objects.requireNonNull(tableClazz, "tableClazz is null.");

		return this.queryableHints.computeIfAbsent(tableClazz, c -> Collections.unmodifiableMap(this.computeQueryableHints(tableClazz)));
	}

	@Override
	public String getQueryableName(final Class<? extends SQLQueryable<?>> tableClass) {
		Objects.requireNonNull(tableClass, "tableClazz is null.");

		return this.structureVisitor.getQueryableName(tableClass, this.getQueryableHints(tableClass));
	}

	@Override
	public String getReferencedColumnName(final ColumnData columnData) {
		Objects.requireNonNull(columnData, "columnData is null.");

		if (!columnData.hasHint(DefaultColumnHints.FOREIGN_KEY_COLUMN)) {
			return columnData.getHint(DefaultColumnHints.FOREIGN_KEY_COLUMN);
		}
		final Class<? extends SQLQueryable<?>> refQueryable = columnData.getHint(DefaultColumnHints.FOREIGN_KEY_TABLE);
		final Class<? extends DataBaseEntry> refType = this.getEntryType(refQueryable);
		@SuppressWarnings("unchecked") final ColumnData[] refPks = this.getPrimaryKeys(refType, (Class) refQueryable);

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
		Objects.requireNonNull(annotatedType, "annotatedType is null.");

		final Map<String, Object> map = this.getTypeHints(annotatedType);
		return this.getTypeFor(annotatedType, map);
	}

	@Override
	public ColumnType getTypeFor(final AnnotatedType annotatedType, final Map<String, Object> typeHints) {
		Objects.requireNonNull(annotatedType, "annotatedType is null.");
		Objects.requireNonNull(typeHints, "typeHints is null.");

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
		Objects.requireNonNull(clazz, "clazz is null.");
		Objects.requireNonNull(type, "type is null.");
		Objects.requireNonNull(typeHints, "typeHints is null.");

		return this.computeType(clazz, typeHints)
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No suitable type found: " + clazz.getName() + "\n" + typeHints))
				.get(type, typeHints);
	}

	@Override
	public ColumnType getTypeFor(final Field field) {
		Objects.requireNonNull(field, "field is null.");

		return this.fieldColumnTypeCache.computeIfAbsent(field, f -> {
			final AnnotatedType annotatedType = f.getAnnotatedType();
			final Map<String, Object> typeHints = this.getTypeHints(annotatedType);
			return this.getTypeFor(annotatedType, typeHints);
		});
	}

	@Override
	public Map<String, Object> getTypeHints(final AnnotatedType annotatedType) {
		Objects.requireNonNull(annotatedType, "annotatedType is null.");

		return this.typeHints.computeIfAbsent(annotatedType, c -> Collections.unmodifiableMap(this.computeTypeHints(c)));
	}

	@Override
	public <T extends DataBaseEntry> String[][] getUniqueKeys(final ConstraintData[] allConstraints, final T data) {
		Objects.requireNonNull(allConstraints, "allConstraints is null.");
		Objects.requireNonNull(data, "data is null.");

		if (allConstraints.length == 0) {
			return new String[0][0];
		}

		return Arrays.stream(this.getUniqueValues(allConstraints, data))
				.map(map -> map.keySet().stream().toArray(String[]::new))
				.toArray(String[][]::new);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataBaseEntry> Map<String, Object>[] getUniqueValues(final ConstraintData[] allConstraints, final T data) {
		Objects.requireNonNull(allConstraints, "allConstraints is null.");
		Objects.requireNonNull(data, "data is null.");

		if (allConstraints.length == 0) {
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
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String[]
			getUpdateColumnsNames(final Class<T> entryClazz, final Class<B> tableClazz) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(tableClazz, "tableClazz is null.");

		return this.updateColumnsNamesCache.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz),
				k -> this.computeUpdateColumnsNames(entryClazz, tableClazz));
	}

	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String[]
			getUpdateGeneratedColumnsNames(final Class<T> entryClazz, final Class<B> tableClazz) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(tableClazz, "tableClazz is null.");

		return this.updateGeneratedColumnsNamesCache.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz),
				k -> this.computeUpdateGeneratedColumnsNames(entryClazz, tableClazz));
	}

	@Override
	public <T extends DataBaseEntry> Method getUpdateMethod(final Class<T> dataClazz) {
		Objects.requireNonNull(dataClazz, "dataClazz is null.");

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
		Objects.requireNonNull(data, "data is null.");

		return this.getUpdateMethod((Class<T>) data.getClass());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> T instance(final Class<T> entryClazz, final Class<B> tableClazz) {
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(tableClazz, "tableClazz is null.");

		return (T) this.argInstanceFactoryCache.computeIfAbsent(entryClazz, k -> this.computeInstanceFactories(entryClazz, tableClazz))
				.get(BaseDataBaseEntryUtils.EMPTY_SET)
				.getValue()
				.apply(BaseDataBaseEntryUtils.EMPTY_ARRAY);
	}

	public DataBaseEntryUtils loadTypes(final ColumnTypeRegistry registry) {
		if (registry == null) {
			return this;
		}
		this.columnTypeFactories.clear();
		this.appendTypes(registry);
		return this;
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> void
			prepareDeleteSQL(final PreparedStatement stmt, final B instance, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(data, "data is null.");

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) instance.getTargetClass();

		int index = 1;
		try {
			for (final ColumnData columnData : this.getPrimaryKeys(entryClazz, tableClazz)) {
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
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> void
			prepareInsertSQL(final PreparedStatement stmt, final B instance, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(data, "data is null.");

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) instance.getTargetClass();

		int index = 1;
		for (final ColumnData columnData : this.getInsertColumns(entryClazz, tableClazz)) {
			final Field field = this.getFieldFor(entryClazz, columnData);
			field.setAccessible(true);

			try {
				final Object value = field.get(data);

				if (value == null && columnData.hasDefaultValue()) {
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
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void
			prepareSelectCountNotNullSQL(final PreparedStatement stmt, final B instance, final String[] notNullKeys, final T data)
					throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(notNullKeys, "notNullKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (notNullKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) instance.getTargetClass();

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
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void
			prepareSelectCountUniqueSQL(final PreparedStatement stmt, final B instance, final String[][] uniqueKeys, final T data)
					throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(uniqueKeys, "uniqueKeys is null.");
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
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void
			prepareSelectSQL(final PreparedStatement stmt, final B instance, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(data, "data is null.");

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) instance.getTargetClass();

		int index = 1;
		try {
			for (final ColumnData columnData : this.getPrimaryKeys(entryClazz, tableClazz)) {
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
	public <B extends SQLQueryable<T>, T extends DataBaseEntry> void
			prepareSelectUniqueSQL(final PreparedStatement stmt, final B instance, final String[][] uniqueKeys, final T data)
					throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(uniqueKeys, "uniqueKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + data.getClass().getName());
		}

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();

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
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> void
			prepareUpdateSQL(final PreparedStatement stmt, final B instance, final T data) throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(data, "data is null.");

		@SuppressWarnings("unchecked") final Class<T> entryClazz = (Class<T>) data.getClass();
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) instance.getTargetClass();

		int index = 1;
		try {
			for (final ColumnData columnData : this.getUpdateColumns(entryClazz, tableClazz)) {
				final Field field = this.getFieldFor(entryClazz, columnData);
				field.setAccessible(true);

				final Object value = field.get(data);
				final ColumnType type = this.getTypeFor(field);

				type.store(stmt, index++, value);
			}

			for (final ColumnData columnData : this.getPrimaryKeys(entryClazz, tableClazz)) {
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
	public <V extends SQLQueryable<T>, T extends DataBaseEntry> String qualifiedName(final Class<V> typeName) {
		Objects.requireNonNull(typeName, "typeName is null.");

		return this.structureVisitor.qualifiedName(typeName, this.getQueryableHints(typeName));
	}

	@Override
	public String
			replaceSQLQualifiers(final Class<? extends SQLQueryable<?>> tableClazz, final String input, final Map<String, String> data) {
		Objects.requireNonNull(tableClazz, "tableClazz is null.");
		Objects.requireNonNull(input, "input is null.");

		final Pattern pattern = Pattern.compile("\\{([^}]+)}");

		final Matcher matcher = pattern.matcher(input);
		final StringBuffer result = new StringBuffer();

		while (matcher.find()) {
			final String token = matcher.group(1);

			final String replacement;

			if (data.containsKey(token)) {
				replacement = data.get(token);
			} else if (token.startsWith(DataBaseEntryUtils.QUALIFIER_KEY)) {
				final String value = token.substring(Query.QUALIFIER_KEY.length());
				replacement = this.structureVisitor.qualifiedName(value);
			} else if (token.startsWith(DataBaseEntryUtils.FUNCTION_KEY)) {
				final String value = token.substring(Query.FUNCTION_KEY.length());
				replacement = this.functionResolver.apply(value);
			} else if (token.startsWith(DataBaseEntryUtils.MEMBER_KEY)) {
				final String value = token.substring(Query.MEMBER_KEY.length());
				final Class<? extends DataBaseEntry> entryClazz = this.getEntryType(tableClazz);
				try {
					final Field field = this.findField(entryClazz, value);
					replacement = this.structureVisitor.qualifiedName(this.fieldToColumnName(field));
				} catch (final NoSuchFieldException e) {
					throw new DBException("No column field found matching: " + value + " on: " + entryClazz, e);
				}
			} else {
				replacement = matcher.group(0);
			}

			matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
		}

		matcher.appendTail(result);

		return result.toString();
	}

	@Override
	public DataBaseStructure scanDataBase(final DataBase dataBase, final Map<String, Object> baseHints) {
		Objects.requireNonNull(dataBase, "dataBase is null.");
		Objects.requireNonNull(baseHints, "baseHints is null.");

		return new DataBaseStructure(dataBase.getDataBaseName(),
				this.getQueryableHints(dataBase.getClass().asSubclass(SQLQueryable.class), baseHints));
	}

	public List<Field> sortFields(final Field[] fields) {
		Objects.requireNonNull(fields, "fields is null.");

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

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> Map<String, ColumnData>
			computeColumnNames(final Class<B> tableClazz, final Class<T> entryClazz) {
		final Map<String, ColumnData> columns = new HashMap<>();
		for (final ColumnData cd : this.getColumnsFor(entryClazz, tableClazz)) {
			columns.put(cd.getName(), cd);
		}
		return Collections.unmodifiableMap(columns);
	}

	@Data
	@AllArgsConstructor
	private static class HintValue {

		String qualifier;
		Object value;
		boolean repeatable;
		Object group;
	}

	@FunctionalInterface
	protected interface HintExtractor<A extends Annotation> {

		void extract(A annotation, Object value, Map<String, List<HintValue>> out, String qualifier, boolean repeatable, Object group);
	}

	protected <A extends Annotation> Map<String, Object>
			computeHints(final AnnotatedElement element, final Class<A> hintType, final HintExtractor<A> extractor) {

		try {
			final Map<String, List<HintValue>> collected = new HashMap<>();
			for (final Annotation annotation : PCUtils.getUnwrappedAnnotations(element)) {
				this.collect(annotation, collected, new HashSet<>(), hintType, extractor, null);
			}

			final Map<String, Object> result = new HashMap<>();

			final Map<Object, Map<String, Object>> grouped = new LinkedHashMap<>();
			final Map<Object, List<Map<String, Object>>> groupedRepeatable = new LinkedHashMap<>();

			for (final Map.Entry<String, List<HintValue>> e : collected.entrySet()) {
				final List<HintValue> matching = e.getValue()
						.stream()
						.filter(v -> this.matchesDbmsQualifier(v.qualifier))
						.collect(Collectors.toList());

				if (matching.isEmpty()) {
					continue;
				}

				final boolean repeatable = matching.get(0).repeatable;
				final boolean groupedHint = matching.get(0).group != null;

				if (!groupedHint) {
					if (repeatable) {
						result.put(e.getKey(), matching.stream().map(v -> v.value).collect(Collectors.toList()));
						continue;
					}

					HintValue best = null;
					for (final HintValue value : matching) {
						if (best == null || best.qualifier.isEmpty() && !value.qualifier.isEmpty()) {
							best = value;
						}
					}

					int count = 0;
					for (final HintValue value : matching) {
						if (Objects.equals(best.qualifier, value.qualifier)) {
							count++;
						}
					}

					if (count > 1) {
						throw new IllegalArgumentException("Multiple values found for non-repeatable hint '" + e.getKey() + "'");
					}

					result.put(e.getKey(), best.value);
					continue;
				}

				if (!repeatable) {
					for (final HintValue value : matching) {
						grouped.computeIfAbsent(value.group, k -> new LinkedHashMap<>()).put(e.getKey(), value.value);
					}
				} else {
					for (final HintValue value : matching) {
						final List<Map<String, Object>> list = groupedRepeatable.computeIfAbsent(value.group, k -> new ArrayList<>());
						Map<String, Object> map;

						if (list.isEmpty()) {
							map = new LinkedHashMap<>();
							list.add(map);
						} else {
							map = list.get(0);
						}

						map.put(e.getKey(), value.value);
					}
				}
			}

			if (!grouped.isEmpty()) {
				result.put("__groups__", grouped);
			}
			if (!groupedRepeatable.isEmpty()) {
				result.put("__repeatableGroups__", groupedRepeatable);
			}

			return result;

		} catch (final Exception e) {
			throw new DBException("Unable to compute hints for: " + element, e);
		}
	}

	protected <A extends Annotation> void collect(
			final Annotation annotation,
			final Map<String, List<HintValue>> out,
			final Set<Class<?>> visited,
			final Class<A> hintType,
			final HintExtractor<A> extractor,
			Object group)
			throws IllegalAccessException,
				InvocationTargetException {

		final Class<? extends Annotation> type = annotation.annotationType();
		if (!visited.add(type)) {
			return;
		}

		try {
			String qualifier = "";
			if (type.isAnnotationPresent(DbmsFilter.class)) {
				qualifier = type.getAnnotation(DbmsFilter.class).value();
			}

			Method qualifierMethod = null;
			for (final Method m : type.getDeclaredMethods()) {
				if (m.isAnnotationPresent(DbmsFilter.class)) {
					if (qualifierMethod != null) {
						throw new IllegalArgumentException("Multiple @DbmsFilter methods on " + type);
					}
					qualifierMethod = m;
				}
			}

			if (qualifierMethod != null) {
				qualifier = (String) qualifierMethod.invoke(annotation);
			}
			if (!qualifier.isEmpty() && !this.matchesDbmsQualifier(qualifier)) {
				return;
			}

			final boolean repeatable = type.isAnnotationPresent(RepeatableHint.class);
			final boolean grouped = type.isAnnotationPresent(Grouped.class);
			if (grouped) {
				group = new Object();
			}

			for (final Annotation meta : PCUtils.getUnwrappedAnnotations(type)) {
				if (hintType.isInstance(meta)) {
					extractor.extract(hintType.cast(meta), null, out, qualifier, repeatable, group);
				} else {
					this.collect(meta, out, visited, hintType, extractor, group);
				}
			}

			for (final Method method : type.getDeclaredMethods()) {
				if (method == qualifierMethod) {
					continue;
				}

				final Object value = method.invoke(annotation);
				for (final Annotation meta : PCUtils.getUnwrappedAnnotations(method)) {
					if (hintType.isInstance(meta)) {
						extractor.extract(hintType.cast(meta), value, out, qualifier, repeatable, group);
					}
				}

				if (value instanceof Annotation) {
					this.collect((Annotation) value, out, visited, hintType, extractor, group);
				} else if (value instanceof Annotation[]) {
					for (final Annotation a : (Annotation[]) value) {
						this.collect(a, out, visited, hintType, extractor, group);
					}
				}
			}

		} finally {
			visited.remove(type);
		}
	}

	protected Map<String, Object> computeColumnHints(final AnnotatedElement element) {
		if (!(element instanceof Field || element instanceof Parameter)) {
			throw new UnsupportedOperationException(element.getClass() + " not supported.");
		}
		return this.computeHints(element,
				ColumnHint.class,
				(hint, value, out, qualifier, repeatable, group) -> out.computeIfAbsent(hint.type(), k -> new ArrayList<>())
						.add(new HintValue(qualifier, value == null ? hint.value() : value, repeatable, group)));
	}

	protected Map<String, Object> computeTypeHints(final AnnotatedType element) {
		return this.computeHints(element,
				TypeHint.class,
				(hint, value, out, qualifier, repeatable, group) -> out.computeIfAbsent(hint.type(), k -> new ArrayList<>())
						.add(new HintValue(qualifier, value == null ? hint.value() : value, repeatable, group)));
	}

	protected Map<String, Object> computeQueryableHints(final Class<?> element) {
		return this.computeHints(element,
				QueryableHint.class,
				(hint, value, out, qualifier, repeatable, group) -> out.computeIfAbsent(hint.type(), k -> new ArrayList<>())
						.add(new HintValue(qualifier, value == null ? hint.value() : value, repeatable, group)));
	}

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			computeColumnsFor(final Class<T> entryClazz, final Class<B> tableClazz) {
		final List<ColumnData> columns = new ArrayList<>();

		for (final Field field : this.sortFields(PCUtils.getAllFields(entryClazz))) {
			field.setAccessible(true);

			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}

			final String columnName = this.fieldToColumnName(field);
			final Map<String, Object> typeHints = this.getTypeHints(field.getAnnotatedType());
			final ColumnType columnType = this.getTypeFor(field.getAnnotatedType(), typeHints);
			final Map<String, Object> columnHints = this.computeColumnHints(field);

			final boolean autoIncrement = (boolean) columnHints.getOrDefault(DefaultColumnHints.AUTO_INCREMENT, false);
			final boolean nullable = (boolean) columnHints.getOrDefault(DefaultColumnHints.NULLABLE, false);

			if (nullable && field.getType().isPrimitive()) {
				throw new DBException("Column: '" + columnName + "' defined by " + field + " is a nullable of primitive type.");
			}

			String defaultValue = (String) columnHints.get(DefaultColumnHints.DEFAULT_VALUE);
			if (DefaultValue.I_KNOW.equals(defaultValue) || DefaultValue.NONE.equals(defaultValue)) {
				defaultValue = null;
			} else if (defaultValue == null && !nullable && this.isForceDefaultValueOnNonNull() && !autoIncrement) {
				throw new DBException("Column: '" + columnName + "' defined by " + field
						+ " isn't nullable and defines no default value for '" + this.dbmsQualifierName + "'.\n"
						+ "Add @DefaultValue(DefaultValue.I_KNOW) on the field or class to disable this error locally or set the option '"
						+ DataBaseEntryUtilsOptionsOwner.FORCE_DEFAULT_VALUE_ON_NON_NULL_PROPERTY
						+ "' to false to disable this check globally, you'll need to make sure that this field actually has a value on insertion/update.");
			}

			final String onUpdate = (String) columnHints.get(DefaultColumnHints.ON_UPDATE);
			final boolean primaryKey = (boolean) columnHints.getOrDefault(DefaultColumnHints.PRIMARY_KEY, false);
			final boolean unique = columnHints.containsKey(DefaultColumnHints.UNIQUE_INDEX);
			final boolean foreignKey = columnHints.containsKey(DefaultColumnHints.FOREIGN_KEY_TABLE);

			ColumnData columnData = new ColumnData(columnName,
					typeHints,
					columnType,
					autoIncrement,
					nullable,
					defaultValue,
					onUpdate,
					primaryKey,
					unique,
					foreignKey,
					Optional.of(field),
					columnHints);

			if (field.isAnnotationPresent(Generated.class)) {
				final Generated gen = field.getAnnotation(Generated.class);

				columnData = new GeneratedColumnData(columnData, gen);
			}

			columns.add(columnData);
		}

		return columns.toArray(new ColumnData[0]);
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

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			computeGeneratedKeys(final Class<T> entryClazz, final Class<B> tableClazz) {
		Objects.requireNonNull(entryClazz, "entry class is null");

		final List<ColumnData> generatedKeys = new ArrayList<>();

		for (final ColumnData columnData : this.getColumnsFor(entryClazz, tableClazz)) {
			if (columnData.isAutoIncrement() || columnData.hasDefaultValue() && columnData.isPrimaryKey()) {
				generatedKeys.add(columnData);
			}
		}
		return generatedKeys.toArray(new ColumnData[0]);
	}

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> String[]
			computeGeneratedColumnNames(final Class<T> entryClazz, final Class<B> tableClazz) {
		Objects.requireNonNull(entryClazz, "entry class is null");

		return Arrays.stream(this.getGeneratedKeys(entryClazz, tableClazz)).map(ColumnData::getName).toArray(String[]::new);
	}

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry>
			Map<Set<String>, ReadOnlyPair<List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>>, ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>>>
			computeInstanceFactories(final Class<T> entryClazz, final Class<B> tableClazz) {
		final Map<Set<String>, ReadOnlyPair<List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>>, ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>>> factories = new HashMap<>();

		for (final Constructor<?> constructor : entryClazz.getConstructors()) {
			final Set<String> args = new HashSet<>(constructor.getParameterCount());
			final List<ReadOnlyQuadruple<String, ColumnData, Type, Integer>> mapping = new ArrayList<>(constructor.getParameterCount());
			for (int i = 0; i < constructor.getParameterCount(); i++) {
				final Parameter p = constructor.getParameters()[i];
				final String name = this.parameterToColumnName(p);
				args.add(name);
				mapping.add(Quadruples
						.readOnly(name, this.getColumnFor(entryClazz, tableClazz, name), constructor.getGenericParameterTypes()[i], i));
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
				mapping.add(Quadruples
						.readOnly(name, this.getColumnFor(entryClazz, tableClazz, name), method.getGenericParameterTypes()[i], i));
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

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			computeNonNullColumns(final Class<T> entryClazz, final Class<B> tableClazz) {
		return Arrays.stream(this.getColumnsFor(entryClazz, tableClazz))
				.filter(c -> !c.hasOnUpdate())
				.filter(c -> !c.isPrimaryKey())
				.filter(c -> !c.isGenerated())
				.toArray(ColumnData[]::new);
	}

	protected <B extends AbstractDBTable<T>, T extends DataBaseEntry> String
			computePreparedDeleteSql(final B table, final Class<T> entryClazz) {
		final String[] pkNames = this.getPrimaryKeysNames(entryClazz, table.getTargetClass());
		if (pkNames.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + entryClazz.getSimpleName());
		}

		return this.structureVisitor.safeDelete(table, pkNames);
	}

	protected <B extends AbstractDBTable<T>, T extends DataBaseEntry> String
			computePreparedUpdateSQL(final B table, final Class<T> entryClazz) {
		@SuppressWarnings("unchecked") final Class<B> tableClazz = (Class<B>) table.getTargetClass();

		final String[] setColumns = this.getUpdateColumnsNames(entryClazz, tableClazz);
		if (setColumns.length == 0) {
			throw new IllegalArgumentException("No columns to update.");
		}

		final String[] whereColumns = this.getPrimaryKeysNames(entryClazz, tableClazz);
		if (whereColumns.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + entryClazz.getSimpleName());
		}

		return this.structureVisitor.safeUpdate(table, setColumns, whereColumns);
	}

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> String[]
			computePrimaryKeyNames(final Class<T> entryClazz, final Class<B> tableClazz) {
		return Arrays.stream(this.getPrimaryKeys(entryClazz, tableClazz)).map(ColumnData::getName).toArray(String[]::new);
	}

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			computePrimaryKeys(final Class<T> entryClazz, final Class<B> tableClazz) {
		return Arrays.stream(this.getColumnsFor(entryClazz, tableClazz))
				.filter(ColumnData::isPrimaryKey)
				.collect(Collectors.toList())
				.toArray(new ColumnData[0]);
	}

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> String[]
			computeUpdateColumnsNames(final Class<T> entryClazz, final Class<B> tableClazz) {
		return Arrays.stream(this.getUpdateColumns(entryClazz, tableClazz)).map(ColumnData::getName).toArray(String[]::new);
	}

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> String[]
			computeUpdateGeneratedColumnsNames(final Class<T> entryClazz, final Class<B> tableClazz) {
		return Arrays
				.stream(PCUtils.combineArrays(this.getPrimaryKeys(entryClazz, tableClazz), this.getGeneratedKeys(entryClazz, tableClazz)))
				.map(ColumnData::getName)
				.toArray(String[]::new);
	}

	protected <T extends DataBaseEntry> T fillLoad(
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

			@SuppressWarnings("unchecked") final T data = (T) factory.apply(params);

			final Method loadMethod = this.getLoadMethod(entryClazz);
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

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			getInsertColumns(final Class<T> entryClazz, final Class<B> tableClazz) {
		return this.insertColumnsCache.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz),
				k -> this.computeInsertColumns(entryClazz, tableClazz));
	}

	/**
	 * NOT OnUpdate, NOT PK, NOT Generated
	 */
	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			getNonNullColumns(final Class<T> entryClazz, final Class<B> tableClazz) {
		return this.nonNullColumnsCache.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz),
				k -> this.computeNonNullColumns(entryClazz, tableClazz));
	}

	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			getUpdateColumns(final Class<T> entryClazz, final Class<B> tableClazz) {
		return this.updateColumnsCache.computeIfAbsent(Pairs.readOnly(entryClazz, tableClazz),
				k -> this.computeUpdateColumns(entryClazz, tableClazz));
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

	private <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			computeInsertColumns(final Class<T> entryClazz, final Class<B> tableClazz) {
		return Arrays.stream(this.getColumnsFor(entryClazz, tableClazz))
				.filter(c -> !c.isGenerated())
				.filter(c -> !c.isAutoIncrement())
				.toArray(ColumnData[]::new);
	}

	private <B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[]
			computeUpdateColumns(final Class<T> entryClazz, final Class<B> tableClazz) {
		return Arrays.stream(this.getColumnsFor(entryClazz, tableClazz))
				.filter(c -> !c.isGenerated())
				.filter(c -> !c.isAutoIncrement())
				.filter(c -> !c.hasOnUpdate())
				.toArray(ColumnData[]::new);
	}

	protected Map<String, Object> getQueryableHints(final Class<?> tableClazz, final Map<String, Object> baseHints) {
		final Map<String, Object> map = new HashMap<>(this.getQueryableHints(tableClazz));
		map.putAll(baseHints);
		return Collections.unmodifiableMap(map);
	}

	@Override
	public <B extends AbstractDBTable<T>, T extends DataBaseEntry> String getTruncateSQL(final B queryable) {
		return this.structureVisitor.getTruncateSQL(queryable);
	}

}
