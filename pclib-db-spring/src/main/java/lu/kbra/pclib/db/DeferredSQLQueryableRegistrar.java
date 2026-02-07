package lu.kbra.pclib.db;

import java.beans.Introspector;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.AssignableTypeFilter;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;
import lu.kbra.pclib.db.table.DeferredNTSQLQueryable;
import lu.kbra.pclib.db.table.DeferredSQLQueryable;
import lu.kbra.pclib.db.view.DeferredDataBaseView;
import lu.kbra.pclib.db.view.DeferredNTDataBaseView;

//@Component
public class DeferredSQLQueryableRegistrar
		implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ResourceLoaderAware {

	private final DataBase dataBase;
	private final SpringDataBaseEntryUtils dataBaseEntryUtils;
	private final ApplicationContext applicationContext;

	private Environment environment;
	private ResourceLoader resourceLoader;

	public DeferredSQLQueryableRegistrar(final ApplicationContext applicationContext, final DataBase dataBase,
			final SpringDataBaseEntryUtils dataBaseEntryUtils) {
		this.dataBase = dataBase;
		this.applicationContext = applicationContext;
		this.dataBaseEntryUtils = dataBaseEntryUtils;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false,
				environment) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				// Always include concrete top-level types (normal Spring behavior)
				if (super.isCandidateComponent(beanDefinition)) {
					return true;
				}

				try {
					final String className = beanDefinition.getMetadata().getClassName();
					final Class<?> clazz = Class.forName(className);

					return DeferredSQLQueryable.class.isAssignableFrom(clazz);
				} catch (ClassNotFoundException e) {
					return false;
				}
			}
		};

		scanner.setResourceLoader(resourceLoader);

		scanner.addIncludeFilter(new AssignableTypeFilter(DeferredSQLQueryable.class));

		String basePackage = resolveRootPackage();

		for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {

			try {
				Class<?> repoClass = Class.forName(bd.getBeanClassName());

				if (repoClass.equals(DeferredSQLQueryable.class) || repoClass.equals(DeferredNTSQLQueryable.class)
						|| repoClass.equals(DeferredDataBaseView.class) || repoClass.equals(DeferredDataBaseTable.class)
						|| repoClass.equals(DeferredNTDataBaseView.class)
						|| repoClass.equals(DeferredNTDataBaseTable.class)) {
					continue;
				}

				registerFactoryBean(registry, repoClass);

			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	protected String resolveRootPackage() {
		return AutoConfigurationPackages.get(applicationContext).get(0);
	}

	protected boolean isDeferredQueryable(BeanDefinition bd) {
		try {
			final String className = bd.getBeanClassName();
			if (className == null)
				return false;

			final Class<?> type = Class.forName(className);

			return DeferredSQLQueryable.class.isAssignableFrom(type);
		} catch (Exception e) {
			return false;
		}
	}

	protected void registerFactoryBean(BeanDefinitionRegistry registry, Class<?> repositoryClass) {
		if (!Modifier.isAbstract(repositoryClass.getModifiers())) {
			throw new IllegalStateException("DeferredSQLQueryable must be abstract: " + repositoryClass.getName());
		}

		final String beanName = Introspector.decapitalize(repositoryClass.getSimpleName());

		final BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition(DeferredSQLQueryableFactoryBean.class);

		builder.addConstructorArgValue(repositoryClass);
		builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

		final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] dependencies = dataBaseEntryUtils
				.resolveDependencies((Class<? extends SQLQueryable<DataBaseEntry>>) repositoryClass);

		final String[] dependencyBeanNames = Arrays.stream(dependencies).map(Class::getSimpleName)
				.map(Introspector::decapitalize).toArray(String[]::new);

		final BeanDefinition beanDefinition = builder.getBeanDefinition();

		beanDefinition.setDependsOn(dependencyBeanNames);

		registry.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}
