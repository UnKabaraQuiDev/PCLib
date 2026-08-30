package lu.kbra.pclib.db.view;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.exception.CountQueryFailedException;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.InternalDBException;
import lu.kbra.pclib.db.exception.NoMatchingRowException;
import lu.kbra.pclib.db.exception.NoStructureException;
import lu.kbra.pclib.db.exception.TooManyMatchingRowsException;
import lu.kbra.pclib.db.exception.UnsupportedQueryTypeException;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.ArrayObject;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

@Getter
@ToString
public class DatabaseView<T extends DatabaseEntry> implements AbstractDBView<T> {

	protected Database database;
	protected DatabaseEntryUtils databaseEntryUtils;
	protected ViewStructure structure;
	protected Map<String, Object> customHints = new HashMap<>();
	@Setter
	protected SQLQueryableHookManager queryableHookManager;

	protected DatabaseView() {
	}

	public DatabaseView(final Database database) {
		this(database, database.getDatabaseEntryUtils());
	}

	protected DatabaseView(final Database database, final DatabaseEntryUtils dbEntryUtils) {
		this.database = database;
		this.databaseEntryUtils = dbEntryUtils;
		this.customHints.put(DefaultQueryableHints.TARGET_CLASS, this.getClass());
		this.queryableHookManager = this.databaseEntryUtils.getQueryableHookManager().cloneLinked();
	}

	protected DatabaseView(
			final Database database,
			final DatabaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBView<T>> viewClass,
			final Map<String, Object> customHints) {
		this.database = database;
		this.databaseEntryUtils = dbEntryUtils;
		this.customHints.putAll(customHints);
		this.customHints.putIfAbsent(DefaultQueryableHints.TARGET_CLASS, this.getClass());
		this.queryableHookManager = this.databaseEntryUtils.getQueryableHookManager().cloneLinked();
	}

	public DatabaseView(final Database database, final String name) {
		this(database, database.getDatabaseEntryUtils());
		this.customHints.put(DefaultQueryableHints.NAME_OVERRIDE, name);
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
				querySQL = PCUtils.getStatementAsSQL(pstmt);

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
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
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

	protected int countUniques(final AbstractConnection c, final T data) {
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
				querySQL = PCUtils.getStatementAsSQL(pstmt);

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
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
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
	public DatabaseViewStatus<T, ? extends DatabaseView<T>> create() throws DBException {
		this.getConnector().reset();

		try (AbstractConnection c = this.use()) {
			return this.create(c);
		}
	}

	protected DatabaseViewStatus<T, ? extends DatabaseView<T>> create(final AbstractConnection c) throws DBException {
		this.validateStructure();

		if (this.exists(c)) {
			return new DatabaseViewStatus<>(true, this.getQueryable());
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
				return new DatabaseViewStatus<>(false, this.getQueryable());
			} catch (final SQLException e) {
				final List<Throwable> suppressed = this.queryableHookManager
						.executeError(RuleHookType.ERROR_CREATE, this.getQueryable(), c, e, null);
				throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e)
						.addSuppressed(suppressed);
			} catch (final DBException e) {
				final List<Throwable> suppressed = this.queryableHookManager
						.executeError(RuleHookType.ERROR_CREATE, this.getQueryable(), c, e, null);
				throw e.addSuppressed(suppressed);
			} finally {
				PCUtils.close(stmt);
			}
		}
	}

