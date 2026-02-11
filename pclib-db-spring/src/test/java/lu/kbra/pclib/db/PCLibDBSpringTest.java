package lu.kbra.pclib.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.config.DataBaseInitializerAutoConfig;
import lu.kbra.pclib.db.config.PCLibDBAutoConfiguration;

public class PCLibDBSpringTest {

	@Test
	void autoConfigLoads() {
		new ApplicationContextRunner()
				.withInitializer(context -> AutoConfigurationPackages.register((BeanDefinitionRegistry) context,
						"lu.kbra.pclib"))
				.withUserConfiguration(DBConfiguration.class)
				.withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, PCLibDBAutoConfiguration.class,
						DataBaseInitializerAutoConfig.class))
				.run(context -> {
					assertThat(context).hasSingleBean(DeferredSQLQueryableRegistrar.class);
					assertThat(context).hasSingleBean(PersonTable.class);

					{
						final PersonTable people = context.getBean(PersonTable.class);
						assertThat(people.count()).isEqualTo(people.truncate());

						people.insertAndReload(new PersonData("name1"));
						people.insertAndReload(new PersonData("name2"));

						assertThat(people.byName("name1")).satisfies(Optional::isPresent);
						assertThat(people.byName("name2")).satisfies(Optional::isPresent);
						assertThat(people.byName("name3")).satisfies(Optional::isEmpty);
					}

					{
						final NTUserTable users = context.getBean(NTUserTable.class);
						assertThat(users.count()).isEqualTo(users.truncate());

						users.insertAndReload(new UserData("name1", "pass1"));
						users.insertAndReload(new UserData("name2", "pass1"));
						users.insertAndReload(new UserData("name3", "pass2"));

						assertThat(users.byName("name1")).satisfies(Optional::isPresent);
						assertThat(users.byName("name2")).satisfies(Optional::isPresent);
						assertThat(users.byName("name3")).satisfies(Optional::isPresent);
						assertThat(users.byName("name4")).satisfies(Optional::isEmpty);

						assertThat(users.byNameAndPass("name1", "pass1")).satisfies(Optional::isPresent);
						assertThat(users.byNameAndPass("name2", "pass1")).satisfies(Optional::isPresent);
						assertThat(users.byNameAndPass("name3", "pass2")).satisfies(Optional::isPresent);
						assertThat(users.byNameAndPass("name3", "pass3")).satisfies(Optional::isEmpty);

						assertThat(users.ntByName().run(List.of("name1"))).satisfies(Objects::nonNull);
						assertThat(users.ntByName().run(List.of("name2"))).satisfies(Objects::nonNull);
						assertThat(users.ntByName().run(List.of("name3"))).satisfies(Objects::nonNull);
						assertThat(users.ntByName().run(List.of("name4"))).satisfies(Objects::isNull);
					}

					context.getBean(NTUserTable.class).drop();
					context.getBean(PersonTable.class).drop();
					context.getBeansOfType(DataBase.class).values().forEach(DataBase::drop);
				});
	}

}
