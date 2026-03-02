package lu.kbra.pclib.db.config;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@ConditionalOnProperty(prefix = "lu.pclib.db", name = "auto-create", havingValue = "true", matchIfMissing = true)
@EnableScheduling
public class DataBaseInitializerAutoConfig {

	protected static final Logger LOGGER = Logger.getLogger(DataBaseInitializerAutoConfig.class.getSimpleName());

	@Bean
	DataBaseInitializer dataBaseInitializer() {
		return new DataBaseInitializer();
	}

	@Autowired
	@Lazy
	private DataBaseInitializer dataBaseInitializer;

	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
	public void keepAlive() {
		dataBaseInitializer.keepAlive();
	}

}
