package lu.kbra.pclib.db.view;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.view.ViewColumnStructure;
import lu.kbra.pclib.db.autobuild.view.ViewStructure;
import lu.kbra.pclib.db.autobuild.view.ViewStructureBuilder;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.AbstractDataBaseConnector.CachedConnection.ConnectionHolder;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLBuilder;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class DataBaseView<T extends DataBaseEntry> implements AbstractDBView<T> {

	protected DataBase dataBase;
	protected DataBaseEntryUtils dbEntryUtils;
	protected ViewStructure viewStructure;
	protected Class<? extends AbstractDBView<T>> viewClass;

	public DataBaseView(final DataBase dataBase) {
		this(dataBase, dataBase.getDataBaseEntryUtils());
	}

	@SuppressWarnings("unchecked")
	public DataBaseView(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.viewClass = (Class<? extends AbstractDBView<T>>) this.getClass();
		gen();
	}

	public DataBaseView(
			final DataBase dataBase,
			final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBView<T>> viewClass) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.viewClass = viewClass;
		gen();
	}

	@Override
	public void requestHook(final SQLRequestType type, final Object query) {
	}

	protected void gen() {
		viewStructure = new ViewStructureBuilder<>(viewClass, dbEntryUtils).build();
	}

	@Override
	public boolean exists() throws DBException {
		try (ConnectionHolder c = this.use()) {
			final DatabaseMetaData dbMetaData = c.getMetaData();

			try (ResultSet rs = dbMetaData.getTables(this.dataBase.getDataBaseName(), null, this.getName(), null)) {
				return rs.next();
			}
		} catch (final SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public DataBaseViewStatus<T, ? extends DataBaseView<T>> create() throws DBException {
		if (this.exists()) {
			return new DataBaseViewStatus<>(true, this.getQueryable());
		} else {
			String querySQL = null;

			try (ConnectionHolder c = this.use(); Statement stmt = c.createStatement()) {

				final String sql = this.getCreateSQL();
				querySQL = sql;

				this.requestHook(SQLRequestType.CREATE_TABLE, sql);

				stmt.executeUpdate(sql);

				return new DataBaseViewStatus<>(false, this.getQueryable());
			} catch (final SQLException e) {
				throw new DBException("Error executing query: " + querySQL, e);
			}
		}
	}

	@Override
	public DataBaseView<T> drop() throws DBException {
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
	public T load(final T data) throws DBException {
		Statement stmt = null;
		ResultSet result = null;
		String querySQL = null;

		try (ConnectionHolder c = this.use()) {
			final PreparedStatement pstmt = c.prepareStatement(this.dbEntryUtils.getPreparedSelectSQL(this.getQueryable(), data));

			this.dbEntryUtils.prepareSelectSQL(pstmt, data);
			querySQL = PCUtils.getStatementAsSQL(pstmt);

			this.requestHook(SQLRequestType.SELECT, pstmt);

			result = pstmt.executeQuery();
			stmt = pstmt;

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			this.dbEntryUtils.fillLoad(data, result);
		} catch (final SQLException e) {
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, stmt);
		}

		return data;
	}

	@Override
	public <B> B query(final SQLQuery<T, B> query) throws DBException {
		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = query.toString();

		try (ConnectionHolder c = this.use()) {
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
		Statement stmt = null;
		ResultSet result = null;
		String querySQL = null;

		try (ConnectionHolder c = this.use()) {
			stmt = c.createStatement();

			final String sql = SQLBuilder.count(this.getQueryable());
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
	public String getCreateSQL() {
		return viewStructure.build(dataBase.getConnector());
	}

	protected DataBaseView<T> getQueryable() {
		return this;
	}

	public String[] getColumnNames() {
		return viewStructure.getTables()
				.stream()
				.flatMap(c -> c.getColumns().stream())
				.map(ViewColumnStructure::getName)
				.toArray(String[]::new);
	}

	@Override
	public String getName() {
		return viewStructure.getName();
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
	public String getQualifiedName() {
		return "`" + this.dataBase.getDataBaseName() + "`.`" + this.getName() + "`";
	}

	@Override
	public Class<? extends SQLQueryable<T>> getTargetClass() {
		return this.getViewClass();
	}

	public Class<? extends AbstractDBView<T>> getViewClass() {
		return this.viewClass;
	}

	@Override
	public DataBaseEntryUtils getDbEntryUtils() {
		return this.dbEntryUtils;
	}

	public void setDbEntryUtils(final DataBaseEntryUtils dbEntryUtils) {
		this.dbEntryUtils = dbEntryUtils;
	}

	public ViewStructure getViewStructure() {
		return viewStructure;
	}

	@Override
	public String toString() {
		return "DataBaseView@" + System.identityHashCode(this) + " [dataBase=" + this.dataBase + ", dbEntryUtils=" + this.dbEntryUtils
				+ ", viewClass=" + this.viewClass + "]";
	}

}
