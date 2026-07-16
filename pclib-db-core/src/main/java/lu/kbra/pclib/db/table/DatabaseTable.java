package lu.kbra.pclib.db.table;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.DatabaseEntry.ReadOnlyDatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DatabaseTable<T extends DatabaseEntry> implements AbstractDBTable<T> {

	protected Database database;
	protected DatabaseEntryUtils databaseEntryUtils;
	protected TableStructure structure;
	protected Map<String, Object> customHints = new HashMap<>();

	public DatabaseTable(final Database database) {
		this(database, database.getDatabaseEntryUtils());
	}

	public DatabaseTable(final Database database, final String name) {
		this(database, database.getDatabaseEntryUtils());
		this.customHints.put(DefaultQueryableHints.NAME_OVERRIDE, name);
	}

	protected DatabaseTable(final Database database, final DatabaseEntryUtils databaseEntryUtils) {
		this.database = database;
		this.databaseEntryUtils = databaseEntryUtils;
		this.customHints.put(DefaultQueryableHints.TARGET_CLASS, this.getClass());
	}

	protected DatabaseTable(
			final Database database,
			final DatabaseEntryUtils databaseEntryUtils,
			final Class<? extends AbstractDBTable<T>> tableClass,
			final Map<String, Object> customHints) {
		this.database = database;
		this.databaseEntryUtils = databaseEntryUtils;
		this.customHints.putAll(customHints);
		this.customHints.putIfAbsent(DefaultQueryableHints.TARGET_CLASS, this.getClass());
	}

	protected DatabaseTable() {
	}

	@Override
	public void setTableStructure(final TableStructure tableStructure) {
		PCUtils.requireNull(this.structure, "TableStructure was already set once.");
		Objects.requireNonNull(tableStructure, "TableStucture is null.");
		this.structure = tableStructure;
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
	public DatabaseTableStatus<T, ? extends DatabaseTable<T>> create() throws DBException {
		this.getConnector().reset();

		try (AbstractConnection c = this.use()) {
			return this.create(c);
		}
	}

	public DatabaseTable<T> createProxy(final Connection connection) {
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
	public DatabaseTable<T> drop() throws DBException {
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

	@Override
	public DatabaseConnector getConnector() {
		return this.database.getConnector();
	}

	@Override
	public String[] getCreateSQL() {
		return this.databaseEntryUtils.getStructureVisitor().create(this.structure);
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

	public void setDbEntryUtils(final DatabaseEntryUtils dbEntryUtils) {
		this.databaseEntryUtils = dbEntryUtils;
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
		this.validateStructure();

		String querySQL = null;

		try (Statement stmt = c.createStatement()) {
			final String sql = "DELETE FROM " + this.getQualifiedName() + ";";
			querySQL = sql;

			// before clear hook
			final int result = stmt.executeUpdate(sql);
			// after clear hook

			return result;
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		}
	}

	protected int count(final Connection c) throws DBException {
		this.validateStructure();

		String querySQL = null;
		ResultSet result = null;

		try (Statement stmt = c.createStatement()) {
			final String sql = this.databaseEntryUtils.getStructureVisitor().count(this.getQueryable());
			querySQL = sql;

			// before count hook
			result = stmt.executeQuery(sql);

			if (!result.next()) {
				throw new IllegalStateException("Couldn't query entry count.");
			}

			final int r = result.getInt("count");

			// after count hook
			return r;
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result);
		}
	}

	protected int countNotNull(final Connection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final String[] notNullKeys = this.databaseEntryUtils.getNonNullKeys(this.getQueryable(), data);

			{
				pstmt = c
						.prepareStatement(this.databaseEntryUtils.getPreparedSelectCountNotNullSQL(this.getQueryable(), notNullKeys, data));

				this.databaseEntryUtils.prepareSelectCountNotNullSQL(pstmt, this.getQueryable(), notNullKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before count hook
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying count by not nulls.");
			}

			final int r = result.getInt("count");
			// after count hook
			return r;
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	protected int countUniques(final Connection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final String[][] uniqueKeys = this.databaseEntryUtils.getUniqueKeys(this.getQueryable(), data);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectCountUniqueSQL(this.getQueryable(), uniqueKeys, data));

				this.databaseEntryUtils.prepareSelectCountUniqueSQL(pstmt, this.getQueryable(), uniqueKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// after count hook
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying count by uniques.");
			}

			final int r = result.getInt("count");
			// after count hook
			return r;
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	protected DatabaseTableStatus<T, ? extends DatabaseTable<T>> create(final Connection c) throws DBException {
		this.validateStructure();

		if (this.exists(c)) {
			return new DatabaseTableStatus<>(true, this.getQueryable());
		} else {
			String querySQL = null;

			try (Statement stmt = c.createStatement()) {
				final String[] sql = this.getCreateSQL();
				// before create hook
				querySQL = "";
				for (final String str : sql) {
					querySQL += str + "\n";

					// during create hook
					final int result = stmt.executeUpdate(str);
				}

				// after create hook
			} catch (final SQLException e) {
				throw new DBException("Error executing query: " + querySQL, e);
			}

			return new DatabaseTableStatus<>(false, this.getQueryable());
		}
	}

	protected T delete(final Connection c, final T data) throws DBException {
		this.validateStructure();

		if (data instanceof ReadOnlyDatabaseEntry) {
			throw new IllegalStateException("Cannot delete a read-only entry (" + data.getClass().getName() + ").");
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		try {
			final ColumnData[] primaryKeys = this.databaseEntryUtils.getPrimaryKeys(this.getStructure());
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getLocalName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedDeleteSQL(this.getQueryable(), data), keyColumns);

				this.databaseEntryUtils.prepareDeleteSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before delete hook
				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't delete data (" + data + ").");
			}

			// after delete hook
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

	protected DatabaseTable<T> drop(final Connection c) throws DBException {
		this.validateStructure();

		String querySQL = null;

		try (Statement stmt = c.createStatement()) {
			final String sql = this.databaseEntryUtils.getStructureVisitor().drop(this.structure);
			querySQL = sql;

			// before drop hook
			stmt.executeUpdate(sql);

			// after drop hook
			return this.getQueryable();
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		}

	}

	protected boolean exists(final Connection c) throws DBException {
		this.validateStructure();

		try {
			final DatabaseMetaData dbMetaData = c.getMetaData();

			try (final ResultSet rs = dbMetaData.getTables(this.database.getDatabaseName(),
					this.databaseEntryUtils.getStructureVisitor().schemaName(this.getQueryable()),
					this.getName(),
					null)) {
				return rs.next();
			}
		} catch (final SQLException e) {
			throw new DBException("Error retrieving tables.", e);
		}
	}

	protected boolean exists(final Connection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			final ColumnData[] primaryKeys = this.databaseEntryUtils.getPrimaryKeys(this.getStructure());
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getLocalName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectSQL(this.getQueryable(), data), keyColumns);

				this.databaseEntryUtils.prepareSelectSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before exists hook
				result = pstmt.executeQuery();
			}

			final boolean n = result.next();
			// after exists hook
			return n;
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

	protected DatabaseTable<T> getQueryable() {
		return this;
	}

	protected T insert(final Connection c, final T data) throws DBException {
		this.validateStructure();

		if (data instanceof ReadOnlyDatabaseEntry) {
			throw new IllegalStateException("Cannot insert a read-only entry (" + data.getClass().getName() + ").");
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet generatedKeys = null;
		int result;

		try {
			final String[] keyColumns = this.databaseEntryUtils.getInsertGeneratedColumnNames(this.getQueryable());

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedInsertSQL(this.getQueryable(), data), keyColumns);

				this.databaseEntryUtils.prepareInsertSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before insert hook
				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new IllegalStateException("Couldn't insert data.");
			}

			if (keyColumns.length != 0) {
				generatedKeys = pstmt.getGeneratedKeys();
				if (!generatedKeys.next()) {
					throw new IllegalStateException("Couldn't get generated keys after insert (" + Arrays.toString(keyColumns) + ").");
				}
				// during insert hook
				this.databaseEntryUtils.fillInsert(this.getQueryable(), data, generatedKeys);
			}
			// after insert hook
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

	protected T load(final Connection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectSQL(this.getQueryable(), data));

				this.databaseEntryUtils.prepareSelectSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before load hook
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			// during load hook
			this.databaseEntryUtils.fillLoad(this.getQueryable(), data, result);
			// after load hook

			return data;
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}

	}

	protected List<T> loadByUnique(final Connection c, final T data) throws DBException {
		return this.query(c, new PreparedQuery<T>() {

			final String[][] uniques = DatabaseTable.this.databaseEntryUtils.getUniqueKeys(DatabaseTable.this.getQueryable(), data);

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<T> table) {
				return DatabaseTable.this.databaseEntryUtils
						.getPreparedSelectUniqueSQL(DatabaseTable.this.getQueryable(), this.uniques, data);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<T> instance, final PreparedStatement stmt) throws SQLException {
				DatabaseTable.this.databaseEntryUtils.prepareSelectUniqueSQL(stmt, instance, this.uniques, data);
			}

		});
	}

	protected Optional<T> loadIfExists(final Connection c, final T data) throws DBException {
		return this.exists(c, data) ? Optional.of(this.load(c, data)) : Optional.empty();
	}

	protected T loadIfExistsElseInsert(final Connection c, final T data) throws DBException {
		return this.exists(c, data) ? this.load(c, data) : this.insert(c, data);
	}

	protected T loadUnique(final Connection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			final String[][] uniqueKeys = this.databaseEntryUtils.getUniqueKeys(this.getQueryable(), data);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectUniqueSQL(this.getQueryable(), uniqueKeys, data));

				this.databaseEntryUtils.prepareSelectUniqueSQL(pstmt, this.getQueryable(), uniqueKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before load hook
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new IllegalStateException("No result when querying by uniques.");
			}

			// during load hook
			this.databaseEntryUtils.fillLoad(this.getQueryable(), data, result);
			// after load hook

			return data;
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
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
			return this.insert(c, data);
		} else {
			throw new IllegalStateException("Too many results when loading " + data.getClass().getName() + ".");
		}
	}

	protected <B> B query(final Connection c, final SQLQuery<T, B> query) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = query.toString();

		try {
			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				pstmt = c.prepareStatement(safeQuery.getPreparedQuerySQL(this.getQueryable()));

				safeQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				result = pstmt.executeQuery();

				// during query hook
				final List<T> output = new ArrayList<>();
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);

				// after query hook
				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				result = pstmt.executeQuery();

				// during query hook
				final B b = safeTransQuery.transform(result);

				// after query hook
				return b;
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				result = pstmt.executeQuery();

				// during query hook
				final List<T> output = new ArrayList<>();
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);

				final B b = safeTransQuery.transform(output);

				// after query hook
				return b;
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
		this.validateStructure();

		final int previousCount = this.count();

		String querySQL = null;

		try (Statement stmt = c.createStatement()) {
			final String sql = this.databaseEntryUtils.getTruncateSQL(this.getQueryable());
			querySQL = sql;

			// before truncate hook
			stmt.executeUpdate(sql);

			final int count = previousCount - this.count();

			// after truncate hook
			return count;
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		}
	}

	protected T update(final Connection c, final T data) throws DBException {
		this.validateStructure();

		if (data instanceof ReadOnlyDatabaseEntry) {
			throw new IllegalStateException("Cannot update a read-only entry (" + data.getClass().getName() + ").");
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;
		ResultSet generatedKeys = null;

		try {
			final String[] generatedColumns = this.databaseEntryUtils.getUpdateGeneratedColumnsNames(this.getQueryable());

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedUpdateSQL(this.getQueryable(), data), generatedColumns);

				this.databaseEntryUtils.prepareUpdateSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before update hook
				result = pstmt.executeUpdate();
				if (result == 0) {
					throw new IllegalStateException("Couldn't update data.");
				}

				generatedKeys = pstmt.getGeneratedKeys();
				// during update hook
				this.databaseEntryUtils.fillUpdate(this.getQueryable(), data, generatedKeys);

				// after update hook
				return data;
			}
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(pstmt, generatedKeys);
		}
	}

	protected T updateAndReload(final Connection c, final T data) throws DBException {
		return this.load(c, this.update(c, data));
	}

	protected AbstractConnection use() throws DBException {
		return this.getConnector().use();
	}

	@Override
	public final String getName() {
		return this.structure.getName();
	}

	@Override
	public final String getQualifiedName() {
		return this.structure.getQualifiedName();
	}

	@Override
	public final Class<T> getEntryClass() {
		return (Class<T>) this.structure.getEntryClass();
	}

	@Override
	public final Class<? extends SQLQueryable<T>> getTargetClass() {
		return (Class<? extends SQLQueryable<T>>) this.structure.getTargetClass();
	}

	protected void validateStructure() {
		if (this.structure == null) {
			throw new DBException(
					"Table hasn't been scanned yet, use Database#register...(...).scanFromBeans() or use an indendent DatabaseScanner.\n"
							+ this.getClass() + " using target "
							+ (this.customHints != null ? this.customHints.getOrDefault(DefaultQueryableHints.TARGET_CLASS, "<unspecified>")
									: "<no custom hints>"),
					null,
					this.structure,
					new IllegalStateException());
		}
	}

}
