package lu.kbra.pclib.db.config;

import java.util.Collection;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.db.config.provider.SpringDbmsProviders;
import lu.kbra.pclib.db.dbms.DbmsProvider;
import lu.kbra.pclib.db.utils.SpringDataBaseEntryUtils;

@AutoConfiguration(after = PCLibDBRegistrarAutoConfiguration.class)
@ConditionalOnProperty(prefix = "pclib.db", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PCLibDBAutoConfiguration {

	@Bean
	static PCLibDBConnectorBeanRegistrar pclibDBConnectorBeanRegistrar() {
		return new PCLibDBConnectorBeanRegistrar();
	}

	@Bean
	@ConditionalOnMissingBean
	PCLibDBProperties pclibDBProperties(final Environment environment) {
		return PCLibDBProperties.bind(environment);
	}

	@Bean
	@ConditionalOnMissingBean
	SpringDataBaseEntryUtils springDataBaseEntryUtils(
			final ObjectMapper objectMapper,
			final ApplicationConversionService conversionService,
			final PCLibDBProperties properties,
			final SpringDbmsProviders providers) {

		final Collection<PCLibDBProperties.Connector> connectors = properties.getConnectors().values();
		if (connectors.size() == 1) {
			final PCLibDBProperties.Connector connector = connectors.iterator().next();
			return new SpringDataBaseEntryUtils(providers.columnTypeRegistryFor(connector.getProtocol()),
					connector.getProtocol(),
					objectMapper,
					conversionService);
		}

		return new SpringDataBaseEntryUtils(objectMapper, conversionService);
	}

	@Bean
	@ConditionalOnMissingBean
	SpringDbmsProviders springDbmsProviders(final ObjectProvider<DbmsProvider> providers) {
		return new SpringDbmsProviders(providers.stream().toList());
	}

}
