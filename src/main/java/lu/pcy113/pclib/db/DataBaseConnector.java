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
	public String user;

	@ConfigProp("password")
	public String pass;

	@ConfigProp("host")
	public String host;

	protected String database = null;

	@ConfigProp("port")
	public int port = DEFAULT_PORT;

	@ConfigProp("characterset")
	public String characterSet;

	@ConfigProp("collation")
	public String collation;

	private Connection connection;

	@Deprecated
	public DataBaseConnector(String user, String pass, String host, String database, int port, String characterSet, String collation) {
		this.user = user;
		this.pass = pass;
		this.host = host;
		this.database = database;
		this.port = port;
		this.characterSet = characterSet;
		this.collation = collation;
	}

	public DataBaseConnector(String user, String pass, String host, int port) {
		this.user = user;
		this.pass = pass;
		this.host = host;
		this.port = port;
	}

	public DataBaseConnector() {
	}

	public Connection connect() throws SQLException {
		return connection != null && !connection.isClosed() ? connection : (connection = createConnection());
	}

	protected Connection createConnection() throws SQLException {
		final String url = "jdbc:" + protocol + "://" + host + ":" + port + "/" + (database != null ? database : "") + (characterSet != null || collation != null ? "?" : "") + (characterSet != null ? "characterSet=" + characterSet : "")
				+ (collation != null && characterSet != null ? "&" : "") + (collation != null ? "collation=" + collation : "");
		return DriverManager.getConnection(url, user, pass);
	}

	public void reset() throws SQLException {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public void setCharacterSet(String characterSet) {
		this.characterSet = characterSet;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	@Override
	public String toString() {
		return "{user='" + user + "', pass='" + pass + "', host='" + host + "', database='" + database + "', port='" + port + "'}";
	}

}
