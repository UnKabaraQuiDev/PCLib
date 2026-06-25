import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.Nullable;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
import lu.kbra.pclib.db.autobuild.table.TableName;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;
import lu.kbra.pclib.db.connector.PostgreSQLDataBaseConnector;
import lu.kbra.pclib.db.connector.SQLiteDataBaseConnector;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.migration.DataBaseMigration;
import lu.kbra.pclib.db.migration.DataBaseSchemaMigrator;
import lu.kbra.pclib.db.migration.SchemaMigrationOptions;
import lu.kbra.pclib.db.table.DataBaseTable;

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
	public static class AddedMigrationPersonData implements DataBaseEntry {

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

	@TableName(MigrationTest.TABLE_NAME)
	public static class AddedMigrationPersonTable extends DataBaseTable<AddedMigrationPersonData> {

		public AddedMigrationPersonTable(final DataBase dataBase) {
			super(dataBase);
		}

	}

	private static final class AddFullNameColumnMigration implements DataBaseMigration {

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
		public void up(final DataBase dataBase, final Connection connection) throws DBException {
			new DataBaseSchemaMigrator(dataBase.getConnector())
					.migrate(connection, List.of(this.table), new SchemaMigrationOptions(true, false));
		}

	}

	private static final class FillFullNameMigration implements DataBaseMigration {

		@Override
		public String name() {
			return "fill_full_name";
		}

		@Override
		public int order() {
			return 2;
		}

		@Override
		public void up(final DataBase dataBase, final Connection connection) throws DBException {
			final String firstName = MigrationTest.quote(dataBase.getConnector(), "first_name");
			final String lastName = MigrationTest.quote(dataBase.getConnector(), "last_name");
			final String fullName = MigrationTest.quote(dataBase.getConnector(), "full_name");
			final String value = MigrationTest.isMySQL(dataBase.getConnector()) ? "CONCAT(" + firstName + ", ' ', " + lastName + ")"
					: firstName + " || ' ' || " + lastName;
			final String sql = "UPDATE " + MigrationTest.tableName(dataBase.getConnector(), dataBase.getDataBaseName()) + " SET " + fullName
					+ " = " + value;

			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate(sql);
			} catch (final SQLException e) {
				throw new DBException("Failed to fill full_name column.", e);
			}
		}

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FinalMigrationPersonData implements DataBaseEntry {

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

	@TableName(MigrationTest.TABLE_NAME)
	public static class FinalMigrationPersonTable extends DataBaseTable<FinalMigrationPersonData> {

		public FinalMigrationPersonTable(final DataBase dataBase) {
			super(dataBase);
		}

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class InitialMigrationPersonData implements DataBaseEntry {

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

	@TableName(MigrationTest.TABLE_NAME)
	public static class InitialMigrationPersonTable extends DataBaseTable<InitialMigrationPersonData> {

		public InitialMigrationPersonTable(final DataBase dataBase) {
			super(dataBase);
		}

	}

	private static final class RemoveObsoleteNoteColumnMigration implements DataBaseMigration {

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
		public void up(final DataBase dataBase, final Connection connection) throws DBException {
			new DataBaseSchemaMigrator(dataBase.getConnector())
					.migrate(connection, List.of(this.table), new SchemaMigrationOptions(false, true));
		}

	}

	private static final String TABLE_NAME = "migration_person";

	private static boolean isMySQL(final DataBaseConnector connector) {
		return "mysql".equalsIgnoreCase(connector.getProtocol());
	}

	private static String quote(final DataBaseConnector connector, final String identifier) {
		if (MigrationTest.isMySQL(connector)) {
			return "`" + identifier.replace("`", "``") + "`";
		}
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	private static String tableName(final DataBaseConnector connector, final String databaseName) {
		return MigrationTest.tableName(connector, databaseName, MigrationTest.TABLE_NAME);
	}

	private static String tableName(final DataBaseConnector connector, final String databaseName, final String tableName) {
		if (MigrationTest.isMySQL(connector)) {
			return MigrationTest.quote(connector, databaseName) + "." + MigrationTest.quote(connector, tableName);
		}
		return MigrationTest.quote(connector, tableName);
	}

	private int countAppliedMigrations(final DataBase dataBase) throws SQLException {
		try (Connection connection = dataBase.openConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT COUNT(*) FROM " + MigrationTest.quote(dataBase.getConnector(), dataBase.getMigrationSchemaName()))) {
			Assertions.assertTrue(rs.next());
			return rs.getInt(1);
		}
	}

	private int countRows(final DataBase dataBase, final String tableName) throws SQLException {
		try (Connection connection = dataBase.openConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM "
						+ MigrationTest.tableName(dataBase.getConnector(), dataBase.getDataBaseName(), tableName))) {
			Assertions.assertTrue(rs.next());
			return rs.getInt(1);
		}
	}

	private String fullNameByFirstName(final DataBase dataBase, final String firstNameValue) throws SQLException {
		try (Connection connection = dataBase.openConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT " + MigrationTest.quote(dataBase.getConnector(), "full_name") + " FROM "
						+ MigrationTest.tableName(dataBase.getConnector(), dataBase.getDataBaseName(), MigrationTest.TABLE_NAME) + " WHERE "
						+ MigrationTest.quote(dataBase.getConnector(), "first_name") + " = '" + firstNameValue + "'")) {
			Assertions.assertTrue(rs.next());
			return rs.getString(1);
		}
	}

	private boolean hasColumn(final DataBase dataBase, final String tableName, final String columnName) throws SQLException {
		try (Connection connection = dataBase.openConnection()) {
			final DatabaseMetaData metaData = connection.getMetaData();
			final String protocol = dataBase.getConnector().getProtocol();
			final String catalog = MigrationTest.isMySQL(dataBase.getConnector()) ? dataBase.getDataBaseName() : null;
			final String schema = "postgres".equalsIgnoreCase(protocol) ? "public" : null;
			try (ResultSet rs = metaData.getColumns(catalog, schema, tableName, null)) {
				while (rs.next()) {
					if (columnName.equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
						return true;
					}
				}
				return false;
			}
		}
	}

	@Test
	void migrationsAddFillAndRemoveColumnsOnMySQL() throws Exception {
		MySQL.start();
		final MySQLDataBaseConnector connector = new MySQLDataBaseConnector(MySQL.USER, MySQL.PASS, "localhost", MySQL.getPort());
		this.runMigrationTest(connector, "pclib_migration_mysql_" + System.nanoTime(), () -> {
		});
	}

	@Test
	void migrationsAddFillAndRemoveColumnsOnPostgreSQL() throws Exception {
		PostgreSQL.start();
		final PostgreSQLDataBaseConnector connector = new PostgreSQLDataBaseConnector(PostgreSQL.USER,
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
		final SQLiteDataBaseConnector connector = new SQLiteDataBaseConnector(dir.toString());
		this.runMigrationTest(connector, "pclib_migration_sqlite", () -> {
			try {
				SQLite.deleteDirectory(dir);
			} catch (final IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private void runMigrationTest(final DataBaseConnector connector, final String databaseName, final Runnable cleanup) throws Exception {
		final DataBase dataBase = new DataBase(connector, databaseName);
		dataBase.setMigrationSchemaName("migration_test_schema_migrations");

		try {
			Assertions.assertTrue(dataBase.create().created(), "Database should be created before migrations run.");

			final InitialMigrationPersonTable initialTable = new InitialMigrationPersonTable(dataBase);
			final AddedMigrationPersonTable addedTable = new AddedMigrationPersonTable(dataBase);
			final FinalMigrationPersonTable finalTable = new FinalMigrationPersonTable(dataBase);

			Assertions.assertTrue(initialTable.create().created(), "Initial table should be created before migrations run.");
			initialTable.insert(new InitialMigrationPersonData("Ada", "Lovelace", "legacy-a"));
			initialTable.insert(new InitialMigrationPersonData("Grace", "Hopper", "legacy-b"));

			Assertions.assertFalse(this.hasColumn(dataBase, MigrationTest.TABLE_NAME, "full_name"));
			Assertions.assertTrue(this.hasColumn(dataBase, MigrationTest.TABLE_NAME, "obsolete_note"));

//			try (AbstractConnection con = connector.use();
//					Statement stmt = con.createStatement();
//					ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + initialTable.getQualifiedName() + ";")) {
//				rs.next();
//				PCUtils.asMap(rs).entrySet().forEach(System.out::println);
//			}

			dataBase.migrate(List.of(new AddFullNameColumnMigration(addedTable),
					new FillFullNameMigration(),
					new RemoveObsoleteNoteColumnMigration(finalTable)));

//			try (AbstractConnection con = connector.use();
//					Statement stmt = con.createStatement();
//					ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE " + initialTable.getQualifiedName() + ";")) {
//				rs.next();
//				PCUtils.asMap(rs).entrySet().forEach(System.out::println);
//			}

			Assertions.assertTrue(this.hasColumn(dataBase, MigrationTest.TABLE_NAME, "full_name"));
			Assertions.assertFalse(this.hasColumn(dataBase, MigrationTest.TABLE_NAME, "obsolete_note"));
			Assertions.assertEquals(2, this.countRows(dataBase, MigrationTest.TABLE_NAME));
			Assertions.assertEquals("Ada Lovelace", this.fullNameByFirstName(dataBase, "Ada"));
			Assertions.assertEquals("Grace Hopper", this.fullNameByFirstName(dataBase, "Grace"));
			Assertions.assertEquals(3, this.countAppliedMigrations(dataBase));
		} finally {
			try {
				dataBase.drop();
			} finally {
				connector.reset();
				cleanup.run();
			}
		}
	}

}
