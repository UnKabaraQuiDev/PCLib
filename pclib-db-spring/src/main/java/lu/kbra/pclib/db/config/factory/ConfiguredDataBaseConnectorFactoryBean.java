package lu.kbra.pclib.db.config.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;

import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBProperties.Connector;
import lu.kbra.pclib.db.config.provider.SpringDbmsProviders;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;

public class ConfiguredDataBaseConnectorFactoryBean implements FactoryBean<DataBaseConnectorFactory>, BeanFactoryAware {

	private final String connectorName;
	private BeanFactory beanFactory;
	private DataBaseConnectorFactory connectorFactory;

	public ConfiguredDataBaseConnectorFactoryBean(final String connectorName) {
		this.connectorName = connectorName;
	}

	@Override
	public DataBaseConnectorFactory getObject() {
		if (this.connectorFactory == null) {
			final PCLibDBProperties properties = this.beanFactory.getBean(PCLibDBProperties.class);
			final SpringDbmsProviders providers = this.beanFactory.getBean(SpringDbmsProviders.class);
			final Connector connector = properties.getRequiredConnector(this.connectorName);
			this.connectorFactory = providers.connectorFactoryFor(connector.getProtocol(), connector.getProperties());
		}
		return this.connectorFactory;
	}

	@Override
	public Class<?> getObjectType() {
		return DataBaseConnectorFactory.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
