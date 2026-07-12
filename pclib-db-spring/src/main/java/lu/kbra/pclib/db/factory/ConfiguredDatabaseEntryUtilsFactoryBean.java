package lu.kbra.pclib.db.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBProperties.Connector;
import lu.kbra.pclib.db.config.provider.SpringDbmsProviders;
import lu.kbra.pclib.db.dbms.DbmsProvider;
import lu.kbra.pclib.db.type.factory.DatabaseTypeFactory;
import lu.kbra.pclib.db.utils.BaseProxyDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

public class ConfiguredDatabaseEntryUtilsFactoryBean implements FactoryBean<DatabaseEntryUtils>, ApplicationContextAware {

	private final String connectorQualifier;
	private ApplicationContext applicationContext;
	private DatabaseEntryUtils databaseEntryUtils;

	public ConfiguredDatabaseEntryUtilsFactoryBean(final String connectorQualifier) {
		this.connectorQualifier = connectorQualifier;
	}

	@Override
	public DatabaseEntryUtils getObject() {
		if (this.databaseEntryUtils == null) {
			final PCLibDBProperties properties = this.applicationContext.getBean(PCLibDBProperties.class);
			final SpringDbmsProviders providers = this.applicationContext.getBean(SpringDbmsProviders.class);
			final Connector connector = properties.getRequiredConnector(this.connectorQualifier);
			final DbmsProvider provider = providers.findRequired(connector.getProtocol());

			this.databaseEntryUtils = new BaseProxyDatabaseEntryUtils(provider.createColumnTypeRegistry(),
					connector.getProtocol(),
					provider.createStructureVisitor(),
					provider.createFunctionResolver());

			for (final DatabaseTypeFactory tf : this.applicationContext.getBeansOfType(DatabaseTypeFactory.class).values()) {
				tf.tryAppendTypes(this.databaseEntryUtils);
			}
		}
		return this.databaseEntryUtils;
	}

	@Override
	public Class<?> getObjectType() {
		return DatabaseEntryUtils.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
