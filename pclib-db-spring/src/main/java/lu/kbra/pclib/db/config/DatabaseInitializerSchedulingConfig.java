package lu.kbra.pclib.db.config;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "pclib.db.keepAlive", havingValue = "true")
public class DatabaseInitializerSchedulingConfig {

	private final DatabaseInitializer databaseInitializer;

	public DatabaseInitializerSchedulingConfig(final DatabaseInitializer databaseInitializer) {
		this.databaseInitializer = databaseInitializer;
	}

	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
	public void keepAlive() {
		this.databaseInitializer.keepAlive();
	}

}
