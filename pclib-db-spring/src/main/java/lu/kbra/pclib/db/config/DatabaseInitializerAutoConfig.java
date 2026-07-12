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
public class DatabaseInitializerAutoConfig {

	protected static final Logger LOGGER = Logger.getLogger(DatabaseInitializerAutoConfig.class.getSimpleName());

	@Bean
	DatabaseInitializer databaseInitializer(final ApplicationContext context, final PCLibDBProperties properties) {
		return new DatabaseInitializer(context, properties);
	}

}
