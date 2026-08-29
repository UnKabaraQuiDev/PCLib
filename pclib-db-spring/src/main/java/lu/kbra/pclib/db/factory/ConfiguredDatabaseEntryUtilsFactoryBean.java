package lu.kbra.pclib.db.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBProperties.Connector;
import lu.kbra.pclib.db.config.provider.SpringDbmsProviders;
import lu.kbra.pclib.db.dbms.DbmsProvider;
import lu.kbra.pclib.db.type.factory.DatabaseColumnTypeFactory;
import lu.kbra.pclib.db.type.factory.DatabaseEncodingTypeFactory;
import lu.kbra.pclib.db.utils.BaseProxyDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.DatabaseQueryableHookTemplate;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

public class ConfiguredDatabaseEntryUtilsFactoryBean implements FactoryBean<DatabaseEntryUtils>, ApplicationContextAware, BeanFactoryAware {

	private final String connectorQualifier;
	private ApplicationContext applicationContext;
	private DatabaseEntryUtils databaseEntryUtils;
	private BeanFactory beanFactory;

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
					provider.createEncodingTypeRegistry(),
					connector.getProtocol(),
					provider.createStructureVisitor(),
					provider.createFunctionResolver());

			for (final DatabaseEncodingTypeFactory ef : this.applicationContext.getBeansOfType(DatabaseEncodingTypeFactory.class)
					.values()) {
				ef.tryAppendTypes(this.databaseEntryUtils);
			}

			for (final DatabaseColumnTypeFactory tf : this.applicationContext.getBeansOfType(DatabaseColumnTypeFactory.class).values()) {
				tf.tryAppendTypes(this.databaseEntryUtils);
			}

			for (final DatabaseQueryableHookTemplate rct : this.applicationContext.getBeansOfType(DatabaseQueryableHookTemplate.class)
					.values()) {
				rct.tryApply(this.databaseEntryUtils, this.connectorQualifier, this.beanFactory);
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

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
