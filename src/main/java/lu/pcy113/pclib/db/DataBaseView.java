package lu.pcy113.pclib.db;

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

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.builder.SQLBuilder;
import lu.pcy113.pclib.db.annotations.view.DB_View;
import lu.pcy113.pclib.db.annotations.view.UnionTable;
import lu.pcy113.pclib.db.annotations.view.ViewColumn;
import lu.pcy113.pclib.db.annotations.view.ViewTable;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;
import lu.pcy113.pclib.db.impl.SQLTypeAnnotated;
import lu.pcy113.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.pcy113.pclib.db.utils.DataBaseEntryUtils;
import lu.pcy113.pclib.impl.DependsOn;

@DependsOn("java.sql.*")
public abstract class DataBaseView<T extends DataBaseEntry> implements AbstractDBView<T>, SQLTypeAnnotated<DB_View> {

	private DataBase dataBase;
	private DataBaseEntryUtils dbEntryUtils = new BaseDataBaseEntryUtils();
	private Class<? extends AbstractDBView<T>> viewClass;

	public DataBaseView(DataBase dataBase) {
		this(dataBase, new BaseDataBaseEntryUtils());
	}

	@SuppressWarnings("unchecked")
	public DataBaseView(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.viewClass = (Class<? extends AbstractDBView<T>>) getClass();
	}

	public DataBaseView(DataBase dataBase, DataBaseEntryUtils dbEntryUtils, Class<? extends AbstractDBView<T>> viewClass) {
		this.dataBase = dataBase;
		this.dbEntryUtils = dbEntryUtils;
		this.viewClass = viewClass;
	}

	@Override
	public void requestHook(SQLRequestType type, Object query) {
	}

