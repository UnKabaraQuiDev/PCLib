package lu.kbra.pclib.db.base;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import lu.kbra.pclib.db.annotations.base.DB_Base;
import lu.kbra.pclib.db.connector.impl.CharacterSetCapable;
import lu.kbra.pclib.db.connector.impl.CollationCapable;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.connector.impl.ImplicitCreationCapable;
import lu.kbra.pclib.db.connector.impl.ImplicitDeletionCapable;
import lu.kbra.pclib.db.utils.SQLRequestType;

public class DataBase {

	protected DataBaseConnector connector;

	protected final String dataBaseName;

	@Deprecated
	public DataBase(DataBaseConnector connector) {
		this.connector = connector;
		this.connector.setDatabase(null);

		final DB_Base tableAnnotation = getTypeAnnotation();
		this.dataBaseName = tableAnnotation.name();
		if (connector instanceof CharacterSetCapable) {
			((CharacterSetCapable) this.connector).setCharacterSet(tableAnnotation.characterSet());
		}
		if (connector instanceof CollationCapable) {
			((CollationCapable) this.connector).setCollation(tableAnnotation.collate());
		}
		if (connector instanceof ImplicitCreationCapable) {
			connector.setDatabase(dataBaseName);
		}
	}

	public DataBase(DataBaseConnector connector, String name) {
		this.connector = connector;
		this.dataBaseName = name;
		if (connector instanceof ImplicitCreationCapable) {
			connector.setDatabase(name);
		}
	}

	public DataBase(DataBaseConnector connector, String name, String charSet, String collation) {
		this.connector = connector;
		this.dataBaseName = name;
		if (connector instanceof CharacterSetCapable) {
			((CharacterSetCapable) this.connector).setCharacterSet(charSet);
		}
		if (connector instanceof CollationCapable) {
			((CollationCapable) this.connector).setCollation(collation);
		}
		if (connector instanceof ImplicitCreationCapable) {
			connector.setDatabase(name);
		}
	}

	public void requestHook(SQLRequestType type, Object query) {
	}

	public boolean exists() throws SQLException {
		if (connector instanceof ImplicitCreationCapable) {
			return ((ImplicitCreationCapable) connector).exists();
		} else {
			final Connection con = connect();

			final DatabaseMetaData dbMetaData = con.getMetaData();
			final ResultSet rs = dbMetaData.getCatalogs();

			while (rs.next()) {
				final String catalogName = rs.getString(1);
				if (catalogName.equals(getDataBaseName())) {
					rs.close();

					return true;
				}
			}

			rs.close();

			return false;
		}
	}

	public DataBaseStatus create() throws SQLException {
		if (connector instanceof ImplicitCreationCapable) {
			final boolean existed = ((ImplicitCreationCapable) connector).exists();
			((ImplicitCreationCapable) connector).create();
			return new DataBaseStatus(existed, getDataBase());
		} else {
			if (exists()) {
				updateDataBaseConnector();
				return new DataBaseStatus(true, getDataBase());
			} else {
				final Connection con = connect();

				final Statement stmt = con.createStatement();

				final String sql = getCreateSQL();

				requestHook(SQLRequestType.CREATE_DATABASE, sql);

				stmt.executeUpdate(sql);

				stmt.close();

				updateDataBaseConnector();
				return new DataBaseStatus(false, getDataBase());
			}
		}
	}

	public DataBase drop() throws SQLException {
		if (connector instanceof ImplicitDeletionCapable) {
			((ImplicitDeletionCapable) connector).delete();
			return getDataBase();
		} else {
			final Connection con = connect();

			final Statement stmt = con.createStatement();

			final String sql = "DROP DATABASE `" + getDataBaseName() + "`;";

			requestHook(SQLRequestType.DROP_DATABASE, sql);

			stmt.executeUpdate(sql);

			stmt.close();

			return getDataBase();
		}
	}

	public void updateDataBaseConnector() throws SQLException {
		this.connector.setDatabase(dataBaseName);
		this.connector.reset();
	}

	private DataBase getDataBase() {
		return this;
	}

	public String getCreateSQL() {
		return "CREATE DATABASE `" + getDataBaseName() + "`"
				+ (connector instanceof CharacterSetCapable ? " CHARACTER SET " + ((CharacterSetCapable) connector).getCharacterSet() : "")
				+ (connector instanceof CollationCapable ? " COLLATE " + ((CollationCapable) connector).getCollation() : "") + ";";
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

	@Override
	public String toString() {
		return "DataBase@" + System.identityHashCode(this) + " [connector=" + connector + ", dataBaseName=" + dataBaseName + "]";
	}

}
