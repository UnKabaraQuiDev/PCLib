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
import lu.kbra.pclib.db.exception.CountQueryFailedException;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.DeleteFailedException;
import lu.kbra.pclib.db.exception.InsertFailedException;
import lu.kbra.pclib.db.exception.InternalDBException;
import lu.kbra.pclib.db.exception.NoGeneratedKeysException;
import lu.kbra.pclib.db.exception.NoMatchingRowException;
import lu.kbra.pclib.db.exception.NoStructureException;
import lu.kbra.pclib.db.exception.ReadOnlyEntryException;
import lu.kbra.pclib.db.exception.TooManyMatchingRowsException;
import lu.kbra.pclib.db.exception.UnsupportedQueryTypeException;
import lu.kbra.pclib.db.exception.UpdateFailedException;
import lu.kbra.pclib.db.hook.RuleHookType;
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

		// prepare clear hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_CLEAR, this.getQueryable(), c, null);
		try (Statement stmt = c.createStatement()) {
			final String sql = "DELETE FROM " + this.getQualifiedName() + ";";
			querySQL = sql;

			// before clear hook
			this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_CLEAR, this.getQueryable(), stmt, null);
			final int r = stmt.executeUpdate(sql);

			// after clear hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_CLEAR, this.getQueryable(), stmt, r);
			return r;
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
		}
	}

	protected int count(final Connection c) throws DBException {
		this.validateStructure();

		String querySQL = null;
		ResultSet result = null;

		// prepare count hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_COUNT, this.getQueryable(), c, null);
		try (Statement stmt = c.createStatement()) {
			final String sql = this.databaseEntryUtils.getStructureVisitor().count(this.getQueryable());
			querySQL = sql;

			// before count hook
			this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_COUNT, this.getQueryable(), stmt, null);
			result = stmt.executeQuery(sql);

			if (!result.next()) {
				throw new CountQueryFailedException(querySQL, this.getStructure());
			}

			final int r = result.getInt("count");

			// after count hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_COUNT, this.getQueryable(), stmt, r);
			return r;
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
		} finally {
			PCUtils.close(result);
		}
	}

	protected int countNotNull(final Connection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		// prepare count hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_COUNT, this.getQueryable(), c, data);
		try {
			final String[] notNullKeys = this.databaseEntryUtils.getNonNullKeys(this.getQueryable(), data);

			{
				pstmt = c
						.prepareStatement(this.databaseEntryUtils.getPreparedSelectCountNotNullSQL(this.getQueryable(), notNullKeys, data));

				this.databaseEntryUtils.prepareSelectCountNotNullSQL(pstmt, this.getQueryable(), notNullKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before count hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeBefore(RuleHookType.BEFORE_COUNT, this.getQueryable(), pstmt, data);
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new NoMatchingRowException("No result when querying count by not nulls.", querySQL, this.getStructure());
			}

			final int r = result.getInt("count");

			// after count hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_COUNT, this.getQueryable(), pstmt, data);
			return r;
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	protected int countUniques(final Connection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		// prepare count
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_COUNT, this.getQueryable(), c, data);
		try {
			final String[][] uniqueKeys = this.databaseEntryUtils.getUniqueKeys(this.getQueryable(), data);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectCountUniqueSQL(this.getQueryable(), uniqueKeys, data));

				this.databaseEntryUtils.prepareSelectCountUniqueSQL(pstmt, this.getQueryable(), uniqueKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before count hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeBefore(RuleHookType.BEFORE_COUNT, this.getQueryable(), pstmt, data);
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new NoMatchingRowException("No result when querying count by uniques.", querySQL, this.getStructure());
			}

			final int r = result.getInt("count");

			// after count hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_COUNT, this.getQueryable(), pstmt, data);
			return r;
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
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

			// prepare create
			this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_CREATE, this.getQueryable(), c, null);
			try (Statement stmt = c.createStatement()) {
				final String[] sql = this.getCreateSQL();

				querySQL = "";
				for (final String str : sql) {
					querySQL += str + "\n";

					// during create hook
					this.databaseEntryUtils.getQueryableHookManager()
							.executeBefore(RuleHookType.BEFORE_CREATE, this.getQueryable(), stmt, null);
					stmt.executeUpdate(str);
				}

				// after create hook
				this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_CREATE, this.getQueryable(), stmt, null);
				return new DatabaseTableStatus<>(false, this.getQueryable());
			} catch (final SQLException e) {
				throw new InternalDBException("Error executing statements.", querySQL, this.getStructure(), e);
			}
		}
	}

	protected T delete(final Connection c, final T data) throws DBException {
		this.validateStructure();

		if (data instanceof ReadOnlyDatabaseEntry) {
			throw new ReadOnlyEntryException("Cannot delete a read-only entry (" + data.getClass().getName() + ").",
					"",
					this.getStructure());
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		// prepare delete
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_DELETE, this.getQueryable(), c, data);
		try {
			final ColumnData[] primaryKeys = this.databaseEntryUtils.getPrimaryKeys(this.getStructure());
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getLocalName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedDeleteSQL(this.getQueryable(), data), keyColumns);

				this.databaseEntryUtils.prepareDeleteSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before delete hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeBefore(RuleHookType.BEFORE_DELETE, this.getQueryable(), pstmt, data);
				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new DeleteFailedException("Couldn't delete data (" + data + ").", querySQL, this.getStructure());
			}

			// after delete hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_DELETE, this.getQueryable(), pstmt, data);
			return data;
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
		} finally {
			PCUtils.close(pstmt);
		}
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

		// prepare drop hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_DROP, this.getQueryable(), c, null);
		try (Statement stmt = c.createStatement()) {
			final String sql = this.databaseEntryUtils.getStructureVisitor().drop(this.structure);
			querySQL = sql;

			// before drop hook
			this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_DROP, this.getQueryable(), stmt, null);
			stmt.executeUpdate(sql);

			// after drop hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_DROP, this.getQueryable(), stmt, null);
			return this.getQueryable();
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing statement.", querySQL, this.getStructure(), e);
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
			throw new InternalDBException("Error retrieving tables.", null, getStructure(), e);
		}
	}

	protected boolean exists(final Connection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		// prepare exists hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_EXISTS, this.getQueryable(), c, data);
		try {
			final ColumnData[] primaryKeys = this.databaseEntryUtils.getPrimaryKeys(this.getStructure());
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getLocalName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectSQL(this.getQueryable(), data), keyColumns);

				this.databaseEntryUtils.prepareSelectSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before exists hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeBefore(RuleHookType.BEFORE_EXISTS, this.getQueryable(), pstmt, data);
				result = pstmt.executeQuery();
			}

			final boolean r = result.next();

			// after exists hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_EXISTS, this.getQueryable(), pstmt, data);
			return r;
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
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
			throw new ReadOnlyEntryException("Cannot insert a read-only entry (" + data.getClass().getName() + ").",
					"",
					this.getStructure());
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet generatedKeys = null;
		int result;

		// prepare insert hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_INSERT, this.getQueryable(), c, data);
		try {
			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedInsertSQL(this.getQueryable(), data),
						Statement.RETURN_GENERATED_KEYS);

				this.databaseEntryUtils.prepareInsertSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before insert hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeBefore(RuleHookType.BEFORE_INSERT, this.getQueryable(), pstmt, data);
				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new InsertFailedException("Couldn't insert data.", querySQL, this.getStructure());
			}

			generatedKeys = pstmt.getGeneratedKeys();
			if (!generatedKeys.next()) {
				throw new NoGeneratedKeysException("Couldn't get generated keys after insert ("
						+ Arrays.toString(PCUtils.getColumnNames(generatedKeys)) + ").", querySQL, this.getStructure());
			}

			// during insert hook
			this.databaseEntryUtils.getQueryableHookManager().executeDuring(RuleHookType.DURING_INSERT, this.getQueryable(), pstmt, data);
			this.databaseEntryUtils.fillInsert(this.getQueryable(), data, generatedKeys);

			// after insert hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_INSERT, this.getQueryable(), pstmt, data);
			return data;
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
		} finally {
			PCUtils.close(generatedKeys, pstmt);
		}

	}

	protected T insertAndReload(final Connection c, final T data) throws DBException {
		return this.load(c, this.insert(c, data));
	}

	protected T load(final Connection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		// prepare load hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_LOAD, this.getQueryable(), c, data);
		try {
			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectSQL(this.getQueryable(), data));

				this.databaseEntryUtils.prepareSelectSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before load hook
				this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_LOAD, this.getQueryable(), pstmt, data);
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new NoMatchingRowException("Couldn't load data, no entry matching query.", querySQL, this.getStructure());
			}

			// during load hook
			this.databaseEntryUtils.getQueryableHookManager().executeDuring(RuleHookType.DURING_LOAD, this.getQueryable(), pstmt, data);
			this.databaseEntryUtils.fillLoad(this.getQueryable(), data, result);

			// after load hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_LOAD, this.getQueryable(), pstmt, data);
			return data;
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
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

		// prepare load hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_LOAD, this.getQueryable(), c, data);
		try {
			final String[][] uniqueKeys = this.databaseEntryUtils.getUniqueKeys(this.getQueryable(), data);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectUniqueSQL(this.getQueryable(), uniqueKeys, data));

				this.databaseEntryUtils.prepareSelectUniqueSQL(pstmt, this.getQueryable(), uniqueKeys, data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before load hook
				this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_LOAD, this.getQueryable(), pstmt, data);
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new NoMatchingRowException("No result when querying by uniques.", querySQL, this.getStructure());
			}

			// during load hook
			this.databaseEntryUtils.getQueryableHookManager().executeDuring(RuleHookType.DURING_LOAD, this.getQueryable(), pstmt, data);
			this.databaseEntryUtils.fillLoad(this.getQueryable(), data, result);

			// after load hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_LOAD, this.getQueryable(), pstmt, data);
			return data;
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
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
			throw new TooManyMatchingRowsException(
					"Too many results when loading " + data.getClass().getName() + " from " + this.getStructure() + ".");
		}
	}

	protected T loadUniqueIfExistsElseInsert(final Connection c, final T data) throws DBException {
		final int count = this.countUniques(c, data);
		if (count == 1) {
			return this.loadUnique(c, data);
		} else if (count == 0) {
			return this.insert(c, data);
		} else {
			throw new TooManyMatchingRowsException(
					"Too many results when loading " + data.getClass().getName() + " from " + this.getStructure() + ".");
		}
	}

	protected <B> B query(final Connection c, final SQLQuery<T, B> query) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = query.toString();

		// prepare load hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_QUERY, this.getQueryable(), c, query);
		try {
			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				pstmt = c.prepareStatement(safeQuery.getPreparedQuerySQL(this.getQueryable()));

				safeQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeBefore(RuleHookType.BEFORE_QUERY, this.getQueryable(), pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeDuring(RuleHookType.DURING_QUERY, this.getQueryable(), pstmt, query);
				final List<T> output = new ArrayList<>();
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);

				// after query hook
				this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_QUERY, this.getQueryable(), pstmt, query);
				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeBefore(RuleHookType.BEFORE_QUERY, this.getQueryable(), pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeDuring(RuleHookType.DURING_QUERY, this.getQueryable(), pstmt, query);
				final B r = safeTransQuery.transform(result);

				// after query hook
				this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_QUERY, this.getQueryable(), pstmt, query);
				return r;
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeBefore(RuleHookType.BEFORE_QUERY, this.getQueryable(), pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeDuring(RuleHookType.DURING_QUERY, this.getQueryable(), pstmt, query);
				final List<T> output = new ArrayList<>();
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);

				// after query hook
				this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_QUERY, this.getQueryable(), pstmt, query);
				return safeTransQuery.transform(output);
			} else {
				throw new UnsupportedQueryTypeException(query.getClass().getName(), querySQL, this.getStructure(), query);
			}
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), query, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	protected int truncate(final Connection c) throws DBException {
		this.validateStructure();

		final int previousCount = this.count();

		String querySQL = null;

		// prepare truncate hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_TRUNCATE, this.getQueryable(), c, null);
		try (Statement stmt = c.createStatement()) {
			final String sql = this.databaseEntryUtils.getTruncateSQL(this.getQueryable());
			querySQL = sql;

			// before truncate hook
			this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_TRUNCATE, this.getQueryable(), stmt, null);
			stmt.executeUpdate(sql);
			final int r = previousCount - this.count();

			// after truncate hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_TRUNCATE, this.getQueryable(), stmt, null);
			return r;
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
		}
	}

	protected T update(final Connection c, final T data) throws DBException {
		this.validateStructure();

		if (data instanceof ReadOnlyDatabaseEntry) {
			throw new ReadOnlyEntryException("Cannot update a read-only entry (" + data.getClass().getName() + ").",
					"",
					this.getStructure());
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		// prepare update hook
		this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_UPDATE, this.getQueryable(), c, data);
		try {
			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedUpdateSQL(this.getQueryable(), data));

				this.databaseEntryUtils.prepareUpdateSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before update hook
				this.databaseEntryUtils.getQueryableHookManager()
						.executeBefore(RuleHookType.BEFORE_UPDATE, this.getQueryable(), pstmt, data);
				result = pstmt.executeUpdate();
				if (result == 0) {
					throw new UpdateFailedException("Couldn't update data.");
				}

				// after update hook
				this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_UPDATE, this.getQueryable(), pstmt, data);
				return data;
			}
		} catch (final SQLException e) {
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e);
		} finally {
			PCUtils.close(pstmt);
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
			throw new NoStructureException(
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
