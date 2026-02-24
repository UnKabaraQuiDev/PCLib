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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.view.AbstractDBView;

public class DataBaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

	protected static final Logger LOGGER = Logger.getLogger(DataBaseInitializer.class.getSimpleName());

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		final ApplicationContext context = event.getApplicationContext();

		for (final DataBase db : context.getBeansOfType(DataBase.class).values()) {
			db.create();
			LOGGER.info("Created: " + db.getDataBaseName());
		}

		for (final AbstractDBTable<?> table : getTablesInDependencyOrder(AbstractDBTable.class, context)) {
			table.create();
			LOGGER.info("Created table: " + table.getQualifiedName());
		}

		for (final AbstractDBView<?> view : getTablesInDependencyOrder(AbstractDBView.class, context)) {
			view.create();
			LOGGER.info("Created view: " + view.getQualifiedName());
		}
	}

	public static <T> List<T> getTablesInDependencyOrder(final Class<T> clazz, final ApplicationContext context) {
		final ConfigurableListableBeanFactory beanFactory = ((ConfigurableListableBeanFactory) context
				.getAutowireCapableBeanFactory());

		final Map<String, T> tableBeans = context.getBeansOfType(clazz);

		final Map<String, Set<String>> dependencies = new HashMap<>();
		for (final String name : tableBeans.keySet()) {
			final BeanDefinition bd = beanFactory.getBeanDefinition(name);
			final String[] dependsOn = bd.getDependsOn();
			dependencies.put(name, dependsOn != null ? new HashSet<>(Arrays.asList(dependsOn)) : new HashSet<>());
		}

		final List<String> sortedNames = topologicalSort(dependencies);

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
			visit(bean, deps, visited, sorted, new HashSet<>());
		}

		return sorted;
	}

	private static void visit(final String bean, final Map<String, Set<String>> deps, final Set<String> visited,
			final List<String> sorted, final Set<String> stack) {
		if (visited.contains(bean))
			return;

		if (stack.contains(bean)) {
			throw new IllegalStateException("Circular dependency detected: " + bean);
		}

		stack.add(bean);
		for (final String dep : deps.getOrDefault(bean, Collections.emptySet())) {
			visit(dep, deps, visited, sorted, stack);
		}
		stack.remove(bean);

		visited.add(bean);
		sorted.add(bean);
	}

	@Override
	public String toString() {
		return "DataBaseInitializer@" + System.identityHashCode(this) + " []";
	}

}
