package lu.kbra.pclib.db.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.db.registrar.DeferredSQLQueryableRegistrar;
import lu.kbra.pclib.db.utils.SpringDataBaseEntryUtils;

@AutoConfiguration(after = ConfigurationPropertiesAutoConfiguration.class)
public class PCLibDBAutoConfiguration {

	@Bean
	@Primary
	@ConditionalOnMissingBean
	public SpringDataBaseEntryUtils defaultSpringDataBaseEntryUtils(final ObjectMapper objectMapper,
			final ConversionService conversionService) {
		return new SpringDataBaseEntryUtils(objectMapper, conversionService);
	}

	@Bean
	@Primary
	@ConditionalOnMissingBean
	public DeferredSQLQueryableRegistrar defaultDeferredSQLQueryableRegistrar(
			final ApplicationContext applicationContext, final SpringDataBaseEntryUtils dataBaseEntryUtils) {
		return new DeferredSQLQueryableRegistrar(applicationContext, dataBaseEntryUtils);
	}

}
