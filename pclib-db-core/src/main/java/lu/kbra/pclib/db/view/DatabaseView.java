package lu.kbra.pclib.db.view;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.Getter;
import lombok.ToString;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.AbstractDatabaseConnector.CachedConnection.ConnectionHolder;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.exception.CountQueryFailedException;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.InternalSQLException;
import lu.kbra.pclib.db.exception.NoMatchingRowException;
import lu.kbra.pclib.db.exception.NoStructureException;
import lu.kbra.pclib.db.exception.UnsupportedQueryTypeException;
import lu.kbra.pclib.db.hook.RuleHookType;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

@Getter
@ToString
public class DatabaseView<T extends DatabaseEntry> implements AbstractDBView<T> {

	protected Database database;
	protected DatabaseEntryUtils databaseEntryUtils;
	protected ViewStructure structure;
	protected Map<String, Object> customHints = new HashMap<>();

	protected DatabaseView() {
	}

	public DatabaseView(final Database database) {
		this(database, database.getDatabaseEntryUtils());
	}

	public DatabaseView(final Database database, final String name) {
		this(database, database.getDatabaseEntryUtils());
		this.customHints.put(DefaultQueryableHints.NAME_OVERRIDE, name);
	}

	protected DatabaseView(final Database database, final DatabaseEntryUtils dbEntryUtils) {
		this.database = database;
		this.databaseEntryUtils = dbEntryUtils;
		this.customHints.put(DefaultQueryableHints.TARGET_CLASS, this.getClass());
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
	}

	@Override
	public void setViewStructure(final ViewStructure viewStructure) {
		PCUtils.requireNull(this.structure, "ViewStucture was already set once.");
		Objects.requireNonNull(viewStructure, "ViewStucture is null.");
		this.structure = viewStructure;
	}

	@Override
	public int count() throws DBException {
		this.validateStructure();

		Statement stmt = null;
		ResultSet result = null;
		String querySQL = null;

		try (ConnectionHolder c = this.use()) {
			// prepare count hook
			this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_COUNT, getQueryable(), c, null);

			stmt = c.createStatement();

			final String sql = this.databaseEntryUtils.getStructureVisitor().count(this.getQueryable());
			querySQL = sql;

			// before count hook
			this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_COUNT, getQueryable(), stmt, null);
			result = stmt.executeQuery(sql);

			if (!result.next()) {
				throw new CountQueryFailedException(querySQL, getStructure());
			}

			final int r = result.getInt("count");

