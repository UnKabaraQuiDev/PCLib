import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.annotations.queryable.def.NameOverride;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.MySQLDatabaseConnector;
import lu.kbra.pclib.db.connector.PostgreSQLDatabaseConnector;
import lu.kbra.pclib.db.connector.SQLiteDatabaseConnector;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.migration.DatabaseMigration;
import lu.kbra.pclib.db.migration.DatabaseSchemaMigrator;
import lu.kbra.pclib.db.migration.SchemaMigrationOptions;
import lu.kbra.pclib.db.table.DatabaseTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mysql.MySQL;
import postgres.PostgreSQL;
import sqlite.SQLite;

public class MigrationTest {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AddedMigrationPersonData implements DatabaseEntry {

		@Column
		@PrimaryKey
		@AutoIncrement
		protected long id;

		@Column(name = "first_name")
		protected @MaxLength(50) String firstName;

		@Column(name = "last_name")
		protected @MaxLength(50) String lastName;

		@Column(name = "obsolete_note")
		protected @MaxLength(50) String obsoleteNote;

		@Column(name = "full_name")
		@Nullable
		protected @MaxLength(120) String fullName;

		@Override
		public MigrationTest.AddedMigrationPersonData clone() {
			return PCUtils.safeClone(super::clone);
		}

	}

	@NameOverride(MigrationTest.TABLE_NAME)
	public static class AddedMigrationPersonTable extends DatabaseTable<AddedMigrationPersonData> {

		public AddedMigrationPersonTable(final Database database) {
			super(database);
		}

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FinalMigrationPersonData implements DatabaseEntry {

		@Column
		@PrimaryKey
		@AutoIncrement
		protected long id;

		@Column(name = "first_name")
		protected @MaxLength(50) String firstName;

		@Column(name = "last_name")
		protected @MaxLength(50) String lastName;

		@Column(name = "full_name")
		@Nullable
		protected @MaxLength(120) String fullName;

		@Override
		public MigrationTest.FinalMigrationPersonData clone() {
			return PCUtils.safeClone(super::clone);
		}

	}

	@NameOverride(MigrationTest.TABLE_NAME)
	public static class FinalMigrationPersonTable extends DatabaseTable<FinalMigrationPersonData> {

		public FinalMigrationPersonTable(final Database database) {
			super(database);
		}

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InitialMigrationPersonData implements DatabaseEntry {

		@Column
		@PrimaryKey
		@AutoIncrement
		protected long id;

		@Column(name = "first_name")
		protected @MaxLength(50) String firstName;

		@Column(name = "last_name")
		protected @MaxLength(50) String lastName;

		@Column(name = "obsolete_note")
		protected @MaxLength(50) String obsoleteNote;

		public InitialMigrationPersonData(final String firstName, final String lastName, final String obsoleteNote) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.obsoleteNote = obsoleteNote;
		}

		@Override
		public MigrationTest.InitialMigrationPersonData clone() {
			return PCUtils.safeClone(super::clone);
		}

	}

	@NameOverride(MigrationTest.TABLE_NAME)
	public static class InitialMigrationPersonTable extends DatabaseTable<InitialMigrationPersonData> {

		public InitialMigrationPersonTable(final Database database) {
			super(database);
		}

	}

	private static final class AddFullNameColumnMigration implements DatabaseMigration {

		private final AddedMigrationPersonTable table;

		private AddFullNameColumnMigration(final AddedMigrationPersonTable table) {
			this.table = table;
		}

		@Override
		public String name() {
			return "add_full_name_column";
		}

		@Override
		public int order() {
			return 1;
		}

		@Override
		public void up(final Database database, final Connection connection) throws DBException {
			new DatabaseSchemaMigrator().migrate(connection, Arrays.asList(this.table), new SchemaMigrationOptions(true, false));
		}

	}

	private static final class FillFullNameMigration implements DatabaseMigration {

		@Override
		public String name() {
			return "fill_full_name";
		}

		@Override
		public int order() {
			return 2;
		}

		@Override
		public void up(final Database database, final Connection connection) throws DBException {
			final String firstName = MigrationTest.quote(database.getConnector(), "first_name");
			final String lastName = MigrationTest.quote(database.getConnector(), "last_name");
			final String fullName = MigrationTest.quote(database.getConnector(), "full_name");
			final String value = MigrationTest.isMySQL(database.getConnector()) ? "CONCAT(" + firstName + ", ' ', " + lastName + ")"
					: firstName + " || ' ' || " + lastName;
			final String sql = "UPDATE " + MigrationTest.tableName(database.getConnector(), database.getDatabaseName()) + " SET " + fullName
					+ " = " + value;

			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate(sql);
			} catch (final SQLException e) {
				throw new DBException("Failed to fill full_name column.", e);
			}
		}

	}

	private static final class RemoveObsoleteNoteColumnMigration implements DatabaseMigration {

		private final FinalMigrationPersonTable table;

		private RemoveObsoleteNoteColumnMigration(final FinalMigrationPersonTable table) {
			this.table = table;
		}

		@Override
		public String name() {
			return "remove_obsolete_note_column";
		}

