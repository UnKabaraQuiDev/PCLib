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
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.table.ConstraintData;
import lu.kbra.pclib.db.autobuild.table.TableName;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.AbstractDataBaseConnector.CachedConnection.ConnectionHolder;
import lu.kbra.pclib.db.connector.impl.CharacterSetCapable;
import lu.kbra.pclib.db.connector.impl.CollationCapable;
import lu.kbra.pclib.db.connector.impl.EngineCapable;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DataBaseEntry.ReadOnlyDataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.table.transaction.DBTableTransaction;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLTypeAnnotated;
import lu.kbra.pclib.db.impl.TransactionSQLHookable;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLBuilder;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class DataBaseTable<T extends DataBaseEntry> implements AbstractDBTable<T>, SQLTypeAnnotated<TableName>, TransactionSQLHookable<T> {

	protected DataBase dataBase;
	protected DataBaseEntryUtils dbEntryUtils;
	protected TableStructure structure;
	protected Class<? extends AbstractDBTable<T>> tableClass;

	protected DataBaseTable() {
	}

	public DataBaseTable(final DataBase dataBase) {
		this(dataBase, dataBase.getDataBaseEntryUtils());
	}

	public DataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.tableClass = (Class<? extends AbstractDBTable<T>>) this.getClass();

		this.gen();
	}

	public DataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBTable<T>> tableClass) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.tableClass = tableClass;

		this.gen();
	}

	protected void gen() {
		this.structure = this.dbEntryUtils.scanTable((Class<? extends AbstractDBTable<T>>) this.tableClass);
		this.structure.update(this.dataBase.getConnector());
		this.dataBase.registerTableBean(this);
	}

	@Override
	public void requestHook(final SQLRequestType type, final Object query) {
	}

	@Override
	public void requestHook(final DBTableTransaction<T> transaction, final SQLRequestType type, final Object query) {
	}

	@Override
	public DBTableTransaction<T> createTransaction() throws DBException {
		return new TableTransaction();
	}

	@Override
	public boolean exists() throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.exists(c);
		}
	}

	public boolean exists(final Connection c) throws DBException {
		try {
			final DatabaseMetaData dbMetaData = c.getMetaData();

			try (final ResultSet rs = dbMetaData.getTables(this.dataBase.getDataBaseName(), null, this.getName(), null)) {
				return rs.next();
			}
		} catch (final SQLException e) {
			throw new DBException("Error retrieving tables.", e);
		}
	}

	@Override
	public DataBaseTableStatus<T, ? extends DataBaseTable<T>> create() throws DBException {
		this.dataBase.getConnector().reset();

		try (ConnectionHolder c = this.use()) {
			return this.create(c);
		}
	}

	public DataBaseTableStatus<T, ? extends DataBaseTable<T>> create(final Connection c) throws DBException {
		if (this.exists(c)) {
			return new DataBaseTableStatus<>(true, this.getQueryable());
		} else {
			String querySQL = null;

			try (Statement stmt = c.createStatement()) {
				final String sql = this.getCreateSQL();
				querySQL = sql;

				this.requestHook(SQLRequestType.CREATE_TABLE, sql);

				final int result = stmt.executeUpdate(sql);
//				if (result == 0) {
//					throw new DBException("Failed to create table.");
//				}
			} catch (final SQLException e) {
				throw new DBException("Error executing query: " + querySQL, e);
			}

			return new DataBaseTableStatus<>(false, this.getQueryable());
		}
	}

	@Override
	public DataBaseTable<T> drop() throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.drop(c);
		}
	}

	public DataBaseTable<T> drop(final Connection c) throws DBException {
		String querySQL = null;

		try (Statement stmt = c.createStatement()) {
			final String sql = "DROP TABLE " + this.getQualifiedName() + ";";
			querySQL = sql;

			this.requestHook(SQLRequestType.DROP_TABLE, sql);

			stmt.executeUpdate(sql);
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		}

		return this.getQueryable();
	}

	@Override
	public int countUniques(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.countUniques(c, data);
		}
	}

	public int countUniques(final Connection c, final T data) throws DBException {
		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final List<String>[] uniqueKeys = this.dbEntryUtils.getUniqueKeys(this.getConstraints(), data);

			query: {
				pstmt = c.prepareStatement(this.dbEntryUtils.getPreparedSelectCountUniqueSQL(this.getQueryable(), uniqueKeys, data));

				this.dbEntryUtils.prepareSelectCountUniqueSQL(pstmt, uniqueKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying count by uniques.");
			}

			return result.getInt("count");
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public int countNotNull(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.countNotNull(c, data);
		}
	}

	public int countNotNull(final Connection c, final T data) throws DBException {
		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final List<String> notNullKeys = this.dbEntryUtils.getNotNullKeys(data);

			query: {
				pstmt = c.prepareStatement(this.dbEntryUtils.getPreparedSelectCountNotNullSQL(this.getQueryable(), notNullKeys, data));

				this.dbEntryUtils.prepareSelectCountNotNullSQL(pstmt, notNullKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying count by not nulls.");
			}

			final int count = result.getInt("count");

			return count;
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public boolean exists(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.exists(c, data);
		}
	}

	public boolean exists(final Connection c, final T data) throws DBException {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			final ColumnData[] primaryKeys = this.dbEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			query: {
				pstmt = c.prepareStatement(this.dbEntryUtils.getPreparedSelectSQL(this.getQueryable(), data), keyColumns);

				this.dbEntryUtils.prepareSelectSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
			}

			return result.next();
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public boolean existsUniques(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.existsUniques(c, data);
		}
	}

	public boolean existsUniques(final Connection c, final T data) throws DBException {
		return this.countUniques(c, data) > 0;
	}

	@Override
	public boolean existsUnique(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.existsUnique(c, data);
		}
	}

	public boolean existsUnique(final Connection c, final T data) throws DBException {
		return this.countUniques(c, data) == 1;
	}

	/**
	 * Loads the first unique result, returns null if none is found and throws an exception if too many
	 * are available.
	 */
	@Override
	public Optional<T> loadUniqueIfExists(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.loadUniqueIfExists(c, data);
		}
	}

	public Optional<T> loadUniqueIfExists(final Connection c, final T data) throws DBException {
		final int count = this.countUniques(c, data);
		if (count == 1) {
			return Optional.of(this.loadUnique(c, data));
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
	public T loadUniqueIfExistsElseInsert(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.loadUniqueIfExistsElseInsert(c, data);
		}
	}

	public T loadUniqueIfExistsElseInsert(final Connection c, final T data) throws DBException {
		final int count = this.countUniques(c, data);
		if (count == 1) {
			return this.loadUnique(c, data);
		} else if (count == 0) {
			return this.insertAndReload(c, data);
		} else {
			throw new IllegalStateException("Too many results when loading " + data.getClass().getName() + ".");
		}
	}

	/**
	 * Loads the first pk result, returns a the newly inserted instance if none is found
	 */
	public T loadIfExistsElseInsert(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.loadIfExistsElseInsert(c, data);
		}
	}

	public T loadIfExistsElseInsert(final Connection c, final T data) throws DBException {
		return this.exists(c, data) ? this.load(c, data) : this.insertAndReload(c, data);
	}

	public Optional<T> loadIfExists(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.loadIfExists(c, data);
		}
	}

	public Optional<T> loadIfExists(final Connection c, final T data) throws DBException {
		return this.exists(c, data) ? Optional.of(this.load(c, data)) : Optional.empty();
	}

	/**
	 * Loads the first unique result, or throws an exception if none is found.
	 */
	@Override
	public T loadUnique(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.loadUnique(c, data);
		}
	}

	public T loadUnique(final Connection c, final T data) throws DBException {
		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final List<String>[] uniqueKeys = this.dbEntryUtils.getUniqueKeys(this.getConstraints(), data);

			query: {
				pstmt = c.prepareStatement(this.dbEntryUtils.getPreparedSelectUniqueSQL(this.getQueryable(), uniqueKeys, data));

				this.dbEntryUtils.prepareSelectUniqueSQL(pstmt, uniqueKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying by uniques.");
			}

			this.dbEntryUtils.fillLoad(data, result);
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}

		return data;
	}

	/**
	 * Returns a list of all the possible entries matching with the unique values of the input.
	 */
	@Override
	public List<T> loadByUnique(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.loadByUnique(c, data);
		}
	}

	public List<T> loadByUnique(final Connection c, final T data) throws DBException {
		return this.query(c, new PreparedQuery<T>() {
			final List<String>[] uniques = DataBaseTable.this.dbEntryUtils.getUniqueKeys(DataBaseTable.this.getConstraints(), data);

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<T> table) {
				return DataBaseTable.this.dbEntryUtils.getPreparedSelectUniqueSQL(DataBaseTable.this.getQueryable(), this.uniques, data);
			}

			@Override
			public void updateQuerySQL(final PreparedStatement stmt) throws SQLException {
				DataBaseTable.this.dbEntryUtils.prepareSelectUniqueSQL(stmt, this.uniques, data);
			}

			@Override
			public T clone() {
				return DataBaseTable.this.dbEntryUtils.instance(data);
			}

		});
	}

	@Override
	public T insert(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.insert(c, data);
		}
	}

	public T insert(final Connection c, final T data) throws DBException {
		if (data instanceof ReadOnlyDataBaseEntry) {
			throw new IllegalStateException("Cannot insert a read-only entry (" + data.getClass().getName() + ").");
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet generatedKeys = null;
		int result;

		try {
			final ColumnData[] generatedKeysColumns = this.dbEntryUtils.getGeneratedKeys(data);
			final String[] keyColumns = Arrays.stream(generatedKeysColumns).map(ColumnData::getName).toArray(String[]::new);

			query: {
				pstmt = c.prepareStatement(this.dbEntryUtils.getPreparedInsertSQL(this.getQueryable(), data), keyColumns);

				this.dbEntryUtils.prepareInsertSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.INSERT, pstmt);

				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't insert data.");
			}

			if (generatedKeysColumns.length != 0) {
				generatedKeys = pstmt.getGeneratedKeys();
				if (!generatedKeys.next()) {
					throw new IllegalStateException(
							"Couldn't get generated keys after insert (" + Arrays.toString(generatedKeysColumns) + ").");
				}
				this.dbEntryUtils.fillInsert(data, generatedKeys);
			}
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(generatedKeys, pstmt);
		}

		return data;
	}

	@Override
	public T insertAndReload(final T data) throws DBException {
		return this.load(this.insert(data));
	}

	public T insertAndReload(final Connection c, final T data) throws DBException {
		return this.load(c, this.insert(c, data));
	}

	@Override
	public T delete(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.delete(c, data);
		}
	}

	public T delete(final Connection c, final T data) throws DBException {
		if (data instanceof ReadOnlyDataBaseEntry) {
			throw new IllegalStateException("Cannot delete a read-only entry (" + data.getClass().getName() + ").");
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		try {
			final ColumnData[] primaryKeys = this.dbEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			query: {
				pstmt = c.prepareStatement(this.dbEntryUtils.getPreparedDeleteSQL(this.getQueryable(), data), keyColumns);

				this.dbEntryUtils.prepareDeleteSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.DELETE, pstmt);

				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't delete data (" + data + ").");
			}
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(pstmt);
		}

		return data;
	}

	@Override
	public Optional<T> deleteIfExists(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.deleteIfExists(c, data);
		}
	}

	public Optional<T> deleteIfExists(final Connection c, final T data) throws DBException {
		return this.exists(c, data) ? Optional.of(this.delete(c, data)) : Optional.empty();
	}

	@Override
	public Optional<T> deleteUnique(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.deleteUnique(c, data);
		}
	}

	public Optional<T> deleteUnique(final Connection c, final T data) throws DBException {
		return this.existsUniques(c, data) ? Optional.of(this.delete(c, this.loadUnique(c, data))) : Optional.empty();
	}

	@Override
	public List<T> deleteUniques(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.deleteUniques(c, data);
		}
	}

	public List<T> deleteUniques(final Connection c, final T data) throws DBException {
		if (this.existsUniques(data)) {
			final List<T> list = new ArrayList<>();
			for (final T el : this.loadByUnique(c, data)) {
				list.add(this.delete(c, el));
			}
			return list;
		} else {
			return Arrays.asList();
		}
	}

	@Override
	public T update(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.update(c, data);
		}
	}

	public T update(final Connection c, final T data) throws DBException {
		if (data instanceof ReadOnlyDataBaseEntry) {
			throw new IllegalStateException("Cannot update a read-only entry (" + data.getClass().getName() + ").");
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		try {
			final ColumnData[] generatedKeysColumns = PCUtils.combineArrays(this.dbEntryUtils.getPrimaryKeys(data),
					this.dbEntryUtils.getGeneratedKeys(data));
			final String[] keyColumns = Arrays.stream(generatedKeysColumns).map(ColumnData::getName).toArray(String[]::new);

			query: {
				pstmt = c.prepareStatement(this.dbEntryUtils.getPreparedUpdateSQL(this.getQueryable(), data), keyColumns);

				this.dbEntryUtils.prepareUpdateSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.UPDATE, pstmt);

				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't update data.");
			}
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(pstmt);
		}

		return data;
	}

	@Override
	public T updateAndReload(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.updateAndReload(c, data);
		}
	}

	public T updateAndReload(final Connection c, final T data) throws DBException {
		return this.load(c, this.update(c, data));
	}

	@Override
	public T load(final T data) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.load(c, data);
		}
	}

	public T load(final Connection c, final T data) throws DBException {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			final ColumnData[] primaryKeys = this.dbEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			query: {
				pstmt = c.prepareStatement(this.dbEntryUtils.getPreparedSelectSQL(this.getQueryable(), data), keyColumns);

				this.dbEntryUtils.prepareSelectSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.INSERT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			this.dbEntryUtils.fillLoad(data, result);
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}

		return data;
	}

	@Override
	public <B> B query(final SQLQuery<T, B> query) throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.query(c, query);
		}
	}

	public <B> B query(final Connection c, final SQLQuery<T, B> query) throws DBException {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = query.toString();

		try {
			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				pstmt = c.prepareStatement(safeQuery.getPreparedQuerySQL(this.getQueryable()));

				safeQuery.updateQuerySQL(pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final List<T> output = new ArrayList<>();
				this.dbEntryUtils.fillLoadAllTable(this.getTargetClass(), query, result, output::add);

				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final B output = safeTransQuery.transform(result);

				return output;
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final List<T> output = new ArrayList<>();
				this.dbEntryUtils.fillLoadAllTable(this.getTargetClass(), query, result, output::add);

				final B filteredOutput = safeTransQuery.transform(output);

				return filteredOutput;
			} else {
				throw new IllegalArgumentException("Unsupported type: " + query.getClass().getName());
			}
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public int count() throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.count(c);
		}
	}

	public int count(final Connection c) throws DBException {
		String querySQL = null;
		ResultSet result = null;

		try (Statement stmt = c.createStatement()) {
			final String sql = SQLBuilder.count(this.getQueryable());
			querySQL = sql;

			this.requestHook(SQLRequestType.SELECT, sql);

			result = stmt.executeQuery(sql);

			if (!result.next()) {
				throw new IllegalStateException("Couldn't query entry count.");
			}

			final int count = result.getInt("count");
			return count;
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result);
		}
	}

	@Override
	public int clear() throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.clear(c);
		}
	}

	public int clear(final Connection c) throws DBException {
		String querySQL = null;

		try (Statement stmt = c.createStatement()) {
			final String sql = "DELETE FROM " + this.getQualifiedName() + ";";
			querySQL = sql;

			this.requestHook(SQLRequestType.DELETE, sql);

			final int result = stmt.executeUpdate(sql);
			return result;
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		}
	}

	@Override
	public int truncate() throws DBException {
		try (ConnectionHolder c = this.use()) {
			return this.truncate(c);
		}
	}

	public int truncate(final Connection c) throws DBException {
		final int previousCount = this.count();

		String querySQL = null;

		try (Statement stmt = c.createStatement()) {
			final String sql = "TRUNCATE TABLE " + this.getQualifiedName() + ";";
			querySQL = sql;

			this.requestHook(SQLRequestType.TRUNCATE, sql);

			stmt.executeUpdate(sql);

			return previousCount - this.count();
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		}
	}

	@Override
	public String getCreateSQL() {
		return this.structure.build(this.getDataBase().getConnector());
	}

	protected DataBaseTable<T> getQueryable() {
		return this;
	}

	@Override
	public String getName() {
		return this.structure.getName();
	}

	@Override
	public String getQualifiedName() {
		return "`" + this.dataBase.getDataBaseName() + "`.`" + this.getName() + "`";
	}

	@Override
	public TableName getTypeAnnotation() {
		return this.tableClass.getAnnotation(TableName.class);
	}

	@Override
	public Class<? extends SQLQueryable<T>> getTargetClass() {
		return this.getTableClass();
	}

	public Class<? extends AbstractDBTable<T>> getTableClass() {
		return this.tableClass;
	}

	public Class<DataBaseEntry> getEntryType() {
		return this.getDbEntryUtils().getEntryType(this.getTableClass());
	}

	public ColumnData[] getColumns() {
		return this.structure.getColumns();
	}

	public String getCharacterSet() {
		return this.structure.getCharacterSet().equals("") && this.dataBase.getConnector() instanceof CharacterSetCapable
				? ((CharacterSetCapable) this.dataBase.getConnector()).getCharacterSet()
				: this.structure.getCharacterSet();
	}

	public String getCollation() {
		return this.structure.getCollation().equals("") && this.dataBase.getConnector() instanceof CollationCapable
				? ((CollationCapable) this.dataBase.getConnector()).getCollation()
				: this.structure.getCollation();
	}

	public String getEngine() {
		return this.structure.getEngine().equals("") && this.dataBase.getConnector() instanceof EngineCapable
				? ((EngineCapable) this.dataBase.getConnector()).getEngine()
				: this.structure.getEngine();
	}

	public ConstraintData[] getConstraints() {
		return this.structure.getConstraints();
	}

	public String[] getColumnNames() {
		return Arrays.stream(this.structure.getColumns()).map((c) -> c.getName()).toArray(String[]::new);
	}

	@Override
	public String[] getPrimaryKeysNames() {
		return Arrays.stream(this.getDbEntryUtils().getPrimaryKeys(this.getEntryType()))
				.map(c -> c.getEscapedName())
				.toArray(String[]::new);
	}

	@Deprecated
	protected Connection connect() throws DBException {
		return this.dataBase.getConnector().connect();
	}

	protected ConnectionHolder use() throws DBException {
		return this.dataBase.getConnector().use();
	}

	@Deprecated
	protected Connection createConnection() throws DBException {
		return this.dataBase.getConnector().createConnection();
	}

	@Override
	public DataBase getDataBase() {
		return this.dataBase;
	}

	@Override
	public DataBaseEntryUtils getDbEntryUtils() {
		return this.dbEntryUtils;
	}

	public void setDbEntryUtils(final DataBaseEntryUtils dbEntryUtils) {
		this.dbEntryUtils = dbEntryUtils;
	}

	public abstract class AbstractTableTransaction implements DBTableTransaction<T> {

		protected final ReentrantLock lock = new ReentrantLock(true);

		protected volatile boolean closed = false;
		protected volatile boolean completed = false;

		protected final Connection connection;

		public AbstractTableTransaction(final Connection connection) {
			this.connection = connection;

			try {
				connection.setAutoCommit(false);
			} catch (final SQLException e) {
				throw new DBException("Couldn't configure connection for transaction.", e);
			}
		}

		public AbstractTableTransaction() {
			this(DataBaseTable.this.createConnection());
		}

		protected void ensureOpen() {
			if (this.closed) {
				throw new IllegalStateException("Transaction already closed.");
			}
		}

		protected <B> B executeLocked(final Supplier<B> action) throws DBException {
			this.lock.lock();
			try {
				this.ensureOpen();
				return action.get();
			} finally {
				this.lock.unlock();
			}
		}

		protected void executeLocked(final Runnable action) throws DBException {
			this.lock.lock();
			try {
				this.ensureOpen();
				action.run();
			} finally {
				this.lock.unlock();
			}
		}

		@Override
		public void commit() throws DBException {
			this.executeLocked(() -> {
				try {
					this.connection.commit();
					this.completed = true;
				} catch (final SQLException e) {
					throw new DBException("Couldn't commit transaction.", e);
				}
			});
		}

		@Override
		public void rollback() throws DBException {
			this.executeLocked(() -> {
				try {
					this.connection.rollback();
					this.completed = true;
				} catch (final SQLException e) {
					throw new DBException("Couldn't rollback transaction.", e);
				}
			});
		}

		@Override
		public boolean isClosed() {
			return this.closed;
		}

		@Override
		public void close() throws DBException {
			this.lock.lock();
			try {
				if (this.closed) {
					return;
				}

				try {
					if (!this.completed) {
						this.connection.rollback();
						this.completed = true;
					}
				} catch (final SQLException e) {
					throw new DBException("Couldn't rollback transaction during close.", e);
				} finally {
					try {
						this.connection.close();
					} catch (final SQLException e) {
						throw new DBException("Couldn't close transaction connection.", e);
					} finally {
						this.closed = true;
					}
				}
			} finally {
				this.lock.unlock();
			}
		}

		@Override
		public String toString() {
			return "AbstractTableTransaction@" + System.identityHashCode(this) + " [lock=" + lock + ", closed=" + closed + ", completed="
					+ completed + ", connection=" + connection + "]";
		}

	}

	public class TableTransaction extends AbstractTableTransaction {

		public TableTransaction(Connection connection) {
			super(connection);
		}

		public TableTransaction() {
		}

		@Override
		public Connection getConnection() {
			return this.connection;
		}

		@Override
		public void requestHook(final SQLRequestType type, final Object query) {
			DataBaseTable.this.requestHook(this, type, query);
		}

		@Override
		public String getName() {
			return DataBaseTable.this.getName();
		}

		@Override
		public String getQualifiedName() {
			return DataBaseTable.this.getQualifiedName();
		}

		@Override
		public int count() throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.count(this.connection));
		}

		@Override
		public <B> B query(final SQLQuery<T, B> query) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.query(this.connection, query));
		}

		@Override
		public Class<? extends SQLQueryable<T>> getTargetClass() {
			return DataBaseTable.this.getTargetClass();
		}

		@Override
		public DataBaseEntryUtils getDbEntryUtils() {
			return DataBaseTable.this.getDbEntryUtils();
		}

		@Override
		public DBTableTransaction<T> createTransaction() throws DBException {
			return DataBaseTable.this.createTransaction();
		}

		@Override
		public int truncate() throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.truncate(this.connection));
		}

		@Override
		public int clear() throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.clear(this.connection));
		}

		@Override
		public T load(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.load(this.connection, data));
		}

		@Override
		public T updateAndReload(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.updateAndReload(this.connection, data));
		}

		@Override
		public T update(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.update(this.connection, data));
		}

		@Override
		public Optional<T> deleteIfExists(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.deleteIfExists(this.connection, data));
		}

		@Override
		public Optional<T> deleteUnique(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.deleteUnique(this.connection, data));
		}

		@Override
		public List<T> deleteUniques(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.deleteUniques(this.connection, data));
		}

		@Override
		public T delete(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.delete(this.connection, data));
		}

		@Override
		public T insertAndReload(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.insertAndReload(this.connection, data));
		}

		@Override
		public T insert(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.insert(this.connection, data));
		}

		@Override
		public List<T> loadByUnique(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.loadByUnique(this.connection, data));
		}

		@Override
		public T loadUnique(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.loadUnique(this.connection, data));
		}

		@Override
		public T loadUniqueIfExistsElseInsert(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.loadUniqueIfExistsElseInsert(this.connection, data));
		}

		@Override
		public Optional<T> loadUniqueIfExists(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.loadUniqueIfExists(this.connection, data));
		}

		@Override
		public boolean exists(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.exists(this.connection, data));
		}

		@Override
		public boolean existsUniques(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.existsUniques(this.connection, data));
		}

		@Override
		public boolean existsUnique(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.existsUnique(this.connection, data));
		}

		@Override
		public int countUniques(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.countUniques(this.connection, data));
		}

		@Override
		public int countNotNull(final T data) throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.countNotNull(this.connection, data));
		}

		@Override
		public AbstractDBTable<T> drop() throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.drop(this.connection));
		}

		@Override
		public DataBaseTableStatus<T, ? extends AbstractDBTable<T>> create() throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.create(this.connection));
		}

		@Override
		public boolean exists() throws DBException {
			return this.executeLocked(() -> DataBaseTable.this.exists(this.connection));
		}

		@Override
		public DataBase getDataBase() {
			return DataBaseTable.this.getDataBase();
		}

		@Override
		public String getCreateSQL() {
			return DataBaseTable.this.getCreateSQL();
		}

		@Override
		public String[] getPrimaryKeysNames() {
			return DataBaseTable.this.getPrimaryKeysNames();
		}

		@Override
		public String toString() {
			return "TableTransaction@" + System.identityHashCode(this) + " [lock=" + lock + ", closed=" + closed + ", completed="
					+ completed + ", connection=" + connection + "]";
		}

	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "<DataBaseTable@" + System.identityHashCode(this) + " [dataBase=" + this.dataBase
				+ ", dbEntryUtils=" + this.dbEntryUtils + ", structure=" + this.structure + ", tableClass=" + this.tableClass + "]";
	}

}
