package lu.kbra.pclib.db.config;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.config.provider.SpringDbmsProviders;
import lu.kbra.pclib.db.dbms.DbmsProvider;
import lu.kbra.pclib.db.query.returns.EnumerationReturnTypeMapper;
import lu.kbra.pclib.db.query.returns.IteratorReturnTypeMapper;
import lu.kbra.pclib.db.query.returns.NextTaskReturnTypeMapper;
import lu.kbra.pclib.db.query.returns.OptionalReturnTypeMapper;
import lu.kbra.pclib.db.query.returns.StreamReturnTypeMapper;
import lu.kbra.pclib.db.registrar.ConnectorBeanRegistrar;
import lu.kbra.pclib.db.type.returns.MonoReturnTypeMapper;
import lu.kbra.pclib.db.validation.ConstraintCreator;
import lu.kbra.pclib.db.validation.TableValidatorFactory;

import jakarta.validation.Validator;

@AutoConfiguration(after = PCLibDBRegistrarAutoConfiguration.class)
@ConditionalOnProperty(prefix = "pclib.db", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackageClasses = { MonoReturnTypeMapper.class })
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

	@Bean
	@ConditionalOnMissingBean
	OptionalReturnTypeMapper optionalReturnTypeMapper() {
		return new OptionalReturnTypeMapper();
	}

	@Bean
	@ConditionalOnMissingBean
	StreamReturnTypeMapper streamReturnTypeMapper() {
		return new StreamReturnTypeMapper();
	}

	@Bean
	@ConditionalOnMissingBean
	IteratorReturnTypeMapper iteratorReturnTypeMapper() {
		return new IteratorReturnTypeMapper();
	}

	@Bean
	@ConditionalOnMissingBean
	EnumerationReturnTypeMapper enumerationReturnTypeMapper() {
		return new EnumerationReturnTypeMapper();
	}

	@Bean
	@ConditionalOnClass(NextTask.class)
	@ConditionalOnMissingBean
	NextTaskReturnTypeMapper nextTaskReturnTypeMapper() {
		return new NextTaskReturnTypeMapper();
	}

}
