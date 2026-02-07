package lu.kbra.pclib.db.table;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.table.ConstraintData;
import lu.kbra.pclib.db.autobuild.table.TableName;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.impl.CharacterSetCapable;
import lu.kbra.pclib.db.connector.impl.CollationCapable;
import lu.kbra.pclib.db.connector.impl.EngineCapable;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DataBaseEntry.ReadOnlyDataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLTypeAnnotated;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLBuilder;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class DataBaseTable<T extends DataBaseEntry> implements AbstractDBTable<T>, SQLTypeAnnotated<TableName> {

	protected DataBase dataBase;
	protected DataBaseEntryUtils dbEntryUtils;
	protected TableStructure structure;
	protected Class<? extends AbstractDBTable<T>> tableClass;

	public DataBaseTable(DataBase dataBase) {
		this(dataBase, new BaseDataBaseEntryUtils());
	}

	public DataBaseTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.tableClass = (Class<? extends AbstractDBTable<T>>) getClass();

		gen();
	}

	public DataBaseTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils, Class<? extends AbstractDBTable<T>> tableClass) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.tableClass = tableClass;

		gen();
	}

	protected void gen() {
		structure = dbEntryUtils.scanTable((Class<? extends AbstractDBTable<T>>) this.tableClass);
		structure.update(dataBase.getConnector());
	}

	@Override
	public void requestHook(SQLRequestType type, Object query) {
	}

	@Override
	public boolean exists() throws SQLException {
		final Connection con = connect();

		final DatabaseMetaData dbMetaData = con.getMetaData();

		try (final ResultSet rs = dbMetaData.getTables(dataBase.getDataBaseName(), null, getName(), null)) {
			return rs.next();
		} catch (SQLException e) {
			throw new SQLException("Error retrieving tables.", e);
		}
	}

	@Override
	public DataBaseTableStatus<T, ? extends DataBaseTable<T>> create() throws SQLException {
		final boolean status = exists();
		if ((Boolean) status) {
			return new DataBaseTableStatus<>(true, getQueryable());
		} else {
			final Connection con = connect();

			final Statement stmt = con.createStatement();
			String querySQL = null;

			try {
				final String sql = getCreateSQL();
				querySQL = sql;

				requestHook(SQLRequestType.CREATE_TABLE, sql);

				final int result = stmt.executeUpdate(sql);
			} catch (SQLException e) {
				throw new SQLException("Error executing query: " + querySQL, e);
			} finally {
				PCUtils.close(stmt);
			}

			return new DataBaseTableStatus<>(false, getQueryable());
		}
	}

	@Override
	public DataBaseTable<T> drop() throws SQLException {
		final Connection con = connect();

		final Statement stmt = con.createStatement();
		String querySQL = null;

		try {
			final String sql = "DROP TABLE " + getQualifiedName() + ";";
			querySQL = sql;

			requestHook(SQLRequestType.DROP_TABLE, sql);

			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(stmt);
		}

		return getQueryable();
	}

	@Override
	public int countUniques(T data) throws SQLException {
		final Connection con = connect();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final List<String>[] uniqueKeys = dbEntryUtils.getUniqueKeys(getConstraints(), data);

			query: {
				pstmt = con.prepareStatement(dbEntryUtils.getPreparedSelectCountUniqueSQL(this.getQueryable(), uniqueKeys, data));

				dbEntryUtils.prepareSelectCountUniqueSQL(pstmt, uniqueKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying count by uniques.");
			}

			return result.getInt("count");
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public int countNotNull(T data) throws SQLException {
		final Connection con = connect();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final List<String> notNullKeys = dbEntryUtils.getNotNullKeys(data);

			query: {
				pstmt = con.prepareStatement(dbEntryUtils.getPreparedSelectCountNotNullSQL(this.getQueryable(), notNullKeys, data));

				dbEntryUtils.prepareSelectCountNotNullSQL(pstmt, notNullKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying count by not nulls.");
			}

			final int count = result.getInt("count");

			return count;
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public boolean exists(T data) throws SQLException {
		final Connection con = connect();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			final ColumnData[] primaryKeys = dbEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			query: {
				pstmt = con.prepareStatement(dbEntryUtils.getPreparedSelectSQL(getQueryable(), data), keyColumns);

				dbEntryUtils.prepareSelectSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
			}

			return result.next();
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public boolean existsUniques(T data) throws SQLException {
		return countUniques(data) > 0;
	}

	@Override
	public boolean existsUnique(T data) throws SQLException {
		return countUniques(data) == 1;
	}

	/**
	 * Loads the first unique result, returns null if none is found and throws an exception if too many
	 * are available.
	 */
	@Override
	public Optional<T> loadIfExists(T data) throws SQLException {
		final int count = countUniques(data);
		if (count == 1) {
			return Optional.of(loadUnique(data));
		} else if (count == 0) {
			return Optional.empty();
		} else {
			throw new IllegalStateException("Too many results when loading " + data.getClass().getName() + ".");
		}
	}

	/**
	 * Loads the first unique result, returns a the newly inserted instance if none is found and throws
	 * an exception if too many are available.
	 */
	@Override
	public T loadIfExistsElseInsert(T data) throws SQLException {
		final int count = countUniques(data);
		if (count == 1) {
			return loadUnique(data);
		} else if (count == 0) {
			return insertAndReload(data);
		} else {
			throw new IllegalStateException("Too many results when loading " + data.getClass().getName() + ".");
		}
	}

	/**
	 * Loads the first pk result, returns a the newly inserted instance if none is found
	 */
	public T loadPKIfExistsElseInsert(T data) throws SQLException {
		return exists(data) ? load(data) : insertAndReload(data);
	}

	public Optional<T> loadPKIfExists(T data) throws SQLException {
		return exists(data) ? Optional.of(load(data)) : Optional.empty();
	}

	/**
	 * Loads the first unique result, or throws an exception if none is found.
	 */
	@Override
	public T loadUnique(T data) throws SQLException {
		final Connection con = connect();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final List<String>[] uniqueKeys = dbEntryUtils.getUniqueKeys(getConstraints(), data);

			query: {
				pstmt = con.prepareStatement(dbEntryUtils.getPreparedSelectUniqueSQL(this.getQueryable(), uniqueKeys, data));

				dbEntryUtils.prepareSelectUniqueSQL(pstmt, uniqueKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying by uniques.");
			}

			dbEntryUtils.fillLoad(data, result);
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}

		return data;
	}

	/**
	 * Returns a list of all the possible entries matching with the unique values of the input.
	 */
	@Override
	public List<T> loadByUnique(T data) throws SQLException {
		return query(new PreparedQuery<T>() {
			final List<String>[] uniques = dbEntryUtils.getUniqueKeys(getConstraints(), data);

			@Override
			public String getPreparedQuerySQL(SQLQueryable<T> table) {
				return dbEntryUtils.getPreparedSelectUniqueSQL(getQueryable(), uniques, data);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				dbEntryUtils.prepareSelectUniqueSQL(stmt, uniques, data);
			}

			@Override
			public T clone() {
				return dbEntryUtils.instance(data);
			}

		});
	}

	@Override
	public T insert(T data) throws SQLException {
		if (data instanceof ReadOnlyDataBaseEntry) {
			throw new IllegalStateException("Cannot insert a read-only entry (" + data.getClass().getName() + ").");
		}

		final Connection con = connect();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet generatedKeys = null;
		int result;

		try {
			final ColumnData[] generatedKeysColumns = /*
														 * PCUtils .combineArrays(dbEntryUtils.getPrimaryKeys(data),
														 */dbEntryUtils.getGeneratedKeys(data)/* ) */;
			final String[] keyColumns = Arrays.stream(generatedKeysColumns).map(ColumnData::getName).toArray(String[]::new);

			query: {
				pstmt = con.prepareStatement(dbEntryUtils.getPreparedInsertSQL(getQueryable(), data), keyColumns);

				dbEntryUtils.prepareInsertSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.INSERT, pstmt);

				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't insert data.");
			}

			if (generatedKeysColumns.length != 0) {
				generatedKeys = pstmt.getGeneratedKeys();
				if (!generatedKeys.next()) {
					throw new IllegalStateException("Couldn't get generated keys after insert.");
				}
				dbEntryUtils.fillInsert(data, generatedKeys);
			}
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(generatedKeys, pstmt);
		}

		return data;
	}

	@Override
	public T insertAndReload(T data) throws SQLException {
		return load(insert(data));
	}

	@Override
	public T delete(T data) throws SQLException {
		if (data instanceof ReadOnlyDataBaseEntry) {
			throw new IllegalStateException("Cannot delete a read-only entry (" + data.getClass().getName() + ").");
		}

		final Connection con = connect();

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		try {
			final ColumnData[] primaryKeys = dbEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			query: {
				pstmt = con.prepareStatement(dbEntryUtils.getPreparedDeleteSQL(getQueryable(), data), keyColumns);

				dbEntryUtils.prepareDeleteSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.DELETE, pstmt);

				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't delete data (" + data + ").");
			}
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(pstmt);
		}

		return data;
	}

	@Override
	public Optional<T> deleteIfExists(T data) throws SQLException {
		return exists(data) ? Optional.of(delete(data)) : Optional.empty();
	}

	@Override
	public Optional<T> deleteUnique(T data) throws SQLException {
		return existsUniques(data) ? Optional.of(delete(loadUnique(data))) : Optional.empty();
	}

	@Override
	public List<T> deleteUniques(T data) throws SQLException {
		if (existsUniques(data)) {
			final List<T> list = new ArrayList<>();
			for (T el : loadByUnique(data)) {
				list.add(delete(el));
			}
			return list;
		} else {
			return Arrays.asList();
		}
	}

	@Override
	public T update(final T data) throws SQLException {
		if (data instanceof ReadOnlyDataBaseEntry) {
			throw new IllegalStateException("Cannot update a read-only entry (" + data.getClass().getName() + ").");
		}

		final Connection con = connect();

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		try {
			final ColumnData[] generatedKeysColumns = PCUtils.combineArrays(dbEntryUtils.getPrimaryKeys(data),
					dbEntryUtils.getGeneratedKeys(data));
			final String[] keyColumns = Arrays.stream(generatedKeysColumns).map(ColumnData::getName).toArray(String[]::new);

			query: {
				pstmt = con.prepareStatement(dbEntryUtils.getPreparedUpdateSQL(getQueryable(), data), keyColumns);

				dbEntryUtils.prepareUpdateSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.UPDATE, pstmt);

				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't update data.");
			}
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(pstmt);
		}

		return data;
	}

	@Override
	public T updateAndReload(T data) throws SQLException {
		return load(update(data));
	}

	@Override
	public T load(final T data) throws SQLException {
		final Connection con = connect();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			final ColumnData[] primaryKeys = dbEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			query: {
				pstmt = con.prepareStatement(dbEntryUtils.getPreparedSelectSQL(getQueryable(), data), keyColumns);

				dbEntryUtils.prepareSelectSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.INSERT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			dbEntryUtils.fillLoad(data, result);
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}

		return data;
	}

	@Override
	public <B> B query(SQLQuery<T, B> query) throws SQLException {
		final Connection con = connect();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = query.toString();

		try {
			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				pstmt = con.prepareStatement(safeQuery.getPreparedQuerySQL(getQueryable()));

				safeQuery.updateQuerySQL(pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final List<T> output = new ArrayList<>();
				dbEntryUtils.fillLoadAllTable(getTargetClass(), query, result, output::add);

				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				pstmt = con.prepareStatement(safeTransQuery.getPreparedQuerySQL(getQueryable()));

				safeTransQuery.updateQuerySQL(pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final B output = safeTransQuery.transform(result);

				return output;
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				pstmt = con.prepareStatement(safeTransQuery.getPreparedQuerySQL(getQueryable()));

				safeTransQuery.updateQuerySQL(pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final List<T> output = new ArrayList<>();
				dbEntryUtils.fillLoadAllTable(getTargetClass(), query, result, output::add);

				final B filteredOutput = safeTransQuery.transform(output);

				return filteredOutput;
			} else {
				throw new IllegalArgumentException("Unsupported type: " + query.getClass().getName());
			}
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public int count() throws SQLException {
		final Connection con = connect();

		final Statement stmt = con.createStatement();
		String querySQL = null;
		ResultSet result = null;

		try {
			final String sql = SQLBuilder.count(getQueryable());
			querySQL = sql;

			requestHook(SQLRequestType.SELECT, sql);

			result = stmt.executeQuery(sql);

			if (!result.next()) {
				throw new IllegalStateException("Couldn't query entry count.");
			}

			final int count = result.getInt("count");
			return count;
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, stmt);
		}
	}

	@Override
	public int clear() throws SQLException {
		final Connection con = connect();

		final Statement stmt = con.createStatement();
		String querySQL = null;

		try {
			final String sql = "DELETE FROM " + getQualifiedName() + ";";
			querySQL = sql;

			requestHook(SQLRequestType.DELETE, sql);

			final int result = stmt.executeUpdate(sql);
			return result;
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(stmt);
		}
	}

	@Override
	public int truncate() throws SQLException {
		final Connection con = connect();

		final Statement stmt = con.createStatement();
		String querySQL = null;

		try {
			final String sql = "TRUNCATE TABLE " + getQualifiedName() + ";";
			querySQL = sql;

			requestHook(SQLRequestType.TRUNCATE, sql);

			final int result = stmt.executeUpdate(sql);
			return result;
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(stmt);
		}
	}

	@Override
	public String getCreateSQL() {
		return structure.build();
	}

	protected DataBaseTable<T> getQueryable() {
		return this;
	}

	@Override
	public String getName() {
		return structure.getName();
	}

	@Override
	public String getQualifiedName() {
		return "`" + dataBase.getDataBaseName() + "`.`" + getName() + "`";
	}

	@Override
	public TableName getTypeAnnotation() {
		return tableClass.getAnnotation(TableName.class);
	}

	@Override
	public Class<? extends SQLQueryable<T>> getTargetClass() {
		return getTableClass();
	}

	public Class<? extends AbstractDBTable<T>> getTableClass() {
		return tableClass;
	}

	public Class<DataBaseEntry> getEntryType() {
		return this.getDbEntryUtils().getEntryType(getTableClass());
	}

	public ColumnData[] getColumns() {
		return structure.getColumns();
	}

	public String getCharacterSet() {
		return structure.getCharacterSet().equals("") && dataBase.getConnector() instanceof CharacterSetCapable
				? ((CharacterSetCapable) dataBase.getConnector()).getCharacterSet()
				: structure.getCharacterSet();
	}

	public String getCollation() {
		return structure.getCollation().equals("") && dataBase.getConnector() instanceof CollationCapable
				? ((CollationCapable) dataBase.getConnector()).getCollation()
				: structure.getCollation();
	}

	public String getEngine() {
		return structure.getEngine().equals("") && dataBase.getConnector() instanceof EngineCapable
				? ((EngineCapable) dataBase.getConnector()).getEngine()
				: structure.getEngine();
	}

	public ConstraintData[] getConstraints() {
		return structure.getConstraints();
	}

	public String[] getColumnNames() {
		return Arrays.stream(structure.getColumns()).map((c) -> c.getName()).toArray(String[]::new);
	}

	public String[] getPrimaryKeysNames() {
		return Arrays.stream(this.getDbEntryUtils().getPrimaryKeys(this.getEntryType()))
				.map(c -> c.getEscapedName())
				.toArray(String[]::new);
	}

	protected Connection connect() throws SQLException {
		return dataBase.getConnector().connect();
	}

	protected Connection createConnection() throws SQLException {
		return dataBase.getConnector().createConnection();
	}

	@Override
	public DataBase getDataBase() {
		return dataBase;
	}

	@Override
	public DataBaseEntryUtils getDbEntryUtils() {
		return dbEntryUtils;
	}

	public void setDbEntryUtils(DataBaseEntryUtils dbEntryUtils) {
		this.dbEntryUtils = dbEntryUtils;
	}

	public static class DataBaseTableStatus<T extends DataBaseEntry, B extends AbstractDBTable<T>> {
		private final boolean existed;
		private final B table;

		protected DataBaseTableStatus(boolean existed, B table) {
			this.existed = existed;
			this.table = table;
		}

		public boolean existed() {
			return existed;
		}

		public boolean created() {
			return !existed;
		}

		public B getQueryable() {
			return table;
		}

		@Override
		public String toString() {
			return "DataBaseTableStatus@" + System.identityHashCode(this) + " [existed=" + existed + ", table=" + table + "]";
		}

	}

	@Override
	public String toString() {
		return "DataBaseTable@" + System.identityHashCode(this) + " [dataBase=" + dataBase + ", dbEntryUtils=" + dbEntryUtils
				+ ", structure=" + structure + ", tableClass=" + tableClass + "]";
	}

}
