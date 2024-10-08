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

	@ConfigProp("username")
	public String user;

	@ConfigProp("password")
	public String pass;

	@ConfigProp("host")
	public String host;

	private String database = null;

	@ConfigProp("port")
	public int port;

	private Connection connection;

	@Deprecated
	public DataBaseConnector(String user, String pass, String host, String database, int port) {
		this.user = user;
		this.pass = pass;
		this.host = host;
		this.database = database;
		this.port = port;
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
		return connection != null && !connection.isClosed() ? connection : createConnection();
	}

	private Connection createConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + (database != null ? database : ""), user, pass);
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

	@Override
	public String toString() {
		return "{user='" + user + "', pass='" + pass + "', host='" + host + "', database='" + database + "', port='" + port + "'}";
	}

}
