package lu.kbra.pclib.db.view;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.view.DB_View;
import lu.kbra.pclib.db.annotations.view.UnionTable;
import lu.kbra.pclib.db.annotations.view.ViewColumn;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.annotations.view.ViewTable.Type;
import lu.kbra.pclib.db.annotations.view.ViewWithTable;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.AbstractDataBaseConnector.CachedConnection.ConnectionHolder;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLTypeAnnotated;
import lu.kbra.pclib.db.table.DBException;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLBuilder;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class DataBaseView<T extends DataBaseEntry> implements AbstractDBView<T>, SQLTypeAnnotated<DB_View> {

	protected DataBase dataBase;
	protected DataBaseEntryUtils dbEntryUtils;
	protected Class<? extends AbstractDBView<T>> viewClass;

	public DataBaseView(final DataBase dataBase) {
		this(dataBase, dataBase.getDataBaseEntryUtils());
	}

	@SuppressWarnings("unchecked")
	public DataBaseView(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.viewClass = (Class<? extends AbstractDBView<T>>) this.getClass();
	}

	public DataBaseView(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils, final Class<? extends AbstractDBView<T>> viewClass) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.viewClass = viewClass;
	}

	@Override
	public void requestHook(final SQLRequestType type, final Object query) {
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
		if (this.getTypeAnnotation().customSQL() != null && !this.getTypeAnnotation().customSQL().equals("")) {
			return this.getTypeAnnotation().customSQL();
		}

		String sql = "CREATE VIEW " + this.getQualifiedName() + " AS \n";
		if (this.getTypeAnnotation().with().length != 0) {
			for (final ViewWithTable vwt : this.getTypeAnnotation().with()) {
				sql += "WITH " + this.escape(vwt.name()) + " AS (\n" + this.getCreateSQL(vwt) + "\n)\n";
			}
		}
		sql += "SELECT" + (this.getMainTable().distinct() ? " DISTINCT" : "") + "\n";
		sql += PCUtils.leftPadLine(Arrays.stream(this.getTables())
				.flatMap(t -> Arrays.stream(t.columns()).map(c -> this.getCreateSQL(t, c)))
				.collect(Collectors.joining(", \n")), "\t") + "\n";

		if (this.getMainTable().join() == ViewTable.Type.MAIN_UNION || this.getMainTable().join() == ViewTable.Type.MAIN_UNION_ALL) {
			sql += "FROM (\n"
					+ PCUtils.leftPadLine(
							Arrays.stream(this.getTypeAnnotation().unionTables())
									.map(c -> this.getCreateSQL(c))
									.collect(Collectors.joining(
											this.getMainTable().join().equals(ViewTable.Type.MAIN_UNION) ? "UNION \n" : "UNION ALL \n")),
							"\t");
			sql += ") " + (this.getMainTable().asName().equals("") ? "" : " AS " + this.escape(this.getMainTable().asName()));
		} else {
			sql += "FROM \n\t"
					+ this.escape(this.getMainTable().name().equals("") ? this.getTypeName(this.getMainTable().typeName())
							: this.getMainTable().name())
					+ " " + (this.getMainTable().asName().equals("") ? "" : " AS " + this.escape(this.getMainTable().asName()));

			for (final ViewTable vt : this.getJoinTables()) {
				sql += "\n" + vt.join() + " JOIN " + (vt.name().equals("") ? this.getTypeName(vt.typeName()) : vt.name())
						+ (vt.asName().equals("") ? "" : " AS " + this.escape(vt.asName())) + " ON " + vt.on();
			}
		}

		if (!this.getTypeAnnotation().condition().equals("")) {
			sql += "\nWHERE \n\t" + this.getTypeAnnotation().condition();
		}
		if (this.getTypeAnnotation().groupBy().length != 0) {
			sql += "\nGROUP BY \n\t"
					+ Arrays.stream(this.getTypeAnnotation().groupBy()).map(o -> this.escape(o)).collect(Collectors.joining(", "));
		}
		if (this.getTypeAnnotation().orderBy().length != 0) {
			sql += "\nORDER BY \n\t" + (Arrays.stream(this.getTypeAnnotation().orderBy())
					.map(o -> this.escape(o.column()) + " " + o.type())
					.collect(Collectors.joining(", ")));
		}
		sql += ";";
		return sql;
	}

	private String getCreateSQL(final ViewWithTable vwt) {
		String sql = "SELECT\n";
		sql += PCUtils.leftPadLine(Arrays.stream(vwt.columns()).map(c -> this.getCreateSQL(c)).collect(Collectors.joining(", \n")), "\t")
				+ "\n";

		final ViewTable mainTable = Arrays.stream(vwt.tables()).filter(t -> t.join() == Type.MAIN).findFirst().orElse(null);

		sql += "FROM \n\t" + this.escape(this.dataBase.getDataBaseName()) + "."
				+ this.escape(mainTable.name().equals("") ? this.getTypeName(mainTable.typeName()) : mainTable.name())
				+ (mainTable.asName().equals("") ? "" : " AS " + mainTable.asName());

		for (final ViewTable vt : Arrays.stream(vwt.tables()).filter(t -> t.join() != Type.MAIN).collect(Collectors.toList())) {
			sql += "\n" + vt.join() + " JOIN " + (vt.name().equals("") ? this.getTypeName(vt.typeName()) : vt.name())
					+ (vt.asName().equals("") ? "" : " AS " + this.escape(vt.asName())) + " ON " + vt.on();
		}

		if (!vwt.condition().equals("")) {
			sql += "\nWHERE \n\t" + vwt.condition();
		}
		if (vwt.groupBy().length != 0) {
			sql += "\nGROUP BY \n\t" + Arrays.stream(vwt.groupBy()).map(o -> this.escape(o)).collect(Collectors.joining(", "));
		}
		if (vwt.orderBy().length != 0) {
			sql += "\nORDER BY \n\t"
					+ (Arrays.stream(vwt.orderBy()).map(o -> this.escape(o.column()) + " " + o.type()).collect(Collectors.joining(", ")));
		}

		return sql;
	}

	private String getCreateSQL(final UnionTable t) {
		String typeName = this.getTypeName(t.typeName());
		if (t.name().equals("") && (typeName == null || typeName.equals(""))) {
			throw new IllegalArgumentException("UnionTable name cannot be empty/undefined.");
		}
		if (typeName == null || typeName.equals("")) {
			typeName = t.name();
		}

		String sql = "SELECT \n\t";
		sql += Arrays.stream(t.columns()).map(o -> this.getCreateSQL(t, o)).collect(Collectors.joining(", \n\t"));
		sql += "\nFROM \n\t" + this.escape(typeName) + "\n";
		return sql;
	}

	private String escape(final String column) {
		if (column == null) {
			throw new IllegalArgumentException("Column name cannot be null.");
		}
		return column.startsWith("`") && column.endsWith("`") ? column : "`" + column + "`";
	}

	protected String getCreateSQL(final ViewTable t, final ViewColumn c) {
		String typeName = this.getTypeName(t.typeName());
		if (t.name().equals("") && (typeName == null || typeName.equals(""))) {
			throw new IllegalArgumentException("ViewTable name cannot be empty/undefined.");
		}
		if (typeName == null || typeName.equals("")) {
			typeName = t.name();
		}

		return (c.name().equals("") ? c.func()
				: (this.escape(t.asName().equals("") ? typeName : t.asName()) + "." + ("*".equals(c.name()) ? "*" : this.escape(c.name()))))
				+ (c.asName().equals("") ? (c.name().equals("") || c.name().equals("*") ? "" : (" AS " + this.escape(c.name())))
						: " AS " + this.escape(c.asName()));
	}

	protected String getCreateSQL(final ViewColumn c) {
		return (c.name().equals("") ? c.func() : ("*".equals(c.name()) ? "*" : this.escape(c.name())))
				+ (c.asName().equals("") ? (c.name().equals("") || c.name().equals("*") ? "" : (" AS " + this.escape(c.name())))
						: " AS " + this.escape(c.asName()));
	}

	protected String getCreateSQL(final UnionTable t, final ViewColumn c) {
		String typeName = this.getTypeName(t.typeName());
		if (t.name().equals("") && (typeName == null || typeName.equals(""))) {
			throw new IllegalArgumentException("UnionTable name cannot be empty/undefined.");
		}
		if (typeName == null || typeName.equals("")) {
			typeName = t.name();
		}

		return (c.name().equals("") ? c.func() : (this.escape(typeName) + "." + ("*".equals(c.name()) ? "*" : this.escape(c.name()))))
				+ (c.asName().equals("") ? (c.name().equals("") || c.name().equals("*") ? "" : (" AS " + this.escape(c.name())))
						: " AS " + this.escape(c.asName()));
	}

	public String getTypeName(final Class<?> clazz) {
		final String name = SQLTypeAnnotated.getTypeName(clazz);
		if (name != null)
			return name;

		if (SQLQueryable.class.isAssignableFrom(clazz)) {
			return this.dbEntryUtils.getQueryableName((Class<? extends SQLQueryable<T>>) clazz);
		}

		return null;
	}

	protected DataBaseView<T> getQueryable() {
		return this;
	}

	public String[] getColumnNames() {
		return Arrays.stream(this.getTypeAnnotation().tables())
				.flatMap(table -> Arrays.stream(table.columns()))
				.map((c) -> c.asName())
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
	public String getName() {
		return this.getTypeAnnotation().name();
	}

	@Override
	public String getQualifiedName() {
		return "`" + this.dataBase.getDataBaseName() + "`.`" + this.getName() + "`";
	}

	private ViewTable getMainTable() {
		return Arrays.stream(this.getTypeAnnotation().tables())
				.filter(t -> t.join().equals(ViewTable.Type.MAIN) || t.join().equals(ViewTable.Type.MAIN_UNION)
						|| t.join().equals(ViewTable.Type.MAIN_UNION_ALL))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No table marked as " + ViewTable.Type.MAIN + "."));
	}

	private ViewTable[] getJoinTables() {
		return Arrays.stream(this.getTypeAnnotation().tables())
				.filter(t -> !t.join().equals(ViewTable.Type.MAIN))
				.toArray(ViewTable[]::new);
	}

	private ViewTable[] getTables() {
		return this.getTypeAnnotation().tables();
	}

	@Override
	public DB_View getTypeAnnotation() {
		return this.viewClass.getAnnotation(DB_View.class);
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

	public static class DataBaseViewStatus<T extends DataBaseEntry, B extends AbstractDBView<T>> {

		private final boolean existed;
		private final B table;

		protected DataBaseViewStatus(final boolean existed, final B table) {
			this.existed = existed;
			this.table = table;
		}

		public boolean existed() {
			return this.existed;
		}

		public boolean created() {
			return !this.existed;
		}

		public B getQueryable() {
			return this.table;
		}

		@Override
		public String toString() {
			return "DataBaseViewStatus{existed=" + this.existed + ", created=" + !this.existed + ", table=" + this.table + "}";
		}

	}

	@Override
	public String toString() {
		return "DataBaseView@" + System.identityHashCode(this) + " [dataBase=" + this.dataBase + ", dbEntryUtils=" + this.dbEntryUtils
				+ ", viewClass=" + this.viewClass + "]";
	}

}