	protected String doubleQuoteEscapeIdentifier(final String identifier) {
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	@Override
	public DatabaseView<T> drop() throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.drop(c);
		}
	}

	protected DatabaseView<T> drop(final AbstractConnection c) throws DBException {
		this.validateStructure();

		Statement stmt = null;
		String querySQL = null;

		try {
			// prepare drop hook
			this.queryableHookManager.executePrepare(RuleHookType.PREPARE_DROP, this.getQueryable(), c, null);

			stmt = c.createStatement();
			final String sql = "DROP VIEW " + this.getQualifiedName() + ";";
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
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
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
				querySQL = PCUtils.getStatementAsSQL(pstmt);

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
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
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
			querySQL = PCUtils.getStatementAsSQL(loadStmt);

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
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
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
					querySQL.append(PCUtils.getStatementAsSQL(pstmt)).append('\n');

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

	protected DatabaseView<T> getQueryable() {
		return this;
	}

	@Override
	public final Class<? extends SQLQueryable<T>> getTargetClass() {
		return (Class<? extends SQLQueryable<T>>) this.structure.getTargetClass();
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
				querySQL = PCUtils.getStatementAsSQL(pstmt);

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
			throw new InternalDBException("Error executing query.", querySQL.toString(), this.getStructure(), e).addSuppressed(suppressed);
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
	public <C extends Collection<T>> C loadAll(final C datas) {
		try (AbstractConnection c = this.use()) {
			return this.loadAll(c, datas);
		}
	}

	protected <C extends Collection<T>> C loadAll(final AbstractConnection c, final C datas) {
		this.validateStructure();

		String querySQL = null;
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
			querySQL = PCUtils.getStatementAsSQL(loadStmt);

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

	@Override
	public List<T> loadByUnique(final T data) throws DBException {
		return this.query(new PreparedQuery<T>() {

			final String[][] uniques = DatabaseView.this.databaseEntryUtils.getUniqueKeys(DatabaseView.this.getQueryable(), data);

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<T> table) {
				return DatabaseView.this.databaseEntryUtils.getPreparedSelectUniqueSQL(DatabaseView.this.getQueryable(), this.uniques);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<T> instance, final PreparedStatement stmt) throws SQLException {
				DatabaseView.this.databaseEntryUtils.prepareSelectUniqueSQL(stmt, instance, this.uniques, data);
			}

		});
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
			querySQL.append(PCUtils.getStatementAsSQL(loadStmt)).append('\n');

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

	@Override
	public Optional<T> loadIfExists(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			// TODO: optimize this
			return this.exists(c, data) ? Optional.of(this.load(c, data)) : Optional.empty();
		}
	}

	@Override
	public T loadUnique(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			this.validateStructure();

			final int count = this.countUniques(c, data);
			if (count == 0) {
				throw new NoMatchingRowException("Not enough results when loading by unique.", null, this.getStructure());
			} else if (count > 1) {
				throw new TooManyMatchingRowsException("Too many results when loading by unique (" + count + ").",
						null,
						this.getStructure());
			}

			return this.loadUniqueInternal(c, data);
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

	@Override
	public Optional<T> loadUniqueIfExists(final T data) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.loadUniqueIfExists(c, data);
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
				querySQL = PCUtils.getStatementAsSQL(pstmt);

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

	@Override
	public <B> B query(final SQLQuery<T, B> query) throws DBException {
		try (AbstractConnection c = this.use()) {
			return this.query(c, query);
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
				querySQL = PCUtils.getStatementAsSQL(pstmt);

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
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_QUERY, this.getQueryable(), c, pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.queryableHookManager.executeDuring(RuleHookType.DURING_QUERY, this.getQueryable(), c, pstmt, query);
				final B r = safeTransQuery.transform(result);

				// after query hook
				this.queryableHookManager.executeAfter(RuleHookType.AFTER_QUERY, this.getQueryable(), c, pstmt, query);
				return r;
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				this.queryableHookManager.executeBefore(RuleHookType.BEFORE_QUERY, this.getQueryable(), c, pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.queryableHookManager.executeDuring(RuleHookType.DURING_QUERY, this.getQueryable(), c, pstmt, query);
				final List<T> output = new ArrayList<>();
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);

				// after query hook
				this.queryableHookManager.executeAfter(RuleHookType.AFTER_QUERY, this.getQueryable(), c, pstmt, query);
				return safeTransQuery.transform(output);
			} else {
				throw new UnsupportedQueryTypeException(query.getClass().getName(), "", this.getStructure(), query);
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

	public void setDbEntryUtils(final DatabaseEntryUtils dbEntryUtils) {
		this.databaseEntryUtils = dbEntryUtils;
	}

	@Override
	public void setViewStructure(final ViewStructure viewStructure) {
		PCUtils.requireNull(this.structure, "ViewStucture was already set once.");
		Objects.requireNonNull(viewStructure, "ViewStucture is null.");
		this.structure = viewStructure;
	}

	protected AbstractConnection use() throws DBException {
		return this.getConnector().use();
	}

	protected void validateStructure() {
		if (this.structure == null) {
			throw new NoStructureException(
					"View hasn't been scanned yet, use Database#register...(...).scanFromBeans() or use an indendent DatabaseScanner.\n"
							+ this.getClass() + " using target "
							+ (this.customHints != null ? this.customHints.getOrDefault(DefaultQueryableHints.TARGET_CLASS, "<unspecified>")
									: "<no custom hints>"),
					null,
					this.structure,
					new IllegalStateException());
		}
	}

}
