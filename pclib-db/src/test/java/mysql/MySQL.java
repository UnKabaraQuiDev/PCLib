package mysql;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

import org.testcontainers.containers.MySQLContainer;

public final class MySQL {

	public static final String USER = "user";
	public static final String PASS = "pass";
	public static final String DB_NAME = "__testdb";

	public static final int DEFAULT_PORT = 3306;

	public static boolean LOCAL_MYSQL = false;

	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withUsername(USER).withPassword(PASS).withDatabaseName(DB_NAME);

	static {
		if (isPortOpen("localhost", DEFAULT_PORT) && canLoginLocal()) {
			LOCAL_MYSQL = true;
			System.out.println("Using local MySQL on port 3306");
		} else {
			startContainer();
		}
	}

	private static boolean isPortOpen(String host, int port) {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, port), 500);
			return true;
		} catch (Exception ignored) {
			return false;
		}
	}

	private static boolean canLoginLocal() {
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:" + DEFAULT_PORT + "/mysql", USER, PASS)) {
			return conn != null && conn.isValid(1);
		} catch (Exception e) {
			return false;
		}
	}

	private static void startContainer() {
		mysql.start();

		try {
			mysql.execInContainer("mysql", "-uroot", "-ppass", "-e", "GRANT ALL PRIVILEGES ON *.* TO '" + USER + "'@'%';");
			mysql.execInContainer("mysql", "-uroot", "-ppass", "-e", "FLUSH PRIVILEGES;");
			mysql.execInContainer("mysql", "-uroot", "-ppass", "-e", "DROP DATABASE IF EXISTS `" + DB_NAME + "`;");
		} catch (Exception e) {
			throw new RuntimeException("Failed to setup MySQL user", e);
		}
	}

	public static void start() {
		// forces class loading
	}

	public static int getPort() {
		return LOCAL_MYSQL ? DEFAULT_PORT : mysql.getFirstMappedPort();
	}

}