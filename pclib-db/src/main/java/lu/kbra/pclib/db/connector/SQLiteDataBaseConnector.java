package lu.kbra.pclib.db.connector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.protobuf.ExperimentalApi;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.config.ConfigLoader.ConfigContainer;
import lu.kbra.pclib.config.ConfigLoader.ConfigProp;
import lu.kbra.pclib.db.connector.impl.ImplicitCreationCapable;
import lu.kbra.pclib.db.connector.impl.ImplicitDeletionCapable;
import lu.kbra.pclib.db.exception.DBException;

@ExperimentalApi
public class SQLiteDataBaseConnector extends AbstractDataBaseConnector
		implements ConfigContainer, ImplicitCreationCapable, ImplicitDeletionCapable {

	public static final String FIX_DB_EXTENSION_PROPERTY = SQLiteDataBaseConnector.class.getSimpleName() + ".fix_db_extension";
	public static boolean FIX_DB_EXTENSION = PCUtils.getBoolean(SQLiteDataBaseConnector.FIX_DB_EXTENSION_PROPERTY, true);

	public static final int DEFAULT_PORT = 3306;

//	@ConfigProp("protocol")
	@Deprecated
	public final String protocol = "sqlite";

	@ConfigProp("dirPath")
	public String dirPath = ".";

	protected String database;

	public SQLiteDataBaseConnector() {
	}

	public SQLiteDataBaseConnector(final String dirPath) {
		this.dirPath = dirPath;
	}

	@Deprecated
	public SQLiteDataBaseConnector(final String dirPath, final String database) {
		this.dirPath = dirPath;
		this.database = database;
	}

	@Override
	public Connection createConnection() throws DBException {
		if (this.database == null || this.database.isEmpty()) {
			throw new IllegalStateException("SQLite database file path not set");
		}

		final String url = "jdbc:sqlite:" + this.getPath();
		try {
			return DriverManager.getConnection(url);
		} catch (final SQLException e) {
			throw new DBException(e);
		}
	}

	public final Path getPath() {
		return Paths.get(this.dirPath).resolve(this.database);
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

	@Override
	public final String getProtocol() {
		return this.protocol;
	}

	@Override
	public final boolean create() throws DBException {
		final boolean existed = this.exists();
		if (existed) {
			return false;
		}
		this.reset();
		this.createConnection();
		if (!existed && !this.exists()) {
			throw new DBException("Failed to create database (" + Paths.get(this.dirPath).resolve(this.database) + ").");
		} else {
			return true;
		}
	}

	@Override
	public final boolean exists() throws DBException {
		return Files.exists(Paths.get(this.dirPath).resolve(this.database));
	}

	@Override
	public boolean delete() throws DBException {
		final boolean existed = this.exists();
		if (existed) {
			try {
				Files.deleteIfExists(Paths.get(this.dirPath).resolve(this.database));
			} catch (final IOException e) {
				throw new DBException(
						"Exception raised while trying to delete db (" + Paths.get(this.dirPath).resolve(this.database) + ").",
						e);
			}
			return !this.exists();
		} else {
			return true;
		}
	}

	@Override
	public String toString() {
		return "SQLiteDataBaseConnector@" + System.identityHashCode(this) + " [protocol=" + this.protocol + ", dirPath=" + this.dirPath
				+ ", database=" + this.database + "]";
	}

	@Override
	public SQLiteDataBaseConnector clone() {
		return new SQLiteDataBaseConnector(this.dirPath);
	}

}
