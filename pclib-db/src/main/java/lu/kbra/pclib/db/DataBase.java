package lu.kbra.pclib.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.annotations.base.DB_Base;

public class DataBase {

	public static final String DEFAULT_CHARSET = "utf8mb4";
	public static final String DEFAULT_COLLATION = "utf8mb4_general_ci";

	private DataBaseConnector connector;

	private final String dataBaseName;
	private String characterSet = DEFAULT_CHARSET;
	private String collation = DEFAULT_COLLATION;

	public DataBase(DataBaseConnector connector) {
		this.connector = connector;
		this.connector.setDatabase(null);

		final DB_Base tableAnnotation = getTypeAnnotation();
		this.dataBaseName = tableAnnotation.name();
		this.characterSet = getTypeAnnotation().characterSet();
		this.collation = getTypeAnnotation().collate();
	}

	public DataBase(DataBaseConnector connector, String name) {
		this.connector = connector;
		this.connector.setDatabase(null);
		this.dataBaseName = name;
	}

	public DataBase(DataBaseConnector connector, String name, String charSet, String collation) {
		this.connector = connector;
		this.connector.setDatabase(null);
		this.dataBaseName = name;
		this.characterSet = charSet;
		this.collation = collation;
	}

	public void requestHook(SQLRequestType type, Object query) {
	}

	public NextTask<Void, Void, Boolean> exists() {
		return NextTask.create(() -> {
			final Connection con = connect();

			DatabaseMetaData dbMetaData = con.getMetaData();
			ResultSet rs = dbMetaData.getCatalogs();

			while (rs.next()) {
				String catalogName = rs.getString(1);
				if (catalogName.equals(getDataBaseName())) {
					rs.close();

					return true;
				}
			}

			rs.close();

			return false;
		});
	}

	public NextTask<Void, Boolean, DataBaseStatus> create() {
		return exists().thenApply((Boolean status) -> {
			if (status) {
				updateDataBaseConnector();
				return new DataBaseStatus(true, getDataBase());
			} else {
				Connection con = connect();

				Statement stmt = con.createStatement();

				final String sql = getCreateSQL();

				requestHook(SQLRequestType.CREATE_DATABASE, sql);

				stmt.executeUpdate(sql);

				stmt.close();

				updateDataBaseConnector();
				return new DataBaseStatus(false, getDataBase());
			}
		});

	}

	public NextTask<Void, Void, DataBase> drop() {
		return NextTask.create(() -> {
			final Connection con = connect();

			Statement stmt = con.createStatement();

			final String sql = "DROP DATABASE `" + getDataBaseName() + "`;";

			requestHook(SQLRequestType.DROP_DATABASE, sql);

			stmt.executeUpdate(sql);

			stmt.close();

			return getDataBase();
		});
	}

	public void updateDataBaseConnector() throws SQLException {
		this.connector.setDatabase(dataBaseName);
		this.connector.setCharacterSet(characterSet);
		this.connector.setCollation(collation);
		this.connector.reset();
	}

	private DataBase getDataBase() {
		return this;
	}

	public String getCreateSQL() {
		return "CREATE DATABASE `" + getDataBaseName() + "` CHARACTER SET " + characterSet + " COLLATE " + collation
				+ ";";
	}

	protected Connection connect() throws SQLException {
		return connector.connect();
	}

	protected Connection createConnection() throws SQLException {
		return connector.createConnection();
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
