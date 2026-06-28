package lu.kbra.pclib.db.config.factory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBProperties.Connector;
import lu.kbra.pclib.db.config.provider.SpringDbmsProviders;
import lu.kbra.pclib.db.dbms.DbmsProvider;
import lu.kbra.pclib.db.type.factory.TypeFactory;
import lu.kbra.pclib.db.utils.SpringDataBaseEntryUtils;

public class ConfiguredDataBaseEntryUtilsFactoryBean implements FactoryBean<SpringDataBaseEntryUtils>, ApplicationContextAware {

	private final String connectorQualifier;
	private ApplicationContext applicationContext;
	private SpringDataBaseEntryUtils dataBaseEntryUtils;

	public ConfiguredDataBaseEntryUtilsFactoryBean(final String connectorQualifier) {
		this.connectorQualifier = connectorQualifier;
	}

	@Override
	public SpringDataBaseEntryUtils getObject() {
		if (this.dataBaseEntryUtils == null) {
			final PCLibDBProperties properties = this.applicationContext.getBean(PCLibDBProperties.class);
			final SpringDbmsProviders providers = this.applicationContext.getBean(SpringDbmsProviders.class);
			final Connector connector = properties.getRequiredConnector(this.connectorQualifier);
			final DbmsProvider provider = providers.findRequired(connector.getProtocol());

			this.dataBaseEntryUtils = new SpringDataBaseEntryUtils(provider.createColumnTypeRegistry(),
					connector.getProtocol(),
					provider.createStructureVisitor(),
					provider.createFunctionResolver());

			for (final TypeFactory tf : this.applicationContext.getBeansOfType(TypeFactory.class).values()) {
				tf.tryAppendTypes(this.dataBaseEntryUtils);
			}
		}
		return this.dataBaseEntryUtils;
	}

	@Override
	public Class<?> getObjectType() {
		return SpringDataBaseEntryUtils.class;
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
