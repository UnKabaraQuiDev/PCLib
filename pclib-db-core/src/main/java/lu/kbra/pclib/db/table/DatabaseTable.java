package lu.kbra.pclib.db.table;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.tuple.Quadruple;
import lu.kbra.pclib.datastructure.tuple.Quadruples;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.dialect.DbmsCapability;
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
import lu.kbra.pclib.db.utils.ArrayObject;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
public class DatabaseTable<T extends DatabaseEntry> implements AbstractDBTable<T> {

	protected Database database;
	protected DatabaseEntryUtils databaseEntryUtils;
	protected TableStructure structure;
	protected Map<String, Object> customHints = new HashMap<>();
	@Setter
	protected SQLQueryableHookManager queryableHookManager;

	protected DatabaseTable() {
	}

	public DatabaseTable(final Database database) {
		this(database, database.getDatabaseEntryUtils());
	}

	protected DatabaseTable(final Database database, final DatabaseEntryUtils databaseEntryUtils) {
		this.database = database;
		this.databaseEntryUtils = databaseEntryUtils;
		this.customHints.put(DefaultQueryableHints.TARGET_CLASS, this.getClass());
		this.queryableHookManager = databaseEntryUtils.getQueryableHookManager().cloneLinked();
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
		this.queryableHookManager = databaseEntryUtils.getQueryableHookManager().cloneLinked();
	}

	public DatabaseTable(final Database database, final String name) {
		this(database, database.getDatabaseEntryUtils());
		this.customHints.put(DefaultQueryableHints.NAME_OVERRIDE, name);
	}

