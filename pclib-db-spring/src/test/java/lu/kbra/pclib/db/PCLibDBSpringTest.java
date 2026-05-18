package lu.kbra.pclib.db;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cglib.proxy.Factory;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.config.DataBaseInitializerAutoConfig;
import lu.kbra.pclib.db.config.PCLibDBAutoConfiguration;
import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBRegistrarAutoConfiguration;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.registrar.DeferredSQLQueryableRegistrar;
import lu.kbra.pclib.db.type.ListType;
import lu.kbra.pclib.db.utils.SpringDataBaseEntryUtils;
import mysql.MySQL;

public class PCLibDBSpringTest {

	static {
		MySQL.start();
	}

	@Test
	void autoConfigLoadsMultipleConnectorsTablesAndDatabases() {
		final String peopleDbName = "pclib_spring_people_" + System.nanoTime();
		final String auditDbName = "pclib_spring_audit_" + System.nanoTime();

		new ApplicationContextRunner()
				.withInitializer(context -> AutoConfigurationPackages.register((BeanDefinitionRegistry) context, "lu.kbra.pclib"))
				.withUserConfiguration(DBConfiguration.class)
				.withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class,
						PCLibDBAutoConfiguration.class,
						PCLibDBRegistrarAutoConfiguration.class,
						DataBaseInitializerAutoConfig.class,
						ConfigurationPropertiesAutoConfiguration.class))
				.withBean(ApplicationConversionService.class, ApplicationConversionService::new)
				.withPropertyValues("pclib.db.enabled=true",
						"pclib.db.people.qualifier=peopleDb",
						"pclib.db.people.protocol=mysql",
						"pclib.db.people.host=localhost",
						"pclib.db.people.username=" + MySQL.USER,
						"pclib.db.people.password=" + MySQL.PASS,
						"pclib.db.people.name=" + peopleDbName,
						"pclib.db.people.port=" + MySQL.getPort(),
						"pclib.db.auditDb.protocol=mysql",
						"pclib.db.auditDb.host=localhost",
						"pclib.db.auditDb.username=" + MySQL.USER,
						"pclib.db.auditDb.password=" + MySQL.PASS,
						"pclib.db.auditDb.name=" + auditDbName,
						"pclib.db.auditDb.port=" + MySQL.getPort())
				.run(context -> {
					Assertions.assertThat(context).hasSingleBean(DeferredSQLQueryableRegistrar.class);

					Assertions.assertThat(context).hasBean("peopleDb");
					Assertions.assertThat(context).hasBean("auditDb");
					Assertions.assertThat(context).hasBean("peopleDbConnector");
					Assertions.assertThat(context).hasBean("auditDbConnector");
					Assertions.assertThat(context).hasBean("peopleDbDataBaseEntryUtils");
					Assertions.assertThat(context).hasBean("auditDbDataBaseEntryUtils");

					Assertions.assertThat(context).hasSingleBean(PersonTable.class);
					Assertions.assertThat(context).hasSingleBean(UserTable.class);
					Assertions.assertThat(context).hasSingleBean(AuditLogTable.class);

					final PCLibDBProperties properties = context.getBean(PCLibDBProperties.class);
					Assertions.assertThat(properties.isEnabled()).isTrue();
					Assertions.assertThat(properties.getConnectors()).containsOnlyKeys("people", "auditDb");
					Assertions.assertThat(properties.getRequiredConnector("people").getQualifier()).isEqualTo("peopleDb");
					Assertions.assertThat(properties.getRequiredConnector("auditDb").getQualifier()).isEqualTo("auditDb");

					final Map<String, DataBase> databases = context.getBeansOfType(DataBase.class);
					Assertions.assertThat(databases).containsOnlyKeys("peopleDb", "auditDb");
					Assertions.assertThat(databases.get("peopleDb").getDataBaseName()).isEqualTo(peopleDbName);
					Assertions.assertThat(databases.get("auditDb").getDataBaseName()).isEqualTo(auditDbName);

					Assertions.assertThat(context.getBeansOfType(DataBaseConnectorFactory.class))
							.containsOnlyKeys("peopleDbConnector", "auditDbConnector");

					final SpringDataBaseEntryUtils peopleEntryUtils = context.getBean("peopleDbDataBaseEntryUtils",
							SpringDataBaseEntryUtils.class);
					final SpringDataBaseEntryUtils auditEntryUtils = context.getBean("auditDbDataBaseEntryUtils",
							SpringDataBaseEntryUtils.class);
					Assertions.assertThat(peopleEntryUtils.getTypeMap())
							.containsKeys(List.class, ArrayList.class, LinkedList.class, ListType.class);
					Assertions.assertThat(auditEntryUtils.getTypeMap())
							.containsKeys(List.class, ArrayList.class, LinkedList.class, ListType.class);

					context.getBeansOfType(DataBase.class).values().forEach(db -> db.getTableBeans().values().forEach(table -> {
						if (table instanceof DeferredSQLQueryable<?>) {
//							System.err.println(table);
							Assertions.assertThat(table).isInstanceOf(Factory.class);
						}
					}));

					try {
						final PersonTable people = context.getBean(PersonTable.class);
						final UserTable users = context.getBean(UserTable.class);
						final AuditLogTable auditLog = context.getBean(AuditLogTable.class);

						Assertions.assertThat(people.getDataBase()).isSameAs(databases.get("peopleDb"));
						Assertions.assertThat(users.getDataBase()).isSameAs(databases.get("peopleDb"));
						Assertions.assertThat(auditLog.getDataBase()).isSameAs(databases.get("auditDb"));

						Assertions.assertThat(people.count()).isEqualTo(people.truncate());
						Assertions.assertThat(users.count()).isEqualTo(users.truncate());
						Assertions.assertThat(auditLog.count()).isEqualTo(auditLog.truncate());

						people.insertAndReload(new PersonData("person-1"));
						people.insertAndReload(new PersonData("person-2"));
						users.insertAndReload(new UserData("user-1", "pass-1"));
						auditLog.insertAndReload(new AuditLogData("audit-1"));

						Assertions.assertThat(people.byName("person-1")).satisfies(Optional::isPresent);
						Assertions.assertThat(people.byName("person-2")).satisfies(Optional::isPresent);
						Assertions.assertThat(people.byName("person-3")).satisfies(Optional::isEmpty);

						Assertions.assertThat(users.byName("user-1")).satisfies(Optional::isPresent);
						Assertions.assertThat(users.byName("user-2")).satisfies(Optional::isEmpty);

						Assertions.assertThat(auditLog.byEvent("audit-1")).satisfies(Optional::isPresent);
						Assertions.assertThat(auditLog.byEvent("audit-2")).satisfies(Optional::isEmpty);
					} finally {
						context.getBeansOfType(DataBase.class).values().forEach(db -> db.getTableBeans().values().forEach(table -> {
							try {
								table.drop();
							} catch (final RuntimeException ignored) {
							}
						}));
						context.getBeansOfType(DataBase.class).values().forEach(db -> {
							try {
								db.drop();
							} catch (final RuntimeException ignored) {
							}
						});
					}
				});
	}

}
