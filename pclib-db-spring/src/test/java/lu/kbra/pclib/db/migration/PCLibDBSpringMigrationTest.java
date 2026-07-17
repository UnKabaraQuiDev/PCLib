package lu.kbra.pclib.db.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.config.DatabaseInitializerAutoConfig;
import lu.kbra.pclib.db.config.PCLibDBAutoConfiguration;
import lu.kbra.pclib.db.config.PCLibDBRegistrarAutoConfiguration;
import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.table.AbstractDBTable;

import sqlite.SQLite;

public class PCLibDBSpringMigrationTest {

	@Test
	@Disabled
	void componentMigrationsAddFillAndRemoveColumns() throws IOException {
		final Path dir = Files.createTempDirectory(SQLite.createTempDirectory(), "spring-migration-");
		final String dbName = MigrationTestConstants.DATABASE_PREFIX + System.nanoTime();

		try {
			new ApplicationContextRunner()
					.withInitializer(context -> AutoConfigurationPackages.register((BeanDefinitionRegistry) context,
							PCLibDBSpringMigrationTest.class.getPackageName()))
					.withUserConfiguration(MigrationSpringTestConfiguration.class)
					.withConfiguration(AutoConfigurations.of(PCLibDBAutoConfiguration.class,
							PCLibDBRegistrarAutoConfiguration.class,
							DatabaseInitializerAutoConfig.class,
							ConfigurationPropertiesAutoConfiguration.class))
					.withBean(ApplicationConversionService.class, ApplicationConversionService::new)
					.withBean(ObjectMapper.class, ObjectMapper::new)
					.withPropertyValues("pclib.db.enabled=true",
							"pclib.db.autoCreate=true",
							"pclib.db.autoMigrate=true",
							"pclib.db.autoRemoveColumns=true",
							"pclib.db.autoAddColumns=true",
							"pclib.db.schemaName=spring_migration_test_schema_migrations",
							"pclib.db.migration.qualifier=migrationDb",
							"pclib.db.migration.protocol=sqlite",
							"pclib.db.migration.name=" + dbName,
							"pclib.db.migration.dir-path=" + dir.toAbsolutePath())
					.run(context -> {
						Assertions.assertThat(context).hasNotFailed();
						Assertions.assertThat(context.getBeansOfType(DatabaseMigration.class))
								.containsOnlyKeys("addFullNameColumnMigration",
										"fillFullNameMigration",
										"removeObsoleteNoteColumnMigration");

						final Database database = context.getBean("migrationDb", Database.class);

						Assertions.assertThat(this.hasColumn(database, MigrationTestConstants.TABLE_NAME, "full_name")).isTrue();
						Assertions.assertThat(this.hasColumn(database, MigrationTestConstants.TABLE_NAME, "obsolete_note")).isFalse();
						Assertions.assertThat(this.countAppliedMigrations(database)).isEqualTo(3);
						Assertions.assertThat(this.countRows(database, MigrationTestConstants.TABLE_NAME)).isZero();

						this.dropAll(context.getBeansOfType(AbstractDBTable.class), context.getBeansOfType(Database.class));
					});
		} finally {
			SQLite.deleteDirectory(dir);
		}
	}

