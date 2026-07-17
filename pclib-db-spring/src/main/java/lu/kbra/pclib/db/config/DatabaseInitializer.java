package lu.kbra.pclib.db.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.exception.CreationFailedException;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.MigrationFailedException;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.migration.DatabaseMigration;
import lu.kbra.pclib.db.migration.SchemaMigrationOptions;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DatabaseTableStatus;
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.db.view.DatabaseViewStatus;

public class DatabaseInitializer implements SmartInitializingSingleton {

	protected static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getSimpleName());

	protected ApplicationContext context;
	protected PCLibDBProperties properties;

	public DatabaseInitializer(final ApplicationContext context, final PCLibDBProperties properties) {
		this.context = context;
		this.properties = properties;
	}

	public void keepAlive() {
		this.context.getBeansOfType(Database.class).values().forEach(c -> {
			if (c.getConnector() == null || c.getConnector().getDatabase() == null) {
//				LOGGER.info("Connection not initialized for: " + c.getDatabaseName());
			} else if (c.getConnector() != null && c.getConnector().getDatabase() != null && c.getConnector().keepAlive(5)) {
				DatabaseInitializer.LOGGER.warning("Connection reset for: " + c.getConnector().getDatabase());
			} else {
//				LOGGER.info("Connection still valid for: " + c.getConnector().getDatabase());
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

			final String schemaName = this.properties.getMigrationSchemaName(connector);
			if (schemaName != null && !schemaName.isBlank()) {
				database.setMigrationSchemaName(schemaName);
			}

			if (!this.properties.isAutoCreate(connector)) {
				continue;
			}

			// -- creation
			final List<SQLQueryable> instances = allSQLQueryable.stream().filter(c -> c.getDatabase() == database).toList();
			database.clearBeans();
			instances.forEach(database::register);
			database.scanFromBeans();
			final List<? extends SQLQueryable<?>> dependencyOrder = database.getStructure().getDependencyTree().toList();

			try {
				database.create();
				DatabaseInitializer.LOGGER.info("Created database: " + database.getDatabaseName());
			} catch (final Exception e) {
				throw new CreationFailedException(database.getConnector().getURI().toString(), e);
			}

			for (final SQLQueryable<?> instance : dependencyOrder) {
				if (instance instanceof final AbstractDBTable<?> table) {
					final DatabaseTableStatus<?, ?> status = table.create();
					if (status.created()) {
						DatabaseInitializer.LOGGER.info("Created table: " + table.getQualifiedName());
					} else if (status.existed()) {
						DatabaseInitializer.LOGGER.info("Table existed: " + table.getQualifiedName());
					} else {
						DatabaseInitializer.LOGGER.info("Couldn't create table: " + table.getQualifiedName());
					}
				} else if (instance instanceof final AbstractDBView<?> view) {
					final DatabaseViewStatus<?, ?> status = view.create();
					if (status.created()) {
						DatabaseInitializer.LOGGER.info("Created view: " + view.getQualifiedName());
					} else if (status.existed()) {
						DatabaseInitializer.LOGGER.info("View existed: " + view.getQualifiedName());
					} else {
						DatabaseInitializer.LOGGER.info("Couldn't create view: " + view.getQualifiedName());
					}
				} else {
					DatabaseInitializer.LOGGER.warning("Unknown SQLQueryable type: " + instance.getClass());
				}

				if (instance instanceof final DeferredSQLQueryable<?> table) {
					table.getInterceptor().build(table);
				}
			}

			// -- migrations
			final List<DatabaseMigration> migrations = allMigrations.stream().sorted((a, b) -> {
				final int order = Integer.compare(a.order(), b.order());
				return order != 0 ? order : a.name().compareTo(b.name());
			}).toList();

			final boolean autoMigrate = this.properties.isAutoMigrate(connector);
			final boolean autoAddColumns = this.properties.isAutoAddColumns(connector);
			final boolean autoRemoveColumns = this.properties.isAutoRemoveColumns(connector);

			if (!autoMigrate || migrations.isEmpty()) {
				DatabaseInitializer.LOGGER
						.info("Skipping migration: " + database.getDatabaseName() + " (" + migrations.size() + " available)");
				continue;
			}

			try {
				final Collection<AbstractDBTable<?>> tables = dependencyOrder.stream()
						.filter(AbstractDBTable.class::isInstance)
						.<AbstractDBTable<?>>map(AbstractDBTable.class::cast)
						.collect(Collectors.toCollection(ArrayList::new));
				final int appliedCount = database
						.migrate(migrations, tables, new SchemaMigrationOptions(autoAddColumns, autoRemoveColumns));
				DatabaseInitializer.LOGGER
						.info("Migrated: " + database.getDatabaseName() + " (" + appliedCount + "/" + migrations.size() + " applied)");
			} catch (final DBException e) {
				throw new MigrationFailedException("Failed to migrate database " + database.getDatabaseName() + ".", e);
			}
		}

	}

}
