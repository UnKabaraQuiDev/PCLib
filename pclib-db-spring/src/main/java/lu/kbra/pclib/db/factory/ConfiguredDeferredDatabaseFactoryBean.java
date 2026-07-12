package lu.kbra.pclib.db.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBProperties.Connector;
import lu.kbra.pclib.db.config.provider.SpringDbmsProviders;
import lu.kbra.pclib.db.connector.impl.DatabaseConnectorFactory;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

public class ConfiguredDeferredDatabaseFactoryBean implements FactoryBean<Database>, BeanFactoryAware {

	private final String connectorQualifier;
	private BeanFactory beanFactory;
	private Database database;

	public ConfiguredDeferredDatabaseFactoryBean(final String connectorQualifier) {
		this.connectorQualifier = connectorQualifier;
	}

	@Override
	public Database getObject() {
		if (this.database == null) {
			final PCLibDBProperties properties = this.beanFactory.getBean(PCLibDBProperties.class);
			final SpringDbmsProviders providers = this.beanFactory.getBean(SpringDbmsProviders.class);
			final Connector connector = properties.getRequiredConnector(this.connectorQualifier);
			final DatabaseConnectorFactory connectorFactory = providers.connectorFactoryFor(connector.getProtocol(),
					connector.getProperties());
			final String entryUtilsBeanName = connector.getQualifier() + "DatabaseEntryUtils";
			final DatabaseEntryUtils utils;
			if (this.beanFactory.containsBean(entryUtilsBeanName)) {
				utils = this.beanFactory.getBean(entryUtilsBeanName, DatabaseEntryUtils.class);
			} else {
				throw new IllegalStateException("No DatabaseEntryUtils found for connector: " + connector.getQualifier());
			}

			this.database = new DeferredDatabase(connectorFactory.get(),
					connector.getName(),
					utils,
					(AutowireCapableBeanFactory) this.beanFactory);
		}
		return this.database;
	}

	@Override
	public Class<?> getObjectType() {
		return DeferredDatabase.class;
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
