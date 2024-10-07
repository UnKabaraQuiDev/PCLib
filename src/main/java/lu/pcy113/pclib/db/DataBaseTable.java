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
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.activation.UnsupportedDataTypeException;

import lu.pcy113.pclib.db.annotations.Column;
import lu.pcy113.pclib.db.annotations.DB_Table;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.SafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLEntry.UnsafeSQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.SafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.UnsafeSQLQuery;

public abstract class DataBaseTable<T extends SQLEntry> {

	private DataBase dataBase;

	private final String tableName;
	private final Column[] columns;

	public DataBaseTable(DataBase dbTest) {
		this.dataBase = dbTest;

		DB_Table tableAnnotation = getTypeAnnotation();
		this.tableName = tableAnnotation.name();
		this.columns = tableAnnotation.columns();
	}

	public CompletableFuture<ReturnData<Boolean>> exists() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				final Connection con = connect();

				DatabaseMetaData dbMetaData = con.getMetaData();
				ResultSet rs = dbMetaData.getTables(null, null, getTableName(), null);

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

	public CompletableFuture<ReturnData<DataBaseTable<T>>> create() {
		return exists().thenApply((ReturnData<Boolean> status) -> {
			if (status.isError()) {
				return status.castError();
			}

			return status.apply((state, data) -> {
				if ((Boolean) data) {
					return ReturnData.existed(getTable());
				} else {
					try {
						Connection con = connect();

						Statement stmt = con.createStatement();

						stmt.executeUpdate(getCreateSQL());

						stmt.close();
						return ReturnData.created(getTable());
					} catch (SQLException e) {
						return ReturnData.error(e);
					}
				}
			});
		});
	}

	public CompletableFuture<ReturnData<DataBaseTable<T>>> drop() {
		return CompletableFuture.supplyAsync(() -> {
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

	public CompletableFuture<ReturnData<T>> insert(T data) {
		return CompletableFuture.supplyAsync(() -> {
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
					return ReturnData.error(new UnsupportedDataTypeException("Unsupported type: " + data.getClass().getName()));
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

				SQLEntryUtils.generatedInsertUpdate(data, generatedKeys);

				generatedKeys.close();
				stmt.close();
				return ReturnData.created(data);
			} catch (Exception e) {
				return ReturnData.error(e);
			}
		});
	}

	public CompletableFuture<ReturnData<T>> insertAndReload(T data) {
		return CompletableFuture.supplyAsync(() -> {
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
					return ReturnData.error(new UnsupportedDataTypeException("Unsupported type: " + data.getClass().getName()));
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

				// final String generatedKeyName = generatedKeys.getMetaData().getColumnName(1);

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
				return ReturnData.created(data);
			} catch (Exception e) {
				return ReturnData.error(e);
			}
		});
	}

	public CompletableFuture<ReturnData<T>> delete(T data) {
		return CompletableFuture.supplyAsync(() -> {
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
					return ReturnData.error(new UnsupportedDataTypeException("Unsupported type: " + data.getClass().getName()));
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

	public CompletableFuture<ReturnData<T>> update(T data) {
		return CompletableFuture.supplyAsync(() -> {
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
					return ReturnData.error(new UnsupportedDataTypeException("Unsupported type: " + data.getClass().getName()));
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

	public CompletableFuture<ReturnData<T>> load(T data) {
		return CompletableFuture.supplyAsync(() -> {
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
					return ReturnData.error(new UnsupportedDataTypeException("Unsupported type: " + data.getClass().getName()));
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

	public CompletableFuture<ReturnData<List<T>>> query(SQLQuery<T> data) {
		return CompletableFuture.supplyAsync(() -> {
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
					return ReturnData.error(new UnsupportedDataTypeException("Unsupported type: " + data.getClass().getName()));
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

	protected String getCreateSQL() {
		String sql = "CREATE TABLE `" + getTableName() + "` (";
		sql += Arrays.stream(columns).map((c) -> getCreateSQL(c)).collect(Collectors.joining(", "));
		sql += ");";
		return sql;
	}

	protected String getCreateSQL(Column c) {
		return "`" + c.name() + "` " + c.type() + (c.autoIncrement() ? " AUTO_INCREMENT" : "") + (c.primaryKey() ? " PRIMARY KEY" : "") + (c.notNull() ? " NOT NULL" : "") + (c.unique() ? " UNIQUE" : "") + (c.index() ? " INDEX" : "")
				+ (!c.default_().equals("") ? " DEFAULT " + c.default_() : "");
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

	protected Connection connect() throws SQLException {
		return dataBase.getConnector().connect();
	}

	public DataBase getDataBase() {
		return dataBase;
	}

	private DB_Table getTypeAnnotation() {
		return getClass().getAnnotation(DB_Table.class);
	}

	@Override
	public String toString() {
		return "DataBaseTable{" + "tableName='" + getTableName() + "'" + '}';
	}

}