		@Override
		public int order() {
			return 3;
		}

		@Override
		public void up(final Database database, final Connection connection) throws DBException {
			new DatabaseSchemaMigrator().migrate(connection, Arrays.asList(this.table), new SchemaMigrationOptions(false, true));
		}

	}

	private static final String TABLE_NAME = "migration_person";

	private static boolean isMySQL(final DatabaseConnector connector) {
		return "mysql".equalsIgnoreCase(connector.getProtocol());
	}

	private static String quote(final DatabaseConnector connector, final String identifier) {
		if (MigrationTest.isMySQL(connector)) {
			return "`" + identifier.replace("`", "``") + "`";
		}
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	private static String tableName(final DatabaseConnector connector, final String databaseName) {
		return MigrationTest.tableName(connector, databaseName, MigrationTest.TABLE_NAME);
	}

	private static String tableName(final DatabaseConnector connector, final String databaseName, final String tableName) {
		if (MigrationTest.isMySQL(connector)) {
			return MigrationTest.quote(connector, databaseName) + "." + MigrationTest.quote(connector, tableName);
		}
		return MigrationTest.quote(connector, tableName);
	}

	@Test
	void migrationsAddFillAndRemoveColumnsOnMySQL() throws Exception {
		MySQL.start();
		final MySQLDatabaseConnector connector = new MySQLDatabaseConnector(MySQL.USER, MySQL.PASS, "localhost", MySQL.getPort());
		this.runMigrationTest(connector, "pclib_migration_mysql_" + System.nanoTime(), () -> {
		});
	}

	@Test
	void migrationsAddFillAndRemoveColumnsOnPostgreSQL() throws Exception {
		PostgreSQL.start();
		final PostgreSQLDatabaseConnector connector = new PostgreSQLDatabaseConnector(PostgreSQL.USER,
				PostgreSQL.PASS,
				"localhost",
				PostgreSQL.getPort());
		this.runMigrationTest(connector, "pclib_migration_postgres_" + System.nanoTime(), () -> {
		});
	}

	@Test
	void migrationsAddFillAndRemoveColumnsOnSQLite() throws Exception {
		final Path dir = SQLite.createTempDirectory().resolve("migration-" + System.nanoTime());
		Files.createDirectories(dir);
		final SQLiteDatabaseConnector connector = new SQLiteDatabaseConnector(dir.toUri());
		this.runMigrationTest(connector, "pclib_migration_sqlite", () -> {
			try {
				SQLite.deleteDirectory(dir);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private int countAppliedMigrations(final Database database) throws SQLException {
		try (AbstractConnection connection = database.use();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT COUNT(*) FROM " + MigrationTest.quote(database.getConnector(), database.getMigrationSchemaName()))) {
			Assertions.assertTrue(rs.next());
			return rs.getInt(1);
		}
	}

	private int countRows(final Database database, final String tableName) throws SQLException {
		try (AbstractConnection connection = database.use();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM "
						+ MigrationTest.tableName(database.getConnector(), database.getDatabaseName(), tableName))) {
			Assertions.assertTrue(rs.next());
			return rs.getInt(1);
		}
	}

	private String fullNameByFirstName(final Database database, final String firstNameValue) throws SQLException {
		try (AbstractConnection connection = database.use();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT " + MigrationTest.quote(database.getConnector(), "full_name") + " FROM "
						+ MigrationTest.tableName(database.getConnector(), database.getDatabaseName(), MigrationTest.TABLE_NAME) + " WHERE "
						+ MigrationTest.quote(database.getConnector(), "first_name") + " = '" + firstNameValue + "'")) {
			Assertions.assertTrue(rs.next());
			return rs.getString(1);
		}
	}

	private boolean hasColumn(final Database database, final String tableName, final String columnName) throws SQLException {
		try (AbstractConnection connection = database.use()) {

//			if (database.getConnector() instanceof PostgreSQLDatabaseConnector) {
//				final PostgreSQLDatabaseConnector co = ((PostgreSQLDatabaseConnector) database.getConnector());
//				try (Connection conn = DriverManager.getConnection("jdbc:postgresql://" + co.getHost() + ":" + co.getPort() + "/"
//						+ co.getMaintenanceDatabase(), co.getUsername(), co.getPassword())) {
//					System.out.println("=== DATABASES ===");
//
//					try (Statement st = conn.createStatement();
//							ResultSet rs = st.executeQuery("SELECT datname FROM pg_database WHERE datistemplate = false")) {
//
//						while (rs.next()) {
//							final String dbName = rs.getString(1);
//							System.out.println("- " + dbName);
//
//							final DatabaseMetaData meta = conn.getMetaData();
//							try (ResultSet tables = meta.getTables(null, "public", "%", new String[] { "TABLE" })) {
//
//								while (tables.next()) {
//									final String table = tables.getString("TABLE_NAME");
//									System.out.println("    - " + table);
//								}
//							}
//						}
//					}
//				}
//			} else if (database.getConnector() instanceof MySQLDatabaseConnector) {
//				final MySQLDatabaseConnector co = (MySQLDatabaseConnector) database.getConnector();
//				try (Connection conn = DriverManager
//						.getConnection("jdbc:mysql://" + co.getHost() + ":" + co.getPort() + "/", co.getUsername(), co.getPassword())) {
//					System.out.println("=== DATABASES ===");
//
//					try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SHOW DATABASES")) {
//						while (rs.next()) {
//							final String dbName = rs.getString(1);
//
//							if (dbName.equals("information_schema") || dbName.equals("mysql") || dbName.equals("performance_schema")
//									|| dbName.equals("sys")) {
//								continue;
//							}
//
//							System.out.println("Tables in " + dbName + ":");
//
//							try (Connection dbConn = DriverManager.getConnection("jdbc:mysql://" + co.getHost() + ":" + co.getPort() + "/"
//									+ dbName, co.getUsername(), co.getPassword())) {
//
//								DatabaseMetaData meta = dbConn.getMetaData();
//								try (ResultSet tables = meta.getTables(dbName, null, "%", new String[] { "TABLE" })) {
//
//									while (tables.next()) {
//
//										final String table = tables.getString("TABLE_NAME");
//										System.out.println("    - " + table);
//
//										try (ResultSet cols = meta.getColumns(dbName, null, table, "%")) {
//
//											while (cols.next()) {
//												final String colName = cols.getString("COLUMN_NAME");
//												final String type = cols.getString("TYPE_NAME");
//
//												System.out.println("        - " + colName + " (" + type + ")");
//											}
//										}
//									}
//								}
//							}
//						}
//					}
//				}
//			}

			final DatabaseMetaData metaData = connection.getMetaData();
			final String catalog = MigrationTest.isMySQL(database.getConnector()) ? connection.getCatalog() : null;
			final String schema = database.getConnector() instanceof PostgreSQLDatabaseConnector ? "public" : null;
//			System.err.println(metaData);
//			System.err.println(catalog);
//			System.err.println(schema);
//			System.err.println(tableName);
			try (ResultSet rs = metaData.getColumns(catalog, schema, tableName, "%")) {
				while (rs.next()) {
					if (columnName.equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
						return true;
					}
				}
				return false;
			}
		}
	}

	private void runMigrationTest(final DatabaseConnector connector, final String databaseName, final Runnable cleanup) throws Exception {
		final Database database = new Database(connector, databaseName);
		database.setMigrationSchemaName("migration_test_schema_migrations");

		try {
			database.clearBeans().scanFromBeans();
			Assertions.assertTrue(database.create().created(), "Database should be created before migrations run.");

			final InitialMigrationPersonTable initialTable = new InitialMigrationPersonTable(database);
			final AddedMigrationPersonTable addedTable = new AddedMigrationPersonTable(database);
			final FinalMigrationPersonTable finalTable = new FinalMigrationPersonTable(database);

			database.clearBeans().register(initialTable).scanFromBeans();
			Assertions.assertTrue(initialTable.create().created(), "Initial table should be created before migrations run.");
			initialTable.insert(new InitialMigrationPersonData("Ada", "Lovelace", "legacy-a"));
			initialTable.insert(new InitialMigrationPersonData("Grace", "Hopper", "legacy-b"));

			Assertions.assertFalse(this.hasColumn(database, MigrationTest.TABLE_NAME, "full_name"));
			Assertions.assertTrue(this.hasColumn(database, MigrationTest.TABLE_NAME, "obsolete_note"));

//			try (AbstractConnection con = connector.use();
//					Statement stmt = con.createStatement();
//					ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + initialTable.getQualifiedName() + ";")) {
//				rs.next();
//				PCUtils.asMap(rs).entrySet().forEach(System.out::println);
//			}

			database.clearBeans().register(addedTable).scanFromBeans();
			database.clearBeans().register(finalTable).scanFromBeans();
			database.clearBeans();
			database.migrate(Arrays.asList(new AddFullNameColumnMigration(addedTable),
					new FillFullNameMigration(),
					new RemoveObsoleteNoteColumnMigration(finalTable)));

//			try (AbstractConnection con = connector.use();
//					Statement stmt = con.createStatement();
//					ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + initialTable.getQualifiedName() + ";")) {
//				rs.next();
//				PCUtils.asMap(rs).entrySet().forEach(System.out::println);
//			}

			Assertions.assertTrue(this.hasColumn(database, MigrationTest.TABLE_NAME, "full_name"));
			Assertions.assertFalse(this.hasColumn(database, MigrationTest.TABLE_NAME, "obsolete_note"));
			Assertions.assertEquals(2, this.countRows(database, MigrationTest.TABLE_NAME));
			Assertions.assertEquals("Ada Lovelace", this.fullNameByFirstName(database, "Ada"));
			Assertions.assertEquals("Grace Hopper", this.fullNameByFirstName(database, "Grace"));
			Assertions.assertEquals(3, this.countAppliedMigrations(database));
		} finally {
			try {
				database.clearBeans().scanFromBeans();
				database.drop();
			} finally {
				connector.reset();
				cleanup.run();
			}
		}
	}

}