			// after count hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_COUNT, getQueryable(), stmt, r);
			return r;
		} catch (final SQLException e) {
			throw new InternalSQLException("Error executing query.", querySQL, getStructure(), e);
		} finally {
			PCUtils.close(result, stmt);
		}
	}

	@Override
	public DatabaseViewStatus<T, ? extends DatabaseView<T>> create() throws DBException {
		this.validateStructure();

		if (this.exists()) {
			return new DatabaseViewStatus<>(true, this.getQueryable());
		} else {
			String querySQL = null;

			try (ConnectionHolder c = this.use()) {
				// prepare create hook
				this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_CREATE, getQueryable(), c, null);
				try (Statement stmt = c.createStatement()) {
					final String[] sql = this.getCreateSQL();
					querySQL = "";
					for (final String str : sql) {
						querySQL += str + "\n";

						// during create hook
						this.databaseEntryUtils.getQueryableHookManager()
								.executeBefore(RuleHookType.BEFORE_CREATE, getQueryable(), stmt, null);
						stmt.executeUpdate(str);
					}

					// after create hook
					this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_CREATE, getQueryable(), stmt, null);
					return new DatabaseViewStatus<>(false, this.getQueryable());
				}
			} catch (final SQLException e) {
				throw new InternalSQLException("Error executing statements.", querySQL, this.structure, e);
			}
		}
	}

	@Override
	public DatabaseView<T> drop() throws DBException {
		this.validateStructure();

		String querySQL = null;

		try (ConnectionHolder c = this.use()) {
			// prepare drop hook
			this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_DROP, getQueryable(), c, null);
			try (Statement stmt = c.createStatement()) {
				final String sql = "DROP VIEW " + this.getQualifiedName() + ";";
				querySQL = sql;

				// before drop hook
				this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_DROP, getQueryable(), stmt, null);
				stmt.executeUpdate(sql);

				// after drop hook
				this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_DROP, getQueryable(), stmt, null);
				return this.getQueryable();
			}
		} catch (final SQLException e) {
			throw new InternalSQLException("Error executing query.", querySQL, getStructure(), e);
		}
	}

	@Override
	public boolean exists() throws DBException {
		this.validateStructure();

		try (ConnectionHolder c = this.use()) {
			final DatabaseMetaData dbMetaData = c.getMetaData();

			try (final ResultSet rs = dbMetaData.getTables(this.database.getDatabaseName(),
					this.databaseEntryUtils.getStructureVisitor().schemaName(this.getQueryable()),
					this.getName(),
					null)) {
				return rs.next();
			}
		} catch (final SQLException e) {
			throw new InternalSQLException("Error retrieving tables.", null, getStructure(), e);
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
	public T load(final T data) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = null;

		try (ConnectionHolder c = this.use()) {
			// prepare load hook
			this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_LOAD, getQueryable(), c, data);
			{
				pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectSQL(this.getQueryable(), data));

				this.databaseEntryUtils.prepareSelectSQL(pstmt, this.getQueryable(), data);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before load hook
				this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_LOAD, getQueryable(), pstmt, data);
				result = pstmt.executeQuery();
			}

			if (!result.next()) {
				throw new NoMatchingRowException("Couldn't load data, no entry matching query.", querySQL, getStructure());
			}

			// during load hook
			this.databaseEntryUtils.getQueryableHookManager().executeDuring(RuleHookType.DURING_LOAD, getQueryable(), pstmt, data);
			this.databaseEntryUtils.fillLoad(this.getQueryable(), data, result);

			// after load hook
			this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_LOAD, getQueryable(), pstmt, data);
			return data;
		} catch (final SQLException e) {
			throw new InternalSQLException("Error executing query.", querySQL, getStructure(), e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public <B> B query(final SQLQuery<T, B> query) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = query.toString();

		try (ConnectionHolder c = this.use()) {
			// prepare load hook
			this.databaseEntryUtils.getQueryableHookManager().executePrepare(RuleHookType.PREPARE_QUERY, getQueryable(), c, query);

			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				pstmt = c.prepareStatement(safeQuery.getPreparedQuerySQL(this.getQueryable()));

				safeQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_QUERY, getQueryable(), pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.databaseEntryUtils.getQueryableHookManager().executeDuring(RuleHookType.DURING_QUERY, getQueryable(), pstmt, query);
				final List<T> output = new ArrayList<>();
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);

				// after query hook
				this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_QUERY, getQueryable(), pstmt, query);
				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_QUERY, getQueryable(), pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.databaseEntryUtils.getQueryableHookManager().executeDuring(RuleHookType.DURING_QUERY, getQueryable(), pstmt, query);
				final B r = safeTransQuery.transform(result);

				// after query hook
				this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_QUERY, getQueryable(), pstmt, query);
				return r;
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				// before query hook
				this.databaseEntryUtils.getQueryableHookManager().executeBefore(RuleHookType.BEFORE_QUERY, getQueryable(), pstmt, query);
				result = pstmt.executeQuery();

				// during query hook
				this.databaseEntryUtils.getQueryableHookManager().executeDuring(RuleHookType.DURING_QUERY, getQueryable(), pstmt, query);
				final List<T> output = new ArrayList<>();
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);

				// after query hook
				this.databaseEntryUtils.getQueryableHookManager().executeAfter(RuleHookType.AFTER_QUERY, getQueryable(), pstmt, query);
				return safeTransQuery.transform(output);
			} else {
				throw new UnsupportedQueryTypeException(query.getClass().getName(), "", getStructure(), query);
			}
		} catch (final SQLException e) {
			throw new InternalSQLException("Error executing query.", querySQL, getStructure(), query, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	public void setDbEntryUtils(final DatabaseEntryUtils dbEntryUtils) {
		this.databaseEntryUtils = dbEntryUtils;
	}

	protected String doubleQuoteEscapeIdentifier(final String identifier) {
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	protected DatabaseView<T> getQueryable() {
		return this;
	}

	protected ConnectionHolder use() throws DBException {
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
