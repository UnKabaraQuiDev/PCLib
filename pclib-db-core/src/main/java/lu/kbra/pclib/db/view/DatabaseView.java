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

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.AbstractDatabaseConnector.CachedConnection.ConnectionHolder;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.SQLRequestType;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.Getter;
import lombok.ToString;

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
			stmt = c.createStatement();

			final String sql = this.databaseEntryUtils.getStructureVisitor().count(this.getQueryable());
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

			try (ConnectionHolder c = this.use(); Statement stmt = c.createStatement()) {
				final String[] sql = this.getCreateSQL();
				querySQL = "";
				for (final String str : sql) {
					querySQL += str + "\n";

					this.requestHook(SQLRequestType.CREATE_TABLE, sql);

					final int result = stmt.executeUpdate(str);
				}
			} catch (final SQLException e) {
				throw new DBException("Error executing statements.", querySQL, this.structure, e);
			}
			return new DatabaseViewStatus<>(false, this.getQueryable());
		}
	}

	@Override
	public DatabaseView<T> drop() throws DBException {
		this.validateStructure();

		String querySQL = null;

		try (ConnectionHolder c = this.use(); Statement stmt = c.createStatement()) {
			final String sql = "DROP VIEW " + this.getQualifiedName() + ";";
			querySQL = sql;

			this.requestHook(SQLRequestType.DROP_VIEW, sql);

			stmt.executeUpdate(sql);
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		}

		return this.getQueryable();
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
			throw new DBException(e);
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

		Statement stmt = null;
		ResultSet result = null;
		String querySQL = null;

		try (ConnectionHolder c = this.use()) {
			final PreparedStatement pstmt = c.prepareStatement(this.databaseEntryUtils.getPreparedSelectSQL(this.getQueryable(), data));

			this.databaseEntryUtils.prepareSelectSQL(pstmt, this.getQueryable(), data);
			querySQL = PCUtils.getStatementAsSQL(pstmt);

			this.requestHook(SQLRequestType.SELECT, pstmt);

			result = pstmt.executeQuery();
			stmt = pstmt;

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			this.databaseEntryUtils.fillLoad(this.getQueryable(), data, result);
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, stmt);
		}

		return data;
	}

	@Override
	public <B> B query(final SQLQuery<T, B> query) throws DBException {
		this.validateStructure();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = query.toString();

		try (ConnectionHolder c = this.use()) {
			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				pstmt = c.prepareStatement(safeQuery.getPreparedQuerySQL(this.getQueryable()));

				safeQuery.updateQuerySQL(this.getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final List<T> output = new ArrayList<>();
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);

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
				this.databaseEntryUtils.fillLoadAll(this.getQueryable(), this.getEntryClass(), result, output::add);

				return safeTransQuery.transform(output);
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
	public void requestHook(final SQLRequestType type, final Object query) {
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
			throw new DBException(
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
