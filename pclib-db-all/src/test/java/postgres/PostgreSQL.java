package postgres;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.PostgreSQLContainer;

import lu.kbra.pclib.PCUtils;

public final class PostgreSQL {

	public static final String USER = "user";
	public static final String PASS = "pass";
	public static final String DB_NAME = "__testdb";

	public static final int DEFAULT_PORT = 5432;
	public static final boolean USE_LOCAL_DB_IF_AVAILABLE = PCUtils
			.getBoolean(PostgreSQL.class.getSimpleName() + ".use_local_db_if_available", true);

	public static boolean LOCAL_POSTGRES = false;

	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine").withUsername(PostgreSQL.USER)
			.withPassword(PostgreSQL.PASS)
			.withDatabaseName(PostgreSQL.DB_NAME);

	static {
		if (PostgreSQL.isPortOpen("localhost", PostgreSQL.DEFAULT_PORT) && PostgreSQL.canLoginLocal()
				&& PostgreSQL.USE_LOCAL_DB_IF_AVAILABLE) {
			PostgreSQL.LOCAL_POSTGRES = true;
			System.out.println("Using local PostgreSQL on port 5432");
		} else {
			PostgreSQL.startContainer();
		}
	}

	public static int getPort() {
		return PostgreSQL.LOCAL_POSTGRES ? PostgreSQL.DEFAULT_PORT : PostgreSQL.postgres.getFirstMappedPort();
	}

	public static void start() {
		// forces class loading
	}

	public static void stop() {
		PostgreSQL.postgres.close();
	}

	private static boolean canLoginLocal() {
		try (Connection conn = DriverManager
				.getConnection("jdbc:postgresql://localhost:" + PostgreSQL.DEFAULT_PORT + "/", PostgreSQL.USER, PostgreSQL.PASS)) {
			return conn != null && conn.isValid(1);
		} catch (final Exception e) {
			return false;
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

	private static void startContainer() {
		PostgreSQL.postgres.start();

		Runtime.getRuntime().addShutdownHook(new Thread(PostgreSQL::stop));

		try {
			final ExecResult result = PostgreSQL.postgres.execInContainer("psql",
					"-U",
					PostgreSQL.USER,
					"-d",
					"postgres",
					"-c",
					"DROP DATABASE IF EXISTS \"" + PostgreSQL.DB_NAME + "\";");

			if (result.getExitCode() != 0) {
				throw new RuntimeException(
						"Failed to drop PostgreSQL test database.\nSTDOUT:\n" + result.getStdout() + "\nSTDERR:\n" + result.getStderr());
			}
		} catch (final Exception e) {
			throw new RuntimeException("Failed to setup PostgreSQL container", e);
		}
	}

	private PostgreSQL() {
	}

}
