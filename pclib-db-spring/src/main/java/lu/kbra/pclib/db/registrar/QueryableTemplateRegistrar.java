package lu.kbra.pclib.db.registrar;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.factory.DeferredQueryableTemplateFactoryBean;
import lu.kbra.pclib.db.factory.QueryableTemplateFactoryBean;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;
import lu.kbra.pclib.db.utils.QueryableTemplate;

public class QueryableTemplateRegistrar implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) {
	}

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		final String[] templates = beanFactory.getBeanNamesForType(QueryableTemplate.class, false, false);
		final BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

		for (final String templateName : templates) {
			final QueryableTemplate template = beanFactory.getBean(templateName, QueryableTemplate.class);
			final Class<? extends SQLQueryable<?>> repositoryClass = template
					.<Class<? extends SQLQueryable<?>>>getHint(DefaultQueryableHints.TARGET_CLASS);

			final String beanName = templateName; // Introspector.decapitalize(repositoryClass.getSimpleName());
			final String templateBeanName = beanName + "Template";

//			if (registry.containsBeanDefinition(beanName)) {
//				continue;
//			}

			// rename defined bean
			final BeanDefinition templateDefinition = registry.getBeanDefinition(templateName);

			registry.removeBeanDefinition(templateName);
			registry.registerBeanDefinition(templateBeanName, templateDefinition);

			// create new bean
			if (DeferredSQLQueryable.class.isAssignableFrom(repositoryClass)) {
				final RootBeanDefinition definition = new RootBeanDefinition(DeferredQueryableTemplateFactoryBean.class);

				definition.getConstructorArgumentValues().addGenericArgumentValue(beanFactory);
				definition.getConstructorArgumentValues().addIndexedArgumentValue(1, new RuntimeBeanReference(templateBeanName));
				definition.getConstructorArgumentValues().addGenericArgumentValue(new QueryMethodInterceptor());
				definition.setTargetType(repositoryClass);

				registry.registerBeanDefinition(beanName, definition);
			} else {
				final RootBeanDefinition definition = new RootBeanDefinition(QueryableTemplateFactoryBean.class);

				definition.getConstructorArgumentValues().addGenericArgumentValue(beanFactory);
				definition.getConstructorArgumentValues().addIndexedArgumentValue(1, new RuntimeBeanReference(templateBeanName));
				definition.setTargetType(repositoryClass);

				registry.registerBeanDefinition(beanName, definition);
			}
		}
	}

}
