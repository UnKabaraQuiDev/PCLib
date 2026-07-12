package lu.kbra.pclib.db.registrar;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBProperties.Connector;
import lu.kbra.pclib.db.factory.ConfiguredDatabaseConnectorFactoryBean;
import lu.kbra.pclib.db.factory.ConfiguredDatabaseEntryUtilsFactoryBean;
import lu.kbra.pclib.db.factory.ConfiguredDeferredDatabaseFactoryBean;

public class ConnectorBeanRegistrar implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

	private Environment environment;

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) throws BeansException {
		final PCLibDBProperties properties = PCLibDBProperties.bind(this.environment);
		if (!properties.isEnabled()) {
			return;
		}

		for (final String connectorKey : properties.getConnectors().keySet()) {
			final Connector connector = properties.getRequiredConnector(connectorKey);

			this.registerIfMissing(registry,
					connectorKey + "DatabaseEntryUtils",
					ConfiguredDatabaseEntryUtilsFactoryBean.class,
					connectorKey,
					BeanDefinition.ROLE_APPLICATION);

			if (properties.isExposeConnector(connector)) {
				this.registerIfMissing(registry,
						connectorKey + "Connector",
						ConfiguredDatabaseConnectorFactoryBean.class,
						connectorKey,
						BeanDefinition.ROLE_APPLICATION);
			}

			if (properties.isExposeDatabase(connector)) {
				this.registerIfMissing(registry,
						connectorKey,
						ConfiguredDeferredDatabaseFactoryBean.class,
						connectorKey,
						BeanDefinition.ROLE_APPLICATION);
			}
		}
	}

	@Override
	public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// no-op
	}

	@Override
	public void setEnvironment(final Environment environment) {
		this.environment = environment;
	}

	private void registerIfMissing(
			final BeanDefinitionRegistry registry,
			final String beanName,
			final Class<?> factoryBeanClass,
			final String connectorQualifier,
			final int role) {
		if (registry.containsBeanDefinition(beanName)) {
			return;
		}

		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(factoryBeanClass);
		builder.addConstructorArgValue(connectorQualifier);
		builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

		final BeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.setRole(role);
		registry.registerBeanDefinition(beanName, beanDefinition);
	}

}
