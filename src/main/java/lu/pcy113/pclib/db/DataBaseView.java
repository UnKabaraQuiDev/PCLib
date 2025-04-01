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

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.annotations.table.Column;
import lu.pcy113.pclib.db.annotations.view.DB_View;
import lu.pcy113.pclib.db.annotations.view.ViewTable;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.ReadOnlySQLEntry.SafeReadOnlySQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.ReadOnlySQLEntry.UnsafeReadOnlySQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.SafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.UnsafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.SafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformativeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformativeSQLQuery.SafeTransformativeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformativeSQLQuery.UnsafeTransformativeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.UnsafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;
import lu.pcy113.pclib.db.utils.SQLEntryUtils;
import lu.pcy113.pclib.impl.DependsOn;

@DependsOn("java.sql.*")
public abstract class DataBaseView<T extends SQLEntry> implements SQLQueryable<T> {

	private DataBase dataBase;

	public DataBaseView(DataBase dbTest) {
		this.dataBase = dbTest;
	}

	public void requestHook(SQLRequestType type, Object query) {
	}

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

	public NextTask<Void, T> load(T data) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result = null;

			if (data instanceof SafeSQLEntry) {
				final SafeSQLEntry safeData = (SafeSQLEntry) data;

				final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedSelectSQL(getQueryable()));

