package lu.kbra.pclib.db.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "lu.pclib.db", name = "auto-create", havingValue = "true", matchIfMissing = true)
public class DataBaseInitializerAutoConfig {

	@Bean
	DataBaseInitializer dataBaseInitializer() {
		return new DataBaseInitializer();
	}

}
