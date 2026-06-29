package lu.kbra.pclib.db.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;

import lu.kbra.pclib.datastructure.tree.dependency.DependencyResolver;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.migration.DataBaseMigration;
import lu.kbra.pclib.db.migration.SchemaMigrationOptions;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DataBaseTableStatus;
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.db.view.DataBaseViewStatus;

public class DataBaseInitializer implements SmartInitializingSingleton {

	protected static final Logger LOGGER = Logger.getLogger(DataBaseInitializer.class.getSimpleName());

	public static <B extends SQLQueryable<?>> List<B>
			getInDependencyOrder(final Class<B> clazz, final ApplicationContext context, final DataBase db) {
		final ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) context.getAutowireCapableBeanFactory();

		final Map<String, B> tableBeans = context.getBeansOfType(clazz)
				.entrySet()
				.parallelStream()
				.filter(e -> e.getValue().getDatabase().equals(db))
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

		final Map<String, Set<String>> dependencies = new HashMap<>();
		for (final String name : tableBeans.keySet()) {
			final BeanDefinition bd = beanFactory.getBeanDefinition(name);
			final String[] dependsOn = bd.getDependsOn();
			dependencies.put(name, dependsOn != null ? new HashSet<>(Arrays.asList(dependsOn)) : new HashSet<>());
		}

		return new DependencyResolver<>(tableBeans.entrySet(), e -> dependencies.get(e.getKey()), Entry::getKey).resolve()
				.parallelStream()
				.map(Entry::getValue)
				.toList();
	}

	protected ApplicationContext context;
	protected PCLibDBProperties properties;

	public DataBaseInitializer(ApplicationContext context, PCLibDBProperties properties) {
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

			try {
				database.create();
				DataBaseInitializer.LOGGER.info("Created database: " + database.getDataBaseName());
			} catch (final Exception e) {
				throw new DBException(database.getConnector().getURI().toString(), e);
			}

			for (final SQLQueryable<?> instance : DataBaseInitializer.getInDependencyOrder(SQLQueryable.class, this.context, database)) {
				if (instance instanceof final AbstractDBTable<?> table) {
					final DataBaseTableStatus<?, ?> status = table.create();
					if (status.created() || status.existed()) {
						DataBaseInitializer.LOGGER.info("Created table: " + table.getQualifiedName());
					} else {
						DataBaseInitializer.LOGGER.info("Couldn't create table: " + table.getQualifiedName());
					}
				} else if (instance instanceof final AbstractDBView<?> view) {
					final DataBaseViewStatus<?, ?> status = view.create();
					if (status.created() || status.existed()) {
						DataBaseInitializer.LOGGER.info("Created view: " + view.getQualifiedName());
					} else {
						DataBaseInitializer.LOGGER.info("Couldn't create view: " + view.getQualifiedName());
					}
				} else {
					DataBaseInitializer.LOGGER.warning("Unknown SQLQueryable type: " + instance + " (" + instance.getClass() + ")");
				}
			}

			this.runMigrations(dbBeanName, connector, database, allMigrations);
		}

	}

	private void runMigrations(
			final String dbBeanName,
			final PCLibDBProperties.Connector connector,
			final DataBase database,
			final Collection<DataBaseMigration> allMigrations) {
		final List<DataBaseMigration> migrations = allMigrations.stream().sorted((a, b) -> {
			final int order = Integer.compare(a.order(), b.order());
			return order != 0 ? order : a.name().compareTo(b.name());
		}).toList();

		final boolean autoMigrate = this.properties.isAutoMigrate(connector);
		final boolean autoAddColumns = this.properties.isAutoAddColumns(connector);
		final boolean autoRemoveColumns = this.properties.isAutoRemoveColumns(connector);

		if (!autoMigrate || migrations.isEmpty()) {
			DataBaseInitializer.LOGGER.info("Skipping migration: " + database.getDataBaseName() + " (" + migrations.size() + " available)");
			return;
		}

		try {
			final List<AbstractDBTable> tables = DataBaseInitializer.getInDependencyOrder(AbstractDBTable.class, this.context, database);
			database.migrate(migrations, tables, new SchemaMigrationOptions(autoAddColumns, autoRemoveColumns));
			DataBaseInitializer.LOGGER.info("Migrated: " + database.getDataBaseName() + " (" + migrations.size() + " applied)");
		} catch (final DBException e) {
			throw new DBException("Failed to migrate database " + database.getDataBaseName() + ".", e);
		}
	}

}
