package lu.kbra.pclib.db.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import lu.kbra.pclib.db.registrar.DeferredSQLQueryableRegistrar;

@AutoConfiguration
@ConditionalOnProperty(prefix = "pclib.db", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PCLibDBRegistrarAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	static DeferredSQLQueryableRegistrar defaultDeferredSQLQueryableRegistrar() {
		return new DeferredSQLQueryableRegistrar();
	}

}
