package lu.pcy113.pclib.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lu.pcy113.pclib.config.ConfigLoader.ConfigContainer;
import lu.pcy113.pclib.config.ConfigLoader.ConfigProp;
import lu.pcy113.pclib.impl.DependsOn;

@DependsOn("java.sql.DriverManager")
public class DataBaseConnector implements ConfigContainer {

	public static final int DEFAULT_PORT = 3306;

	@ConfigProp("protocol")
	public String protocol = "mysql";

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
	public String characterSet = "utf8mb4";

	@ConfigProp("collation")
	public String collation = "utf8mb4_general_ci";

	@ConfigProp("engine")
	public String engine = "InnoDB";

	private Connection connection;

	@Deprecated
	public DataBaseConnector(String user, String pass, String host, String database, int port, String characterSet,
			String collation) {
		this.username = user;
		this.password = pass;
		this.host = host;
		this.database = database;
		this.port = port;
		this.characterSet = characterSet;
		this.collation = collation;
	}

	public DataBaseConnector(String user, String pass, String host, int port) {
		this.username = user;
		this.password = pass;
		this.host = host;
		this.port = port;
	}

	public DataBaseConnector() {
	}

	public Connection connect() throws SQLException {
		return connection != null && !connection.isClosed() ? connection : (connection = createConnection());
	}

	public Connection createConnection() throws SQLException {
		final String url = "jdbc:" + protocol + "://" + host + ":" + port + "/" + (database != null ? database : "")
				+ (characterSet != null || collation != null ? "?" : "")
				+ (characterSet != null ? "characterSet=" + characterSet : "")
				+ (collation != null && characterSet != null ? "&" : "")
				+ (collation != null ? "collation=" + collation : "");
		return DriverManager.getConnection(url, username, password);
	}

	public void reset() throws SQLException {
		if (connection == null) {
			return;
		}
		connection.close();
		connection = null;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getCharacterSet() {
		return characterSet;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

	public String getCollation() {
		return collation;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	public String getEngine() {
		return engine;
	}

	public void setEngine(String engine) {
		this.engine = engine;
	}

	@Override
	public String toString() {
		return "DataBaseConnector [protocol=" + protocol + ", user=" + username + ", pass=" + password + ", host="
				+ host + ", database=" + database + ", port=" + port + ", characterSet=" + characterSet + ", collation="
				+ collation + ", engine=" + engine + "]";
	}

}
