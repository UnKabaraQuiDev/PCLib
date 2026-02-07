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
import lu.kbra.pclib.db.DataBase;
import lu.kbra.pclib.db.annotations.view.DB_View;
import lu.kbra.pclib.db.annotations.view.UnionTable;
import lu.kbra.pclib.db.annotations.view.ViewColumn;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.annotations.view.ViewTable.Type;
import lu.kbra.pclib.db.annotations.view.ViewWithTable;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLTypeAnnotated;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLBuilder;
import lu.kbra.pclib.db.utils.SQLRequestType;

public abstract class DataBaseView<T extends DataBaseEntry> implements AbstractDBView<T>, SQLTypeAnnotated<DB_View> {

	protected DataBase dataBase;
	protected DataBaseEntryUtils dbEntryUtils = new BaseDataBaseEntryUtils();
	protected Class<? extends AbstractDBView<T>> viewClass;

	public DataBaseView(DataBase dataBase) {
		this(dataBase, new BaseDataBaseEntryUtils());
	}

	@SuppressWarnings("unchecked")
	public DataBaseView(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.viewClass = (Class<? extends AbstractDBView<T>>) getClass();
	}

	public DataBaseView(DataBase dataBase, DataBaseEntryUtils dbEntryUtils,
			Class<? extends AbstractDBView<T>> viewClass) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.viewClass = viewClass;
	}

	@Override
	public void requestHook(SQLRequestType type, Object query) {
	}

	@Override
	public boolean exists() throws SQLException {
		final Connection con = connect();

		final DatabaseMetaData dbMetaData = con.getMetaData();
		try (ResultSet rs = dbMetaData.getTables(dataBase.getDataBaseName(), null, getName(), null)) {

			if (rs.next()) {
				rs.close();

				return true;
			} else {
				rs.close();

				return false;
			}
		} catch (SQLException e) {
			throw e;
		}
	}

	@Override
	public DataBaseViewStatus<T, ? extends DataBaseView<T>> create() throws SQLException {
		if (exists()) {
			return new DataBaseViewStatus<>(true, getQueryable());
		} else {
			final Connection con = connect();

			String querySQL = null;

			try (Statement stmt = con.createStatement()) {

				final String sql = getCreateSQL();
				querySQL = sql;

				requestHook(SQLRequestType.CREATE_TABLE, sql);

				stmt.executeUpdate(sql);

				return new DataBaseViewStatus<>(false, getQueryable());
			} catch (SQLException e) {
				throw new SQLException("Error executing query: " + querySQL, e);
			}
		}
	}

	@Override
	public DataBaseView<T> drop() throws SQLException {
		final Connection con = connect();

		String querySQL = null;

		try (Statement stmt = con.createStatement()) {
			final String sql = "DROP VIEW " + getQualifiedName() + ";";
			querySQL = sql;

			requestHook(SQLRequestType.DROP_VIEW, sql);

			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		}

		return getQueryable();
	}

	@Override
	public T load(T data) throws SQLException {
		final Connection con = connect();

		Statement stmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			final PreparedStatement pstmt = con
					.prepareStatement(dbEntryUtils.getPreparedSelectSQL(getQueryable(), data));

			dbEntryUtils.prepareSelectSQL(pstmt, data);
			querySQL = PCUtils.getStatementAsSQL(pstmt);

			requestHook(SQLRequestType.SELECT, pstmt);

			result = pstmt.executeQuery();
			stmt = pstmt;

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			dbEntryUtils.fillLoad(data, result);
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, stmt);
		}

		return data;
	}

	@Override
	public <B> B query(SQLQuery<T, B> query) throws SQLException {
		final Connection con = connect();

		PreparedStatement pstmt = null;
		ResultSet result = null;
		String querySQL = query.toString();

		try {
			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				pstmt = con.prepareStatement(safeQuery.getPreparedQuerySQL(getQueryable()));

				safeQuery.updateQuerySQL(pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final List<T> output = new ArrayList<>();
				dbEntryUtils.fillLoadAllTable(getTargetClass(), query, result, output::add);

				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				pstmt = con.prepareStatement(safeTransQuery.getPreparedQuerySQL(getQueryable()));

				safeTransQuery.updateQuerySQL(pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final B output = safeTransQuery.transform(result);

				return output;
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				pstmt = con.prepareStatement(safeTransQuery.getPreparedQuerySQL(getQueryable()));

				safeTransQuery.updateQuerySQL(pstmt);
				querySQL = PCUtils.getStatementAsSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();

				final List<T> output = new ArrayList<>();
				dbEntryUtils.fillLoadAllTable(getTargetClass(), query, result, output::add);

				final B filteredOutput = safeTransQuery.transform(output);

				return filteredOutput;
			} else {
				throw new IllegalArgumentException("Unsupported type: " + query.getClass().getName());
			}
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, pstmt);
		}
	}

	@Override
	public int count() throws SQLException {
		final Connection con = connect();

		Statement stmt = null;
		ResultSet result = null;
		String querySQL = null;

		try {
			stmt = con.createStatement();

			final String sql = SQLBuilder.count(getQueryable());
			querySQL = sql;

			requestHook(SQLRequestType.SELECT, sql);

			result = stmt.executeQuery(sql);

			if (!result.next()) {
				throw new IllegalStateException("Couldn't query entry count.");
			}

			return result.getInt("count");
		} catch (SQLException e) {
			throw new SQLException("Error executing query: " + querySQL, e);
		} finally {
			PCUtils.close(result, stmt);
		}
	}

	@Override
	public String getCreateSQL() {
		if (getTypeAnnotation().customSQL() != null && !getTypeAnnotation().customSQL().equals("")) {
			return getTypeAnnotation().customSQL();
		}

		String sql = "CREATE VIEW " + getQualifiedName() + " AS \n";
		if (getTypeAnnotation().with().length != 0) {
			for (ViewWithTable vwt : getTypeAnnotation().with()) {
				sql += "WITH " + escape(vwt.name()) + " AS (\n" + getCreateSQL(vwt) + "\n)\n";
			}
		}
		sql += "SELECT\n";
		sql += PCUtils.leftPadLine(
				Arrays.stream(getTables()).flatMap(t -> Arrays.stream(t.columns()).map(c -> getCreateSQL(t, c)))
						.collect(Collectors.joining(", \n")),
				"\t") + "\n";

		if (getMainTable().join().equals(ViewTable.Type.MAIN_UNION)
				|| getMainTable().join().equals(ViewTable.Type.MAIN_UNION_ALL)) {
			sql += "FROM (\n" + PCUtils.leftPadLine(Arrays.stream(getTypeAnnotation().unionTables())
					.map(c -> getCreateSQL(c))
					.collect(Collectors.joining(
							getMainTable().join().equals(ViewTable.Type.MAIN_UNION) ? "UNION \n" : "UNION ALL \n")),
					"\t");
			sql += ") " + (getMainTable().asName().equals("") ? "" : " AS " + escape(getMainTable().asName()));
		} else {
			sql += "FROM \n\t"
					+ escape(getMainTable().name().equals("") ? getTypeName(getMainTable().typeName())
							: getMainTable().name())
					+ " " + (getMainTable().asName().equals("") ? "" : " AS " + escape(getMainTable().asName()));

			for (ViewTable vt : getJoinTables()) {
				sql += "\n" + vt.join() + " JOIN " + (vt.name().equals("") ? getTypeName(vt.typeName()) : vt.name())
						+ (vt.asName().equals("") ? "" : " AS " + escape(vt.asName())) + " ON " + vt.on();
			}
		}

		if (!getTypeAnnotation().condition().equals("")) {
			sql += "\nWHERE \n\t" + getTypeAnnotation().condition();
		}
		if (getTypeAnnotation().groupBy().length != 0) {
			sql += "\nGROUP BY \n\t" + Arrays.stream(getTypeAnnotation().groupBy()).map(o -> escape(o))
					.collect(Collectors.joining(", "));
		}
		if (getTypeAnnotation().orderBy().length != 0) {
			sql += "\nORDER BY \n\t" + (Arrays.stream(getTypeAnnotation().orderBy())
					.map(o -> escape(o.column()) + " " + o.type()).collect(Collectors.joining(", ")));
		}
		sql += ";";
		return sql;
	}

	private String getCreateSQL(ViewWithTable vwt) {
		String sql = "SELECT\n";
		sql += PCUtils.leftPadLine(
				Arrays.stream(vwt.columns()).map(c -> getCreateSQL(c)).collect(Collectors.joining(", \n")), "\t")
				+ "\n";

		final ViewTable mainTable = Arrays.stream(vwt.tables()).filter(t -> t.join() == Type.MAIN).findFirst()
				.orElse(null);

		sql += "FROM \n\t" + escape(dataBase.getDataBaseName()) + "."
				+ escape(mainTable.name().equals("") ? getTypeName(mainTable.typeName()) : mainTable.name())
				+ (mainTable.asName().equals("") ? "" : " AS " + mainTable.asName());

		for (ViewTable vt : Arrays.stream(vwt.tables()).filter(t -> t.join() != Type.MAIN)
				.collect(Collectors.toList())) {
			sql += "\n" + vt.join() + " JOIN " + (vt.name().equals("") ? getTypeName(vt.typeName()) : vt.name())
					+ (vt.asName().equals("") ? "" : " AS " + escape(vt.asName())) + " ON " + vt.on();
		}

		if (!vwt.condition().equals("")) {
			sql += "\nWHERE \n\t" + vwt.condition();
		}
		if (vwt.groupBy().length != 0) {
			sql += "\nGROUP BY \n\t"
					+ Arrays.stream(vwt.groupBy()).map(o -> escape(o)).collect(Collectors.joining(", "));
		}
		if (vwt.orderBy().length != 0) {
			sql += "\nORDER BY \n\t" + (Arrays.stream(vwt.orderBy()).map(o -> escape(o.column()) + " " + o.type())
					.collect(Collectors.joining(", ")));
		}

		return sql;
	}

	private String getCreateSQL(UnionTable t) {
		String typeName = getTypeName(t.typeName());
		if (t.name().equals("") && (typeName == null || typeName.equals(""))) {
			throw new IllegalArgumentException("UnionTable name cannot be empty/undefined.");
		}
		if (typeName == null || typeName.equals("")) {
			typeName = t.name();
		}

		String sql = "SELECT \n\t";
		sql += Arrays.stream(t.columns()).map(o -> getCreateSQL(t, o)).collect(Collectors.joining(", \n\t"));
		sql += "\nFROM \n\t" + escape(typeName) + "\n";
		return sql;
	}

	private String escape(String column) {
		if (column == null) {
			throw new IllegalArgumentException("Column name cannot be null.");
		}
		return column.startsWith("`") && column.endsWith("`") ? column : "`" + column + "`";
	}

	protected String getCreateSQL(ViewTable t, ViewColumn c) {
		String typeName = getTypeName(t.typeName());
		if (t.name().equals("") && (typeName == null || typeName.equals(""))) {
			throw new IllegalArgumentException("ViewTable name cannot be empty/undefined.");
		}
		if (typeName == null || typeName.equals("")) {
			typeName = t.name();
		}

		return (c.name().equals("") ? c.func()
				: (escape(t.asName().equals("") ? typeName : t.asName()) + "."
						+ ("*".equals(c.name()) ? "*" : escape(c.name()))))
				+ (c.asName().equals("")
						? (c.name().equals("") || c.name().equals("*") ? "" : (" AS " + escape(c.name())))
						: " AS " + escape(c.asName()));
	}

	protected String getCreateSQL(ViewColumn c) {
		return (c.name().equals("") ? c.func() : ("*".equals(c.name()) ? "*" : escape(c.name())))
				+ (c.asName().equals("")
						? (c.name().equals("") || c.name().equals("*") ? "" : (" AS " + escape(c.name())))
						: " AS " + escape(c.asName()));
	}

	protected String getCreateSQL(UnionTable t, ViewColumn c) {
		String typeName = getTypeName(t.typeName());
		if (t.name().equals("") && (typeName == null || typeName.equals(""))) {
			throw new IllegalArgumentException("UnionTable name cannot be empty/undefined.");
		}
		if (typeName == null || typeName.equals("")) {
			typeName = t.name();
		}

		return (c.name().equals("") ? c.func()
				: (escape(typeName) + "." + ("*".equals(c.name()) ? "*" : escape(c.name()))))
				+ (c.asName().equals("")
						? (c.name().equals("") || c.name().equals("*") ? "" : (" AS " + escape(c.name())))
						: " AS " + escape(c.asName()));
	}

	public String getTypeName(Class<?> clazz) {
		final String name = SQLTypeAnnotated.getTypeName(clazz);
		if (name != null)
			return name;

		if (NTSQLQueryable.class.isAssignableFrom(clazz)) {
			return dbEntryUtils.getQueryableName((Class<? extends NTSQLQueryable<T>>) clazz);
		}

		return null;
		// throw new IllegalArgumentException("Cannot determine name of type: " +
		// clazz.getName());
	}

	protected DataBaseView<T> getQueryable() {
		return this;
	}

	public String[] getColumnNames() {
		return Arrays.stream(getTypeAnnotation().tables()).flatMap(table -> Arrays.stream(table.columns()))
				.map((c) -> c.asName()).toArray(String[]::new);
	}

	protected Connection connect() throws SQLException {
		return dataBase.getConnector().connect();
	}

	protected Connection createConnection() throws SQLException {
		return dataBase.getConnector().createConnection();
	}

	@Override
	public DataBase getDataBase() {
		return dataBase;
	}

	@Override
	public String getName() {
		return getTypeAnnotation().name();
	}

	@Override
	public String getQualifiedName() {
		return "`" + dataBase.getDataBaseName() + "`.`" + getName() + "`";
	}

	private ViewTable getMainTable() {
		return Arrays.stream(getTypeAnnotation().tables())
				.filter(t -> t.join().equals(ViewTable.Type.MAIN) || t.join().equals(ViewTable.Type.MAIN_UNION)
						|| t.join().equals(ViewTable.Type.MAIN_UNION_ALL))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No table marked as " + ViewTable.Type.MAIN + "."));
	}

	private ViewTable[] getJoinTables() {
		return Arrays.stream(getTypeAnnotation().tables()).filter(t -> !t.join().equals(ViewTable.Type.MAIN))
				.toArray(ViewTable[]::new);
	}

	private ViewTable[] getTables() {
		return getTypeAnnotation().tables();
	}

	@Override
	public DB_View getTypeAnnotation() {
		return viewClass.getAnnotation(DB_View.class);
	}

	@Override
	public Class<? extends SQLQueryable<T>> getTargetClass() {
		return getViewClass();
	}

	public Class<? extends AbstractDBView<T>> getViewClass() {
		return viewClass;
	}

	@Override
	public DataBaseEntryUtils getDbEntryUtils() {
		return dbEntryUtils;
	}

	public void setDbEntryUtils(DataBaseEntryUtils dbEntryUtils) {
		this.dbEntryUtils = dbEntryUtils;
	}

	public static class DataBaseViewStatus<T extends DataBaseEntry, B extends AbstractDBView<T>> {

		private final boolean existed;
		private final B table;

		protected DataBaseViewStatus(boolean existed, B table) {
			this.existed = existed;
			this.table = table;
		}

		public boolean existed() {
			return existed;
		}

		public boolean created() {
			return !existed;
		}

		public B getQueryable() {
			return table;
		}

		@Override
		public String toString() {
			return "DataBaseViewStatus{existed=" + existed + ", created=" + !existed + ", table=" + table + "}";
		}

	}

	@Override
	public String toString() {
		return "DataBaseView@" + System.identityHashCode(this) + " [dataBase=" + dataBase + ", dbEntryUtils="
				+ dbEntryUtils + ", viewClass=" + viewClass + "]";
	}

}
