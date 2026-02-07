package lu.kbra.pclib.db;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.config.ConfigLoader.ConfigContainer;
import lu.kbra.pclib.config.ConfigLoader.ConfigProp;

public class SQLiteDataBaseConnector extends AbstractDataBaseConnector implements ConfigContainer, ImplicitCreationCapable {

	public static final String FIX_DB_EXTENSION_PROPERTY = SQLiteDataBaseConnector.class.getSimpleName() + ".fix_db_extension";
	public static boolean FIX_DB_EXTENSION = PCUtils.getBoolean(FIX_DB_EXTENSION_PROPERTY, true);

	public static final int DEFAULT_PORT = 3306;

//	@ConfigProp("protocol")
	@Deprecated
	public final String protocol = "sqlite";

	@ConfigProp("dirPath")
	public String dirPath = ".";

	protected String database;

	public SQLiteDataBaseConnector(String dirPath, String database) {
		this.dirPath = dirPath;
		this.database = database;
	}

	public SQLiteDataBaseConnector(String dirPath) {
		this.dirPath = dirPath;
	}

	public SQLiteDataBaseConnector() {
	}

	@Override
	public Connection createConnection() throws SQLException {
		if (this.database == null || this.database.isEmpty()) {
			throw new IllegalStateException("SQLite database file path not set");
		}

		final String url = "jdbc:sqlite:" + Paths.get(dirPath).resolve(this.database);
		return DriverManager.getConnection(url);
	}

	@Override
	public final String getDatabase() {
		return this.database;
	}

	@Override
	public final void setDatabase(String database) {
		if (this.database != null) {
			throw new IllegalStateException(this.getClass().getSimpleName() + " already used by db: " + this.database);
		}
		if (!(database.endsWith(".db") || database.endsWith(".sqlite"))) {
			database += ".sqlite";
		}
		this.database = database;
	}

	public final String getProtocol() {
		return protocol;
	}

	@Override
	public final boolean create() throws SQLException {
		final boolean existed = exists();
		if (existed) {
			return false;
		}
		reset();
		createConnection();
		if (!existed && !exists()) {
			throw new SQLException("Failed to create database (" + Paths.get(dirPath).resolve(database) + ").");
		} else {
			return true;
		}
	}

	@Override
	public final boolean exists() throws SQLException {
		return Files.exists(Paths.get(dirPath).resolve(database));
	}

	@Override
	public String toString() {
		return "SQLiteDataBaseConnector@" + System.identityHashCode(this) + " [protocol=" + protocol + ", dirPath=" + dirPath
				+ ", database=" + database + "]";
	}

}
