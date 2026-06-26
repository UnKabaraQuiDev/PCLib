package lu.kbra.pclib.db.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.migration.DataBaseMigration;
import lu.kbra.pclib.db.migration.SchemaMigrationOptions;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.view.AbstractDBView;

public class DataBaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

	protected static final Logger LOGGER = Logger.getLogger(DataBaseInitializer.class.getSimpleName());

	public static <T> List<T> getTablesInDependencyOrder(final Class<T> clazz, final ApplicationContext context) {
		final ConfigurableListableBeanFactory beanFactory = (ConfigurableListableBeanFactory) context.getAutowireCapableBeanFactory();

		final Map<String, T> tableBeans = context.getBeansOfType(clazz);

		final Map<String, Set<String>> dependencies = new HashMap<>();
		for (final String name : tableBeans.keySet()) {
			final BeanDefinition bd = beanFactory.getBeanDefinition(name);
			final String[] dependsOn = bd.getDependsOn();
			dependencies.put(name, dependsOn != null ? new HashSet<>(Arrays.asList(dependsOn)) : new HashSet<>());
		}

		final List<String> sortedNames = DataBaseInitializer.topologicalSort(dependencies);

		final List<T> sortedTables = new ArrayList<>();
		for (final String name : sortedNames) {
			sortedTables.add(tableBeans.get(name));
		}

		return sortedTables.stream().filter(Objects::nonNull).toList();
	}

	private static List<String> topologicalSort(final Map<String, Set<String>> deps) {
		final List<String> sorted = new ArrayList<>();
		final Set<String> visited = new HashSet<>();

		for (final String bean : deps.keySet()) {
			DataBaseInitializer.visit(bean, deps, visited, sorted, new HashSet<>());
		}

		return sorted;
	}

	private static void visit(
			final String bean,
			final Map<String, Set<String>> deps,
			final Set<String> visited,
			final List<String> sorted,
			final Set<String> stack) {
		if (visited.contains(bean)) {
			return;
		}

		if (stack.contains(bean)) {
			throw new IllegalStateException("Circular dependency detected: " + bean);
		}

		stack.add(bean);
		for (final String dep : deps.getOrDefault(bean, Collections.emptySet())) {
			DataBaseInitializer.visit(dep, deps, visited, sorted, stack);
		}
		stack.remove(bean);

		visited.add(bean);
		sorted.add(bean);
	}

	protected ApplicationContext context;
	private PCLibDBProperties properties;

	public DataBaseInitializer() {
	}

	public DataBaseInitializer(final PCLibDBProperties properties) {
		this.properties = properties;
	}

	private void applyMigrationSchemaName(final String beanName, final DataBase dataBase) {
		if (this.properties == null || dataBase == null) {
			return;
		}
		final PCLibDBProperties.Connector connector = this.findConnector(beanName, dataBase);
		final String schemaName = connector == null ? this.properties.getMigrationSchemaName()
				: this.properties.getMigrationSchemaName(connector);
		if (schemaName != null && !schemaName.isBlank()) {
			dataBase.setMigrationSchemaName(schemaName);
		}
	}

	private PCLibDBProperties.Connector findConnector(final String beanName, final DataBase dataBase) {
		if (this.properties == null || dataBase == null) {
			return null;
		}
		for (final PCLibDBProperties.Connector connector : this.properties.getConnectors().values()) {
			if (connector.getQualifier().equals(beanName) || connector.getName().equals(dataBase.getDataBaseName())) {
				return connector;
			}
		}
		return null;
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
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		this.context = event.getApplicationContext();

		for (final Map.Entry<String, DataBase> entry : this.context.getBeansOfType(DataBase.class).entrySet()) {
			final DataBase db = entry.getValue();
			this.applyMigrationSchemaName(entry.getKey(), db);
			if (!this.shouldAutoCreate(entry.getKey(), db)) {
				continue;
			}
			try {
				db.create();
				DataBaseInitializer.LOGGER.info("Created: " + db.getDataBaseName());
			} catch (final Exception e) {
				throw new RuntimeException(db.getConnector().getURI().toString(), e);
			}
		}

		for (final AbstractDBTable<?> table : DataBaseInitializer.getTablesInDependencyOrder(AbstractDBTable.class, this.context)) {
			if (!this.shouldAutoCreate(table.getDatabase())) {
				continue;
			}
			table.create();
			DataBaseInitializer.LOGGER.info("Created table: " + table.getQualifiedName());
		}

		this.runMigrations();

		for (final AbstractDBView<?> view : DataBaseInitializer.getTablesInDependencyOrder(AbstractDBView.class, this.context)) {
			if (!this.shouldAutoCreate(view.getDatabase())) {
				continue;
			}
			view.create();
			DataBaseInitializer.LOGGER.info("Created view: " + view.getQualifiedName());
		}

	}

	private void runMigrations() {
		final List<DataBaseMigration> migrations = this.context.getBeansOfType(DataBaseMigration.class).values().stream().sorted((a, b) -> {
			final int order = Integer.compare(a.order(), b.order());
			return order != 0 ? order : a.name().compareTo(b.name());
		}).toList();

		for (final Map.Entry<String, DataBase> entry : this.context.getBeansOfType(DataBase.class).entrySet()) {
			final DataBase dataBase = entry.getValue();
			final PCLibDBProperties.Connector connector = this.findConnector(entry.getKey(), dataBase);
			final boolean autoMigrate = this.properties == null || connector == null
					? this.properties == null || this.properties.isAutoMigrate()
					: this.properties.isAutoMigrate(connector);
			final boolean autoAddColumns = this.properties != null
					&& (connector == null ? this.properties.isAutoAddColumns() : this.properties.isAutoAddColumns(connector));
			final boolean autoRemoveColumns = this.properties != null
					&& (connector == null ? this.properties.isAutoRemoveColumns() : this.properties.isAutoRemoveColumns(connector));

			if (!autoMigrate && !autoAddColumns && !autoRemoveColumns) {
				continue;
			}
			if (migrations.isEmpty() && !autoAddColumns && !autoRemoveColumns) {
				continue;
			}

			final List<DataBaseTable<? extends DataBaseEntry>> tables = DataBaseInitializer
					.getTablesInDependencyOrder(AbstractDBTable.class, this.context)
					.stream()
					.filter(DataBaseTable.class::isInstance)
					.map(DataBaseTable.class::cast)
					.filter(table -> table.getDatabase() == dataBase)
					.map(table -> (DataBaseTable<? extends DataBaseEntry>) table)
					.collect(Collectors.toList());

			try {
				dataBase.migrate(migrations, tables, new SchemaMigrationOptions(autoAddColumns, autoRemoveColumns));
				DataBaseInitializer.LOGGER.info("Migrated: " + dataBase.getDataBaseName());
			} catch (final DBException e) {
				throw new RuntimeException("Failed to migrate database " + dataBase.getDataBaseName() + ".", e);
			}
		}
	}

	private boolean shouldAutoCreate(final DataBase dataBase) {
		if (dataBase == null) {
			return true;
		}
		for (final Map.Entry<String, DataBase> entry : this.context.getBeansOfType(DataBase.class).entrySet()) {
			if (entry.getValue() == dataBase) {
				return this.shouldAutoCreate(entry.getKey(), dataBase);
			}
		}
		return true;
	}

	private boolean shouldAutoCreate(final String beanName, final DataBase dataBase) {
		if (this.properties == null) {
			return true;
		}
		for (final PCLibDBProperties.Connector connector : this.properties.getConnectors().values()) {
			if (connector.getQualifier().equals(beanName) || connector.getName().equals(dataBase.getDataBaseName())) {
				return this.properties.isAutoCreate(connector);
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "DataBaseInitializer@" + System.identityHashCode(this) + " []";
	}

}
