package lu.kbra.pclib.db;

import java.io.IOException;
import java.nio.file.Path;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import mysql.MySQL;
import postgres.PostgreSQL;
import sqlite.SQLite;

@Data
@RequiredArgsConstructor
public final class ProtocolConfig {

	public static ProtocolConfig mysql() {
		MySQL.start();
		return new ProtocolConfig("mysql",
				"mysql",
				new String[] { "host=localhost", "username=" + MySQL.USER, "password=" + MySQL.PASS, "port=" + MySQL.getPort() },
				() -> {
				});
	}

	public static ProtocolConfig postgres() {
		PostgreSQL.start();
		return new ProtocolConfig("postgresql",
				"postgresql",
				new String[] {
						"host=localhost",
						"username=" + PostgreSQL.USER,
						"password=" + PostgreSQL.PASS,
						"port=" + PostgreSQL.getPort() },
				() -> {
				});
	}

	public static ProtocolConfig sqlite(final Path dir) {
		return new ProtocolConfig("sqlite", "sqlite", new String[] { "dir-path=" + dir.toAbsolutePath() }, () -> {
			try {
				SQLite.deleteDirectory(dir);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private final String displayName;
	private final String protocol;
	private final String[] connectionProperties;
	private final Runnable cleanup;

	@Override
	public String toString() {
		return this.displayName;
	}

	public void cleanup() {
		this.cleanup.run();
	}

	public String[] properties(final String connectorName, final String qualifier, final String databaseName) {
		final String prefix = "pclib.db." + connectorName + ".";
		final String[] properties = new String[3 + this.connectionProperties.length];
		properties[0] = prefix + "qualifier=" + qualifier;
		properties[1] = prefix + "protocol=" + this.protocol;
		properties[2] = prefix + "name=" + databaseName;

		for (int i = 0; i < this.connectionProperties.length; i++) {
			properties[i + 3] = prefix + this.connectionProperties[i];
		}
		return properties;
	}

}
