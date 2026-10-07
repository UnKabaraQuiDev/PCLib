package lu.kbra.pclib.db.connector;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.sqlite.meta.SQLiteDatabaseHints;
import lu.kbra.pclib.db.connector.impl.ImplicitCreationCapable;
import lu.kbra.pclib.db.connector.impl.ImplicitDeletionCapable;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;
import lu.kbra.pclib.db.exception.ConnectionFailedException;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.InternalDBException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SQLiteDatabaseConnector extends ThreadLocalDatabaseConnector implements ImplicitCreationCapable, ImplicitDeletionCapable {

	public static final String FIX_DB_EXTENSION_PROPERTY = SQLiteDatabaseConnector.class.getSimpleName() + ".fix_db_extension";
	public static boolean FIX_DB_EXTENSION = PCUtils.getBoolean(SQLiteDatabaseConnector.FIX_DB_EXTENSION_PROPERTY, true);

	public static final int DEFAULT_PORT = 3306;

	public static final String PROTOCOL = SQLiteDbmsProvider.DBMS_QUALIFIER_NAME;

	public URI dirPath = Paths.get(".").toUri();

	protected String database;

	public SQLiteDatabaseConnector(final URI dirPath) {
		this.dirPath = dirPath;
	}

	@Override
	public SQLiteDatabaseConnector clone() {
		return new SQLiteDatabaseConnector(this.dirPath, this.database);
	}

	@Override
	public final boolean create() throws DBException {
		final boolean existed = this.exists();
		if (existed) {
			return false;
		}
		this.reset();
		try (Connection ignored = this.createConnection()) {
			// Opening a SQLite JDBC connection creates the file.
			try (Statement statement = ignored.createStatement()) {
				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.AUTO_VACUUM)) {
					statement.execute(
							"PRAGMA auto_vacuum = " + this.databaseStructure.getStringHint(SQLiteDatabaseHints.AUTO_VACUUM, "NONE"));
				}
				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.APPLICATION_ID)) {
					statement
							.execute("PRAGMA application_id = " + this.databaseStructure.getIntHint(SQLiteDatabaseHints.APPLICATION_ID, 0));
				}
				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.USER_VERSION)) {
					statement.execute("PRAGMA user_version = " + this.databaseStructure.getIntHint(SQLiteDatabaseHints.USER_VERSION, 0));
				}
				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.JOURNAL_MODE)) {
					statement.execute(
							"PRAGMA journal_mode = " + this.databaseStructure.getStringHint(SQLiteDatabaseHints.JOURNAL_MODE, "WAL"));
				}
			}
		} catch (final SQLException e) {
			throw new ConnectionFailedException(e);
		}
		if (!existed && !this.exists()) {
			throw new InternalDBException("Failed to create database (" + Paths.get(this.dirPath).resolve(this.database) + ").");
		} else {
			return true;
		}
	}

	@Override
	public Connection createConnection() throws DBException {
		if (this.database == null || this.database.isEmpty()) {
			throw new IllegalStateException("SQLite database file path not set");
		}

		try {
			Files.createDirectories(Paths.get(this.dirPath));
			final Connection connection = DriverManager.getConnection(this.getURI().toString());
			try (Statement statement = connection.createStatement()) {
				statement.execute("PRAGMA foreign_keys = "
						+ (this.databaseStructure.getBooleanHint(SQLiteDatabaseHints.FOREIGN_KEYS, true) ? "ON" : "OFF"));

				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.SYNCHRONOUS)) {
					statement.execute(
							"PRAGMA synchronous = " + this.databaseStructure.getStringHint(SQLiteDatabaseHints.SYNCHRONOUS, "NORMAL"));
				}
				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.BUSY_TIMEOUT)) {
					statement.execute("PRAGMA busy_timeout = " + this.databaseStructure.getIntHint(SQLiteDatabaseHints.BUSY_TIMEOUT, 5000));
				}
				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.CACHE_SIZE)) {
					statement.execute("PRAGMA cache_size = " + this.databaseStructure.getIntHint(SQLiteDatabaseHints.CACHE_SIZE, -20000));
				}
				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.TEMP_STORE)) {
					statement.execute(
							"PRAGMA temp_store = " + this.databaseStructure.getStringHint(SQLiteDatabaseHints.TEMP_STORE, "MEMORY"));
				}
				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.SECURE_DELETE)) {
					statement.execute("PRAGMA secure_delete = "
							+ (this.databaseStructure.getBooleanHint(SQLiteDatabaseHints.SECURE_DELETE) ? "ON" : "OFF"));
				}
				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.RECURSIVE_TRIGGERS)) {
					statement.execute("PRAGMA recursive_triggers = "
							+ (this.databaseStructure.getBooleanHint(SQLiteDatabaseHints.RECURSIVE_TRIGGERS) ? "ON" : "OFF"));
				}
				if (this.databaseStructure.hasHint(SQLiteDatabaseHints.CASE_SENSITIVE_LIKE)) {
					statement.execute("PRAGMA case_sensitive_like = "
							+ (this.databaseStructure.getBooleanHint(SQLiteDatabaseHints.CASE_SENSITIVE_LIKE) ? "ON" : "OFF"));
				}
			}
			return connection;
		} catch (final SQLException | IOException e) {
			throw new InternalDBException(e);
		}
	}

	@Override
	public boolean delete() throws DBException {
		final boolean existed = this.exists();
		if (existed) {
			try {
				Files.deleteIfExists(Paths.get(this.dirPath).resolve(this.database));
			} catch (final IOException e) {
				throw new InternalDBException(
						"Exception raised while trying to delete db (" + Paths.get(this.dirPath).resolve(this.database) + ").",
						e);
			}
			return !this.exists();
		} else {
			return true;
		}
	}

	@Override
	public final boolean exists() throws DBException {
		return Files.exists(Paths.get(this.dirPath).resolve(this.database));
	}

	@Override
	public final String getDatabase() {
		return this.database;
	}

	public final URI getPath() {
		return Paths.get(this.dirPath).resolve(this.database).toUri();
	}

	@Override
	public final String getProtocol() {
		return SQLiteDatabaseConnector.PROTOCOL;
	}

	@Override
	public URI getURI() {
		return URI.create("jdbc:sqlite:" + this.getPath().toString());
	}

	@Override
	public final void setDatabase(String database) {
		if (database != null && SQLiteDatabaseConnector.FIX_DB_EXTENSION && !(database.endsWith(".db") || database.endsWith(".sqlite"))) {
			database += ".sqlite";
		}
		if (this.database != null && this.database.equals(database)) {
			return;
		}
		if (this.database != null && database != null) {
			throw new IllegalStateException(this.getClass().getSimpleName() + " already used by db: " + this.database);
		}
		this.database = database;
	}

}