				safeData.prepareSelectSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;
			} else if (data instanceof UnsafeSQLEntry) {
				final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

				stmt = con.createStatement();

				final String sql = unsafeData.getSelectSQL(getQueryable());

				requestHook(SQLRequestType.SELECT, sql);

				result = stmt.executeQuery(sql);
			} else if (data instanceof SafeReadOnlySQLEntry) {
				final SafeReadOnlySQLEntry safeData = (SafeReadOnlySQLEntry) data;

				final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedSelectSQL(getQueryable()));

				safeData.prepareSelectSQL(pstmt);

				requestHook(SQLRequestType.SELECT, pstmt);

				result = pstmt.executeQuery();
				stmt = pstmt;
			} else if (data instanceof UnsafeReadOnlySQLEntry) {
				final UnsafeReadOnlySQLEntry unsafeData = (UnsafeReadOnlySQLEntry) data;

				stmt = con.createStatement();

				final String sql = unsafeData.getSelectSQL(getQueryable());

				requestHook(SQLRequestType.SELECT, sql);

				result = stmt.executeQuery(sql);
			} else {
				throw new IllegalArgumentException("Unsupported type: " + data.getClass().getName());
			}

			if (!result.next()) {
				throw new IllegalStateException("Couldn't load data, no entry matching query.");
			}

			SQLEntryUtils.reload(data, result);

			result.close();
			stmt.close();

			return data;
		});
	}

	public NextTask<Void, List<T>> query(SQLQuery<T> query) {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = null;
			ResultSet result = null;

			if (query instanceof SafeSQLQuery || query instanceof UnsafeSQLQuery) {

				if (query instanceof SafeSQLQuery) {
					final SafeSQLQuery<T> safeQuery = (SafeSQLQuery<T>) query;

					final PreparedStatement pstmt = con.prepareStatement(safeQuery.getPreparedQuerySQL(getQueryable()));

					safeQuery.updateQuerySQL(pstmt);

					requestHook(SQLRequestType.SELECT, pstmt);

					result = pstmt.executeQuery();
					stmt = pstmt;
				} else if (query instanceof UnsafeSQLQuery) {
					final UnsafeSQLQuery<T> unsafeQuery = (UnsafeSQLQuery<T>) query;

					stmt = con.createStatement();

					final String sql = unsafeQuery.getQuerySQL(getQueryable());

					requestHook(SQLRequestType.SELECT, sql);

					result = stmt.executeQuery(sql);
				}

				final List<T> output = new ArrayList<>();
				SQLEntryUtils.copyAll(query, result, output::add);

				stmt.close();
				return output;
			} else if (query instanceof TransformativeSQLQuery) {
				final TransformativeSQLQuery<T> transformativeQuery = (TransformativeSQLQuery<T>) query;

				if (query instanceof SafeTransformativeSQLQuery) {
					final SafeTransformativeSQLQuery<T> safeQuery = (SafeTransformativeSQLQuery<T>) query;

					final PreparedStatement pstmt = con.prepareStatement(safeQuery.getPreparedQuerySQL(getQueryable()));

					safeQuery.updateQuerySQL(pstmt);

					requestHook(SQLRequestType.SELECT, pstmt);

					result = pstmt.executeQuery();
					stmt = pstmt;
				} else if (query instanceof UnsafeTransformativeSQLQuery) {
					final UnsafeTransformativeSQLQuery<T> unsafeQuery = (UnsafeTransformativeSQLQuery<T>) query;

					stmt = con.createStatement();

					final String sql = unsafeQuery.getQuerySQL(getQueryable());

					requestHook(SQLRequestType.SELECT, sql);

					result = stmt.executeQuery(sql);
				}

				final List<T> output = transformativeQuery.transform(result);

				stmt.close();
				return output;
			} else {
				throw new IllegalArgumentException("Unsupported type: " + query.getClass().getName());
			}
		});
	}

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

	public String getCreateSQL() {
		String sql = "CREATE VIEW " + getQualifiedName() + " AS SELECT \n\t";
		sql += Arrays.stream(getTables()).flatMap(t -> Arrays.stream(t.columns()).map(c -> (c.name().equals("") ? c.func() : ("`" + t.name() + "`." + ("*".equals(c.name()) ? "*" : ("`" + c.name() + "`"))))
				+ (c.asName().equals("") ? (c.name().equals("") | c.name().equals("*") ? "" : (" AS `" + c.name() + "`")) : " AS `" + c.asName() + "`"))).collect(Collectors.joining(", \n\t"));
		sql += "FROM \n\t`" + dataBase.getDataBaseName() + "`.`" + getMainTable().name() + "`";
		for (ViewTable vt : getJoinTables()) {
			sql += "\n" + vt.join() + " JOIN " + vt.name() + " ON " + vt.on();
		}
		if (!getTypeAnnotation().condition().equals("")) {
			sql += "\nWHERE \n\t" + getTypeAnnotation().condition();
		}
		if (getTypeAnnotation().groupBy().length != 0) {
			sql += "\nGROUP BY \n\t" + Arrays.stream(getTypeAnnotation().groupBy()).map(o -> "`" + o + "`").collect(Collectors.joining(", "));
		}
		if (getTypeAnnotation().orderBy().length != 0) {
			sql += "\nORDER BY \n\t" + (Arrays.stream(getTypeAnnotation().orderBy()).map(o -> "`" + o.column() + "` " + o.type()).collect(Collectors.joining(", ")));
		}
		sql += ";";
		return sql;
	}

	protected String getCreateSQL(Column c) {
		return "`" + c.name() + "` " + c.type() + (c.autoIncrement() ? " AUTO_INCREMENT" : "") + (!c.generated() && c.notNull() ? " NOT NULL" : "") + (c.index() ? " INDEX" : "") + (!c.default_().equals("") ? " DEFAULT " + c.default_() : "")
				+ (!c.onUpdate().equals("") ? " ON UPDATE " + c.onUpdate() : "") + (c.generated() ? " GENERATED ALWAYS AS (" + c.generator() + ") " + c.generatedType().name() : "");
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
		return Arrays.stream(getTypeAnnotation().tables()).filter(t -> t.join().equals(ViewTable.Type.MAIN)).findFirst().orElseThrow(() -> new IllegalArgumentException("No table marked as " + ViewTable.Type.MAIN + "."));
	}

	private ViewTable[] getJoinTables() {
		return Arrays.stream(getTypeAnnotation().tables()).filter(t -> !t.join().equals(ViewTable.Type.MAIN)).toArray(ViewTable[]::new);
	}

	private ViewTable[] getTables() {
		return getTypeAnnotation().tables();
	}

	private DB_View getTypeAnnotation() {
		return getClass().getAnnotation(DB_View.class);
	}

	@Override
	public String toString() {
		return "DataBaseView{" + "viewName=" + getQualifiedName() + "" + '}';
	}

	public static class DataBaseViewStatus<T extends SQLEntry> {
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
