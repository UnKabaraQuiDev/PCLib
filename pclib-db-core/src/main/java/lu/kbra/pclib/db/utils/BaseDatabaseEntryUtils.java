package lu.kbra.pclib.db.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Factory;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.entry.Insert;
import lu.kbra.pclib.db.annotations.entry.Load;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Update;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.dbms.DbmsProviders;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolvers;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitors;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.domain.table.UniqueData;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider.ArgData;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider.FactoryMethod;
import lu.kbra.pclib.db.utils.impl.SQLColumnTypeProvider;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.DefaultSQLColumnTypeProvider;
import lu.kbra.pclib.impl.function.ThrowingFunction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class BaseDatabaseEntryUtils implements DatabaseEntryUtils {

	protected final String dbmsQualifierName;

	protected HintScanner hintScanner;
	protected SQLColumnTypeProvider columnTypeProvider;
	protected EntryInstanceProvider entryInstanceProvider;
	protected SQLFunctionResolver functionResolver;
	protected SQLStructureVisitor structureVisitor;

	protected Map<String, Object> options = new HashMap<>();

	public BaseDatabaseEntryUtils(final ColumnTypeRegistry typeRegistry, final String protocolName) {
		Objects.requireNonNull(protocolName, "protocolName is null.");
		this.dbmsQualifierName = protocolName;
		this.structureVisitor = SQLStructureVisitors.forProtocol(protocolName);
		this.functionResolver = SQLFunctionResolvers.forProtocol(protocolName);
		this.hintScanner = new HintScanner(protocolName);
		this.columnTypeProvider = new DefaultSQLColumnTypeProvider();
		this.entryInstanceProvider = new DefaultEntryInstanceProvider(this);
		this.loadTypes(typeRegistry);
	}

	public BaseDatabaseEntryUtils(
			final ColumnTypeRegistry typeRegistry,
			final String protocolName,
			final SQLStructureVisitor structureVisitor,
			final SQLFunctionResolver functionResolver) {
		Objects.requireNonNull(protocolName, "protocolName is null.");
		Objects.requireNonNull(structureVisitor, "structureVisitor is null.");
		Objects.requireNonNull(functionResolver, "functionResolver is null.");
		this.dbmsQualifierName = protocolName;
		this.structureVisitor = structureVisitor;
		this.functionResolver = functionResolver;
		this.hintScanner = new HintScanner(protocolName);
		this.columnTypeProvider = new DefaultSQLColumnTypeProvider();
		this.entryInstanceProvider = new DefaultEntryInstanceProvider(this);
		this.loadTypes(typeRegistry);
	}

	public BaseDatabaseEntryUtils(final String protocolName) {
		this(DbmsProviders.columnTypeRegistryFor(protocolName), protocolName);
	}

	@Override
	public void appendTypes(final ColumnTypeRegistry addColumnTypeRegistry) {
		addColumnTypeRegistry.registerTypes(this.columnTypeProvider.getColumnTypeFactories());
	}

	@Override
	public <T extends DatabaseEntry> void fillInsert(final AbstractDBTable<T> table, final T data, final ResultSet rs) throws SQLException {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(rs, "rs is null.");

		try {
			for (final ColumnData columnData : this.getPrimaryKeys(table)) {
				if (!columnData.isPrimaryKey()) {
					continue;
				}

				final Field field = columnData.getField();
				field.setAccessible(true);
				final String columnName = columnData.getLocalName();
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

			final Method insertMethod = this.getInsertMethod(table.getEntryClass());
			if (insertMethod != null) {
				try {
					insertMethod.invoke(data);
				} catch (final Exception e) {
					throw new DBException("Exception while invoking insert method.", e);
				}
			}
		} catch (final Exception e) {
			throw new DBException(
					"Failed to update fields on " + table.getTargetClass() + "<" + table.getEntryClass() + ">" + " for input: "
							+ PCUtils.asMap(rs),
					e);
		}
	}

	@Override
	public <T extends DatabaseEntry> void fillLoad(final SQLQueryable<T> table, final T data, final ResultSet rs) throws SQLException {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(rs, "rs is null.");

		try {
			for (final ColumnData columnData : table.getStructure().getColumns()) {
				final Field field = columnData.getField();
				field.setAccessible(true);

				final String columnName = columnData.getLocalName();
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

			final Method loadMethod = this.getLoadMethod(table.getEntryClass());
			if (loadMethod != null) {
				try {
					loadMethod.invoke(data);
				} catch (final Exception e) {
					throw new DBException("Exception while invoking load method.", e);
				}
			}
		} catch (final Exception e) {
			throw new DBException(
					"Failed to update fields on " + table.getTargetClass() + "<" + table.getEntryClass() + "> for input: "
							+ PCUtils.asMap(rs),
					e);
		}
	}

	@Override
	public <T extends DatabaseEntry> void
			fillLoadAll(final SQLQueryable<T> table, final Class<T> entryClazz, final ResultSet rs, final Consumer<T> listExporter)
					throws SQLException {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(entryClazz, "entryClazz is null.");
		Objects.requireNonNull(rs, "rs is null.");
		Objects.requireNonNull(listExporter, "listExporter is null.");

		final ResultSetMetaData resultMetaData = rs.getMetaData();
		final int columnCount = resultMetaData.getColumnCount();
		final String[] columns = new String[columnCount];
		for (int i = 0; i < columns.length; i++) {
			columns[i] = resultMetaData.getColumnLabel(i + 1);
		}

		final FactoryMethod factoryMethod = this.entryInstanceProvider.getFactoryMethod(table, columns);

		if (factoryMethod != null) {

			while (rs.next()) {
				final T copy = this.fillLoad(entryClazz, rs, factoryMethod);
				listExporter.accept(copy);
			}

		} else {

			while (rs.next()) {
				final T copy = this.entryInstanceProvider.instance(table);
				this.fillLoad(table, copy, rs);
				listExporter.accept(copy);
			}

		}
	}

	@Override
	public <T extends DatabaseEntry> void fillUpdate(final AbstractDBTable<T> table, final T data, final ResultSet rs) throws SQLException {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(rs, "rs is null.");

		try {
			for (final ColumnData columnData : table.getStructure().getColumns()) {
				if (!columnData.hasOnUpdate() || !columnData.isGenerated()) {
					continue;
				}

				final Field field = columnData.getField();
				field.setAccessible(true);
				final String columnName = columnData.getLocalName();
				final ColumnType type = columnData.getType();

				final Object value = type.load(rs, columnName, field.getGenericType());
				field.set(data, rs.wasNull() ? null : value);
			}

			final Method updateMethod = this.getUpdateMethod(table.getEntryClass());
			if (updateMethod != null) {
				try {
					updateMethod.invoke(data);
				} catch (final Exception e) {
					throw new DBException("Exception while invoking update method.", e);
				}
			}
		} catch (final IllegalAccessException e) {
			throw new DBException("Failed to update update keys on " + table.getEntryClass(), e);
		}
	}

	// TODO: move this to SQLStructure something
	@Override
	public ColumnData getColumnFor(final SQLQueryableStructure structure, final String localName) {
		Objects.requireNonNull(structure, "structure is null.");
		Objects.requireNonNull(localName, "localName is null.");

		for (final ColumnData cd : structure.getColumns()) {
			if (cd.getLocalName().equals(localName)) {
				return cd;
			}
		}

		throw new IllegalArgumentException("No column with name: " + localName + " found on: " + structure.getTargetClass() + "<"
				+ structure.getEntryClass() + "> (named: " + structure.getName() + ")");
	}

	@Override
	public <T extends DatabaseEntry> ColumnData[] getGeneratedKeys(final AbstractDBTable<T> table) {
		Objects.requireNonNull(table, "table is null");

		final List<ColumnData> generatedKeys = new ArrayList<>();

		for (final ColumnData columnData : table.getStructure().getColumns()) {
			if (columnData.isAutoIncrement() || columnData.hasDefaultValue() && columnData.isPrimaryKey()) {
				generatedKeys.add(columnData);
			}
		}
		return generatedKeys.toArray(new ColumnData[0]);
	}

	@Override
	public <T extends DatabaseEntry> String[] getGeneratedColumnNames(final AbstractDBTable<T> table) {
		Objects.requireNonNull(table, "table is null.");

		return Arrays.stream(this.getGeneratedKeys(table)).map(ColumnData::getLocalName).toArray(String[]::new);
	}

	@Override
	public <T extends DatabaseEntry> Method getInsertMethod(final Class<T> dataClazz) {
		Objects.requireNonNull(dataClazz, "dataClazz is null.");

		for (final Method m : dataClazz.getDeclaredMethods()) {
			if (m.isAnnotationPresent(Insert.class)) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	@Override
	public <T extends DatabaseEntry> Method getLoadMethod(final Class<T> dataClazz) {
		Objects.requireNonNull(dataClazz, "dataClazz is null.");

		for (final Method m : dataClazz.getDeclaredMethods()) {
			if (m.isAnnotationPresent(Load.class)) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	@Override
	public <T extends DatabaseEntry> String[] getNonNullKeys(final SQLQueryable<T> instance, final T data) {
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(data, "data is null.");

		return this.getNonNullValues(instance, data).keySet().toArray(new String[0]);
	}

	@Override
	public <T extends DatabaseEntry> Map<String, Object> getNonNullValues(final SQLQueryable<T> instance, final T data) {
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(data, "data is null.");

		final Map<String, Object> result = new HashMap<>();

		for (final ColumnData columnData : this.getNonNullColumns(instance)) {
			if (columnData.isGenerated() || columnData.isPrimaryKey() || columnData.hasOnUpdate()) {
				continue;
			}

			try {
				final Field field = columnData.getField();
				field.setAccessible(true);
				final Object value = field.get(data);

				if (value == null) {
					continue;
				}

				result.put(columnData.getLocalName(), value);
			} catch (final IllegalAccessException e) {
				throw new DBException(
						"Exception while getting non-null values from: " + instance.getTargetClass() + "<" + instance.getEntryClass() + ">",
						e);
			}
		}

		return result;
	}

	@Override
	public <T extends DatabaseEntry> String getPreparedDeleteSQL(final AbstractDBTable<T> table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final String[] pkNames = this.getPrimaryKeyNames(table);
		if (pkNames.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		return this.structureVisitor.safeDelete(table, pkNames);
	}

	@Override
	public <T extends DatabaseEntry> String getPreparedInsertSQL(final AbstractDBTable<T> table, final T data) {
		Objects.requireNonNull(data, "data is null.");
		Objects.requireNonNull(table, "table is null.");

		final String[] columns = Arrays.stream(this.getInsertColumns(table)).filter(column -> {
			final Field f = column.getField();
			f.setAccessible(true);
			try {
				final Object value = f.get(data);

				if (value == null && column.hasDefaultValue()) {
					return false;
				}
				return true;
			} catch (final IllegalAccessException e) {
				throw new DBException("Failed to access field value for field: " + f, e);
			}
		}).map(ColumnData::getLocalName).toArray(String[]::new);

		return this.structureVisitor.safeInsert(table, columns);
	}

	@Override
	public <T extends DatabaseEntry> String
			getPreparedSelectCountNotNullSQL(final SQLQueryable<T> instance, final String[] notNullKeys, final T data) {
		Objects.requireNonNull(instance, "instance is null.");
		Objects.requireNonNull(notNullKeys, "notNullKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (notNullKeys.length == 0) {
			throw new IllegalArgumentException(
					"No non-null keys found for " + instance.getTargetClass() + "<" + instance.getEntryClass() + ">");
		}

		return this.structureVisitor.safeSelectCountUniqueCollision(instance, new String[][] { notNullKeys });
	}

	@Override
	public <T extends DatabaseEntry> String
			getPreparedSelectCountUniqueSQL(final SQLQueryable<T> table, final String[][] uniqueKeys, final T data) {
		Objects.requireNonNull(table, "table.getTargetClass()+\"<\"+table.getEntryClass()+\">\" is null.");
		Objects.requireNonNull(uniqueKeys, "uniqueKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		return this.structureVisitor.safeSelectCountUniqueCollision(table, uniqueKeys);
	}

	@Override
	public <T extends DatabaseEntry> String getPreparedSelectSQL(final SQLQueryable<T> table, final T data) {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");

		final ColumnData[] whereColumns = this.getPrimaryKeys(table);

		if (whereColumns.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		return this.structureVisitor.safeSelect(table, Arrays.stream(whereColumns).map(ColumnData::getLocalName).toArray(String[]::new));
	}

	@Override
	public <T extends DatabaseEntry> String
			getPreparedSelectUniqueSQL(final SQLQueryable<T> table, final String[][] uniqueKeys, final T data) {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(uniqueKeys, "uniqueKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		return this.structureVisitor.safeSelectUniqueCollision(table, uniqueKeys);
	}

	@Override
	public <T extends DatabaseEntry> String getPreparedUpdateSQL(final AbstractDBTable<T> table, final T data) {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");

		final String[] setColumns = this.getUpdateColumnsNames(table);
		if (setColumns.length == 0) {
			throw new IllegalArgumentException("No columns to update on " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		final String[] whereColumns = this.getPrimaryKeyNames(table);
		if (whereColumns.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		return this.structureVisitor.safeUpdate(table, setColumns, whereColumns);
	}

	@Override
	public <T extends DatabaseEntry> String[][] getUniqueKeys(final AbstractDBTable<T> table, final T data) {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");

		if (table.getStructure().getConstraints().length == 0) {
			return new String[0][0];
		}

		return Arrays.stream(this.getUniqueValues(table, data))
				.map(map -> map.keySet().stream().toArray(String[]::new))
				.toArray(String[][]::new);
	}

	@Override
	public <T extends DatabaseEntry> Map<String, Object>[] getUniqueValues(final AbstractDBTable<T> table, final T data) {
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");

		final ConstraintData[] allConstraints = table.getStructure().getConstraints();
		Objects.requireNonNull(allConstraints, "allConstraints is null.");
		if (allConstraints.length == 0) {
			return new Map[0];
		}

		final List<UniqueData> uniqueConstraints = Arrays.stream(allConstraints)
				.filter(UniqueData.class::isInstance)
				.map(UniqueData.class::cast)
				.collect(Collectors.toList());

		final Map<String, Object>[] result = new Map[uniqueConstraints.size()];

		for (int i = 0; i < uniqueConstraints.size(); i++) {
			final UniqueData unique = uniqueConstraints.get(i);
			final ColumnData[] columns = unique.getColumns();

			final Map<String, Object> keyMap = new LinkedHashMap<>();

			for (final ColumnData columnData : columns) {
				try {
					final Field field = columnData.getField();

					field.setAccessible(true);
					final Object value = field.get(data);
					keyMap.put(columnData.getLocalName(), value);
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

	public <T extends DatabaseEntry> Method getUpdateMethod(final Class<T> dataClazz) {
		Objects.requireNonNull(dataClazz, "dataClazz is null.");

		for (final Method m : dataClazz.getDeclaredMethods()) {
			if (m.isAnnotationPresent(Update.class)) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	public DatabaseEntryUtils loadTypes(final ColumnTypeRegistry registry) {
		if (registry == null) {
			return this;
		}
		this.columnTypeProvider.getColumnTypeFactories().clear();
		this.appendTypes(registry);
		return this;
	}

	@Override
	public <T extends DatabaseEntry> void prepareDeleteSQL(final PreparedStatement stmt, final AbstractDBTable<T> table, final T data)
			throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");

		int index = 1;
		try {
			for (final ColumnData columnData : this.getPrimaryKeys(table)) {
				final Field field = columnData.getField();

				field.setAccessible(true);
				final Object value = field.get(data);

				final ColumnType type = columnData.getType();
				type.store(stmt, index++, value);
			}
		} catch (final IllegalAccessException e) {
			throw new DBException("Failed to access field value", e);
		}
	}

	@Override
	public <T extends DatabaseEntry> void prepareInsertSQL(final PreparedStatement stmt, final AbstractDBTable<T> table, final T data)
			throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");

		int index = 1;
		for (final ColumnData columnData : this.getInsertColumns(table)) {
			final Field field = columnData.getField();
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
				final ColumnType type = columnData.getType();

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
	public <T extends DatabaseEntry> void prepareSelectCountNotNullSQL(
			final PreparedStatement stmt,
			final SQLQueryable<T> table,
			final String[] notNullKeys,
			final T data)
			throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(notNullKeys, "notNullKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (notNullKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		try {
			int index = 1;
			for (final String columnName : notNullKeys) {
				final ColumnData column = this.getColumnFor(table, columnName);
				final Field field = column.getField();
				field.setAccessible(true);

				final ColumnType type = column.getType();
				type.store(stmt, index++, field.get(data));
			}
		} catch (final IllegalAccessException e) {
			throw new DBException(e);
		}
	}

	@Override
	public <T extends DatabaseEntry> void prepareSelectCountUniqueSQL(
			final PreparedStatement stmt,
			final SQLQueryable<T> table,
			final String[][] uniqueKeys,
			final T data)
			throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(uniqueKeys, "uniqueKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		try {
			int index = 1;
			for (final String[] list : uniqueKeys) {
				for (final String columnName : list) {
					final ColumnData column = this.getColumnFor(table, columnName);
					final Field field = column.getField();
					field.setAccessible(true);

					final ColumnType type = column.getType();
					type.store(stmt, index++, field.get(data));
				}
			}
		} catch (final IllegalAccessException e) {
			throw new DBException(e);
		}
	}

	@Override
	public <T extends DatabaseEntry> void prepareSelectSQL(final PreparedStatement stmt, final SQLQueryable<T> table, final T data)
			throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");

		int index = 1;
		try {
			for (final ColumnData column : this.getPrimaryKeys(table)) {
				final Field field = column.getField();

				field.setAccessible(true);
				final Object value = field.get(data);

				final ColumnType type = column.getType();
				type.store(stmt, index++, value);
			}
		} catch (final IllegalAccessException e) {
			throw new DBException("Failed to access field value", e);
		}
	}

	@Override
	public <T extends DatabaseEntry> void
			prepareSelectUniqueSQL(final PreparedStatement stmt, final SQLQueryable<T> table, final String[][] uniqueKeys, final T data)
					throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(uniqueKeys, "uniqueKeys is null.");
		Objects.requireNonNull(data, "data is null.");

		if (uniqueKeys.length == 0) {
			throw new IllegalArgumentException("No unique keys found for " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		try {
			int index = 1;
			for (final String[] list : uniqueKeys) {
				for (final String columnName : list) {
					final ColumnData columnData = this.getColumnFor(table, columnName);
					final Field field = columnData.getField();
					field.setAccessible(true);

					final ColumnType type = columnData.getType();
					type.store(stmt, index++, field.get(data));
				}
			}
		} catch (final IllegalAccessException e) {
			throw new DBException(e);
		}
	}

	@Override
	public <T extends DatabaseEntry> void prepareUpdateSQL(final PreparedStatement stmt, final AbstractDBTable<T> table, final T data)
			throws SQLException {
		Objects.requireNonNull(stmt, "stmt is null.");
		Objects.requireNonNull(table, "table is null.");
		Objects.requireNonNull(data, "data is null.");

		int index = 1;
		try {
			for (final ColumnData column : this.getUpdateColumns(table)) {
				final Field field = column.getField();
				field.setAccessible(true);

				final Object value = field.get(data);
				final ColumnType type = column.getType();

				type.store(stmt, index++, value);
			}

			for (final ColumnData column : this.getPrimaryKeys(table)) {
				final Field field = column.getField();

				field.setAccessible(true);
				final Object value = field.get(data);
				final ColumnType type = column.getType();

				type.store(stmt, index++, value);
			}
		} catch (final IllegalAccessException e) {
			throw new DBException("Failed to access field value", e);
		}
	}

	@Override
	public <T extends DatabaseEntry> String
			replaceSQLQualifiers(final SQLQueryable<T> table, final String input, Map<String, String> data) {
		Objects.requireNonNull(table, "table is null.");
		if (input == null) {
			return null;
		}
		if (data == null) {
			data = new HashMap<>(1);
		}

		final Pattern pattern = Pattern.compile("\\{([^}]+)}");

		final Matcher matcher = pattern.matcher(input);
		final StringBuffer result = new StringBuffer();

		data.putIfAbsent(DatabaseEntryUtils.TABLE_NAME_KEY, table.getQualifiedName());

		while (matcher.find()) {
			final String token = matcher.group(1);

			final String replacement;

			if (data.containsKey(token)) {
				replacement = data.get(token);
			} else if (token.startsWith(DatabaseEntryUtils.QUALIFIER_KEY)) {
				final String value = token.substring(Query.QUALIFIER_KEY.length());
				replacement = this.structureVisitor.qualifiedName(value);
			} else if (token.startsWith(DatabaseEntryUtils.FUNCTION_KEY)) {
				final String value = token.substring(Query.FUNCTION_KEY.length());
				replacement = this.functionResolver.apply(value);
			} else if (token.startsWith(DatabaseEntryUtils.MEMBER_KEY)) {
				final String value = token.substring(Query.MEMBER_KEY.length());
				final Class<? extends DatabaseEntry> entryClazz = table.getEntryClass();
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

	/**
	 * NOT OnUpdate, NOT PK, NOT Generated
	 */
	public <T extends DatabaseEntry> ColumnData[] getNonNullColumns(final SQLQueryable<T> table) {
		return Arrays.stream(table.getStructure().getColumns())
				.filter(c -> !c.hasOnUpdate())
				.filter(c -> !c.isPrimaryKey())
				.filter(c -> !c.isGenerated())
				.toArray(ColumnData[]::new);
	}

	public <T extends DatabaseEntry> String getPreparedDeleteSql(final AbstractDBTable<T> table) {
		final String[] pkNames = this.getPrimaryKeyNames(table);
		if (pkNames.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		return this.structureVisitor.safeDelete(table, pkNames);
	}

	public <T extends DatabaseEntry> String getPreparedUpdateSQL(final AbstractDBTable<?> table) {
		final String[] setColumns = this.getUpdateColumnsNames(table);
		if (setColumns.length == 0) {
			throw new IllegalArgumentException("No columns to update.");
		}

		final String[] whereColumns = this.getPrimaryKeyNames(table);
		if (whereColumns.length == 0) {
			throw new IllegalArgumentException("No primary key defined on " + table.getTargetClass() + "<" + table.getEntryClass() + ">");
		}

		return this.structureVisitor.safeUpdate(table, setColumns, whereColumns);
	}

	@Override
	public <T extends DatabaseEntry> ColumnData[] getPrimaryKeys(final SQLQueryable<T> table) {
		return Arrays.stream(table.getStructure().getColumns()).filter(ColumnData::isPrimaryKey).toArray(ColumnData[]::new);
	}

	@Override
	public <T extends DatabaseEntry> String[] getUpdateColumnsNames(final AbstractDBTable<T> table) {
		return Arrays.stream(this.getUpdateColumns(table)).map(ColumnData::getLocalName).toArray(String[]::new);
	}

	@Override
	public <T extends DatabaseEntry> String[] getUpdateGeneratedColumnsNames(final SQLQueryable<T> table) {
		if (table instanceof AbstractDBTable<?>) {
			return Arrays.stream(PCUtils.combineArrays(this.getPrimaryKeys(table), this.getGeneratedKeys((AbstractDBTable<T>) table)))
					.map(ColumnData::getLocalName)
					.toArray(String[]::new);
		}

		return Arrays.stream(this.getPrimaryKeys(table)).map(ColumnData::getLocalName).toArray(String[]::new);
	}

	@Override
	public <T extends DatabaseEntry> ColumnData[] getUpdateGeneratedColumns(final SQLQueryable<T> table) {
		if (table instanceof AbstractDBTable<?>) {
			return Arrays.stream(PCUtils.combineArrays(this.getPrimaryKeys(table), this.getGeneratedKeys((AbstractDBTable<T>) table)))
					.toArray(ColumnData[]::new);
		}

		return Arrays.stream(this.getPrimaryKeys(table)).toArray(ColumnData[]::new);
	}

	protected <T extends DatabaseEntry> T fillLoad(final Class<T> entryClazz, final ResultSet rs, final FactoryMethod factoryMethod)
			throws SQLException {
		final List<ArgData> mapping = factoryMethod.getArgs();
		final ThrowingFunction<Object[], ? extends DatabaseEntry, DBException> factory = factoryMethod.getFunction();

		try {
			final Object[] params = new Object[mapping.size()];
			for (final ArgData pair : mapping) {
				final String columnName = pair.getName();
				final ColumnData columnData = pair.getColumnData();
				final ColumnType type = columnData.getType();

				try {
					final Object value = type.load(rs, columnName, pair.getType());
					params[pair.getIndex()] = rs.wasNull() ? null : value;
				} catch (final Exception e) {
					throw new DBException(
							"Failed to decode value/update field for: " + columnData.getLocalName() + " as " + columnName + " with value '"
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

	public <T extends DatabaseEntry> ColumnData[] getInsertColumns(final AbstractDBTable<T> table) {
		return Arrays.stream(table.getStructure().getColumns())
				.filter(c -> !c.isGenerated())
				.filter(c -> !c.isAutoIncrement())
				.toArray(ColumnData[]::new);
	}

	public <T extends DatabaseEntry> ColumnData[] getUpdateColumns(final AbstractDBTable<T> table) {
		return Arrays.stream(table.getStructure().getColumns())
				.filter(c -> !c.isGenerated())
				.filter(c -> !c.isAutoIncrement())
				.filter(c -> !c.hasOnUpdate())
				.toArray(ColumnData[]::new);
	}

	@Override
	public <T extends DatabaseEntry> String getTruncateSQL(final AbstractDBTable<T> table) {
		return this.structureVisitor.getTruncateSQL(table);
	}

}
