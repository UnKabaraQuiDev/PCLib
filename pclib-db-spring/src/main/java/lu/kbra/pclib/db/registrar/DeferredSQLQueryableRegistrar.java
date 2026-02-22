package lu.kbra.pclib.db.registrar;

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
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;

import lu.kbra.pclib.db.factory.DeferredSQLQueryableFactoryBean;
import lu.kbra.pclib.db.factory.SQLQueryableFactoryBean;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredNTSQLQueryable;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;
import lu.kbra.pclib.db.table.NTDataBaseTable;
import lu.kbra.pclib.db.utils.SpringDataBaseEntryUtils;
import lu.kbra.pclib.db.view.DataBaseView;
import lu.kbra.pclib.db.view.DeferredDataBaseView;
import lu.kbra.pclib.db.view.DeferredNTDataBaseView;
import lu.kbra.pclib.db.view.NTDataBaseView;

//@Component
public class DeferredSQLQueryableRegistrar
		implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ResourceLoaderAware {

	private final SpringDataBaseEntryUtils dataBaseEntryUtils;
	private final ApplicationContext applicationContext;

	private Environment environment;
	private ResourceLoader resourceLoader;

	public DeferredSQLQueryableRegistrar(final ApplicationContext applicationContext,
			final SpringDataBaseEntryUtils dataBaseEntryUtils) {
		this.applicationContext = applicationContext;
		this.dataBaseEntryUtils = dataBaseEntryUtils;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) {
		this.createDeferred(registry);
		this.createNormal(registry);
	}

	protected void createNormal(final BeanDefinitionRegistry registry) {
		for (final String beanName : registry.getBeanDefinitionNames()) {

			final BeanDefinition bd = registry.getBeanDefinition(beanName);

			final String className = bd.getBeanClassName();
			if (className == null) {
				continue;
			}

			try {
				final Class<?> repoClass = Class.forName(bd.getBeanClassName());

				if (!repoClass.isAssignableFrom(SQLQueryable.class)
						|| repoClass.isAssignableFrom(DeferredSQLQueryable.class)) {
					continue;
				}

				if (repoClass.equals(SQLQueryable.class) || repoClass.equals(NTSQLQueryable.class)
						|| repoClass.equals(DataBaseView.class) || repoClass.equals(DataBaseTable.class)
						|| repoClass.equals(NTDataBaseView.class) || repoClass.equals(NTDataBaseTable.class)) {
					continue;
				}

				registry.removeBeanDefinition(bd.getBeanClassName());

				this.registerNormalFactoryBean(registry, repoClass);

			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	protected void createDeferred(final BeanDefinitionRegistry registry) {
		final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				false, this.environment) {
			@Override
			protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
				// Always include concrete top-level types (normal Spring behavior)
				if (super.isCandidateComponent(beanDefinition)) {
					return true;
				}

				final AnnotationMetadata metadata = beanDefinition.getMetadata();

				final boolean isSpringComponent = metadata.hasAnnotation("org.springframework.stereotype.Component")
						|| metadata.hasMetaAnnotation("org.springframework.stereotype.Component");

				if (!isSpringComponent) {
					return false;
				}

				try {
					final String className = metadata.getClassName();
					final Class<?> clazz = Class.forName(className);

					return DeferredSQLQueryable.class.isAssignableFrom(clazz);
				} catch (final ClassNotFoundException e) {
					return false;
				}
			}
		};

		scanner.setResourceLoader(this.resourceLoader);

		scanner.addIncludeFilter(new AssignableTypeFilter(DeferredSQLQueryable.class));

		final String basePackage = this.resolveRootPackage();

		for (final BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {

			try {
				final Class<?> repoClass = Class.forName(bd.getBeanClassName());

				if (repoClass.equals(DeferredSQLQueryable.class) || repoClass.equals(DeferredNTSQLQueryable.class)
						|| repoClass.equals(DeferredDataBaseView.class) || repoClass.equals(DeferredDataBaseTable.class)
						|| repoClass.equals(DeferredNTDataBaseView.class)
						|| repoClass.equals(DeferredNTDataBaseTable.class)) {
					continue;
				}

				this.registerDeferredFactoryBean(registry, repoClass);

			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	protected String resolveRootPackage() {
		return AutoConfigurationPackages.get(this.applicationContext).get(0);
	}

	protected void registerDeferredFactoryBean(final BeanDefinitionRegistry registry, final Class<?> repositoryClass) {
		if (!Modifier.isAbstract(repositoryClass.getModifiers())) {
			throw new IllegalStateException("DeferredSQLQueryable must be abstract: " + repositoryClass.getName());
		}

		final String beanName = Introspector.decapitalize(repositoryClass.getSimpleName());

		final BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition(DeferredSQLQueryableFactoryBean.class);

		builder.addConstructorArgValue(repositoryClass);
		builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

		final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] dependencies = this.dataBaseEntryUtils
				.resolveDependencies((Class<? extends SQLQueryable<DataBaseEntry>>) repositoryClass);

		final String[] dependencyBeanNames = Arrays.stream(dependencies).map(Class::getSimpleName)
				.map(Introspector::decapitalize).toArray(String[]::new);

		final BeanDefinition beanDefinition = builder.getBeanDefinition();

		beanDefinition.setDependsOn(dependencyBeanNames);

		registry.registerBeanDefinition(beanName, beanDefinition);
	}

	protected void registerNormalFactoryBean(final BeanDefinitionRegistry registry, final Class<?> repositoryClass) {
		if (Modifier.isAbstract(repositoryClass.getModifiers())) {
			throw new IllegalStateException(
					"SQLQueryable cannot be abstract, use DeferredSQLQueryable instead: " + repositoryClass.getName());
		}

		final String beanName = Introspector.decapitalize(repositoryClass.getSimpleName());

		final BeanDefinitionBuilder builder = BeanDefinitionBuilder
				.genericBeanDefinition(SQLQueryableFactoryBean.class);

		builder.addConstructorArgValue(repositoryClass);
		builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

		final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] dependencies = this.dataBaseEntryUtils
				.resolveDependencies((Class<? extends SQLQueryable<DataBaseEntry>>) repositoryClass);

		final String[] dependencyBeanNames = Arrays.stream(dependencies).map(Class::getSimpleName)
				.map(Introspector::decapitalize).toArray(String[]::new);

		final BeanDefinition beanDefinition = builder.getBeanDefinition();

		beanDefinition.setDependsOn(dependencyBeanNames);

		registry.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public void setEnvironment(final Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setResourceLoader(final ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

}
