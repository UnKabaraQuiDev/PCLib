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
import java.util.Map;
import java.util.stream.Collectors;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.annotations.Column;
import lu.pcy113.pclib.db.annotations.Constraint;
import lu.pcy113.pclib.db.annotations.DB_Table;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.SafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.UnsafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.SafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.UnsafeSQLQuery;
import lu.pcy113.pclib.db.utils.SQLEntryUtils;
import lu.pcy113.pclib.impl.DependsOn;

@DependsOn("java.sql.*")
public abstract class DataBaseTable<T extends SQLEntry> {

	private DataBase dataBase;

	private final String tableName;
	private final Column[] columns;
	private final Constraint[] constraints;

	public DataBaseTable(DataBase dbTest) {
		this.dataBase = dbTest;

		DB_Table tableAnnotation = getTypeAnnotation();
		this.tableName = tableAnnotation.name();
		this.columns = tableAnnotation.columns();
		this.constraints = tableAnnotation.constraints();
	}

	public NextTask<Void, ReturnData<Boolean>> exists() {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				DatabaseMetaData dbMetaData = con.getMetaData();
				ResultSet rs = dbMetaData.getTables(dataBase.getDataBaseName(), null, getTableName(), null);

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

	public NextTask<Void, ReturnData<DataBaseTableStatus<T>>> create() {
		return exists().thenApply((ReturnData<Boolean> status) -> {
			if (status.isError()) {
				return status.castError();
			}

			return status.apply((state, data) -> {
				if ((Boolean) data) {
					return ReturnData.ok(new DataBaseTableStatus<T>(true, getTable()));
				} else {
					try {
						Connection con = connect();

						Statement stmt = con.createStatement();

						stmt.executeUpdate(getCreateSQL());

						stmt.close();
						return ReturnData.ok(new DataBaseTableStatus<T>(false, getTable()));
					} catch (SQLException e) {
						return ReturnData.error(e);
					}
				}
			});
		});
	}

	public NextTask<Void, ReturnData<DataBaseTable<T>>> drop() {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				Statement stmt = con.createStatement();

				stmt.executeUpdate("DROP TABLE `" + getTableName() + "`;");

				stmt.close();

				return ReturnData.ok(getTable());
			} catch (SQLException e) {
				return ReturnData.error(e);
			}
		});
	}

	@SuppressWarnings("unused")
	public NextTask<Void, ReturnData<Boolean>> exists(T data) {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				Statement stmt = null;
				ResultSet result;

				final Map<String, Object> uniques = SQLEntryUtils.getUniqueKeys(getColumns(), data);

				query: {
					final String safeQuery = SQLBuilder.safeSelectUniqueCollision(getTable(), uniques.keySet().stream());

					final PreparedStatement pstmt = con.prepareStatement(safeQuery);

					int i = 1;
					for (Object obj : uniques.values()) {
						pstmt.setObject(i++, obj);
					}

					result = pstmt.executeQuery();
					stmt = pstmt;
				}

				if (!result.next()) {
					return ReturnData.error(stmt.getWarnings());
				}

				final int count = result.getInt("count");

				stmt.close();
				return ReturnData.ok(count > 0);
			} catch (Exception e) {
				return ReturnData.error(e);
			}
		});
	}

	public NextTask<Void, ReturnData<T>> insert(T data) {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				Statement stmt = null;
				int result = -1;

				if (data instanceof SafeSQLEntry) {
					final SafeSQLEntry safeData = (SafeSQLEntry) data;

					final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedInsertSQL(getTable()), Statement.RETURN_GENERATED_KEYS);

					safeData.prepareInsertSQL(pstmt);

					result = pstmt.executeUpdate();
					stmt = pstmt;
				} else if (data instanceof UnsafeSQLEntry) {
					final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

					stmt = con.createStatement();

					result = stmt.executeUpdate(unsafeData.getInsertSQL(getTable()), Statement.RETURN_GENERATED_KEYS);
				} else {
					return ReturnData.error(new IllegalArgumentException("Unsupported type: " + data.getClass().getName()));
				}

				if (result == 0) {
					return ReturnData.error(stmt.getWarnings());
				}

				final ResultSet generatedKeys = stmt.getGeneratedKeys();
				if (!generatedKeys.next()) {
					generatedKeys.close();
					stmt.close();
					return ReturnData.error(stmt.getWarnings());
				}

				SQLEntryUtils.generatedKeyUpdate(data, generatedKeys);

				generatedKeys.close();
				stmt.close();
				return ReturnData.ok(data);
			} catch (Exception e) {
				return ReturnData.error(e);
			}
		});
	}

	public NextTask<Void, ReturnData<T>> insertAndReload(T data) {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				Statement stmt = null;
				int result = -1;

				if (data instanceof SafeSQLEntry) {
					final SafeSQLEntry safeData = (SafeSQLEntry) data;

					final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedInsertSQL(getTable()), Statement.RETURN_GENERATED_KEYS);

					safeData.prepareInsertSQL(pstmt);

					result = pstmt.executeUpdate();
					stmt = pstmt;
				} else if (data instanceof UnsafeSQLEntry) {
					final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

					stmt = con.createStatement();

					result = stmt.executeUpdate(unsafeData.getInsertSQL(getTable()), Statement.RETURN_GENERATED_KEYS);
				} else {
					return ReturnData.error(new IllegalArgumentException("Unsupported type: " + data.getClass().getName()));
				}

				if (result == 0) {
					return ReturnData.error(stmt.getWarnings());
				}

				final ResultSet generatedKeys = stmt.getGeneratedKeys();
				if (!generatedKeys.next()) {
					generatedKeys.close();
					stmt.close();
					return ReturnData.error(stmt.getWarnings());
				}

				SQLEntryUtils.generatedKeyUpdate(data, generatedKeys);

				final PreparedStatement pstmt = con.prepareStatement("SELECT * FROM `" + getTableName() + "` WHERE `" + SQLEntryUtils.getGeneratedKeyName(data) + "` = ?;");
				pstmt.setObject(1, generatedKeys.getObject(1));

				generatedKeys.close();
				stmt.close();

				final ResultSet rs = pstmt.executeQuery();

				if (!rs.next()) {
					rs.close();
					pstmt.close();
					return ReturnData.error(stmt.getWarnings());
				}

				SQLEntryUtils.reload(data, rs);

				rs.close();
				pstmt.close();
				return ReturnData.ok(data);
			} catch (Exception e) {
				e.printStackTrace();
				return ReturnData.error(e);
			}
		});
	}

	public NextTask<Void, ReturnData<T>> delete(T data) {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				Statement stmt = null;
				int result = -1;

				if (data instanceof SafeSQLEntry) {
					final SafeSQLEntry safeData = (SafeSQLEntry) data;

					final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedDeleteSQL(getTable()));

					safeData.prepareDeleteSQL(pstmt);

					result = pstmt.executeUpdate();
					stmt = pstmt;
				} else if (data instanceof UnsafeSQLEntry) {
					final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

					stmt = con.createStatement();

					result = stmt.executeUpdate(unsafeData.getDeleteSQL(getTable()));
				} else {
					return ReturnData.error(new IllegalArgumentException("Unsupported type: " + data.getClass().getName()));
				}

				if (result == 0) {
					return ReturnData.error(stmt.getWarnings());
				}

				stmt.close();
				return ReturnData.ok(data);
			} catch (Exception e) {
				return ReturnData.error(e);
			}
		});
	}

	public NextTask<Void, ReturnData<T>> update(T data) {
		return NextTask.create(() -> {
			try {
				final Connection con = connect();

				Statement stmt = null;
				int result = -1;

				if (data instanceof SafeSQLEntry) {
					final SafeSQLEntry safeData = (SafeSQLEntry) data;

					final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedUpdateSQL(getTable()));

					safeData.prepareUpdateSQL(pstmt);

					result = pstmt.executeUpdate();
					stmt = pstmt;
				} else if (data instanceof UnsafeSQLEntry) {
					final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

					stmt = con.createStatement();

					result = stmt.executeUpdate(unsafeData.getUpdateSQL(getTable()));
				} else {
					return ReturnData.error(new IllegalArgumentException("Unsupported type: " + data.getClass().getName()));
				}

				if (result == 0) {
					return ReturnData.error(stmt.getWarnings());
				}

				stmt.close();
				return ReturnData.ok(data);
			} catch (Exception e) {
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

					final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedSelectSQL(getTable()));

					safeData.prepareSelectSQL(pstmt);

					result = pstmt.executeQuery();
					stmt = pstmt;
				} else if (data instanceof UnsafeSQLEntry) {
					final UnsafeSQLEntry unsafeData = (UnsafeSQLEntry) data;

					stmt = con.createStatement();

					result = stmt.executeQuery(unsafeData.getSelectSQL(getTable()));
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

					final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedQuerySQL(getTable()));

					safeData.updateQuerySQL(pstmt);

					result = pstmt.executeQuery();
					stmt = pstmt;
				} else if (data instanceof UnsafeSQLQuery) {
					final UnsafeSQLQuery<T> unsafeData = (UnsafeSQLQuery<T>) data;

					stmt = con.createStatement();

					result = stmt.executeQuery(unsafeData.getQuerySQL(getTable()));
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
	
	public NextTask<Void, ReturnData<Integer>> update(SQLQuery<T> data){
		return NextTask.create(() -> {
			try {
				final Connection con = connect();
				
				Statement stmt = null;
				int result = -1;
				
				if(data instanceof SafeSQLQuery) {
					final SafeSQLQuery<T> safeData = (SafeSQLQuery<T>) data;

					final PreparedStatement pstmt = con.prepareStatement(safeData.getPreparedQuerySQL(getTable()));

					safeData.updateQuerySQL(pstmt);
					
					result = pstmt.executeUpdate();
					
					stmt = pstmt;
				} else if (data instanceof UnsafeSQLQuery) {
					final UnsafeSQLQuery<T> unsafeData = (UnsafeSQLQuery<T>) data;

					stmt = con.createStatement();

					result = stmt.executeUpdate(unsafeData.getQuerySQL(getTable()));
				} else {
					return ReturnData.error(new IllegalArgumentException("Unsupported type: " + data.getClass().getName()));
				}
				stmt.close();
				return ReturnData.ok(result);
				
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

				result = stmt.executeQuery(SQLBuilder.count(getTable()));

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
		String sql = "CREATE TABLE `" + getTableName() + "` (";
		sql += Arrays.stream(columns).map((c) -> getCreateSQL(c)).collect(Collectors.joining(", "));
		sql += constraints.length > 0 ? "," + Arrays.stream(constraints).map((c) -> getCreateSQL(c)).collect(Collectors.joining(", ")) : "";
		sql += ");";
		return sql;
	}

	protected String getCreateSQL(Column c) {
		return "`" + c.name() + "` " + c.type() +
				(c.autoIncrement() ? " AUTO_INCREMENT" : "") +
				(c.primaryKey() ? " PRIMARY KEY" : "") +
				(!c.generated() && c.notNull() ? " NOT NULL" : "") +
				(c.unique() ? " UNIQUE" : "") +
				(c.index() ? " INDEX" : "") +
				(!c.default_().equals("") ? " DEFAULT " + c.default_() : "") +
				(!c.onUpdate().equals("") ? " ON UPDATE " + c.onUpdate() : "") +
				(!c.check().equals("") ? " CHECK (" + c.check() + ")" : "") +
				(c.generated() ? " GENERATED ALWAYS AS (" + c.generator() + ") " + c.generatedType().name() : "");
	}

	protected String getCreateSQL(Constraint c) {
		if (c.type().equals(Constraint.Type.FOREIGN_KEY)) {
			return "CONSTRAINT " + c.name() + " FOREIGN KEY (" + c.foreignKey() + ") REFERENCES " + c.referenceTable() + " (" + c.referenceColumn() + ") ON DELETE " + c.onDelete() + " ON UPDATE " + c.onUpdate();
		} else if (c.type().equals(Constraint.Type.UNIQUE)) {
			return "CONSTRAINT " + c.name() + " UNIQUE (" + (Arrays.stream(c.columns()).collect(Collectors.joining(", "))) + ")";
		} else if(c.type().equals(Constraint.Type.CHECK)) {
			return "CONSTRAINT " + c.name() + " CHECK (" + c.check() + ")";
		} else if(c.type().equals(Constraint.Type.PRIMARY_KEY)) {
			return "CONSTRAINT " + c.name() + " PRIMARY KEY (" + (Arrays.stream(c.columns()).collect(Collectors.joining(", "))) + ")";
		} else {} {
			throw new IllegalArgumentException(c + ", is not defined");
		}
	}

	protected DataBaseTable<T> getTable() {
		return this;
	}

	public String getTableName() {
		return tableName;
	}

	public Column[] getColumns() {
		return columns;
	}

	public String[] getColumnNames() {
		return Arrays.stream(columns).map((c) -> c.name()).toArray(String[]::new);
	}

	protected Connection connect() throws SQLException {
		return dataBase.getConnector().connect();
	}

	public DataBase getDataBase() {
		return dataBase;
	}

	public String getQualifiedName() {
		return "`" + dataBase.getDataBaseName() + "`.`" + getTableName() + "`";
	}

	private DB_Table getTypeAnnotation() {
		return getClass().getAnnotation(DB_Table.class);
	}

	@Override
	public String toString() {
		return "DataBaseTable{" + "tableName='" + getTableName() + "'" + '}';
	}

	public static class DataBaseTableStatus<T extends SQLEntry> {
		private boolean existed;
		private DataBaseTable<T> table;

		protected DataBaseTableStatus(boolean existed, DataBaseTable<T> table) {
			this.existed = existed;
			this.table = table;
		}

		public boolean existed() {
			return existed;
		}

		public boolean created() {
			return !existed;
		}

		public DataBaseTable<T> getTable() {
			return table;
		}

		@Override
		public String toString() {
			return "DataBaseTableStatus{existed=" + existed + ", created=" + !existed + ", table=" + table + "}";
		}

	}

}
