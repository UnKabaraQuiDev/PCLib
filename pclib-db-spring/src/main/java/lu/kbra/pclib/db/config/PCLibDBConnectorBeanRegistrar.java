package lu.kbra.pclib.db.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import lu.kbra.pclib.db.config.PCLibDBProperties.Connector;
import lu.kbra.pclib.db.config.factory.ConfiguredDataBaseConnectorFactoryBean;
import lu.kbra.pclib.db.config.factory.ConfiguredDataBaseEntryUtilsFactoryBean;
import lu.kbra.pclib.db.config.factory.ConfiguredDeferredDataBaseFactoryBean;

public class PCLibDBConnectorBeanRegistrar implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

	private Environment environment;

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
		final PCLibDBProperties properties = PCLibDBProperties.bind(this.environment);
		if (!properties.isEnabled()) {
			return;
		}

		final boolean needsQualifiedEntryUtils = properties.getConnectors().size() > 1;
		for (final String connectorName : properties.getConnectors().keySet()) {
			final Connector connector = properties.getRequiredConnector(connectorName);
			final String qualifier = connector.getQualifier();

			if (needsQualifiedEntryUtils) {
				this.registerIfMissing(registry,
						qualifier + "DataBaseEntryUtils",
						ConfiguredDataBaseEntryUtilsFactoryBean.class,
						connectorName,
						BeanDefinition.ROLE_INFRASTRUCTURE);
			}

			if (properties.isExposeConnector(connector)) {
				this.registerIfMissing(registry,
						qualifier + "Connector",
						ConfiguredDataBaseConnectorFactoryBean.class,
						connectorName,
						BeanDefinition.ROLE_APPLICATION);
			}

			if (properties.isExposeDatabase(connector)) {
				this.registerIfMissing(registry,
						qualifier,
						ConfiguredDeferredDataBaseFactoryBean.class,
						connectorName,
						BeanDefinition.ROLE_APPLICATION);
			}
		}
	}

	private void registerIfMissing(
			final BeanDefinitionRegistry registry,
			final String beanName,
			final Class<?> factoryBeanClass,
			final String connectorName,
			final int role) {
		if (registry.containsBeanDefinition(beanName)) {
			return;
		}

		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(factoryBeanClass);
		builder.addConstructorArgValue(connectorName);
		builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

		final BeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.setRole(role);
		registry.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// no-op
	}

	@Override
	public void setEnvironment(final Environment environment) {
		this.environment = environment;
	}

}