	@Override
	public NextTask<Void, Boolean> exists() {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				DatabaseMetaData dbMetaData = con.getMetaData();
				ResultSet rs = dbMetaData.getTables(dataBase.getDataBaseName(), null, getName(), null);

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
		});
	}

	@Override
	public NextTask<Void, DataBaseViewStatus<T>> create() {
		return exists().thenApply((Boolean status) -> {
			if ((Boolean) status) {
				return new DataBaseViewStatus<T>(true, getQueryable());
			} else {
				Connection con = connect();

				Statement stmt = con.createStatement();

				final String sql = getCreateSQL();

				requestHook(SQLRequestType.CREATE_TABLE, sql);

				stmt.executeUpdate(sql);

				stmt.close();
				return new DataBaseViewStatus<T>(false, getQueryable());
			}
		});
	}

	@Override
	public NextTask<Void, DataBaseView<T>> drop() {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = con.createStatement();

			final String sql = "DROP VIEW " + getQualifiedName() + ";";

			requestHook(SQLRequestType.DROP_VIEW, sql);

			stmt.executeUpdate(sql);

			stmt.close();

			return getQueryable();
		});
	}

	@Override
	public NextTask<Void, T> load(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result = null;

			final PreparedStatement pstmt = con.prepareStatement(dbEntryUtils.getPreparedSelectSQL(getQueryable(), data));

			dbEntryUtils.prepareSelectSQL(pstmt, data);

			requestHook(SQLRequestType.SELECT, pstmt);

			result = pstmt.executeQuery();
			stmt = pstmt;

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			dbEntryUtils.fillLoad(data, result);

			result.close();
			stmt.close();

			return data;
		});
	}

	@Override
	public <B> NextTask<Void, B> query(SQLQuery<T, B> query) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result = null;

			if (query instanceof PreparedQuery) {
				final PreparedQuery<T> safeQuery = (PreparedQuery<T>) query;

				final PreparedStatement pstmt = con.prepareStatement(safeQuery.getPreparedQuerySQL(getQueryable()));

				safeQuery.updateQuerySQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;

				final List<T> output = new ArrayList<>();
				dbEntryUtils.fillLoadAllTable((Class<? extends SQLQueryable<T>>) getQueryable().getClass(), query, result, output::add);

				stmt.close();
				return (B) output;
			} else if (query instanceof RawTransformingQuery) {
				final RawTransformingQuery<T, B> safeTransQuery = (RawTransformingQuery<T, B>) query;

				final PreparedStatement pstmt = con.prepareStatement(safeTransQuery.getPreparedQuerySQL(getQueryable()));

				safeTransQuery.updateQuerySQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;

				final B output = safeTransQuery.transform(result);

				stmt.close();
				return output;
			} else if (query instanceof TransformingQuery) {
				final TransformingQuery<T, B> safeTransQuery = (TransformingQuery<T, B>) query;

				final PreparedStatement pstmt = con.prepareStatement(safeTransQuery.getPreparedQuerySQL(getQueryable()));

				safeTransQuery.updateQuerySQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;

				final List<T> output = new ArrayList<>();
				dbEntryUtils.fillLoadAllTable((Class<? extends SQLQueryable<T>>) getQueryable().getClass(), query, result, output::add);

				final B filteredOutput = safeTransQuery.transform(output);

				stmt.close();
				return filteredOutput;
			} else {
				throw new IllegalArgumentException("Unsupported type: " + query.getClass().getName());
			}
		});
	}

	@Override
	public NextTask<Void, Integer> count() {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = con.createStatement();
			ResultSet result;

			final String sql = SQLBuilder.count(getQueryable());

			requestHook(SQLRequestType.SELECT, sql);

			result = stmt.executeQuery(sql);

			if (!result.next()) {
				throw new IllegalStateException("Couldn't query entry count.");
			}

			final int count = result.getInt("count");

			result.close();
			stmt.close();
			return count;
		});
	}

	@Override
	public String getCreateSQL() {
		if (getTypeAnnotation().customSQL() != null && !getTypeAnnotation().customSQL().equals("")) {
			return getTypeAnnotation().customSQL();
		}

		String sql = "CREATE VIEW " + getQualifiedName() + " AS SELECT \n";
		sql += PCUtils.leftPadLine(Arrays.stream(getTables()).flatMap(t -> Arrays.stream(t.columns()).map(c -> getCreateSQL(t, c))).collect(Collectors.joining(", \n")), "\t") + "\n";

		if (getMainTable().join().equals(ViewTable.Type.MAIN_UNION) || getMainTable().join().equals(ViewTable.Type.MAIN_UNION_ALL)) {
			sql += "FROM (\n" + PCUtils.leftPadLine(Arrays.stream(getTypeAnnotation().unionTables()).map(c -> getCreateSQL(c)).collect(Collectors.joining(getMainTable().join().equals(ViewTable.Type.MAIN_UNION) ? "UNION \n" : "UNION ALL \n")), "\t");
			sql += ") " + (getMainTable().asName().equals("") ? "" : " AS " + escape(getMainTable().asName()));
		} else {
			sql += "FROM \n\t" + escape(dataBase.getDataBaseName()) + "." + escape(getMainTable().name().equals("") ? getTypeName(getMainTable().typeName()) : getMainTable().name()) + " "
					+ (getMainTable().asName().equals("") ? "" : " AS " + escape(getMainTable().asName()));

			for (ViewTable vt : getJoinTables()) {
				sql += "\n" + vt.join() + " JOIN " + (vt.name().equals("") ? getTypeName(vt.typeName()) : vt.name()) + (vt.asName().equals("") ? "" : " AS " + escape(vt.asName())) + " ON " + vt.on();
			}
		}

		if (!getTypeAnnotation().condition().equals("")) {
			sql += "\nWHERE \n\t" + getTypeAnnotation().condition();
		}
		if (getTypeAnnotation().groupBy().length != 0) {
			sql += "\nGROUP BY \n\t" + Arrays.stream(getTypeAnnotation().groupBy()).map(o -> escape(o)).collect(Collectors.joining(", "));
		}
		if (getTypeAnnotation().orderBy().length != 0) {
			sql += "\nORDER BY \n\t" + (Arrays.stream(getTypeAnnotation().orderBy()).map(o -> escape(o.column()) + " " + o.type()).collect(Collectors.joining(", ")));
		}
		sql += ";";
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
			throw new IllegalArgumentException("UnionTable name cannot be empty/undefined.");
		}
		if (typeName == null || typeName.equals("")) {
			typeName = t.name();
		}

		return (c.name().equals("") ? c.func() : (escape(t.asName().equals("") ? typeName : t.asName()) + "." + ("*".equals(c.name()) ? "*" : escape(c.name()))))
				+ (c.asName().equals("") ? (c.name().equals("") || c.name().equals("*") ? "" : (" AS " + escape(c.name()))) : " AS " + escape(c.asName()));
	}

	protected String getCreateSQL(UnionTable t, ViewColumn c) {
		String typeName = getTypeName(t.typeName());
		if (t.name().equals("") && (typeName == null || typeName.equals(""))) {
			throw new IllegalArgumentException("UnionTable name cannot be empty/undefined.");
		}
		if (typeName == null || typeName.equals("")) {
			typeName = t.name();
		}

		return (c.name().equals("") ? c.func() : (escape(typeName) + "." + ("*".equals(c.name()) ? "*" : escape(c.name()))))
				+ (c.asName().equals("") ? (c.name().equals("") || c.name().equals("*") ? "" : (" AS " + escape(c.name()))) : " AS " + escape(c.asName()));
	}

	public String getTypeName(Class<?> clazz) {
		final String name = SQLTypeAnnotated.getTypeName(clazz);
		if (name != null)
			return name;

		if (SQLQueryable.class.isAssignableFrom(clazz)) {
			return dbEntryUtils.getQueryableName((Class<? extends SQLQueryable<T>>) clazz);
		}

		throw new IllegalArgumentException("Cannot determine name of type: " + clazz.getName());
	}

	protected DataBaseView<T> getQueryable() {
		return this;
	}

	public String[] getColumnNames() {
		return Arrays.stream(getTypeAnnotation().tables()).flatMap(table -> Arrays.stream(table.columns())).map((c) -> c.asName()).toArray(String[]::new);
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
		return Arrays.stream(getTypeAnnotation().tables()).filter(t -> t.join().equals(ViewTable.Type.MAIN) || t.join().equals(ViewTable.Type.MAIN_UNION) || t.join().equals(ViewTable.Type.MAIN_UNION_ALL)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No table marked as " + ViewTable.Type.MAIN + "."));
	}

	private ViewTable[] getJoinTables() {
		return Arrays.stream(getTypeAnnotation().tables()).filter(t -> !t.join().equals(ViewTable.Type.MAIN)).toArray(ViewTable[]::new);
	}

	private ViewTable[] getTables() {
		return getTypeAnnotation().tables();
	}

	@Override
	public DB_View getTypeAnnotation() {
		return viewClass.getAnnotation(DB_View.class);
	}

	@Override
	public DataBaseEntryUtils getDbEntryUtils() {
		return dbEntryUtils;
	}

	public void setDbEntryUtils(DataBaseEntryUtils dbEntryUtils) {
		this.dbEntryUtils = dbEntryUtils;
	}

	@Override
	public String toString() {
		return "DataBaseView{" + "viewName=" + getQualifiedName() + "" + '}';
	}

	public static class DataBaseViewStatus<T extends DataBaseEntry> {
		private boolean existed;
		private DataBaseView<T> table;

		protected DataBaseViewStatus(boolean existed, DataBaseView<T> table) {
			this.existed = existed;
			this.table = table;
		}

		public boolean existed() {
			return existed;
		}

		public boolean created() {
			return !existed;
		}

		public DataBaseView<T> getQueryable() {
			return table;
		}

		@Override
		public String toString() {
			return "DataBaseViewStatus{existed=" + existed + ", created=" + !existed + ", table=" + table + "}";
		}

	}

}
