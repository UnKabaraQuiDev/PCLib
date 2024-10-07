package lu.pcy113.pclib.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import lu.pcy113.pclib.db.ReturnData.ReturnStatus;
import lu.pcy113.pclib.db.annotations.Column;
import lu.pcy113.pclib.db.annotations.DB_Table;

public abstract class DataBaseTable<T> {

	private DataBase dataBase;

	private final String tableName;
	private final Column[] columns;

	public DataBaseTable(DataBase dbTest) {
		this.dataBase = dbTest;
		
		DB_Table tableAnnotation = getTypeAnnotation();
		this.tableName = tableAnnotation.name();
		this.columns = tableAnnotation.columns();
	}

	public CompletableFuture<ReturnData<?>> exists() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				final Connection con = connect();

				DatabaseMetaData dbMetaData = con.getMetaData();
				ResultSet rs = dbMetaData.getTables(null, null, getTableName(), null);

				if (rs.next()) {
					rs.close();

					return ReturnData.fine(true);
				} else {
					rs.close();

					return ReturnData.fine(false);
				}
			} catch (SQLException e) {
				return ReturnData.error(e);
			}
		});
	}

	public CompletableFuture<ReturnData<?>> create() {
		return exists().thenApply((status) -> {
			if(status.getStatus().equals(ReturnStatus.ERROR)) {
				return status;
			}
			
			return status.apply((state, data) -> {
				if ((boolean) data) {
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

	protected String getCreateSQL() {
		String sql = "CREATE TABLE " + getTableName() + " (";
		sql += Arrays.stream(columns).map((c) -> getCreateSQL(c)).collect(Collectors.joining(", "));
		sql += ");";
		return sql;
	}

	protected String getCreateSQL(Column c) {
		return c.name() + " " + c.type() + (c.autoIncrement() ? " AUTO_INCREMENT" : "") + (c.primaryKey() ? " PRIMARY KEY" : "") + (c.notNull() ? " NOT NULL" : "") + (c.unique() ? " UNIQUE" : "") + (c.index() ? " INDEX" : "");
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

}
