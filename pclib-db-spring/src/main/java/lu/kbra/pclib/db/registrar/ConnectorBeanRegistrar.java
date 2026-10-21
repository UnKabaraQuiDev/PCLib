package lu.kbra.pclib.db.registrar;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBProperties.Connector;
import lu.kbra.pclib.db.factory.ConfiguredDatabaseConnectorFactoryBean;
import lu.kbra.pclib.db.factory.ConfiguredDatabaseEntryUtilsFactoryBean;
import lu.kbra.pclib.db.factory.ConfiguredDeferredDatabaseFactoryBean;

import lombok.Setter;

public class ConnectorBeanRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

	@Setter
	private Environment environment;

	@Override
	public void registerBeanDefinitions(final AnnotationMetadata importingClassMetadata, final BeanDefinitionRegistry registry) {
		final PCLibDBProperties properties = PCLibDBProperties.bind(this.environment);

		if (!properties.isEnabled()) {
			return;
		}

		for (final String connectorKey : properties.getConnectors().keySet()) {
			System.out.println(connectorKey);
			final Connector connector = properties.getRequiredConnector(connectorKey);

			this.registerIfMissing(registry,
					connectorKey + "DatabaseEntryUtils",
					ConfiguredDatabaseEntryUtilsFactoryBean.class,
					connectorKey);

			if (properties.isExposeConnector(connector)) {
				this.registerIfMissing(registry, connectorKey + "Connector", ConfiguredDatabaseConnectorFactoryBean.class, connectorKey);
			}

			if (properties.isExposeDatabase(connector)) {
				this.registerIfMissing(registry, connectorKey, ConfiguredDeferredDatabaseFactoryBean.class, connectorKey);
			}
		}
	}

	private void registerIfMissing(
			final BeanDefinitionRegistry registry,
			final String beanName,
			final Class<?> factoryBeanClass,
			final String connectorQualifier) {
		if (registry.containsBeanDefinition(beanName)) {
			return;
		}

		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(factoryBeanClass);

		builder.addConstructorArgValue(connectorQualifier);
		builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

		registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
		System.out.println("registering db: " + beanName);
	}

}
