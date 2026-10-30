package lu.kbra.pclib.db.registrar;

import org.springframework.aot.AotDetector;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.aot.AbstractAotProcessor;

import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.factory.DeferredQueryableTemplateFactoryBean;
import lu.kbra.pclib.db.factory.QueryableTemplateFactoryBean;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.QueryableTemplate;

public class QueryableTemplateRegistrar implements BeanDefinitionRegistryPostProcessor {

	public static final String AOT_TEMPLATE = "pclib.db.aot.templateHints";

	private boolean aotProcessing;

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) {
	}

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (AotDetector.useGeneratedArtifacts()) {
			return;
		}

		this.aotProcessing = Boolean.getBoolean(AbstractAotProcessor.AOT_PROCESSING);

		final String[] templates = beanFactory.getBeanNamesForType(QueryableTemplate.class, false, false);
		final BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

		for (final String templateName : templates) {
			final QueryableTemplate template = beanFactory.getBean(templateName, QueryableTemplate.class);
			final Class<? extends SQLQueryable<?>> repositoryClass = template
					.<Class<? extends SQLQueryable<?>>>getHint(DefaultQueryableHints.TARGET_CLASS);

			final String beanName = templateName; // Introspector.decapitalize(repositoryClass.getSimpleName());
			final String templateBeanName = beanName + "Template";

			// rename defined bean
			final BeanDefinition templateDefinition = registry.getBeanDefinition(templateName);

			registry.removeBeanDefinition(templateName);
			registry.registerBeanDefinition(templateBeanName, templateDefinition);

			// create new bean
			final RootBeanDefinition definition;
			if (DeferredSQLQueryable.class.isAssignableFrom(repositoryClass)) {
				definition = new RootBeanDefinition(DeferredQueryableTemplateFactoryBean.class);

				definition.getConstructorArgumentValues().addGenericArgumentValue(beanFactory);
				definition.getConstructorArgumentValues().addIndexedArgumentValue(1, new RuntimeBeanReference(templateBeanName));
				definition.setTargetType(repositoryClass);
			} else {
				definition = new RootBeanDefinition(QueryableTemplateFactoryBean.class);

				definition.getConstructorArgumentValues().addGenericArgumentValue(beanFactory);
				definition.getConstructorArgumentValues().addIndexedArgumentValue(1, new RuntimeBeanReference(templateBeanName));
				definition.setTargetType(repositoryClass);
			}

			definition.setAttribute(QueryableTemplateRegistrar.AOT_TEMPLATE, true);

			if (this.aotProcessing) {
				definition.setAttribute(BeanRegistrationAotProcessor.IGNORE_REGISTRATION_ATTRIBUTE, true);
			}

			registry.registerBeanDefinition(beanName, definition);
		}
	}

}
