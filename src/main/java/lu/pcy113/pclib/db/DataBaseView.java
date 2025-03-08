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
import lu.pcy113.pclib.db.impl.SQLEntry.SafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.UnsafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.SafeSQLQuery;
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

	public NextTask<Void, ReturnData<Boolean>> exists() {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				DatabaseMetaData dbMetaData = con.getMetaData();
				ResultSet rs = dbMetaData.getTables(dataBase.getDataBaseName(), null, getName(), null);

				if (rs.next()) {
					rs.close();

					return ReturnData.ok(true);
				} else {
					rs.close();

					return ReturnData.ok(false);
				}
			} catch (SQLException e) {
				return ReturnData.error(e);
			}
		});
	}

	public NextTask<Void, ReturnData<DataBaseViewStatus<T>>> create() {
		return exists().thenApply((ReturnData<Boolean> status) -> {
			if (status.isError()) {
				return status.castError();
			}

			return status.apply((state, data) -> {
				if ((Boolean) data) {
					return ReturnData.ok(new DataBaseViewStatus<T>(true, getView()));
				} else {
					try {
						Connection con = connect();

						Statement stmt = con.createStatement();

						stmt.executeUpdate(getCreateSQL());

						stmt.close();
						return ReturnData.ok(new DataBaseViewStatus<T>(false, getView()));
					} catch (SQLException e) {
						return ReturnData.error(e);
					}
				}
			});
		});
	}

	public NextTask<Void, ReturnData<DataBaseView<T>>> drop() {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				Statement stmt = con.createStatement();

				stmt.executeUpdate("DROP VIEW `" + getQualifiedName() + "`;");

				stmt.close();

				return ReturnData.ok(getView());
			} catch (SQLException e) {
				return ReturnData.error(e);
			}
		});
	}

	public NextTask<Void, ReturnData<T>> load(T data) {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				Statement stmt = null;
				ResultSet result = null;

				if (data instanceof SafeSQLEntry) {
					final SafeSQLEntry safeData = (SafeSQLEntry) data;

					final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedSelectSQL(getView()));

					safeData.prepareSelectSQL(pstmt);

					result = pstmt.executeQuery();
					stmt = pstmt;
				} else if (data instanceof UnsafeSQLEntry) {
					final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

					stmt = con.createStatement();

					result = stmt.executeQuery(unsafeData.getSelectSQL(getView()));
				} else {
					return ReturnData.error(new IllegalArgumentException("Unsupported type: " + data.getClass().getName()));
				}

				if (!result.next()) {
					return ReturnData.error(stmt.getWarnings());
				}

				SQLEntryUtils.reload(data, result);

				result.close();
				stmt.close();
				return ReturnData.ok(data);
			} catch (Exception e) {
				return ReturnData.error(e);
			}
		});
	}

	public NextTask<Void, ReturnData<List<T>>> query(SQLQuery<T> data) {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				Statement stmt = null;
				ResultSet result = null;

				if (data instanceof SafeSQLQuery) {
					final SafeSQLQuery<T> safeData = (SafeSQLQuery<T>) data;

					final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedQuerySQL(getView()));

					safeData.updateQuerySQL(pstmt);

					result = pstmt.executeQuery();
					stmt = pstmt;
				} else if (data instanceof UnsafeSQLQuery) {
					final UnsafeSQLQuery<T> unsafeData = (UnsafeSQLQuery<T>) data;

					stmt = con.createStatement();

					result = stmt.executeQuery(unsafeData.getQuerySQL(getView()));
				} else {
					return ReturnData.error(new IllegalArgumentException("Unsupported type: " + data.getClass().getName()));
				}

				final List<T> output = new ArrayList<>();
				SQLEntryUtils.copyAll(data, result, output::add);

				result.close();
				stmt.close();
				return ReturnData.ok(output);
			} catch (Exception e) {
				return ReturnData.error(e);
			}
		});
	}

	public NextTask<Void, ReturnData<Integer>> count() {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				Statement stmt = con.createStatement();
				ResultSet result;

				result = stmt.executeQuery(SQLBuilder.count(getView()));

				if (!result.next()) {
					return ReturnData.error(stmt.getWarnings());
				}

				final int count = result.getInt("count");

				result.close();
				stmt.close();
				return ReturnData.ok(count);
			} catch (Exception e) {
				return ReturnData.error(e);
			}
		});
	}

	public String getCreateSQL() {
		String sql = "CREATE VIEW " + getQualifiedName() + " AS SELECT ";
		sql += Arrays.stream(getTables()).flatMap(
				t -> Arrays.stream(t.columns()).map(c -> (c.name().equals("") ? c.func() : ("`" + t.name() + "`.`" + c.name() + "`")) + (c.asName().equals("") ? (c.name().equals("") ? "" : (" AS `" + c.name() + "`")) : " AS `" + c.asName() + "`")))
				.collect(Collectors.joining(", ")) + " ";
		sql += "FROM `" + dataBase.getDataBaseName() + "`.`" + getMainTable().name() + "` ";
		for (ViewTable vt : getJoinTables()) {
			sql += vt.join() + " JOIN " + vt.name() + " ON " + vt.on() + " ";
		}
		if (!getTypeAnnotation().condition().equals("")) {
			sql += "WHERE " + getTypeAnnotation().condition() + " ";
		}
		if (getTypeAnnotation().groupBy().length != 0) {
			sql += "GROUP BY " + Arrays.stream(getTypeAnnotation().groupBy()).map(o -> "`" + o + "`").collect(Collectors.joining(", ")) + " ";
		}
		if (getTypeAnnotation().orderBy().length != 0) {
			sql += "ORDER BY " + (Arrays.stream(getTypeAnnotation().orderBy()).map(o -> "`" + o.column() + "` " + o.type()).collect(Collectors.joining(", "))) + " ";
		}
		sql += ";";
		return sql;
	}

	protected String getCreateSQL(Column c) {
		return "`" + c.name() + "` " + c.type() + (c.autoIncrement() ? " AUTO_INCREMENT" : "") + (!c.generated() && c.notNull() ? " NOT NULL" : "") + (c.index() ? " INDEX" : "") + (!c.default_().equals("") ? " DEFAULT " + c.default_() : "")
				+ (!c.onUpdate().equals("") ? " ON UPDATE " + c.onUpdate() : "") + (c.generated() ? " GENERATED ALWAYS AS (" + c.generator() + ") " + c.generatedType().name() : "");
	}

	protected DataBaseView<T> getView() {
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

		public DataBaseView<T> getView() {
			return table;
		}

		@Override
		public String toString() {
			return "DataBaseViewStatus{existed=" + existed + ", created=" + !existed + ", table=" + table + "}";
		}

	}

}
