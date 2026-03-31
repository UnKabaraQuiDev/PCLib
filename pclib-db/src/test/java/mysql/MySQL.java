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

	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withUsername(MySQL.USER)
			.withPassword(MySQL.PASS)
			.withDatabaseName(MySQL.DB_NAME);

	static {
		if (MySQL.isPortOpen("localhost", MySQL.DEFAULT_PORT) && MySQL.canLoginLocal()) {
			MySQL.LOCAL_MYSQL = true;
			System.out.println("Using local MySQL on port 3306");
		} else {
			MySQL.startContainer();
		}
	}

	private static boolean isPortOpen(final String host, final int port) {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress(host, port), 500);
			return true;
		} catch (final Exception ignored) {
			return false;
		}
	}

	private static boolean canLoginLocal() {
		try (Connection conn = DriverManager
				.getConnection("jdbc:mysql://localhost:" + MySQL.DEFAULT_PORT + "/mysql", MySQL.USER, MySQL.PASS)) {
			return conn != null && conn.isValid(1);
		} catch (final Exception e) {
			return false;
		}
	}

	private static void startContainer() {
		MySQL.mysql.start();

		try {
			MySQL.mysql.execInContainer("mysql", "-uroot", "-ppass", "-e", "GRANT ALL PRIVILEGES ON *.* TO '" + MySQL.USER + "'@'%';");
			MySQL.mysql.execInContainer("mysql", "-uroot", "-ppass", "-e", "FLUSH PRIVILEGES;");
			MySQL.mysql.execInContainer("mysql", "-uroot", "-ppass", "-e", "DROP DATABASE IF EXISTS `" + MySQL.DB_NAME + "`;");
		} catch (final Exception e) {
			throw new RuntimeException("Failed to setup MySQL user", e);
		}
	}

	public static void start() {
		// forces class loading
	}

	public static int getPort() {
		return MySQL.LOCAL_MYSQL ? MySQL.DEFAULT_PORT : MySQL.mysql.getFirstMappedPort();
	}

}
