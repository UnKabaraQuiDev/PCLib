package lu.kbra.pclib.db.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.migration.DataBaseMigration;
import lu.kbra.pclib.db.migration.SchemaMigrationOptions;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DataBaseTableStatus;
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.db.view.DataBaseViewStatus;

public class DataBaseInitializer implements SmartInitializingSingleton {

	protected static final Logger LOGGER = Logger.getLogger(DataBaseInitializer.class.getSimpleName());

	protected ApplicationContext context;
	protected PCLibDBProperties properties;

	public DataBaseInitializer(final ApplicationContext context, final PCLibDBProperties properties) {
		this.context = context;
		this.properties = properties;
	}

	public void keepAlive() {
		this.context.getBeansOfType(DataBase.class).values().forEach(c -> {
			if (c.getConnector() == null || c.getConnector().getDatabase() == null) {
//				LOGGER.info("Connection not initialized for: " + c.getDataBaseName());
			} else if (c.getConnector() != null && c.getConnector().getDatabase() != null && c.getConnector().keepAlive(5)) {
				DataBaseInitializer.LOGGER.warning("Connection reset for: " + c.getConnector().getDatabase());
			} else {
//				LOGGER.info("Connection still valid for: " + c.getConnector().getDatabase());
			}
		});
	}

	@Override
	public void afterSingletonsInstantiated() {
		final Collection<DataBaseMigration> allMigrations = this.context.getBeansOfType(DataBaseMigration.class).values();
		final Collection<SQLQueryable> allSQLQueryable = this.context.getBeansOfType(SQLQueryable.class).values();

		for (final Map.Entry<String, DataBase> entry : this.context.getBeansOfType(DataBase.class).entrySet()) {
			final String dbBeanName = entry.getKey();
			final DataBase database = entry.getValue();
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
				DataBaseInitializer.LOGGER.info("Created database: " + database.getDataBaseName());
			} catch (final Exception e) {
				throw new DBException(database.getConnector().getURI().toString(), e);
			}

			for (final SQLQueryable<?> instance : dependencyOrder) {
				if (instance instanceof final AbstractDBTable<?> table) {
					final DataBaseTableStatus<?, ?> status = table.create();
					if (status.created()) {
						DataBaseInitializer.LOGGER.info("Created table: " + table.getQualifiedName());
					} else if (status.existed()) {
						DataBaseInitializer.LOGGER.info("Table existed: " + table.getQualifiedName());
					} else {
						DataBaseInitializer.LOGGER.info("Couldn't create table: " + table.getQualifiedName());
					}
				} else if (instance instanceof final AbstractDBView<?> view) {
					final DataBaseViewStatus<?, ?> status = view.create();
					if (status.created()) {
						DataBaseInitializer.LOGGER.info("Created view: " + view.getQualifiedName());
					} else if (status.existed()) {
						DataBaseInitializer.LOGGER.info("View existed: " + view.getQualifiedName());
					} else {
						DataBaseInitializer.LOGGER.info("Couldn't create view: " + view.getQualifiedName());
					}
				} else {
					DataBaseInitializer.LOGGER.warning("Unknown SQLQueryable type: " + instance.getClass());
				}

				if (instance instanceof final DeferredSQLQueryable<?> table) {
					table.getInterceptor().build(table);
				}
			}

			// -- migrations
			final List<DataBaseMigration> migrations = allMigrations.stream().sorted((a, b) -> {
				final int order = Integer.compare(a.order(), b.order());
				return order != 0 ? order : a.name().compareTo(b.name());
			}).toList();

			final boolean autoMigrate = this.properties.isAutoMigrate(connector);
			final boolean autoAddColumns = this.properties.isAutoAddColumns(connector);
			final boolean autoRemoveColumns = this.properties.isAutoRemoveColumns(connector);

			if (!autoMigrate || migrations.isEmpty()) {
				DataBaseInitializer.LOGGER
						.info("Skipping migration: " + database.getDataBaseName() + " (" + migrations.size() + " available)");
				continue;
			}

			try {
				final Collection<AbstractDBTable<?>> tables = dependencyOrder.stream()
						.filter(AbstractDBTable.class::isInstance)
						.<AbstractDBTable<?>>map(AbstractDBTable.class::cast)
						.collect(Collectors.toCollection(ArrayList::new));
				tables.forEach(c -> System.err.println(c.getName()));
				final int appliedCount = database
						.migrate(migrations, tables, new SchemaMigrationOptions(autoAddColumns, autoRemoveColumns));
				DataBaseInitializer.LOGGER
						.info("Migrated: " + database.getDataBaseName() + " (" + appliedCount + "/" + migrations.size() + " applied)");
			} catch (final DBException e) {
				throw new DBException("Failed to migrate database " + database.getDataBaseName() + ".", e);
			}
		}

	}

}
