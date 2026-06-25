package lu.kbra.pclib.db.config.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.base.DeferredDataBase;
import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBProperties.Connector;
import lu.kbra.pclib.db.config.provider.SpringDbmsProviders;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class ConfiguredDeferredDataBaseFactoryBean implements FactoryBean<DataBase>, BeanFactoryAware {

	private final String connectorName;
	private BeanFactory beanFactory;
	private DataBase dataBase;

	public ConfiguredDeferredDataBaseFactoryBean(final String connectorName) {
		this.connectorName = connectorName;
	}

	@Override
	public DataBase getObject() {
		if (this.dataBase == null) {
			final PCLibDBProperties properties = this.beanFactory.getBean(PCLibDBProperties.class);
			final SpringDbmsProviders providers = this.beanFactory.getBean(SpringDbmsProviders.class);
			final Connector connector = properties.getRequiredConnector(this.connectorName);
			final DataBaseConnectorFactory connectorFactory = providers.connectorFactoryFor(connector.getProtocol(),
					connector.getProperties());
			final String entryUtilsBeanName = connector.getQualifier() + "DataBaseEntryUtils";
			final DataBaseEntryUtils dataBaseEntryUtils = this.beanFactory.containsBean(entryUtilsBeanName)
					? this.beanFactory.getBean(entryUtilsBeanName, DataBaseEntryUtils.class)
					: this.beanFactory.getBean(DataBaseEntryUtils.class);
			this.dataBase = new DeferredDataBase(connectorFactory,
					connector.getName(),
					dataBaseEntryUtils,
					(AutowireCapableBeanFactory) this.beanFactory);
		}
		return this.dataBase;
	}

	@Override
	public Class<?> getObjectType() {
		return DeferredDataBase.class;
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
