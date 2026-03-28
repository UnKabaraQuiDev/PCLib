package lu.kbra.pclib.db.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import lu.kbra.pclib.config.ConfigLoader.ConfigContainer;
import lu.kbra.pclib.config.ConfigLoader.ConfigProp;
import lu.kbra.pclib.db.connector.impl.CharacterSetCapable;
import lu.kbra.pclib.db.connector.impl.CollationCapable;
import lu.kbra.pclib.db.connector.impl.EngineCapable;
import lu.kbra.pclib.db.exception.DBException;

public class MySQLDataBaseConnector extends AbstractDataBaseConnector
		implements ConfigContainer, CharacterSetCapable, EngineCapable, CollationCapable {

	public static final int DEFAULT_PORT = 3306;

	public static final String DEFAULT_CHARSET = "utf8mb4";
	public static final String DEFAULT_COLLATION = "utf8mb4_general_ci";
	public static final String DEFAULT_ENGINE = "InnoDB";

//	@ConfigProp("protocol")
	@Deprecated
	public final String protocol = "mysql";

	@ConfigProp("username")
	public String username;

	@ConfigProp("password")
	public String password;

	@ConfigProp("host")
	public String host;

	protected String database = null;

	@ConfigProp("port")
	public int port = DEFAULT_PORT;

	@ConfigProp("characterset")
	public String characterSet = DEFAULT_CHARSET;

	@ConfigProp("collation")
	public String collation = DEFAULT_COLLATION;

	@ConfigProp("engine")
	public String engine = DEFAULT_ENGINE;

	@Deprecated
	public MySQLDataBaseConnector(
			final String user,
			final String pass,
			final String host,
			final String database,
			final int port,
			final String characterSet,
			final String collation) {
		this.username = user;
		this.password = pass;
		this.host = host;
		this.database = database;
		this.port = port;
		this.characterSet = characterSet;
		this.collation = collation;
	}

	public MySQLDataBaseConnector(final String user, final String pass, final String host, final int port) {
		this.username = user;
		this.password = pass;
		this.host = host;
		this.port = port;
	}

	public MySQLDataBaseConnector() {
	}

	@Override
	public Connection createConnection() throws DBException {
		final StringBuilder url = new StringBuilder();
		url.append("jdbc:mysql://").append(this.host).append(":").append(this.port).append("/");

		if (this.database != null && !this.database.isEmpty()) {
			url.append(this.database);
		}

		final Map<String, String> params = new LinkedHashMap<>();

		if (this.characterSet != null && !this.characterSet.isEmpty()) {
			params.put("characterSet", this.characterSet);
		}

		if (this.collation != null && !this.collation.isEmpty()) {
			params.put("collation", this.collation);
		}

		params.put("connectTimeout", "0");
		params.put("socketTimeout", "0");
		params.put("autoReconnect", "true");

		if (!params.isEmpty()) {
			url.append("?");
			url.append(params.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&")));
		}

		try {
			return DriverManager.getConnection(url.toString(), this.username, this.password);
		} catch (final SQLException e) {
			throw new DBException(e);
		}
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
	public final String getCharacterSet() {
		return this.characterSet;
	}

	@Override
	public final void setCharacterSet(final String characterSet) {
		this.characterSet = characterSet;
	}

	@Override
	public final String getCollation() {
		return this.collation;
	}

	@Override
	public final void setCollation(final String collation) {
		this.collation = collation;
	}

	@Override
	public final String getEngine() {
		return this.engine;
	}

	@Override
	public final void setEngine(final String engine) {
		this.engine = engine;
	}

	@Override
	public String toString() {
		return "MySQLDataBaseConnector@" + System.identityHashCode(this) + " [protocol=" + this.protocol + ", username=" + this.username
				+ ", password=" + this.password + ", host=" + this.host + ", database=" + this.database + ", port=" + this.port
				+ ", characterSet=" + this.characterSet + ", collation=" + this.collation + ", engine=" + this.engine + "]";
	}

	@Override
	public MySQLDataBaseConnector clone() {
		return new MySQLDataBaseConnector(this.username, this.password, this.host, this.port);
	}

}
