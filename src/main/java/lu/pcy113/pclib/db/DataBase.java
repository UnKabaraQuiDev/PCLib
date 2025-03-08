package lu.pcy113.pclib.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.annotations.base.DB_Base;
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

	public NextTask<Void, ReturnData<Boolean>> exists() {
		return NextTask.create(() -> {
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

	public NextTask<Void, ReturnData<DataBaseStatus>> create() {
		return exists().thenApply((status) -> {
			if (status.isError()) {
				return status.castError();
			}

			return status.apply((state, data) -> {
				if ((Boolean) data) {
					try {
						updateDataBaseConnector();
						return ReturnData.ok(new DataBaseStatus(true, getDataBase()));
					} catch (SQLException e) {
						return ReturnData.error(e);
					}
				} else {
					try {
						Connection con = connect();

						Statement stmt = con.createStatement();

						stmt.executeUpdate(getCreateSQL());

						stmt.close();

						updateDataBaseConnector();
						return ReturnData.ok(new DataBaseStatus(false, getDataBase()));
					} catch (SQLException e) {
						return ReturnData.error(e);
					}
				}
			});
		});
	}

	public NextTask<Void, ReturnData<DataBase>> drop() {
		return NextTask.create(() -> {
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

	public void updateDataBaseConnector() throws SQLException {
		this.connector.setDatabase(dataBaseName);
		this.connector.setCharacterSet(getTypeAnnotation().characterSet());
		this.connector.setCollation(getTypeAnnotation().collate());
		this.connector.reset();
	}

	private DataBase getDataBase() {
		return this;
	}

	public String getCreateSQL() {
		String sql =  "CREATE DATABASE `" + getDataBaseName() + "` CHARACTER SET " + getTypeAnnotation().characterSet() + " COLLATE " + getTypeAnnotation().collate() + ";";
		return sql;
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

	public static class DataBaseStatus {
		private boolean existed;
		private DataBase database;

		protected DataBaseStatus(boolean existed, DataBase database) {
			this.existed = existed;
			this.database = database;
		}

		public boolean existed() {
			return existed;
		}

		public boolean created() {
			return !existed;
		}

		public DataBase getDatabase() {
			return database;
		}

		@Override
		public String toString() {
			return "DataBaseStatus{existed=" + existed + ", created=" + !existed + ", db=" + database + "}";
		}

	}

}