	@Override
	public int clear() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.clear(c);
		}
	}

	protected int clear(final AbstractConnection c) throws DBException {
		this.validateStructure();

		Statement stmt = null;
		String querySQL = null;

		try {
			// prepare clear hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_CLEAR, this.getQueryable(), c, null);

			stmt = c.createStatement();
			final String sql = "DELETE FROM " + this.getQualifiedName() + ";";
			querySQL = sql;

			// before clear hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_CLEAR, this.getQueryable(), c, stmt, null);
			final int r = stmt.executeUpdate(sql);

			// after clear hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_CLEAR, this.getQueryable(), c, stmt, r);
			return r;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_CLEAR, this.getQueryable(), c, e, null);
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_CLEAR, this.getQueryable(), c, e, null);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(stmt);
		}
	}

	@Override
	public int count() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.count(c);
		}
	}

	protected int count(final AbstractConnection c) throws DBException {
		this.validateStructure();

		Statement stmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			// prepare count hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_COUNT, this.getQueryable(), c, null);

			stmt = c.createStatement();
			final String sql = this.databaseEntryUtils.getStructureVisitor().count(this.getQueryable());
			querySQL = sql;

			// before count hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_COUNT, this.getQueryable(), c, stmt, null);
			result = stmt.executeQuery(sql);

			if (!result.next()) {
				throw new CountQueryFailedException(querySQL, this.getStructure());
			}

			final int r = result.getInt("count");

			// after count hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_COUNT, this.getQueryable(), c, stmt, r);
			return r;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_COUNT, this.getQueryable(), c, e, null);
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_COUNT, this.getQueryable(), c, e, null);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(result, stmt);
		}
	}

	protected int countNotNull(final AbstractConnection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			// prepare count hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_COUNT, this.getQueryable(), c, data);

			final String[] notNullKeys = this.databaseEntryUtils.getNonNullKeys(this.getQueryable(), data);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectCountNotNullSQL(this.getQueryable(), notNullKeys));

				this.databaseEntryUtils.prepareSelectCountNotNullSQL(pstmt, this.getQueryable(), notNullKeys, data);
				querySQL = this.getStatementAsSQL(pstmt);

				// before count hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_COUNT, this.getQueryable(), c, pstmt, data);
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new NoMatchingRowException("No result when querying count by not nulls.", querySQL, this.getStructure());
			}

			final int r = result.getInt("count");

			// after count hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_COUNT, this.getQueryable(), c, pstmt, data);
			return r;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_COUNT, this.getQueryable(), c, e, data);
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_COUNT, this.getQueryable(), c, e, data);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public int countNotNull(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.countNotNull(c, data);
		}
	}

	protected int countUniques(final AbstractConnection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			// prepare count
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_COUNT, this.getQueryable(), c, data);

			final String[][] uniqueKeys = this.databaseEntryUtils.getUniqueKeys(this.getQueryable(), data);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectCountUniqueSQL(this.getQueryable(), uniqueKeys));

				this.databaseEntryUtils.prepareSelectCountUniqueSQL(pstmt, this.getQueryable(), uniqueKeys, data);
				querySQL = this.getStatementAsSQL(pstmt);

				// before count hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_COUNT, this.getQueryable(), c, pstmt, data);
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new NoMatchingRowException("No result when querying count by uniques.", querySQL, this.getStructure());
			}

			final int r = result.getInt("count");

			// after count hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_COUNT, this.getQueryable(), c, pstmt, data);
			return r;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_COUNT, this.getQueryable(), c, e, data);
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_COUNT, this.getQueryable(), c, e, data);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(result, pstmt);
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

	protected DatabaseTableStatus<T, ? extends DatabaseTable<T>> create(final AbstractConnection c) throws DBException {
		this.validateStructure();

		if (this.exists(c)) {
			return new DatabaseTableStatus<>(true, this.getQueryable());
		} else {
			final StringBuilder querySQL = new StringBuilder();

			Statement stmt = null;
			try {
				// prepare create
				this.queryableHookManager.executePrepare(RuleHookType.PREPARE_CREATE, this.getQueryable(), c, null);

				stmt = c.createStatement();
				final String[] sql = this.getCreateSQL();

				for (final String str : sql) {
					querySQL.append(str).append('\n');

					// during create hook
					this.queryableHookManager.executeBefore(RuleHookType.BEFORE_CREATE, this.getQueryable(), c, stmt, null);
					stmt.executeUpdate(str);
				}

				// after create hook
				this.queryableHookManager.executeAfter(RuleHookType.AFTER_CREATE, this.getQueryable(), c, stmt, null);
				return new DatabaseTableStatus<>(false, this.getQueryable());
			} catch (final SQLException e) {
				final List<Throwable> suppressed = this.queryableHookManager
						.executeError(RuleHookType.ERROR_CREATE, this.getQueryable(), c, e, null);
				throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e)
						.addSuppressed(suppressed);
			} catch (final DBException e) {
				final List<Throwable> suppressed = this.queryableHookManager
						.executeError(RuleHookType.ERROR_COUNT, this.getQueryable(), c, e, null);
				throw e.addSuppressed(suppressed);
			} finally {
				PCUtils.close(stmt);
			}
		}
	}

	public DatabaseTable<T> createProxy(final Supplier<AbstractConnection> connection) {
		return new DBTableProxy<>(this, connection);
	}

	protected T delete(final AbstractConnection c, final T data) throws DBException {
		this.validateStructure();

		if (data instanceof ReadOnlyDatabaseEntry) {
			throw new ReadOnlyEntryException("Cannot delete a read-only entry (" + data.getClass().getName() + ").",
					"",
					this.getStructure());
		}

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		try {
			// prepare delete
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_DELETE, this.getQueryable(), c, data);

			final ColumnData[] primaryKeys = this.databaseEntryUtils.getPrimaryKeys(this.getStructure());
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getLocalName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedDeleteSQL(this.getQueryable()), keyColumns);

				this.databaseEntryUtils.prepareDeleteSQL(pstmt, this.getQueryable(), data);
				querySQL = this.getStatementAsSQL(pstmt);

				// before delete hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_DELETE, this.getQueryable(), c, pstmt, data);
				result = pstmt.executeUpdate();
			}

			if (result == 0) {
				throw new DeleteFailedException("Couldn't delete data (" + data + ").", querySQL, this.getStructure());
			}

			// after delete hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_DELETE, this.getQueryable(), c, pstmt, data);
			return data;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_DELETE, this.getQueryable(), c, e, data);
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_DELETE, this.getQueryable(), c, e, data);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(pstmt);
		}
	}

	@Override
	public T delete(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.delete(c, data);
		}
	}

	@Override
	public <C extends Collection<T>> C deleteAll(final C datas) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.deleteAll(c, datas);
		}
	}

	protected <C extends Collection<T>> C deleteAll(final AbstractConnection c, final C datas) {
		this.validateStructure();

		if (datas.size() == 0) {
			return datas;
		}

		PreparedStatement pstmt = null;
		final StringBuilder querySQL = new StringBuilder();

		try {
			// prepare delete hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_DELETE, this.getQueryable(), c, datas);

			pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedDeleteSQL(this.getQueryable()));
			for (final T data : datas) {
				this.databaseEntryUtils.prepareDeleteSQL(pstmt, this.getQueryable(), data);
				querySQL.append(this.getStatementAsSQL(pstmt)).append('\n');
				pstmt.addBatch();
			}

			// before delete hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_DELETE, this.getQueryable(), c, pstmt, datas);
			final int[] result = pstmt.executeBatch();

			for (int i = 0; i < result.length; i++) {
				final int s = result[i];
				if (s == Statement.EXECUTE_FAILED) {
					throw new DeleteFailedException("Couldn't delete data (idx:" + i + ", code:" + s + ").",
							querySQL.toString(),
							this.getStructure());
				}
			}

			// after delete hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_DELETE, this.getQueryable(), c, pstmt, datas);

			return datas;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_DELETE, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_DELETE, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(pstmt);
		}
	}

	protected <D extends Collection<T>, C extends Collection<T>> D
			deleteIfExists(final AbstractConnection c, final C datas, final Supplier<D> supplier) {
		this.validateStructure();

		if (datas.size() == 0) {
			return supplier.get();
		}

		final D returned = supplier.get();

		PreparedStatement pstmt = null;
		final StringBuilder querySQL = new StringBuilder();

		try {
			// prepare delete hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_DELETE, this.getQueryable(), c, datas);

			pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedDeleteSQL(this.getQueryable()));
			for (final T data : datas) {
				this.databaseEntryUtils.prepareDeleteSQL(pstmt, this.getQueryable(), data);
				querySQL.append(this.getStatementAsSQL(pstmt)).append('\n');
				pstmt.addBatch();
			}

			// before delete hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_DELETE, this.getQueryable(), c, pstmt, datas);
			final int[] result = pstmt.executeBatch();

			int i = 0;
			for (final T data : datas) {
				final int s = result[i];
				if (s == Statement.EXECUTE_FAILED) {
					throw new DeleteFailedException("Couldn't delete data (idx:" + i + ", code:" + s + ").",
							querySQL.toString(),
							this.getStructure());
				}
				if (s > 0) {
					returned.add(data);
				}
				i++;
			}

			// after delete hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_DELETE, this.getQueryable(), c, pstmt, datas);

			return returned;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_DELETE, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_DELETE, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(pstmt);
		}
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D deleteIfExists(final C datas, final Supplier<D> supplier)
			throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.deleteIfExists(c, datas, supplier);
		}
	}

	protected Optional<T> deleteIfExists(final AbstractConnection c, final T data) throws DBException {
		return this.exists(c, data) ? Optional.of(this.delete(c, data)) : Optional.empty();
	}

	@Override
	public Optional<T> deleteIfExists(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.deleteIfExists(c, data);
		}
	}

	protected Optional<T> deleteUnique(final AbstractConnection c, final T data) throws DBException {
		return this.existsUniques(c, data) ? Optional.of(this.delete(c, this.loadUnique(c, data))) : Optional.empty();
	}

	@Override
	public Optional<T> deleteUnique(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.deleteUnique(c, data);
		}
	}

	protected List<T> deleteUniques(final AbstractConnection c, final T data) throws DBException {
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
	public List<T> deleteUniques(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.deleteUniques(c, data);
		}
	}

	protected String doubleQuoteEscapeIdentifier(final String identifier) {
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	@Override
	public DatabaseTable<T> drop() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.drop(c);
		}
	}

	protected DatabaseTable<T> drop(final AbstractConnection c) throws DBException {
		this.validateStructure();

		Statement stmt = null;
		String querySQL = null;

		try {
			// prepare drop hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_DROP, this.getQueryable(), c, null);

			stmt = c.createStatement();
			final String sql = this.databaseEntryUtils.getStructureVisitor().drop(this.structure);
			querySQL = sql;

			// before drop hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_DROP, this.getQueryable(), c, stmt, null);
			stmt.executeUpdate(sql);

			// after drop hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_DROP, this.getQueryable(), c, stmt, null);
			return this.getQueryable();
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_DROP, this.getQueryable(), c, e, null);
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_DROP, this.getQueryable(), c, e, null);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(stmt);
		}
	}

	@Override
	public boolean exists() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.exists(c);
		}
	}

	protected boolean exists(final AbstractConnection c) throws DBException {
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
			throw new InternalDBException("Error retrieving tables.", null, this.getStructure(), e);
		}
	}

	protected boolean exists(final AbstractConnection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			// prepare exists hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_EXISTS, this.getQueryable(), c, data);

			final ColumnData[] primaryKeys = this.databaseEntryUtils.getPrimaryKeys(this.getStructure());
			final String[] keyColumns = Arrays.stream(primaryKeys).map(ColumnData::getLocalName).toArray(String[]::new);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectSQL(this.getQueryable()), keyColumns);

				this.databaseEntryUtils.prepareSelectSQL(pstmt, this.getQueryable(), data);
				querySQL = this.getStatementAsSQL(pstmt);

				// before exists hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_EXISTS, this.getQueryable(), c, pstmt, data);
				result = pstmt.executeQuery();
			}

			final boolean r = result.next();

			// after exists hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_EXISTS, this.getQueryable(), c, pstmt, data);
			return r;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_EXISTS, this.getQueryable(), c, e, data);
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_EXISTS, this.getQueryable(), c, e, data);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public boolean exists(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.exists(c, data);
		}
	}

	protected boolean existsUnique(final AbstractConnection c, final T data) throws DBException {
		return this.countUniques(c, data) == 1;
	}

	@Override
	public boolean existsUnique(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.existsUnique(c, data);
		}
	}

	protected boolean existsUniques(final AbstractConnection c, final T data) throws DBException {
		return this.countUniques(c, data) > 0;
	}

	@Override
	public boolean existsUniques(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.existsUniques(c, data);
		}
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D filterExists(final C datas, final Supplier<D> supplier) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.filterExists(c, datas, supplier);
		}
	}

	protected <C extends Collection<T>, D extends Collection<T>> D
			filterExists(final AbstractConnection c, final C datas, final Supplier<D> supplier) {
		this.validateStructure();

		if (datas.isEmpty()) {
			return supplier.get();
		}

		final D returned = supplier.get();

		String querySQL = null;
		PreparedStatement loadStmt = null;
		ResultSet rs = null;

		try {
			// prepare exists hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_EXISTS, this.getQueryable(), c, datas);

			final Map<ArrayObject<Object>, T> pkMap = new HashMap<>(datas.size());
			datas.forEach(d -> pkMap.put(new ArrayObject<>(this.databaseEntryUtils.getPrimaryKeyValues(this.getQueryable(), d)), d));

			final ColumnData[] columns = this.databaseEntryUtils.getPrimaryKeys(this.getQueryable());
			final int pkCount = columns.length;
			loadStmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectAllSQL(this.getQueryable(), datas.size()));
			int index = 1;
			for (final Entry<ArrayObject<Object>, T> pkT : pkMap.entrySet()) {
				for (int i = 0; i < pkCount; i++) {
					columns[i].getType().store(loadStmt, index, pkT.getKey().getValues()[i]);
					index++;
				}
			}
			querySQL = this.getStatementAsSQL(loadStmt);

			// before exists hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_EXISTS, this.getQueryable(), c, loadStmt, datas);
			rs = loadStmt.executeQuery();

			index = 1;
			while (rs.next()) {
				final Object[] nPk = new Object[pkCount];
				for (int i = 0; i < pkCount; i++) {
					nPk[i] = columns[i].getType().load(rs, i + 1, columns[i].getStorageBinding().getGenericType());
					index++;
				}

				final T data = pkMap.get(new ArrayObject<>(nPk));

				returned.add(data);
			}

			// after exists hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_EXISTS, this.getQueryable(), c, loadStmt, datas);

			return returned;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_EXISTS, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_EXISTS, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(rs, loadStmt);
		}
	}

	protected <D extends Collection<T>, C extends Collection<T>> D
			filterExistsUnique(final AbstractConnection c, final C datas, final Supplier<D> supplier) {
		this.validateStructure();

		if (datas.isEmpty()) {
			return supplier.get();
		}

		final D returned = supplier.get();

		final Map<ArrayObject<String[]>, PreparedStatement> statements = new HashMap<>();
		final StringBuilder querySQL = new StringBuilder();

		try {
			// prepare count
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_COUNT, this.getQueryable(), c, datas);

			for (final T data : datas) {
				final String[][] uniqueKeys = this.databaseEntryUtils.getUniqueKeys(this.getQueryable(), data);
				if (uniqueKeys.length == 0) {
					continue;
				}

				final ArrayObject<String[]> key = new ArrayObject<>(uniqueKeys);
				final PreparedStatement pstmt;
				if (statements.containsKey(key)) {
					pstmt = statements.get(key);
				} else {
					pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectUniqueSQL(this.getQueryable(), uniqueKeys));
					statements.put(key, pstmt);
				}

				{
					this.databaseEntryUtils.prepareSelectCountUniqueSQL(pstmt, this.getQueryable(), uniqueKeys, data);
					querySQL.append(this.getStatementAsSQL(pstmt)).append('\n');

					// before count hook
					this.queryableHookManager.executeBefore(RuleHookType.BEFORE_COUNT, this.getQueryable(), c, pstmt, data);
					try (ResultSet result = pstmt.executeQuery()) {
						if (result.next()) {
							returned.add(data);
						}
					}
				}

				// after count hook
				this.queryableHookManager.executeAfter(RuleHookType.AFTER_COUNT, this.getQueryable(), c, pstmt, data);
			}

			return returned;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_COUNT, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_COUNT, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			statements.values().forEach(PCUtils::close);
		}
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D filterExistsUnique(final C datas, final Supplier<D> supplier)
			throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.filterExistsUnique(c, datas, supplier);
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
	public final Class<T> getEntryClass() {
		return (Class<T>) this.structure.getEntryClass();
	}

	@Override
	public final String getName() {
		return this.structure.getName();
	}

	@Override
	public final String getQualifiedName() {
		return this.structure.getQualifiedName();
	}

	protected DatabaseTable<T> getQueryable() {
		return this;
	}

	@Override
	public final Class<? extends SQLQueryable<T>> getTargetClass() {
		return (Class<? extends SQLQueryable<T>>) this.structure.getTargetClass();
	}

	protected T insert(final AbstractConnection c, final T data) throws DBException {
		this.validateStructure();
		this.validateWrite();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet generatedKeys = null;
		int result;

		try {
			// prepare insert hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_INSERT, this.getQueryable(), c, data);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedInsertSQL(this.getQueryable(), data),
						Statement.RETURN_GENERATED_KEYS);

				this.databaseEntryUtils.prepareInsertSQL(pstmt, this.getQueryable(), data);
				querySQL = this.getStatementAsSQL(pstmt);

				// before insert hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_INSERT, this.getQueryable(), c, pstmt, data);
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
			this.queryableHookManager.executeDuring(RuleHookType.DURING_INSERT, this.getQueryable(), c, pstmt, data);
			this.databaseEntryUtils.fillInsert(this.getQueryable(), data, generatedKeys);

			// after insert hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_INSERT, this.getQueryable(), c, pstmt, data);
			return data;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_INSERT, this.getQueryable(), c, e, data);
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_INSERT, this.getQueryable(), c, e, data);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(generatedKeys, pstmt);
		}
	}

	@Override
	public T insert(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.insert(c, data);
		}
	}

	@Override
	public <C extends Collection<T>> C insertAll(final C data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.insertAll(c, data);
		}
	}

	protected <C extends Collection<T>> C insertAll(final AbstractConnection c, final C datas) {
		this.validateStructure();
		this.validateWrite();

		if (datas.size() == 0) {
			return datas;
		}

		if (!this.databaseEntryUtils.getStructureVisitor().supports(DbmsCapability.BATCH_INSERT_RETURN_GENERATED_KEYS)) {
			datas.forEach(data -> this.insert(c, data));
			return datas;
		}

		final Map<BitSet, Quadruple<PreparedStatement, List<T>, ResultSet, int[]>> statements = new HashMap<>();
		final StringBuilder querySQL = new StringBuilder();

		try {
			// prepare insert hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_INSERT, this.getQueryable(), c, datas);

			for (final T data : datas) {

				final BitSet key = this.databaseEntryUtils.computeInsertColumnMask(this.getQueryable(), data);
				final Quadruple<PreparedStatement, List<T>, ResultSet, int[]> pair;
				if (!statements.containsKey(key)) {
					pair = Quadruples.quadruple(c.prepareStatement(this.databaseEntryUtils.getPreparedInsertSQL(this.getQueryable(), data),
							Statement.RETURN_GENERATED_KEYS), new ArrayList<>(), null, null);
					statements.put(key, pair);
				} else {
					pair = statements.get(key);
				}
				final PreparedStatement pstmt = pair.getFirst();
				pair.getSecond().add(data);

				{
					this.databaseEntryUtils.prepareInsertSQL(pstmt, this.getQueryable(), data);
					querySQL.append(this.getStatementAsSQL(pstmt)).append('\n');
					pstmt.addBatch();
				}
			}

			int subIndex = 0;
			for (final Entry<BitSet, Quadruple<PreparedStatement, List<T>, ResultSet, int[]>> entry : statements.entrySet()) {
				final Quadruple<PreparedStatement, List<T>, ResultSet, int[]> pair = entry.getValue();
				final PreparedStatement pstmt = pair.getFirst();
				final List<T> list = pair.getSecond();

				// before insert hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_INSERT, this.getQueryable(), c, pstmt, list);

				pair.setFourth(pstmt.executeBatch());

				final ResultSet generatedKeys = pstmt.getGeneratedKeys();
				entry.getValue().setThird(generatedKeys);

				// during insert hook
				this.queryableHookManager.executeDuring(RuleHookType.DURING_INSERT, this.getQueryable(), c, pstmt, list);

				subIndex = 0;
				for (final T data : list) {
					if (pair.getFourth()[subIndex] == Statement.EXECUTE_FAILED) {
						throw new InsertFailedException("Couldn't insert data.", querySQL.toString(), this.getStructure());
					}
					if (!generatedKeys.next()) {
						throw new NoGeneratedKeysException("Couldn't get generated keys after insert ("
								+ Arrays.toString(PCUtils.getColumnNames(generatedKeys)) + ").", querySQL.toString(), this.getStructure());
					}

					this.databaseEntryUtils.fillInsert(this.getQueryable(), data, generatedKeys);

					subIndex++;
				}

				// after insert hook
				this.queryableHookManager.executeAfter(RuleHookType.AFTER_INSERT, this.getQueryable(), c, pstmt, list);
			}

			return datas;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_INSERT, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_INSERT, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			for (final Quadruple<PreparedStatement, List<T>, ResultSet, int[]> d : statements.values()) {
				PCUtils.close(d.getThird(), d.getFirst());
			}
		}
	}

	protected T insertAndReload(final AbstractConnection c, final T data) throws DBException {
		return this.load(c, this.insert(c, data));
	}

	@Override
	public T insertAndReload(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.insertAndReload(c, data);
		}
	}

	@Override
	public <C extends Collection<T>> C insertAndReloadAll(final C datas) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.insertAndReloadAll(c, datas);
		}
	}

	protected <C extends Collection<T>> C insertAndReloadAll(final AbstractConnection c, final C datas) {
		this.validateStructure();
		this.validateWrite();

		if (datas.size() == 0) {
			return datas;
		}

		if (!this.databaseEntryUtils.getStructureVisitor().supports(DbmsCapability.BATCH_INSERT_RETURN_GENERATED_KEYS)) {
			datas.forEach(data -> this.insertAndReload(c, data));
			return datas;
		}

		final Map<BitSet, Quadruple<PreparedStatement, List<T>, ResultSet, int[]>> insertStatements = new HashMap<>();
		final Map<ArrayObject<Object>, T> pkMap = new HashMap<>(datas.size());
		PreparedStatement loadStmt = null;
		ResultSet rs = null;
		final StringBuilder querySQL = new StringBuilder();

		try {
			// prepare insert hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_INSERT, this.getQueryable(), c, datas);

			for (final T data : datas) {

				final BitSet key = this.databaseEntryUtils.computeInsertColumnMask(this.getQueryable(), data);
				final Quadruple<PreparedStatement, List<T>, ResultSet, int[]> pair;
				if (!insertStatements.containsKey(key)) {
					pair = Quadruples.quadruple(c.prepareStatement(this.databaseEntryUtils.getPreparedInsertSQL(this.getQueryable(), data),
							Statement.RETURN_GENERATED_KEYS), new ArrayList<>(), null, null);
					insertStatements.put(key, pair);
				} else {
					pair = insertStatements.get(key);
				}
				final PreparedStatement pstmt = pair.getFirst();
				pair.getSecond().add(data);

				{
					this.databaseEntryUtils.prepareInsertSQL(pstmt, this.getQueryable(), data);
					querySQL.append(this.getStatementAsSQL(pstmt)).append('\n');
					pstmt.addBatch();
				}
			}

			int subIndex = 0;
			for (final Entry<BitSet, Quadruple<PreparedStatement, List<T>, ResultSet, int[]>> entry : insertStatements.entrySet()) {
				final Quadruple<PreparedStatement, List<T>, ResultSet, int[]> pair = entry.getValue();
				final PreparedStatement pstmt = pair.getFirst();
				final List<T> list = pair.getSecond();

				// before insert hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_INSERT, this.getQueryable(), c, pstmt, list);

				pair.setFourth(pstmt.executeBatch());

				final ResultSet generatedKeys = pstmt.getGeneratedKeys();
				entry.getValue().setThird(generatedKeys);

				subIndex = 0;
				for (final T data : list) {
					if (pair.getFourth()[subIndex] == Statement.EXECUTE_FAILED) {
						throw new InsertFailedException("Couldn't insert data.", querySQL.toString(), this.getStructure());
					}
					if (!generatedKeys.next()) {
						throw new NoGeneratedKeysException("Couldn't get generated keys after insert ("
								+ Arrays.toString(PCUtils.getColumnNames(generatedKeys)) + ").", querySQL.toString(), this.getStructure());
					}

					// during insert hook
					this.queryableHookManager.executeDuring(RuleHookType.DURING_INSERT, this.getQueryable(), c, pstmt, list);

					this.databaseEntryUtils.fillInsert(this.getQueryable(), data, generatedKeys);
					pkMap.put(new ArrayObject<>(this.databaseEntryUtils.getPrimaryKeyValues(this.getQueryable(), data)), data);

					subIndex++;
				}

				// after insert hook
				this.queryableHookManager.executeAfter(RuleHookType.AFTER_INSERT, this.getQueryable(), c, pstmt, list);
			}

			insertStatements.values().forEach(q -> PCUtils.close(q.getFirst()));
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_INSERT, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_INSERT, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			for (final Quadruple<PreparedStatement, List<T>, ResultSet, int[]> d : insertStatements.values()) {
				PCUtils.close(d.getThird(), d.getFirst());
			}
		}

		try {
			// prepare load hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_LOAD, this.getQueryable(), c, datas);

			final ColumnData[] columns = this.databaseEntryUtils.getPrimaryKeys(this.getQueryable());
			final int pkCount = columns.length;
			loadStmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectAllSQL(this.getQueryable(), datas.size()));
			int index = 1;
			for (final Entry<ArrayObject<Object>, T> pkT : pkMap.entrySet()) {
				for (int i = 0; i < pkCount; i++) {
					columns[i].getType().store(loadStmt, index, pkT.getKey().getValues()[i]);
					index++;
				}
			}
			querySQL.append(this.getStatementAsSQL(loadStmt)).append('\n');

			// before load hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_LOAD, this.getQueryable(), c, loadStmt, datas);
			rs = loadStmt.executeQuery();

			index = 1;
			while (rs.next()) {
				final Object[] nPk = new Object[pkCount];
				for (int i = 0; i < pkCount; i++) {
					nPk[i] = columns[i].getType().load(rs, i + 1, columns[i].getStorageBinding().getGenericType());
					index++;
				}

				final T data = pkMap.get(new ArrayObject<>(nPk));

				// during load hook
				this.queryableHookManager.executeDuring(RuleHookType.DURING_LOAD, this.getQueryable(), c, loadStmt, data);

				this.databaseEntryUtils.fillLoad(this.getQueryable(), data, rs);
			}

			// after load hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_LOAD, this.getQueryable(), c, loadStmt, datas);

			return datas;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(rs, loadStmt);
		}
	}

	protected T load(final AbstractConnection c, final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			// prepare load hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_LOAD, this.getQueryable(), c, data);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectSQL(this.getQueryable()));

				this.databaseEntryUtils.prepareSelectSQL(pstmt, this.getQueryable(), data);
				querySQL = this.getStatementAsSQL(pstmt);

				// before load hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_LOAD, this.getQueryable(), c, pstmt, data);
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new NoMatchingRowException("Couldn't load data, no entry matching query.", querySQL, this.getStructure());
			}

			// during load hook
			this.queryableHookManager.executeDuring(RuleHookType.DURING_LOAD, this.getQueryable(), c, pstmt, data);
			this.databaseEntryUtils.fillLoad(this.getQueryable(), data, result);

			// after load hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_LOAD, this.getQueryable(), c, pstmt, data);
			return data;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, data);
			throw new InternalDBException("Error executing query.", querySQL, this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, data);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(result, pstmt);
		}

	}

	@Override
	public T load(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.load(c, data);
		}
	}

	@Override
	public <C extends Collection<T>> C loadAll(final C datas) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadAll(c, datas);
		}
	}

	protected <C extends Collection<T>> C loadAll(final AbstractConnection c, final C datas) {
		this.validateStructure();

		if (datas.isEmpty()) {
			return datas;
		}

		String querySQL = null;
		PreparedStatement loadStmt = null;
		ResultSet rs = null;
		int loadedCount = 0;

		try {
			// prepare load hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_LOAD, this.getQueryable(), c, datas);

			final Map<ArrayObject<Object>, T> pkMap = new HashMap<>(datas.size());
			for (final T d : datas) {
				final ArrayObject<Object> key = new ArrayObject<>(this.databaseEntryUtils.getPrimaryKeyValues(this.getQueryable(), d));
				if (pkMap.put(key, d) != null) {
					throw new IllegalArgumentException("Duplicate primary key in batch: " + key);
				}
			}

			final ColumnData[] columns = this.databaseEntryUtils.getPrimaryKeys(this.getQueryable());
			final int pkCount = columns.length;
			loadStmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectAllSQL(this.getQueryable(), datas.size()));
			int index = 1;
			for (final Entry<ArrayObject<Object>, T> pkT : pkMap.entrySet()) {
				for (int i = 0; i < pkCount; i++) {
					columns[i].getType().store(loadStmt, index, pkT.getKey().getValues()[i]);
					index++;
				}
			}
			querySQL = this.getStatementAsSQL(loadStmt);

			// before load hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_LOAD, this.getQueryable(), c, loadStmt, datas);
			rs = loadStmt.executeQuery();

			index = 1;
			while (rs.next()) {
				final Object[] nPk = new Object[pkCount];
				for (int i = 0; i < pkCount; i++) {
					nPk[i] = columns[i].getType().load(rs, i + 1, columns[i].getStorageBinding().getGenericType());
					index++;
				}

				final T data = pkMap.get(new ArrayObject<>(nPk));

				// during load hook
				this.queryableHookManager.executeDuring(RuleHookType.DURING_LOAD, this.getQueryable(), c, loadStmt, data);

				this.databaseEntryUtils.fillLoad(this.getQueryable(), data, rs);
				loadedCount++;
			}

			if (loadedCount < datas.size()) {
				throw new NoMatchingRowException("Couldn't load data, no entry matching query.", querySQL, this.getStructure());
			}

			// after load hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_LOAD, this.getQueryable(), c, loadStmt, datas);

			return datas;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(rs, loadStmt);
		}
	}

	protected List<T> loadByUnique(final AbstractConnection c, final T data) throws DBException {
		return this.query(c, new PreparedQuery<T>() {

			final String[][] uniques = DatabaseTable.this.databaseEntryUtils.getUniqueKeys(DatabaseTable.this.getQueryable(), data);

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<T> table) {
				return DatabaseTable.this.databaseEntryUtils.getPreparedSelectUniqueSQL(DatabaseTable.this.getQueryable(), this.uniques);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<T> instance, final PreparedStatement stmt) throws SQLException {
				DatabaseTable.this.databaseEntryUtils.prepareSelectUniqueSQL(stmt, instance, this.uniques, data);
			}

		});
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

	protected <C extends Collection<T>, D extends Collection<T>> D
			loadIfExists(final AbstractConnection c, final C datas, final Supplier<D> supplier) {
		this.validateStructure();

		if (datas.isEmpty()) {
			return supplier.get();
		}

		final D returned = supplier.get();

		final StringBuilder querySQL = new StringBuilder();
		PreparedStatement loadStmt = null;
		ResultSet rs = null;

		try {
			// prepare load hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_LOAD, this.getQueryable(), c, datas);

			final Map<ArrayObject<Object>, T> pkMap = new HashMap<>(datas.size());
			datas.forEach(d -> pkMap.put(new ArrayObject<>(this.databaseEntryUtils.getPrimaryKeyValues(this.getQueryable(), d)), d));

			final ColumnData[] columns = this.databaseEntryUtils.getPrimaryKeys(this.getQueryable());
			final int pkCount = columns.length;
			loadStmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectAllSQL(this.getQueryable(), datas.size()));
			int index = 1;
			for (final Entry<ArrayObject<Object>, T> pkT : pkMap.entrySet()) {
				for (int i = 0; i < pkCount; i++) {
					columns[i].getType().store(loadStmt, index, pkT.getKey().getValues()[i]);
					index++;
				}
			}
			querySQL.append(this.getStatementAsSQL(loadStmt)).append('\n');

			// before load hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_LOAD, this.getQueryable(), c, loadStmt, datas);
			rs = loadStmt.executeQuery();

			index = 1;
			while (rs.next()) {
				final Object[] nPk = new Object[pkCount];
				for (int i = 0; i < pkCount; i++) {
					nPk[i] = columns[i].getType().load(rs, i + 1, columns[i].getStorageBinding().getGenericType());
					index++;
				}

				final T data = pkMap.get(new ArrayObject<>(nPk));

				// during load hook
				this.queryableHookManager.executeDuring(RuleHookType.DURING_LOAD, this.getQueryable(), c, loadStmt, data);

				this.databaseEntryUtils.fillLoad(this.getQueryable(), data, rs);
				returned.add(data);
			}

			// after load hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_LOAD, this.getQueryable(), c, loadStmt, datas);

			return returned;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(rs, loadStmt);
		}
	}

	@Override
	public <C extends Collection<T>, D extends Collection<T>> D loadIfExists(final C datas, final Supplier<D> supplier) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadIfExists(c, datas, supplier);
		}
	}

	protected Optional<T> loadIfExists(final AbstractConnection c, final T data) throws DBException {
		// TODO: optimize this
		return this.exists(c, data) ? Optional.of(this.load(c, data)) : Optional.empty();
	}

	@Override
	public Optional<T> loadIfExists(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadIfExists(c, data);
		}
	}

	protected T loadIfExistsElseInsert(final AbstractConnection c, final T data) throws DBException {
		return this.exists(c, data) ? this.load(c, data) : this.insert(c, data);
	}

	/**
	 * Loads the first pk result, returns a the newly inserted instance if none is found
	 */
	public T loadIfExistsElseInsert(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadIfExistsElseInsert(c, data);
		}
	}

	protected T loadUnique(final AbstractConnection c, final T data) throws DBException {
		this.validateStructure();

		final int count = this.countUniques(c, data);
		if (count == 0) {
			throw new NoMatchingRowException("Not enough results when loading by unique.", null, this.getStructure());
		} else if (count > 1) {
			throw new TooManyMatchingRowsException("Too many results when loading by unique (" + count + ").", null, this.getStructure());
		}

		return this.loadUniqueInternal(c, data);
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

	protected Optional<T> loadUniqueIfExists(final AbstractConnection c, final T data) throws DBException {
		final int count = this.countUniques(c, data);
		if (count == 1) {
			return Optional.of(this.loadUniqueInternal(c, data));
		} else if (count == 0) {
			return Optional.empty();
		} else {
			throw new TooManyMatchingRowsException("Too many results when loading by unique (" + count + ").", null, this.getStructure());
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

	protected T loadUniqueIfExistsElseInsert(final AbstractConnection c, final T data) throws DBException {
		return this.loadUniqueIfExists(c, data).orElseGet(() -> this.insert(c, data));
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

	protected T loadUniqueInternal(final AbstractConnection c, final T data) {
		this.validateStructure();

		PreparedStatement pstmt = null;
		String querySQL = null;
		ResultSet result = null;

		try {
			// prepare load hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_LOAD, this.getQueryable(), c, data);

			final String[][] uniqueKeys = this.databaseEntryUtils.getUniqueKeys(this.getQueryable(), data);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectUniqueSQL(this.getQueryable(), uniqueKeys));

				this.databaseEntryUtils.prepareSelectUniqueSQL(pstmt, this.getQueryable(), uniqueKeys, data);
				querySQL = this.getStatementAsSQL(pstmt);

				// before load hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_LOAD, this.getQueryable(), c, pstmt, data);
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new NoMatchingRowException("Not enough results when loading datas.", querySQL, this.getStructure());
			}

			// during load hook
			this.queryableHookManager.executeDuring(RuleHookType.DURING_LOAD, this.getQueryable(), c, pstmt, data);
			this.databaseEntryUtils.fillLoad(this.getQueryable(), data, result);

			// after load hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_LOAD, this.getQueryable(), c, pstmt, data);
			return data;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, data);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, data);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	protected <B> B query(final AbstractConnection c, final SQLQuery<T, B> query) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = query.toString();

		try {
			// prepare load hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_QUERY, this.getQueryable(), c, query);

			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				pstmt = c.prepareStatement(safeQuery.getPreparedQuerySQL(this.getQueryable()));

				safeQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = this.getStatementAsSQL(pstmt);

				// before query hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_QUERY, this.getQueryable(), c, pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.queryableHookManager.executeDuring(RuleHookType.DURING_QUERY, this.getQueryable(), c, pstmt, query);
				final List<T> output = new ArrayList<>();
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);

				// after query hook
				this.queryableHookManager.executeAfter(RuleHookType.AFTER_QUERY, this.getQueryable(), c, pstmt, query);
				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = this.getStatementAsSQL(pstmt);

				// before query hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_QUERY, this.getQueryable(), c, pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.queryableHookManager.executeDuring(RuleHookType.DURING_QUERY, this.getQueryable(), c, pstmt, query);
				final B r = safeTransQuery.transform(this.getQueryable(), result);

				// after query hook
				this.queryableHookManager.executeAfter(RuleHookType.AFTER_QUERY, this.getQueryable(), c, pstmt, query);
				return r;
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = this.getStatementAsSQL(pstmt);

				// before query hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_QUERY, this.getQueryable(), c, pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.queryableHookManager.executeDuring(RuleHookType.DURING_QUERY, this.getQueryable(), c, pstmt, query);
				final List<T> output = new ArrayList<>();
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);
				final B r = safeTransQuery.transform(this.getQueryable(), output);

				// after query hook
				this.queryableHookManager.executeAfter(RuleHookType.AFTER_QUERY, this.getQueryable(), c, pstmt, query);
				return r;
			} else {
				throw new UnsupportedQueryTypeException(query.getClass().getName(), querySQL, this.getStructure(), query);
			}
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_QUERY, this.getQueryable(), c, e, query);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_QUERY, this.getQueryable(), c, e, query);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(result, pstmt);
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
	public void setTableStructure(final TableStructure tableStructure) {
		PCUtils.requireNull(this.structure, "TableStructure was already set once.");
		Objects.requireNonNull(tableStructure, "TableStucture is null.");
		this.structure = tableStructure;
	}

	@Override
	public int truncate() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.truncate(c);
		}
	}

	protected int truncate(final AbstractConnection c) throws DBException {
		this.validateStructure();

		final int previousCount = this.count();

		Statement stmt = null;
		String querySQL = null;

		try {
			// prepare truncate hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_TRUNCATE, this.getQueryable(), c, null);

			stmt = c.createStatement();
			final String sql = this.databaseEntryUtils.getTruncateSQL(this.getQueryable());
			querySQL = sql;

			// before truncate hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_TRUNCATE, this.getQueryable(), c, stmt, null);
			stmt.executeUpdate(sql);
			final int r = previousCount - this.count();

			// after truncate hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_TRUNCATE, this.getQueryable(), c, stmt, null);
			return r;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_TRUNCATE, this.getQueryable(), c, e, null);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_TRUNCATE, this.getQueryable(), c, e, null);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(stmt);
		}
	}

	protected T update(final AbstractConnection c, final T data) throws DBException {
		this.validateStructure();
		this.validateWrite();

		PreparedStatement pstmt = null;
		String querySQL = null;
		int result = -1;

		try {
			// prepare update hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_UPDATE, this.getQueryable(), c, data);

			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedUpdateSQL(this.getQueryable()));

				this.databaseEntryUtils.prepareUpdateSQL(pstmt, this.getQueryable(), data);
				querySQL = this.getStatementAsSQL(pstmt);

				// before update hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_UPDATE, this.getQueryable(), c, pstmt, data);
				result = pstmt.executeUpdate();
				if (result == 0) {
					throw new UpdateFailedException("Couldn't update data.");
				}

				// after update hook
				this.queryableHookManager.executeAfter(RuleHookType.AFTER_UPDATE, this.getQueryable(), c, pstmt, data);
				return data;
			}
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_UPDATE, this.getQueryable(), c, e, data);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_UPDATE, this.getQueryable(), c, e, data);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(pstmt);
		}
	}

	@Override
	public T update(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.update(c, data);
		}
	}

	@Override
	public <C extends Collection<T>> C updateAll(final C datas) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.updateAll(c, datas);
		}
	}

	protected <C extends Collection<T>> C updateAll(final AbstractConnection c, final C datas) {
		this.validateStructure();
		this.validateWrite();

		if (datas.size() == 0) {
			return datas;
		}

		PreparedStatement pstmt = null;
		final StringBuilder querySQL = new StringBuilder();

		try {
			// prepare update hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_UPDATE, this.getQueryable(), c, datas);

			pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedUpdateSQL(this.getQueryable()));
			for (final T data : datas) {
				this.databaseEntryUtils.prepareUpdateSQL(pstmt, this.getQueryable(), data);
				querySQL.append(this.getStatementAsSQL(pstmt)).append('\n');
				pstmt.addBatch();
			}

			// before update hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_UPDATE, this.getQueryable(), c, pstmt, datas);
			final int[] result = pstmt.executeBatch();

			for (int i = 0; i < result.length; i++) {
				final int s = result[i];
				if (s == Statement.EXECUTE_FAILED) {
					throw new DeleteFailedException("Couldn't delete data (idx:" + i + ", code:" + s + ").",
							querySQL.toString(),
							this.getStructure());
				}
			}

			// after update hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_UPDATE, this.getQueryable(), c, pstmt, datas);

			return datas;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_UPDATE, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_UPDATE, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(pstmt);
		}
	}

	protected T updateAndReload(final AbstractConnection c, final T data) throws DBException {
		return this.load(c, this.update(c, data));
	}

	@Override
	public T updateAndReload(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.updateAndReload(c, data);
		}
	}

	@Override
	public <C extends Collection<T>> C updateAndReloadAll(final C datas) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.updateAndReloadAll(c, datas);
		}
	}

	protected <C extends Collection<T>> C updateAndReloadAll(final AbstractConnection c, final C datas) {
		this.validateStructure();
		this.validateWrite();

		if (datas.size() == 0) {
			return datas;
		}

		final StringBuilder querySQL = new StringBuilder();
		PreparedStatement updateStmt = null;
		PreparedStatement loadStmt = null;
		ResultSet rs = null;

		try {
			// prepare update hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_UPDATE, this.getQueryable(), c, datas);

			updateStmt = c.prepareStatement(this.databaseEntryUtils.getPreparedUpdateSQL(this.getQueryable()));
			for (final T data : datas) {
				this.databaseEntryUtils.prepareUpdateSQL(updateStmt, this.getQueryable(), data);
				querySQL.append(this.getStatementAsSQL(updateStmt)).append('\n');
				updateStmt.addBatch();
			}

			// before update hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_UPDATE, this.getQueryable(), c, updateStmt, datas);
			final int[] result = updateStmt.executeBatch();

			for (int i = 0; i < result.length; i++) {
				final int s = result[i];
				if (s == Statement.EXECUTE_FAILED) {
					throw new DeleteFailedException("Couldn't delete data (idx:" + i + ", code:" + s + ").",
							querySQL.toString(),
							this.getStructure());
				}
			}

			// after update hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_UPDATE, this.getQueryable(), c, updateStmt, datas);
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_UPDATE, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_UPDATE, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(updateStmt);
		}

		try {
			// prepare load hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_LOAD, this.getQueryable(), c, datas);

			final Map<ArrayObject<Object>, T> pkMap = new HashMap<>(datas.size());
			datas.forEach(d -> pkMap.put(new ArrayObject<>(this.databaseEntryUtils.getPrimaryKeyValues(this.getQueryable(), d)), d));

			final ColumnData[] columns = this.databaseEntryUtils.getPrimaryKeys(this.getQueryable());
			final int pkCount = columns.length;
			loadStmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectAllSQL(this.getQueryable(), datas.size()));
			int index = 1;
			for (final Entry<ArrayObject<Object>, T> pkT : pkMap.entrySet()) {
				for (int i = 0; i < pkCount; i++) {
					columns[i].getType().store(loadStmt, index, pkT.getKey().getValues()[i]);
					index++;
				}
			}
			querySQL.append(this.getStatementAsSQL(loadStmt)).append('\n');

			// before load hook
			this.queryableHookManager.executeBefore(RuleHookType.BEFORE_LOAD, this.getQueryable(), c, loadStmt, datas);
			rs = loadStmt.executeQuery();

			index = 1;
			while (rs.next()) {
				final Object[] nPk = new Object[pkCount];
				for (int i = 0; i < pkCount; i++) {
					nPk[i] = columns[i].getType().load(rs, i + 1, columns[i].getStorageBinding().getGenericType());
					index++;
				}

				final T data = pkMap.get(new ArrayObject<>(nPk));

				// during load hook
				this.queryableHookManager.executeDuring(RuleHookType.DURING_LOAD, this.getQueryable(), c, loadStmt, data);

				this.databaseEntryUtils.fillLoad(this.getQueryable(), data, rs);
			}

			// after load hook
			this.queryableHookManager.executeAfter(RuleHookType.AFTER_LOAD, this.getQueryable(), c, loadStmt, datas);

			return datas;
		} catch (final SQLException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, datas);
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
		} catch (final DBException e) {
			final List<Throwable> suppressed = this.queryableHookManager
					.executeError(RuleHookType.ERROR_LOAD, this.getQueryable(), c, e, datas);
			throw e.addSuppressed(suppressed);
		} finally {
			PCUtils.close(rs, loadStmt);
		}
	}

	protected AbstractConnection use() throws DBException {
		return this.getConnector().use();
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

	protected void validateWrite() {
		if (this.structure.isReadOnly()) {
			throw new ReadOnlyEntryException(this.structure);
		}
	}

	@Override
	public String toString() {
		return this.structure != null ? this.structure.toString() : this.getClass().getName() + "<no structure>";
	}

}
