package lu.kbra.pclib.db.config;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import lu.kbra.pclib.db.config.provider.SpringDbmsProviders;
import lu.kbra.pclib.db.dbms.DbmsProvider;
import lu.kbra.pclib.db.registrar.ConnectorBeanRegistrar;
import lu.kbra.pclib.db.validation.ConstraintCreator;
import lu.kbra.pclib.db.validation.TableValidatorFactory;

import jakarta.validation.Validator;

@AutoConfiguration(after = PCLibDBRegistrarAutoConfiguration.class)
@ConditionalOnProperty(prefix = "pclib.db", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PCLibDBAutoConfiguration {

	@Bean
	static ConnectorBeanRegistrar pclibDBConnectorBeanRegistrar() {
		return new ConnectorBeanRegistrar();
	}

	@Bean
	@ConditionalOnMissingBean
	PCLibDBProperties pclibDBProperties(final Environment environment) {
		return PCLibDBProperties.bind(environment);
	}

	@Bean
	@ConditionalOnMissingBean
	SpringDbmsProviders springDbmsProviders(final ObjectProvider<DbmsProvider> providers) {
		return new SpringDbmsProviders(providers.stream().toList());
	}

	@Bean
	@ConditionalOnClass(Validator.class)
	@ConditionalOnMissingBean
	TableValidatorFactory defaultTableValidatorFactory(final List<ConstraintCreator> ccs) {
		return new TableValidatorFactory(ccs);
	}

}
