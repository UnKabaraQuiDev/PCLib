package lu.kbra.pclib.db.config;

import java.util.logging.Logger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConditionalOnProperty(prefix = "pclib.db", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableScheduling
public class DataBaseInitializerAutoConfig {

	protected static final Logger LOGGER = Logger.getLogger(DataBaseInitializerAutoConfig.class.getSimpleName());

	@Bean
	DataBaseInitializer dataBaseInitializer(final ApplicationContext context, final PCLibDBProperties properties) {
		return new DataBaseInitializer(context, properties);
	}

}
