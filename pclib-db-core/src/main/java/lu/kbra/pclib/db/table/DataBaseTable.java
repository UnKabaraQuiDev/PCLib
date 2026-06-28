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

import lombok.Getter;
import lombok.ToString;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DataBaseEntry.ReadOnlyDataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.SQLRequestType;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

@ToString
public class DataBaseTable<T extends DataBaseEntry> implements AbstractDBTable<T> {

	@Getter
	protected DataBase database;
	@Getter
	protected DataBaseEntryUtils dataBaseEntryUtils;
	@Getter
	protected TableStructure tableStructure;
	@Getter
	protected Class<? extends AbstractDBTable<T>> tableClass;

	public DataBaseTable(final DataBase dataBase) {
		this(dataBase, dataBase.getDataBaseEntryUtils());
	}

	public DataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		this.database = dataBase;
		this.dataBaseEntryUtils = dbEntryUtils;
		this.tableClass = (Class<? extends AbstractDBTable<T>>) this.getClass();

		this.gen();
	}

	public DataBaseTable(
			final DataBase dataBase,
			final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBTable<T>> tableClass) {
		this.database = dataBase;
		this.dataBaseEntryUtils = dbEntryUtils;
		this.tableClass = tableClass;

		this.gen();
	}

	protected DataBaseTable() {
	}

	@Override
	public int clear() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.clear(c);
		}
	}

	@Override
	public int count() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.count(c);
		}
	}

	@Override
	public int countNotNull(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.countNotNull(c, data);
		}
	}

	@Override
	public int countUniques(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.countUniques(c, data);
		}
	}

	@Override
	public DataBaseTableStatus<T, ? extends DataBaseTable<T>> create() throws DBException {
		this.getConnector().reset();

		try (AbstractConnection c = this.use()) {
			return this.create(c);
		}
	}

	public DataBaseTable<T> createProxy(final Connection connection) {
		return new DBTableProxy<>(this, connection);
	}

	@Override
	public T delete(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.delete(c, data);
		}
	}

	@Override
	public Optional<T> deleteIfExists(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.deleteIfExists(c, data);
		}
	}

	@Override
	public Optional<T> deleteUnique(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.deleteUnique(c, data);
		}
	}

	@Override
	public List<T> deleteUniques(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.deleteUniques(c, data);
		}
	}

	@Override
	public DataBaseTable<T> drop() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.drop(c);
		}
	}

	@Override
	public boolean exists() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.exists(c);
		}
	}

	@Override
	public boolean exists(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.exists(c, data);
		}
	}

	@Override
	public boolean existsUnique(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.existsUnique(c, data);
		}
	}

	@Override
	public boolean existsUniques(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.existsUniques(c, data);
		}
	}

	public String[] getColumnNames() {
		return Arrays.stream(this.tableStructure.getColumns()).map(ColumnData::getName).toArray(String[]::new);
	}

	public ColumnData[] getColumns() {
		return this.tableStructure.getColumns();
	}

	@Override
	public DataBaseConnector getConnector() {
		return this.database.getConnector();
	}

	public ConstraintData[] getConstraints() {
		return this.tableStructure.getConstraints();
	}

	@Override
	public String getCreateSQL() {
		return this.dataBaseEntryUtils.getStructureVisitor().create(this.tableStructure);
	}

	public Class<DataBaseEntry> getEntryType() {
		return this.getDataBaseEntryUtils().getEntryType(this.getTableClass());
	}

	@Override
	public String getName() {
		return this.tableStructure.getName();
	}

	@Override
	public String[] getPrimaryKeysNames() {
		return this.dataBaseEntryUtils.getPrimaryKeysNames(this.getTableStructure().getEntryClass());
	}

	@Override
	public String getQualifiedName() {
		return this.dataBaseEntryUtils.getStructureVisitor().qualifiedName(this);
	}

	@Override
	public Class<? extends SQLQueryable<T>> getTargetClass() {
		return this.getTableClass();
	}

	@Override
	public T insert(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.insert(c, data);
		}
	}

	@Override
	public T insertAndReload(final T data) throws DBException {
		return this.load(this.insert(data));
	}

	@Override
	public T load(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.load(c, data);
		}
	}

	/**
	 * Returns a list of all the possible entries matching with the unique values of the input.
	 */
	@Override
	public List<T> loadByUnique(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadByUnique(c, data);
		}
	}

	public Optional<T> loadIfExists(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadIfExists(c, data);
		}
	}

	/**
	 * Loads the first pk result, returns a the newly inserted instance if none is found
	 */
	public T loadIfExistsElseInsert(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadIfExistsElseInsert(c, data);
		}
	}

	/**
	 * Loads the first unique result, or throws an exception if none is found.
	 */
	@Override
	public T loadUnique(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadUnique(c, data);
		}
	}

	/**
	 * Loads the first unique result, returns null if none is found and throws an exception if too many
	 * are available.
	 */
	@Override
	public Optional<T> loadUniqueIfExists(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadUniqueIfExists(c, data);
		}
	}

	/**
	 * Loads the first unique result, returns a the newly inserted instance if none is found and throws
	 * an exception if too many are available.
	 */
	@Override
	public T loadUniqueIfExistsElseInsert(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadUniqueIfExistsElseInsert(c, data);
		}
	}

	@Override
	public <B> B query(final SQLQuery<T, B> query) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.query(c, query);
		}
	}

	@Override
	public void requestHook(final SQLRequestType type, final Object query) {
	}

	public void setDbEntryUtils(final DataBaseEntryUtils dbEntryUtils) {
		this.dataBaseEntryUtils = dbEntryUtils;
	}

	@Override
	public int truncate() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.truncate(c);
		}
	}

	@Override
	public T update(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.update(c, data);
		}
	}

	@Override
	public T updateAndReload(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.updateAndReload(c, data);
		}
	}

	protected int clear(final Connection c) throws DBException {
		String querySQL = null;

		try (Statement stmt = c.createStatement()) {
			final String sql = "DELETE FROM " + this.getQualifiedName() + ";";
			querySQL = sql;

			this.requestHook(SQLRequestType.DELETE, sql);

			return stmt.executeUpdate(sql);
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		}
	}

	protected int count(final Connection c) throws DBException {
		String querySQL = null;
		ResultSet result = null;

		try (Statement stmt = c.createStatement()) {
			final String sql = this.dataBaseEntryUtils.getStructureVisitor().count(this.getQueryable());
			querySQL = sql;

			this.requestHook(SQLRequestType.SELECT, sql);

			result = stmt.executeQuery(sql);

			if (!result.next()) {
				throw new IllegalStateException("Couldn't query entry count.");
			}

			return result.getInt("count");
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result);
		}
	}

	protected int countNotNull(final Connection c, final T data) throws DBException {
		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final String[] notNullKeys = this.dataBaseEntryUtils.getNonNullKeys(data);

			{
				pstmt = c
						.prepareStatement(this.dataBaseEntryUtils.getPreparedSelectCountNotNullSQL(this.getQueryable(), notNullKeys, data));

				this.dataBaseEntryUtils.prepareSelectCountNotNullSQL(pstmt, notNullKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying count by not nulls.");
			}

			return result.getInt("count");
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	protected int countUniques(final Connection c, final T data) throws DBException {
		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final String[][] uniqueKeys = this.dataBaseEntryUtils.getUniqueKeys(this.getConstraints(), data);

			{
				pstmt = c.prepareStatement(this.dataBaseEntryUtils.getPreparedSelectCountUniqueSQL(this.getQueryable(), uniqueKeys, data));

				this.dataBaseEntryUtils.prepareSelectCountUniqueSQL(pstmt, uniqueKeys, data);
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

	protected DataBaseTableStatus<T, ? extends DataBaseTable<T>> create(final Connection c) throws DBException {
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

	protected T delete(final Connection c, final T data) throws DBException {
		if (data instanceof ReadOnlyDataBaseEntry) {
			throw new IllegalStateException("Cannot delete a read-only entry (" + data.getClass().getName() + ").");
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		try {
			final ColumnData[] primaryKeys = this.dataBaseEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.dataBaseEntryUtils.getPreparedDeleteSQL(this.getQueryable(), data), keyColumns);

				this.dataBaseEntryUtils.prepareDeleteSQL(pstmt, data);
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

	protected Optional<T> deleteIfExists(final Connection c, final T data) throws DBException {
		return this.exists(c, data) ? Optional.of(this.delete(c, data)) : Optional.empty();
	}

	protected Optional<T> deleteUnique(final Connection c, final T data) throws DBException {
		return this.existsUniques(c, data) ? Optional.of(this.delete(c, this.loadUnique(c, data))) : Optional.empty();
	}

	protected List<T> deleteUniques(final Connection c, final T data) throws DBException {
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

	protected String doubleQuoteEscapeIdentifier(final String identifier) {
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	protected DataBaseTable<T> drop(final Connection c) throws DBException {
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

	protected boolean exists(final Connection c) throws DBException {
		try {
			final DatabaseMetaData dbMetaData = c.getMetaData();

			try (final ResultSet rs = dbMetaData.getTables(c.getCatalog(), c.getSchema(), this.getName(), null)) {
				return rs.next();
			}
		} catch (final SQLException e) {
			throw new DBException("Error retrieving tables.", e);
		}
	}

	protected boolean exists(final Connection c, final T data) throws DBException {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			final ColumnData[] primaryKeys = this.dataBaseEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.dataBaseEntryUtils.getPreparedSelectSQL(this.getQueryable(), data), keyColumns);

				this.dataBaseEntryUtils.prepareSelectSQL(pstmt, data);
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

	protected boolean existsUnique(final Connection c, final T data) throws DBException {
		return this.countUniques(c, data) == 1;
	}

	protected boolean existsUniques(final Connection c, final T data) throws DBException {
		return this.countUniques(c, data) > 0;
	}

	protected void gen() {
		this.tableStructure = this.dataBaseEntryUtils.scanTable(this.tableClass);
//		getConnector().update(tableStructure);
//		this.dataBase.registerTableBean(this);
	}

	protected DataBaseTable<T> getQueryable() {
		return this;
	}

	protected T insert(final Connection c, final T data) throws DBException {
		if (data instanceof ReadOnlyDataBaseEntry) {
			throw new IllegalStateException("Cannot insert a read-only entry (" + data.getClass().getName() + ").");
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet generatedKeys = null;
		int result;

		try {
			final ColumnData[] generatedKeysColumns = this.dataBaseEntryUtils.getGeneratedKeys(data);
			final String[] keyColumns = Arrays.stream(generatedKeysColumns).map(ColumnData::getName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.dataBaseEntryUtils.getPreparedInsertSQL(this.getQueryable(), data), keyColumns);

				this.dataBaseEntryUtils.prepareInsertSQL(pstmt, data);
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
				this.dataBaseEntryUtils.fillInsert(data, generatedKeys);
			}
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(generatedKeys, pstmt);
		}

		return data;
	}

	protected T insertAndReload(final Connection c, final T data) throws DBException {
		return this.load(c, this.insert(c, data));
	}

	@Deprecated
	protected boolean isSQLite() {
		return "sqlite".equalsIgnoreCase(this.getConnector().getProtocol());
	}

	protected T load(final Connection c, final T data) throws DBException {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			final ColumnData[] primaryKeys = this.dataBaseEntryUtils.getPrimaryKeys(data);
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.dataBaseEntryUtils.getPreparedSelectSQL(this.getQueryable(), data), keyColumns);

				this.dataBaseEntryUtils.prepareSelectSQL(pstmt, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.INSERT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			this.dataBaseEntryUtils.fillLoad(data, result);
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}

		return data;
	}

	protected List<T> loadByUnique(final Connection c, final T data) throws DBException {
		return this.query(c, new PreparedQuery<T>() {
			final String[][] uniques = DataBaseTable.this.dataBaseEntryUtils.getUniqueKeys(DataBaseTable.this.getConstraints(), data);

			@Override
			public T clone() {
				return DataBaseTable.this.dataBaseEntryUtils.instance(data);
			}

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<T> table) {
				return DataBaseTable.this.dataBaseEntryUtils
						.getPreparedSelectUniqueSQL(DataBaseTable.this.getQueryable(), this.uniques, data);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<T> instance, final PreparedStatement stmt) throws SQLException {
				DataBaseTable.this.dataBaseEntryUtils.prepareSelectUniqueSQL(stmt, this.uniques, data);
			}

		});
	}

	protected Optional<T> loadIfExists(final Connection c, final T data) throws DBException {
		return this.exists(c, data) ? Optional.of(this.load(c, data)) : Optional.empty();
	}

	protected T loadIfExistsElseInsert(final Connection c, final T data) throws DBException {
		return this.exists(c, data) ? this.load(c, data) : this.insertAndReload(c, data);
	}

	protected T loadUnique(final Connection c, final T data) throws DBException {
		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final String[][] uniqueKeys = this.dataBaseEntryUtils.getUniqueKeys(this.getConstraints(), data);

			{
				pstmt = c.prepareStatement(this.dataBaseEntryUtils.getPreparedSelectUniqueSQL(this.getQueryable(), uniqueKeys, data));

				this.dataBaseEntryUtils.prepareSelectUniqueSQL(pstmt, uniqueKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying by uniques.");
			}

			this.dataBaseEntryUtils.fillLoad(data, result);
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}

		return data;
	}

	protected Optional<T> loadUniqueIfExists(final Connection c, final T data) throws DBException {
		final int count = this.countUniques(c, data);
		if (count == 1) {
			return Optional.of(this.loadUnique(c, data));
		} else if (count == 0) {
			return Optional.empty();
		} else {
			throw new IllegalStateException("Too many results when loading " + data.getClass().getName() + ".");
		}
	}

	protected T loadUniqueIfExistsElseInsert(final Connection c, final T data) throws DBException {
		final int count = this.countUniques(c, data);
		if (count == 1) {
			return this.loadUnique(c, data);
		} else if (count == 0) {
			return this.insertAndReload(c, data);
		} else {
			throw new IllegalStateException("Too many results when loading " + data.getClass().getName() + ".");
		}
	}

	protected <B> B query(final Connection c, final SQLQuery<T, B> query) throws DBException {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = query.toString();

		try {
			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				pstmt = c.prepareStatement(safeQuery.getPreparedQuerySQL(this.getQueryable()));

				safeQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final List<T> output = new ArrayList<>();
				this.dataBaseEntryUtils.fillLoadAll(this.getEntryClass(), result, output::add);

				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				return safeTransQuery.transform(result);
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final List<T> output = new ArrayList<>();
				this.dataBaseEntryUtils.fillLoadAll(this.getEntryClass(), result, output::add);

				return safeTransQuery.transform(output);
			} else {
				throw new IllegalArgumentException("Unsupported type: " + query.getClass().getName());
			}
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL + "\n" + query, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	protected int truncate(final Connection c) throws DBException {
		final int previousCount = this.count();

		if (this.isSQLite()) {
			this.clear(c);
			return previousCount - this.count();
		}

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

	protected T update(final Connection c, final T data) throws DBException {
		if (data instanceof ReadOnlyDataBaseEntry) {
			throw new IllegalStateException("Cannot update a read-only entry (" + data.getClass().getName() + ").");
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		try {
			final ColumnData[] generatedKeysColumns = PCUtils.combineArrays(this.dataBaseEntryUtils.getPrimaryKeys(data),
					this.dataBaseEntryUtils.getGeneratedKeys(data));
			final String[] keyColumns = Arrays.stream(generatedKeysColumns).map(ColumnData::getName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.dataBaseEntryUtils.getPreparedUpdateSQL(this.getQueryable(), data), keyColumns);

				this.dataBaseEntryUtils.prepareUpdateSQL(pstmt, data);
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

	protected T updateAndReload(final Connection c, final T data) throws DBException {
		return this.load(c, this.update(c, data));
	}

	protected AbstractConnection use() throws DBException {
		return this.getConnector().use();
	}

}
