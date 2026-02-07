package lu.kbra.pclib.db;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import lu.kbra.pclib.db.config.DataBaseInitializerConfiguration;
import lu.kbra.pclib.db.config.PCLibDBAutoConfiguration;

public class PCLibDBSpringTest {

	@Test
	void autoConfigLoads() {
		new ApplicationContextRunner()
				.withInitializer(context -> AutoConfigurationPackages
						.register((BeanDefinitionRegistry) context, "lu.kbra.pclib"))
				.withUserConfiguration(DBConfiguration.class)
				.withConfiguration(
						AutoConfigurations.of(JacksonAutoConfiguration.class, PCLibDBAutoConfiguration.class, DataBaseInitializerConfiguration.class))
				.run(context -> {
					assertThat(context).hasSingleBean(DeferredSQLQueryableRegistrar.class);
					assertThat(context).hasSingleBean(PersonTable.class);
				});
	}

}
