package lu.kbra.pclib.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.convert.ConversionService;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.config.DataBaseInitializerAutoConfig;
import lu.kbra.pclib.db.config.PCLibDBAutoConfiguration;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.registrar.DeferredSQLQueryableRegistrar;
import lu.kbra.pclib.db.type.ListType;
import lu.kbra.pclib.db.utils.SpringDataBaseEntryUtils;
import mysql.MySQL;

public class PCLibDBSpringTest {

	@Test
	void autoConfigLoads() {
		new ApplicationContextRunner()
				.withInitializer(context -> AutoConfigurationPackages.register((BeanDefinitionRegistry) context, "lu.kbra.pclib"))
				.withUserConfiguration(DBConfiguration.class)
				.withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class,
						PCLibDBAutoConfiguration.class,
						DataBaseInitializerAutoConfig.class,
						ConfigurationPropertiesAutoConfiguration.class))
				.withBean(ConversionService.class, ApplicationConversionService::new)
				.run(context -> {
					Assertions.assertThat(context).hasSingleBean(DeferredSQLQueryableRegistrar.class);
					Assertions.assertThat(context).hasSingleBean(PersonTable.class);
					Assertions.assertThat(context).hasSingleBean(SpringDataBaseEntryUtils.class);

					{
						final SpringDataBaseEntryUtils dbEntryUtils = context.getBean(SpringDataBaseEntryUtils.class);
						Assertions.assertThat(dbEntryUtils.getTypeMap())
								.containsKeys(List.class, ArrayList.class, LinkedList.class, ListType.class);
					}

					{
						context.getBeansOfType(DataBase.class).values().forEach(db -> db.getTableBeans().values().forEach(f -> {
							if (f instanceof DeferredSQLQueryable<?>) {
								Assertions.assertThat(AopUtils.isAopProxy(f));
							}
						}));
					}

					{
						final PersonTable people = context.getBean(PersonTable.class);
						Assertions.assertThat(people.count()).isEqualTo(people.truncate());

						people.insertAndReload(new PersonData("name1"));
						people.insertAndReload(new PersonData("name2"));

						Assertions.assertThat(people.byName("name1")).satisfies(Optional::isPresent);
						Assertions.assertThat(people.byName("name2")).satisfies(Optional::isPresent);
						Assertions.assertThat(people.byName("name3")).satisfies(Optional::isEmpty);
					}

					context.getBean(PersonTable.class).drop();
					context.getBeansOfType(DataBase.class).values().forEach(DataBase::drop);
				});
	}

}
