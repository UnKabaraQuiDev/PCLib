package lu.pcy113.pclib.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;

import lu.pcy113.pclib.db.annotations.DB_Base;
import lu.pcy113.pclib.impl.DependsOn;

@DependsOn("java.sql.*")
public class DataBase {

	private DataBaseConnector connector;

	private final String dataBaseName;

	public DataBase(DataBaseConnector connector) {
		this.connector = connector;
		this.connector.setDatabase(null);

		DB_Base tableAnnotation = getTypeAnnotation();
		this.dataBaseName = tableAnnotation.name();
	}

	public CompletableFuture<ReturnData<Boolean>> exists() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				final Connection con = connect();

				DatabaseMetaData dbMetaData = con.getMetaData();
				ResultSet rs = dbMetaData.getCatalogs();

				while (rs.next()) {
					String catalogName = rs.getString(1);
					if (catalogName.equals(getDataBaseName())) {
						rs.close();

						return ReturnData.ok(true);
					}
				}

				rs.close();

				return ReturnData.ok(false);
			} catch (SQLException e) {
				return ReturnData.error(e);
			}
		});
	}

	public CompletableFuture<ReturnData<DataBase>> create() {
		return exists().thenApply((status) -> {
			if (status.isError()) {
				return status.castError();
			}

			return status.apply((state, data) -> {
				if ((Boolean) data) {
					return ReturnData.existed(getDataBase());
				} else {
					try {
						Connection con = connect();

						Statement stmt = con.createStatement();

						stmt.executeUpdate(getCreateSQL());

						stmt.close();
						return ReturnData.created(getDataBase());
					} catch (SQLException e) {
						return ReturnData.error(e);
					}
				}
			});
		});
	}

	public CompletableFuture<ReturnData<DataBase>> drop() {
		return CompletableFuture.supplyAsync(() -> {
			try {
				final Connection con = connect();

				Statement stmt = con.createStatement();

				stmt.executeUpdate("DROP DATABASE `" + getDataBaseName() + "`;");

				stmt.close();

				return ReturnData.ok(getDataBase());
			} catch (SQLException e) {
				return ReturnData.error(e);
			}
		});
	}

	public void updateDataBaseConnector() {
		this.connector.setDatabase(dataBaseName);
	}

	private DataBase getDataBase() {
		return this;
	}

	private String getCreateSQL() {
		return "CREATE DATABASE `" + getDataBaseName() + "`;";
	}

	protected Connection connect() throws SQLException {
		return connector.connect();
	}

	public String getDataBaseName() {
		return dataBaseName;
	}

	private DB_Base getTypeAnnotation() {
		return getClass().getAnnotation(DB_Base.class);
	}

	public DataBaseConnector getConnector() {
		return connector;
	}

	@Override
	public String toString() {
		return "DataBase{" + "dataBaseName='" + getDataBaseName() + "'" + '}';
	}

}
