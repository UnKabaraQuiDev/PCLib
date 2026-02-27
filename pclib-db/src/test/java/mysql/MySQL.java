package mysql;

import org.testcontainers.containers.MySQLContainer;

final class MySQL {

	public static final String USER = "user";
	public static final String PASS = "pass";
	public static final String DB_NAME = "__testdb";

	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withUsername(USER).withPassword(PASS).withDatabaseName(DB_NAME);

	static {
		mysql.start();
		try {
//			mysql.execInContainer("mysql", "-uuser", "-ppass", "-e", "CREATE USER '" + USER + "'@'%' IDENTIFIED BY '" + PASS + "';");
//			mysql.execInContainer("mysql", "-uuser", "-ppass", "-e", "GRANT ALL PRIVILEGES ON *.* TO '" + USER + "'@'%';");
//			mysql.execInContainer("mysql", "-uuser", "-ppass", "-e", "FLUSH PRIVILEGES;");
			mysql.execInContainer("mysql", "-uuser", "-ppass", "-e", "DROP DATABASE IF EXISTS `" + DB_NAME + "`;");
		} catch (Exception e) {
			throw new RuntimeException("Failed to setup MySQL user", e);
		}
	}

	public static void start() {
		// does nothing, only loads the class
	}

	public static int getPort() {
		return mysql.getFirstMappedPort();
	}

}
