package lu.kbra.pclib.db.connector;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lu.kbra.pclib.config.ConfigLoader.ConfigContainer;
import lu.kbra.pclib.config.ConfigLoader.ConfigProp;
import lu.kbra.pclib.db.connector.impl.ImplicitCreationCapable;
import lu.kbra.pclib.db.connector.impl.ImplicitDeletionCapable;
import lu.kbra.pclib.db.exception.DBException;

public class PostgreSQLDataBaseConnector extends ThreadLocalDataBaseConnector
		implements
			ConfigContainer,
			ImplicitCreationCapable,
			ImplicitDeletionCapable {

	public static final int DEFAULT_PORT = 5432;
	public static final String DEFAULT_MAINTENANCE_DATABASE = "postgres";

//	@ConfigProp("protocol")
	@Deprecated
	public final String protocol = "postgres";

	@ConfigProp("username")
	public String username;

	@ConfigProp("password")
	public String password;

	@ConfigProp("host")
	public String host;

	protected String database = null;

	@ConfigProp("port")
	public int port = PostgreSQLDataBaseConnector.DEFAULT_PORT;

	@ConfigProp("maintenanceDatabase")
	public String maintenanceDatabase = PostgreSQLDataBaseConnector.DEFAULT_MAINTENANCE_DATABASE;

	public PostgreSQLDataBaseConnector() {
	}

	public PostgreSQLDataBaseConnector(final String user, final String pass, final String host, final int port) {
		this.username = user;
		this.password = pass;
		this.host = host;
		this.port = port;
	}

	public PostgreSQLDataBaseConnector(
			final String user,
			final String pass,
			final String host,
			final String database,
			final int port,
			final String maintenanceDatabase) {
		this.username = user;
		this.password = pass;
		this.host = host;
		this.database = database;
		this.port = port;
		this.maintenanceDatabase = maintenanceDatabase;
	}

	@Override
	public URI getURI() {
		return this.getURI(this.database == null || this.database.isEmpty() ? this.maintenanceDatabase : this.database);
	}

	protected URI getURI(final String databaseName) {
		final StringBuilder url = new StringBuilder();
		url.append("jdbc:postgresql://").append(this.host).append(":").append(this.port).append("/");

		if (databaseName != null && !databaseName.isEmpty()) {
			url.append(databaseName);
		}

		final Map<String, String> params = new LinkedHashMap<>();
		params.put("connectTimeout", "0");
		params.put("socketTimeout", "0");

		if (!params.isEmpty()) {
			url.append("?");
			url.append(params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&")));
		}

		return URI.create(url.toString());
	}

	@Override
	public Connection createConnection() throws DBException {
		try {
			return DriverManager.getConnection(this.getURI().toString(), this.username, this.password);
		} catch (final SQLException e) {
			throw new DBException(e);
		}
	}

	protected Connection createMaintenanceConnection() throws DBException {
		try {
			return DriverManager.getConnection(this.getURI(this.maintenanceDatabase).toString(), this.username, this.password);
		} catch (final SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public boolean exists() throws DBException {
		if (this.database == null || this.database.isEmpty()) {
			return false;
		}
		try (Connection con = this.createMaintenanceConnection();
				PreparedStatement stmt = con.prepareStatement("SELECT 1 FROM pg_database WHERE datname = ?")) {
			stmt.setString(1, this.database);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next();
			}
		} catch (final SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public boolean create() throws DBException {
		if (this.database == null || this.database.isEmpty()) {
			throw new IllegalStateException("PostgreSQL database name not set");
		}
		final boolean existed = this.exists();
		if (existed) {
			return false;
		}
		try (Connection con = this.createMaintenanceConnection(); Statement stmt = con.createStatement()) {
			stmt.executeUpdate("CREATE DATABASE " + this.escapeIdentifier(this.database));
			return true;
		} catch (final SQLException e) {
			throw new DBException(e);
		}
	}

	@Override
	public boolean delete() throws DBException {
		if (this.database == null || this.database.isEmpty()) {
			return true;
		}
		final boolean existed = this.exists();
		if (!existed) {
			return true;
		}
		this.reset();
		try (Connection con = this.createMaintenanceConnection(); Statement stmt = con.createStatement()) {
			this.terminateDatabaseConnections(stmt);
			stmt.executeUpdate("DROP DATABASE IF EXISTS " + this.escapeIdentifier(this.database));
			return !this.exists();
		} catch (final SQLException e) {
			throw new DBException(e);
		}
	}

	private void terminateDatabaseConnections(final Statement stmt) throws SQLException {
		stmt.execute("SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname = '" + this.database.replace("'", "''")
				+ "' AND pid <> pg_backend_pid()");
	}

	private String escapeIdentifier(final String identifier) {
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	@Override
	public final String getDatabase() {
		return this.database;
	}

	@Override
	public final void setDatabase(final String database) {
		if (this.database != null && this.database.equals(database)) {
			return;
		}
		if (this.database != null && database != null) {
			throw new IllegalStateException(this.getClass().getSimpleName() + " already used by db: " + this.database);
		}
		this.database = database;
	}

	@Override
	public final String getProtocol() {
		return this.protocol;
	}

	@Override
	public String toString() {
		return "PostgreSQLDataBaseConnector@" + System.identityHashCode(this) + " [protocol=" + this.protocol + ", username="
				+ this.username + ", password=" + this.password + ", host=" + this.host + ", database=" + this.database + ", port="
				+ this.port + ", maintenanceDatabase=" + this.maintenanceDatabase + "]";
	}

	@Override
	public PostgreSQLDataBaseConnector clone() {
		return new PostgreSQLDataBaseConnector(this.username, this.password, this.host, this.database, this.port, this.maintenanceDatabase);
	}

}
