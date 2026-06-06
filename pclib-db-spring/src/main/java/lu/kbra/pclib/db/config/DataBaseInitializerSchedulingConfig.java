package lu.kbra.pclib.db.config;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true")
public class DataBaseInitializerSchedulingConfig {

	private final DataBaseInitializer dataBaseInitializer;

	public DataBaseInitializerSchedulingConfig(DataBaseInitializer dataBaseInitializer) {
		this.dataBaseInitializer = dataBaseInitializer;
	}

	@Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
	public void keepAlive() {
		dataBaseInitializer.keepAlive();
	}

}