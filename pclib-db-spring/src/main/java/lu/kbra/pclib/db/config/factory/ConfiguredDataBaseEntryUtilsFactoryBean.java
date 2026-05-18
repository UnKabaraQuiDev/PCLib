package lu.kbra.pclib.db.config.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.convert.ConversionService;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBProperties.Connector;
import lu.kbra.pclib.db.config.provider.SpringDbmsProviders;
import lu.kbra.pclib.db.utils.SpringDataBaseEntryUtils;

public class ConfiguredDataBaseEntryUtilsFactoryBean implements FactoryBean<SpringDataBaseEntryUtils>, BeanFactoryAware {

	private final String connectorName;
	private BeanFactory beanFactory;
	private SpringDataBaseEntryUtils dataBaseEntryUtils;

	public ConfiguredDataBaseEntryUtilsFactoryBean(final String connectorName) {
		this.connectorName = connectorName;
	}

	@Override
	public SpringDataBaseEntryUtils getObject() {
		if (this.dataBaseEntryUtils == null) {
			final PCLibDBProperties properties = this.beanFactory.getBean(PCLibDBProperties.class);
			final SpringDbmsProviders providers = this.beanFactory.getBean(SpringDbmsProviders.class);
			final ObjectMapper objectMapper = this.beanFactory.getBean(ObjectMapper.class);
			final ConversionService conversionService = this.beanFactory.getBean(ConversionService.class);
			final Connector connector = properties.getRequiredConnector(this.connectorName);
			this.dataBaseEntryUtils = new SpringDataBaseEntryUtils(objectMapper,
					conversionService,
					providers.columnTypeRegistryFor(connector.getProtocol()));
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
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
