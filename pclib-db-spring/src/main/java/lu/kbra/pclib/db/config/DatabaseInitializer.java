package lu.kbra.pclib.db.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.function.BiConsumer;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.exception.CreationFailedException;
import lu.kbra.pclib.db.exception.MigrationFailedException;
import lu.kbra.pclib.db.exception.ScanFailedException;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.migration.DatabaseMigration;
import lu.kbra.pclib.db.migration.MigratedPhase;
import lu.kbra.pclib.db.migration.MigrationOption;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.view.AbstractDBView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseInitializer implements SmartInitializingSingleton {

	protected ApplicationContext context;
	protected PCLibDBProperties properties;

	public DatabaseInitializer(final ApplicationContext context, final PCLibDBProperties properties) {
		this.context = context;
		this.properties = properties;
	}

	public void keepAlive() {
		this.context.getBeansOfType(Database.class).values().forEach(c -> {
			if (c.getConnector() == null || c.getConnector().getDatabase() == null) {
//				log.info("Connection not initialized for: " + c.getDatabaseName());
			} else if (c.getConnector() != null && c.getConnector().getDatabase() != null && c.getConnector().keepAlive(5)) {
				DatabaseInitializer.log.warn("Connection reset for: {}", c.getConnector().getDatabase());
			} else {
//				log.info("Connection still valid for: " + c.getConnector().getDatabase());
			}
		});
	}

	@Override
	public void afterSingletonsInstantiated() {
		final Collection<DatabaseMigration> allMigrations = this.context.getBeansOfType(DatabaseMigration.class).values();
		final Collection<SQLQueryable> allSQLQueryable = this.context.getBeansOfType(SQLQueryable.class).values();

		for (final Map.Entry<String, Database> entry : this.context.getBeansOfType(Database.class).entrySet()) {
			final String dbBeanName = entry.getKey();
			final Database database = entry.getValue();
			final PCLibDBProperties.Connector connector = this.properties.getRequiredConnector(dbBeanName);

			final String migrationSchemaName = this.properties.getMigrationSchemaName(connector);
			if (migrationSchemaName != null && !migrationSchemaName.isBlank()) {
				database.setMigrationSchemaName(migrationSchemaName);
			}

			if (!this.properties.isAutoCreate(connector)) {
				continue;
			}

			final boolean autoMigrate = this.properties.isAutoMigrate(connector);
			final boolean autoAddColumns = this.properties.isAutoAddColumns(connector);
			final boolean autoRemoveColumns = this.properties.isAutoRemoveColumns(connector);
			final boolean autoRemoveTables = this.properties.isAutoRemoveTables(connector);

			final List<SQLQueryable> instances;
			try {
				// -- creation
				instances = allSQLQueryable.stream().filter(c -> c.getDatabase() == database).toList();
				database.clearBeans();
				database.setMigrationSupport(autoMigrate).initMigrationSupport();
				instances.forEach(database::register);
				database.scanFromBeans();
			} catch (final Exception e) {
				throw new ScanFailedException("Scan failed for database: " + database.getDatabaseName() + " registered as: " + dbBeanName,
						e);
			}

			try {
				database.create();
				DatabaseInitializer.log.info("Created database: {}", database.getDatabaseName());
			} catch (final Exception e) {
				throw new CreationFailedException(database.getConnector().getURI().toString(), e);
			}

			// -- migrations
			final List<DatabaseMigration> migrations = new ArrayList<>(allMigrations);

			final BiConsumer<Object, Boolean> printer = (t, b) -> {
				if (t instanceof final AbstractDBTable<?> table && !table.getStructure().isInternal()
						&& !table.getStructure().isSynthetic()) {
					DatabaseInitializer.log.info((b ? "Created table: " : "Table existed: ") + table.getName());
				} else if (t instanceof final AbstractDBView<?> view && !view.getStructure().isInternal()
						&& !view.getStructure().isSynthetic()) {
					DatabaseInitializer.log.info((b ? "Created view: " : "View existed: ") + view.getName());
				} else if (t instanceof final MigratedPhase phase && b) {
					DatabaseInitializer.log.info("Executed phase: {} > {}", phase.getMigration().id(), phase.getPhase().id());
				}
			};

			if (!autoMigrate) {
				database.createBeans(printer);
				DatabaseInitializer.log.info("Skipping migration: {} ({} available)", database.getDatabaseName(), migrations.size());
				continue;
			}

			try {
				final EnumSet<MigrationOption> allowedOptions = EnumSet.noneOf(MigrationOption.class);
				if (autoAddColumns) {
					allowedOptions.add(MigrationOption.ADD_COLUMNS);
				}
				if (autoRemoveColumns) {
					allowedOptions.add(MigrationOption.REMOVE_COLUMNS);
				}
				if (autoRemoveTables) {
					allowedOptions.add(MigrationOption.REMOVE_TABLES);
				}

				final OptionalInt appliedCount = database.migrate(migrations, printer, allowedOptions);

				for (final SQLQueryable<?> instance : instances) {
					if (instance instanceof final DeferredSQLQueryable<?> table) {
						if (table.getInterceptor() == null) {
							throw new IllegalStateException(
									"DeferredSQLQueryable QueryMethodInterceptor is null, did you forget to make it abstract again ?");
						}
						table.getInterceptor().build(table);
					}
				}

				DatabaseInitializer.log.info("Migrated: {} ({}/{} applied)",
						database.getDatabaseName(),
						appliedCount.isEmpty() ? "x" : appliedCount.getAsInt(),
						migrations.stream().mapToInt(c -> c.phases().length).sum());
			} catch (final Exception e) {
				throw new MigrationFailedException("Failed to migrate database " + database.getDatabaseName() + ".", e);
			}
		}
	}

}
