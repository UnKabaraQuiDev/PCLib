package mysql;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Container.ExecResult;

public final class MySQL {

	public static final String USER = "user";
	public static final String PASS = "pass";
	public static final String DB_NAME = "__testdb";

	static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withUsername(USER)
//			.withRootPassword("root")
			.withPassword(PASS)
			.withDatabaseName(DB_NAME);

	static {
		mysql.start();
		try {
//			mysql.execInContainer("mysql", "-uroot", "-ppass", "-e", "CREATE USER '" + USER + "'@'%' IDENTIFIED BY '" + PASS + "';");
			mysql.execInContainer("mysql", "-uroot", "-ppass", "-e", "GRANT ALL PRIVILEGES ON *.* TO '" + USER + "'@'%';");
			mysql.execInContainer("mysql", "-uroot", "-ppass", "-e", "FLUSH PRIVILEGES;");
			final ExecResult er = mysql.execInContainer("mysql", "-uroot", "-ppass", "-e", "DROP DATABASE IF EXISTS `" + DB_NAME + "`;");
//			System.err.println(er.getStderr());
//			System.err.println(er.getStdout());
//			System.err.println(er.getExitCode());
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