	@Test
	@Disabled
	void componentMigrationsCanFillExistingRowsCreatedBeforeSpringMigrateRuns() throws IOException {
		final Path dir = Files.createTempDirectory(SQLite.createTempDirectory(), "spring-migration-existing-");
		final String dbName = MigrationTestConstants.DATABASE_PREFIX + System.nanoTime();

		try {
			new ApplicationContextRunner()
					.withInitializer(context -> AutoConfigurationPackages.register((BeanDefinitionRegistry) context,
							"lu.kbra.pclib.db.migrationtest"))
					.withUserConfiguration(MigrationSpringTestConfiguration.class)
					.withConfiguration(AutoConfigurations.of(PCLibDBAutoConfiguration.class,
							PCLibDBRegistrarAutoConfiguration.class,
							DatabaseInitializerAutoConfig.class,
							ConfigurationPropertiesAutoConfiguration.class))
					.withBean(ApplicationConversionService.class, ApplicationConversionService::new)
					.withBean(ObjectMapper.class, ObjectMapper::new)
					.withPropertyValues("pclib.db.enabled=true",
							"pclib.db.autoCreate=true",
							"pclib.db.autoMigrate=false",
							"pclib.db.schemaName=spring_migration_test_schema_migrations",
							"pclib.db.migration.qualifier=migrationDb",
							"pclib.db.migration.protocol=sqlite",
							"pclib.db.migration.name=" + dbName,
							"pclib.db.migration.dir-path=" + dir.toAbsolutePath())
					.run(context -> {
						Assertions.assertThat(context).hasNotFailed();

						final Database database = context.getBean("migrationDb", Database.class);
						final MigrationPersonInitialTable table = context.getBean(MigrationPersonInitialTable.class);
						table.insert(new MigrationPersonInitialData("Ada", "Lovelace", "legacy-a"));
						table.insert(new MigrationPersonInitialData("Grace", "Hopper", "legacy-b"));

						final int appliedCount = database.migrate(context.getBeansOfType(DatabaseMigration.class).values(),
								SchemaMigrationOptions.ADD_AND_REMOVE);

						Assertions.assertThat(this.hasColumn(database, MigrationTestConstants.TABLE_NAME, "full_name")).isTrue();
						Assertions.assertThat(this.hasColumn(database, MigrationTestConstants.TABLE_NAME, "obsolete_note")).isFalse();
						Assertions.assertThat(this.fullNameByFirstName(database, "Ada")).isEqualTo("Ada Lovelace");
						Assertions.assertThat(this.fullNameByFirstName(database, "Grace")).isEqualTo("Grace Hopper");
						Assertions.assertThat(appliedCount).isEqualTo(3);
						Assertions.assertThat(this.countAppliedMigrations(database)).isEqualTo(3);

						this.dropAll(context.getBeansOfType(AbstractDBTable.class), context.getBeansOfType(Database.class));
					});
		} finally {
			SQLite.deleteDirectory(dir);
		}
	}

	private int countAppliedMigrations(final Database database) throws SQLException {
		try (AbstractConnection connection = database.use();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt
						.executeQuery("SELECT COUNT(*) FROM " + this.quote(database.getConnector(), database.getMigrationSchemaName()))) {
			Assertions.assertThat(rs.next()).isTrue();
			return rs.getInt(1);
		}
	}

	private int countRows(final Database database, final String tableName) throws SQLException {
		try (AbstractConnection connection = database.use();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(
						"SELECT COUNT(*) FROM " + this.tableName(database.getConnector(), database.getDatabaseName(), tableName))) {
			Assertions.assertThat(rs.next()).isTrue();
			return rs.getInt(1);
		}
	}

	private void dropAll(final Map<String, AbstractDBTable> map, final Map<String, Database> databases) {
		map.values().forEach(table -> {
			try {
				table.drop();
			} catch (final RuntimeException ignored) {
			}
		});
		databases.values().forEach(db -> {
			try {
				db.drop();
			} catch (final RuntimeException ignored) {
			}
		});
	}

	private String fullNameByFirstName(final Database database, final String firstNameValue) throws SQLException {
		try (AbstractConnection connection = database.use();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT " + this.quote(database.getConnector(), "full_name") + " FROM "
						+ this.tableName(database.getConnector(), database.getDatabaseName(), MigrationTestConstants.TABLE_NAME) + " WHERE "
						+ this.quote(database.getConnector(), "first_name") + " = '" + firstNameValue + "'")) {
			Assertions.assertThat(rs.next()).isTrue();
			return rs.getString(1);
		}
	}

	private boolean hasColumn(final Database database, final String tableName, final String columnName) throws SQLException {
		try (AbstractConnection connection = database.use()) {
			final DatabaseMetaData metaData = connection.getMetaData();
			try (ResultSet rs = metaData.getColumns(connection.getCatalog(), connection.getSchema(), tableName, null)) {
				while (rs.next()) {
					if (columnName.equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
						return true;
					}
				}
				return false;
			}
		}
	}

	private boolean isMySQL(final DatabaseConnector connector) {
		return "mysql".equalsIgnoreCase(connector.getProtocol());
	}

	private String quote(final DatabaseConnector connector, final String identifier) {
		if (this.isMySQL(connector)) {
			return "`" + identifier.replace("`", "``") + "`";
		}
		return "\"" + identifier.replace("\"", "\"\"") + "\"";
	}

	private String tableName(final DatabaseConnector connector, final String databaseName, final String tableName) {
		if (this.isMySQL(connector)) {
			return this.quote(connector, databaseName) + "." + this.quote(connector, tableName);
		}
		return this.quote(connector, tableName);
	}

}
