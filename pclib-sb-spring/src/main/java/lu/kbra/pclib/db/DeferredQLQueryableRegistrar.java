package lu.kbra.pclib.db;

import java.beans.Introspector;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AssignableTypeFilter;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.table.DeferredSQLQueryable;
import lu.kbra.pclib.db.table.NTDataBaseTable;
import lu.kbra.pclib.db.view.DataBaseView;
import lu.kbra.pclib.db.view.NTDataBaseView;

@Configuration(proxyBeanMethods = false)
public class DeferredQLQueryableRegistrar implements BeanDefinitionRegistryPostProcessor {

	private final SpringDataBaseEntryUtils dataBaseEntryUtils;
	private final ApplicationContext applicationContext;

	public DeferredQLQueryableRegistrar(final ApplicationContext applicationContext,
			final SpringDataBaseEntryUtils dataBaseEntryUtils) {
		this.applicationContext = applicationContext;
		this.dataBaseEntryUtils = dataBaseEntryUtils;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) {
		final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
				true) {
			@Override
			protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
				if (super.isCandidateComponent(beanDefinition)) {
					return true;
				}

				try {
					final String className = beanDefinition.getMetadata().getClassName();
					final Class<?> clazz = Class.forName(className);

					return DeferredSQLQueryable.class.isAssignableFrom(clazz);
				} catch (final ClassNotFoundException e) {
					return false;
				}
			}
		};

		scanner.addExcludeFilter(new AssignableTypeFilter(DataBase.class));
		scanner.addExcludeFilter(new AssignableTypeFilter(DataBaseTable.class));
		scanner.addExcludeFilter(new AssignableTypeFilter(DataBaseView.class));
		scanner.addExcludeFilter(new AssignableTypeFilter(NTDataBaseTable.class));
		scanner.addExcludeFilter(new AssignableTypeFilter(NTDataBaseView.class));

//		scanner.addIncludeFilter(new AssignableTypeFilter(DeferredSQLQueryable.class));

		final String basePackage = AutoConfigurationPackages.get(this.applicationContext).get(0);

		for (final BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
			try {
				final Class<?> clazz = Class.forName(bd.getBeanClassName());

				if (DeferredSQLQueryable.class.isAssignableFrom(clazz)) {
					// register with proxy
					final Class<? extends DeferredSQLQueryable> iface = (Class<? extends DeferredSQLQueryable>) clazz;
					final String baseBeanName = Introspector.decapitalize(iface.getSimpleName());

					final BeanDefinitionBuilder builder = BeanDefinitionBuilder
							.genericBeanDefinition(DeferredSQLQueryableFactoryBean.class);
					builder.addConstructorArgValue(iface);

					final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

					final Class<? extends SQLQueryable<? extends DataBaseEntry>>[] dependencies = this.dataBaseEntryUtils
							.resolveDependencies((Class<? extends SQLQueryable<DataBaseEntry>>) iface);

					final String[] dependencyBeanNames = Arrays.stream(dependencies).map(Class::getSimpleName)
							.map(Introspector::decapitalize).toArray(String[]::new);
					beanDefinition.setDependsOn(dependencyBeanNames);

					registry.registerBeanDefinition(baseBeanName, beanDefinition);

				} else {
					// register normally
					final String beanName = clazz.getSimpleName();
					if (!registry.containsBeanDefinition(beanName)) {
						final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
						registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
					}

				}
			} catch (final ClassNotFoundException e) {
				PCUtils.throwRuntime(e);
			}
		}
	}

}
