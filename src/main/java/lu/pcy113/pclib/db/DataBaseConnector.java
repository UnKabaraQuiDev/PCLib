package lu.pcy113.pclib.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnector {

	public static final int DEFAULT_PORT = 3306;

	private String user, pass, host, database = null;
	private int port;

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

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
