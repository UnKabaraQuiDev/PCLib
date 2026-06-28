package lu.kbra.pclib.db.view;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.AbstractDataBaseConnector.CachedConnection.ConnectionHolder;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.domain.view.ViewColumnStructure;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.domain.view.ViewStructureBuilder;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.SQLRequestType;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

@ToString
public class DataBaseView<T extends DataBaseEntry> implements AbstractDBView<T> {

	@Getter
	protected DataBase database;
	@Getter
	protected DataBaseEntryUtils dataBaseEntryUtils;
	@Getter
	protected ViewStructure viewStructure;
	@Getter
	protected Class<? extends AbstractDBView<T>> viewClass;

	public DataBaseView(final DataBase dataBase) {
		this(dataBase, dataBase.getDataBaseEntryUtils());
	}

	@SuppressWarnings("unchecked")
	public DataBaseView(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		this.database = dataBase;
		this.dataBaseEntryUtils = dbEntryUtils;
		this.viewClass = (Class<? extends AbstractDBView<T>>) this.getClass();
		this.gen();
	}

	public DataBaseView(
			final DataBase dataBase,
			final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBView<T>> viewClass) {
		this.database = dataBase;
		this.dataBaseEntryUtils = dbEntryUtils;
		this.viewClass = viewClass;
		this.gen();
	}

	@Override
	public int count() throws DBException {
		Statement stmt = null;
		ResultSet result = null;
		String querySQL = null;

		try (ConnectionHolder c = this.use()) {
			stmt = c.createStatement();

			final String sql = dataBaseEntryUtils.getStructureVisitor().count(this.getQueryable());
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
	public boolean exists() throws DBException {
		try (ConnectionHolder c = this.use()) {
			final DatabaseMetaData dbMetaData = c.getMetaData();

			try (ResultSet rs = dbMetaData.getTables(c.getCatalog(), c.getSchema(), this.getName(), null)) {
				return rs.next();
			}
		} catch (final SQLException e) {
			throw new DBException(e);
		}
	}

	public String[] getColumnNames() {
		return this.viewStructure.getTables()
				.stream()
				.flatMap(c -> c.getColumns().stream())
				.map(ViewColumnStructure::getName)
				.toArray(String[]::new);
	}

	@Override
	public DataBaseConnector getConnector() {
		return this.database.getConnector();
	}

	@Override
	public String getCreateSQL() {
		return dataBaseEntryUtils.getStructureVisitor().create(this.viewStructure);
	}

	@Override
	public String getName() {
		return this.viewStructure.getName();
	}

	@Override
	public String getQualifiedName() {
		return this.dataBaseEntryUtils.getStructureVisitor().qualifiedName(this);
	}

	@Override
	public Class<? extends SQLQueryable<T>> getTargetClass() {
		return this.getViewClass();
	}

	@Override
	public T load(final T data) throws DBException {
		Statement stmt = null;
		ResultSet result = null;
		String querySQL = null;

		try (ConnectionHolder c = this.use()) {
			final PreparedStatement pstmt = c.prepareStatement(this.dataBaseEntryUtils.getPreparedSelectSQL(this.getQueryable(), data));

			this.dataBaseEntryUtils.prepareSelectSQL(pstmt, data);
			querySQL = PCUtils.getStatementAsSQL(pstmt);

			this.requestHook(SQLRequestType.SELECT, pstmt);

			result = pstmt.executeQuery();
			stmt = pstmt;

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			this.dataBaseEntryUtils.fillLoad(data, result);
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

				safeQuery.updateQuerySQL(getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final List<T> output = new ArrayList<>();
				this.dataBaseEntryUtils.fillLoadAll(this.getEntryClass(), result, output::add);

				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(getQueryable(), pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				this.requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				return safeTransQuery.transform(result);
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				pstmt = c.prepareStatement(safeTransQuery.getPreparedQuerySQL(this.getQueryable()));

				safeTransQuery.updateQuerySQL(getQueryable(), pstmt);
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
			throw new DBException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public void requestHook(final SQLRequestType type, final Object query) {
	}

	public void setDbEntryUtils(final DataBaseEntryUtils dbEntryUtils) {
		this.dataBaseEntryUtils = dbEntryUtils;
	}

	@Deprecated
	protected Connection connect() throws DBException {
		return this.getConnector().connect();
	}

	@Deprecated
	protected Connection createConnection() throws DBException {
		return this.getConnector().createConnection();
	}

	protected String doubleQuoteEscapeIdentifier(final String identifier) {
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	protected void gen() {
		this.viewStructure = new ViewStructureBuilder<>(this.viewClass, this.dataBaseEntryUtils).build();
	}

	protected DataBaseView<T> getQueryable() {
		return this;
	}

	@Deprecated
	protected boolean isSQLite() {
		return "sqlite".equalsIgnoreCase(this.getConnector().getProtocol());
	}

	protected ConnectionHolder use() throws DBException {
		return this.getConnector().use();
	}

}
