package lu.kbra.pclib.db.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.db.base.MoreTypeFactory.Age;
import lu.kbra.pclib.db.base.MoreTypeFactory.AgeType;
import lu.kbra.pclib.db.config.DataBaseInitializerAutoConfig;
import lu.kbra.pclib.db.config.PCLibDBAutoConfiguration;
import lu.kbra.pclib.db.config.PCLibDBProperties;
import lu.kbra.pclib.db.config.PCLibDBRegistrarAutoConfiguration;
import lu.kbra.pclib.db.connector.impl.DataBaseConnectorFactory;
import lu.kbra.pclib.db.dbms.SQLiteStructureVisitor;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.registrar.DeferredSQLQueryableRegistrar;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.SpringDataBaseEntryUtils;
import mysql.MySQL;
import postgres.PostgreSQL;
import sqlite.SQLite;

public class PCLibDBSpringTest {

	private static final class ProtocolConfig {

		private static ProtocolConfig mysql() {
			MySQL.start();
			return new ProtocolConfig("mysql",
					"mysql",
					new String[] { "host=localhost", "username=" + MySQL.USER, "password=" + MySQL.PASS, "port=" + MySQL.getPort() },
					() -> {
					});
		}

		private static ProtocolConfig postgres() {
			PostgreSQL.start();
			return new ProtocolConfig("postgresql",
					"postgresql",
					new String[] {
							"host=localhost",
							"username=" + PostgreSQL.USER,
							"password=" + PostgreSQL.PASS,
							"port=" + PostgreSQL.getPort() },
					() -> {
					});
		}

		private static ProtocolConfig sqlite(final Path dir) {
			return new ProtocolConfig("sqlite", "sqlite", new String[] { "dir-path=" + dir.toAbsolutePath() }, () -> {
				try {
					SQLite.deleteDirectory(dir);
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			});
		}

		private final String displayName;
		private final String protocol;
		private final String[] connectionProperties;
		private final Runnable cleanup;

		private ProtocolConfig(
				final String displayName,
				final String protocol,
				final String[] connectionProperties,
				final Runnable cleanup) {
			this.displayName = displayName;
			this.protocol = protocol;
			this.connectionProperties = connectionProperties;
			this.cleanup = cleanup;
		}

		@Override
		public String toString() {
			return this.displayName;
		}

		private void cleanup() {
			this.cleanup.run();
		}

		private String[] properties(final String connectorName, final String qualifier, final String databaseName) {
			final String prefix = "pclib.db." + connectorName + ".";
			final String[] properties = new String[3 + this.connectionProperties.length];
			properties[0] = prefix + "qualifier=" + qualifier;
			properties[1] = prefix + "protocol=" + this.protocol;
			properties[2] = prefix + "name=" + databaseName;

			for (int i = 0; i < this.connectionProperties.length; i++) {
				properties[i + 3] = prefix + this.connectionProperties[i];
			}
			return properties;
		}

	}

	static {
		MySQL.start();
		PostgreSQL.start();
	}

	static Stream<Arguments> queryProtocols() throws IOException {
		final Path sqliteDir = Files.createTempDirectory(SQLite.createTempDirectory(), "spring-query-");

		return Stream.of(Arguments.of(ProtocolConfig.mysql()),
				Arguments.of(ProtocolConfig.postgres()),
				Arguments.of(ProtocolConfig.sqlite(sqliteDir)));
	}

	private static void assertPersonName(final Optional<PersonData> person, final String name) {
		Assertions.assertThat(person).isPresent();
		Assertions.assertThat(person.get().name).isEqualTo(name);
	}

	private static void assertPersonQueryMethods(final PersonTable people) {
		final PersonData alpha = people.insertAndReload(new PersonData("query-alpha"));
		final PersonData beta = people.insertAndReload(new PersonData("query-beta"));
		final PersonData gamma = people.insertAndReload(new PersonData("query-gamma"));
		people.insertAndReload(new PersonData("other-delta"));

		PCLibDBSpringTest.assertPersonName(people.byName("query-alpha"), "query-alpha");
		PCLibDBSpringTest.assertPersonName(people.byNameWithExplicitSql("query-beta"), "query-beta");
		PCLibDBSpringTest.assertPersonName(people.byNameWithParam("query-gamma"), "query-gamma");

		Assertions.assertThat(people.byName("missing")).isEmpty();
		Assertions.assertThat(people.byNameWithExplicitSql("missing")).isEmpty();
		Assertions.assertThat(people.byNameWithParam("missing")).isEmpty();

		Assertions.assertThat(people.byNameLike("query-%"))
				.extracting(person -> person.name)
				.containsExactlyInAnyOrder("query-alpha", "query-beta", "query-gamma");

		Assertions.assertThat(people.orderedByIdDesc(null, 2, 1))
				.extracting(person -> person.name)
				.containsExactly("query-gamma", "query-beta");

		Assertions.assertThat(people.orderedByIdDesc("query-beta", 10, 0)).extracting(person -> person.name).containsExactly("query-beta");

		Assertions.assertThat(people.nameValueByName("query-alpha")).isEqualTo("query-alpha");
		Assertions.assertThat(people.nameValueByName("query-beta")).isEqualTo("query-beta");

		Assertions.assertThat(people.optionalNameValueByName("query-gamma")).contains("query-gamma");
		Assertions.assertThat(people.optionalNameValueByName("missing")).isEmpty();

		Assertions.assertThat(people.nameValuesByNameLike("query-%")).containsExactly("query-alpha", "query-beta", "query-gamma");

		Assertions.assertThat(people.nameValuesByNameLike("missing-%")).isEmpty();

		Assertions.assertThat(people.countByNameLike("query-%")).isEqualTo(3);
		Assertions.assertThat(people.countByNameLike("other-%")).isEqualTo(1);
		Assertions.assertThat(people.countByNameLike("missing-%")).isZero();

		Assertions.assertThat(people.idValueByName("query-alpha")).isEqualTo(alpha.id);
		Assertions.assertThat(people.idValueByName("query-beta")).isEqualTo(beta.id);
		Assertions.assertThat(people.idValueByName("query-gamma")).isEqualTo(gamma.id);
	}

	private static void assertUserAndAuditQueryMethods(final UserTable users, final AuditLogTable auditLog) {
		users.insertAndReload(new UserData("query-user", "query-pass"));
		auditLog.insertAndReload(new AuditLogData("query-audit"));

		Assertions.assertThat(users.byName("query-user")).isPresent();
		Assertions.assertThat(users.byName("missing-user")).isEmpty();
		Assertions.assertThat(auditLog.byEvent("query-audit")).isPresent();
		Assertions.assertThat(auditLog.byEvent("missing-audit")).isEmpty();
	}

	private static void dropAll(final Map<String, AbstractDBTable> tables, final Map<String, DataBase> databases) {
		tables.values().forEach(table -> {
			try {
				table.drop();
			} catch (final RuntimeException ignored) {
			}
		});
		databases.values().forEach(db -> {
			try {
				db.drop();
			} catch (final RuntimeException ignored) {
			}
		});
	}

	@Test
	void autoConfigLoadsMultipleConnectorsTablesAndDatabases() {
		final String peopleName = "pclib_spring_people_" + System.nanoTime();
		final String auditDbName = "pclib_spring_audit_" + System.nanoTime();

		new ApplicationContextRunner()
				.withInitializer(context -> AutoConfigurationPackages.register((BeanDefinitionRegistry) context,
						PCLibDBSpringTest.class.getPackageName()))
				.withUserConfiguration(DBConfiguration.class)
				.withConfiguration(AutoConfigurations.of(PCLibDBAutoConfiguration.class,
						PCLibDBRegistrarAutoConfiguration.class,
						DataBaseInitializerAutoConfig.class,
						ConfigurationPropertiesAutoConfiguration.class))
				.withBean(ApplicationConversionService.class, ApplicationConversionService::new)
				.withBean(ObjectMapper.class, ObjectMapper::new)
				.withPropertyValues("pclib.db.enabled=true",
						"pclib.db.people.qualifier=people",
						"pclib.db.people.protocol=mysql",
						"pclib.db.people.host=localhost",
						"pclib.db.people.username=" + MySQL.USER,
						"pclib.db.people.password=" + MySQL.PASS,
						"pclib.db.people.name=" + peopleName,
						"pclib.db.people.port=" + MySQL.getPort(),
						"pclib.db.auditDb.protocol=mysql",
						"pclib.db.auditDb.host=localhost",
						"pclib.db.auditDb.username=" + MySQL.USER,
						"pclib.db.auditDb.password=" + MySQL.PASS,
						"pclib.db.auditDb.name=" + auditDbName,
						"pclib.db.auditDb.port=" + MySQL.getPort())
				.run(context -> {
					Assertions.assertThat(context).hasSingleBean(DeferredSQLQueryableRegistrar.class);

					Assertions.assertThat(context).hasBean("people");
					Assertions.assertThat(context).hasBean("auditDb");
					Assertions.assertThat(context).hasBean("peopleConnector");
					Assertions.assertThat(context).hasBean("auditDbConnector");
					Assertions.assertThat(context).hasBean("peopleDataBaseEntryUtils");
					Assertions.assertThat(context).hasBean("auditDbDataBaseEntryUtils");

					Assertions.assertThat(context).hasSingleBean(PersonTable.class);
					Assertions.assertThat(context).hasSingleBean(UserTable.class);
					Assertions.assertThat(context).hasSingleBean(AuditLogTable.class);

					final PCLibDBProperties properties = context.getBean(PCLibDBProperties.class);
					Assertions.assertThat(properties.isEnabled()).isTrue();
					Assertions.assertThat(properties.getConnectors()).containsOnlyKeys("people", "auditDb");
					Assertions.assertThat(properties.getRequiredConnector("people").getQualifier()).isEqualTo("people");
					Assertions.assertThat(properties.getRequiredConnector("auditDb").getQualifier()).isEqualTo("auditDb");

					final Map<String, DataBase> databases = context.getBeansOfType(DataBase.class);
					Assertions.assertThat(databases).containsOnlyKeys("people", "auditDb");
					Assertions.assertThat(databases.get("people").getDataBaseName()).isEqualTo(peopleName);
					Assertions.assertThat(databases.get("auditDb").getDataBaseName()).isEqualTo(auditDbName);

					Assertions.assertThat(context.getBeansOfType(DataBaseConnectorFactory.class))
							.containsOnlyKeys("peopleConnector", "auditDbConnector");

					final SpringDataBaseEntryUtils peopleEntryUtils = context.getBean("peopleDataBaseEntryUtils",
							SpringDataBaseEntryUtils.class);
					final SpringDataBaseEntryUtils auditEntryUtils = context.getBean("auditDbDataBaseEntryUtils",
							SpringDataBaseEntryUtils.class);

					Assertions.assertThat(peopleEntryUtils).isNotNull();
					Assertions.assertThat(auditEntryUtils).isNotNull();

					try {
						final PersonTable people = context.getBean(PersonTable.class);
						final UserTable users = context.getBean(UserTable.class);
						final AuditLogTable auditLog = context.getBean(AuditLogTable.class);

						Assertions.assertThat(people.getDatabase()).isSameAs(databases.get("people"));
						Assertions.assertThat(users.getDatabase()).isSameAs(databases.get("people"));
						Assertions.assertThat(auditLog.getDatabase()).isSameAs(databases.get("auditDb"));

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

						Assertions.assertThat(people.nameValueByName("person-1")).isEqualTo("person-1");
						Assertions.assertThat(people.optionalNameValueByName("person-2")).contains("person-2");
						Assertions.assertThat(people.optionalNameValueByName("person-3")).isEmpty();

						Assertions.assertThat(users.byName("user-1")).satisfies(Optional::isPresent);
						Assertions.assertThat(users.byName("user-2")).satisfies(Optional::isEmpty);

						Assertions.assertThat(auditLog.byEvent("audit-1")).satisfies(Optional::isPresent);
						Assertions.assertThat(auditLog.byEvent("audit-2")).satisfies(Optional::isEmpty);
					} finally {
						PCLibDBSpringTest.dropAll(context.getBeansOfType(AbstractDBTable.class), context.getBeansOfType(DataBase.class));
					}
				});
	}

	@ParameterizedTest(name = "@Query methods work on {0}")
	@MethodSource("queryProtocols")
	void queryMethodsWorkForAllProtocols(final ProtocolConfig protocol) {
		final String peopleName = "pclib_spring_query_people_" + System.nanoTime();
		final String auditDbName = "pclib_spring_query_audit_" + System.nanoTime();

		try {
			System.setProperty(SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY, Boolean.TRUE.toString());

			new ApplicationContextRunner()
					.withInitializer(context -> AutoConfigurationPackages.register((BeanDefinitionRegistry) context, "lu.kbra.pclib"))
					.withUserConfiguration(DBConfiguration.class)
					.withConfiguration(AutoConfigurations.of(PCLibDBAutoConfiguration.class,
							PCLibDBRegistrarAutoConfiguration.class,
							DataBaseInitializerAutoConfig.class,
							ConfigurationPropertiesAutoConfiguration.class))
					.withBean(ApplicationConversionService.class, ApplicationConversionService::new)
					.withBean(ObjectMapper.class, ObjectMapper::new)
					.withBean(MoreTypeFactory.class, MoreTypeFactory::new)
					.withPropertyValues(protocol.properties("people", "people", peopleName))
					.withPropertyValues(protocol.properties("auditDb", "auditDb", auditDbName))
					.run(context -> {
						try {
							final PersonTable people = context.getBean(PersonTable.class);
							final UserTable users = context.getBean(UserTable.class);
							final AuditLogTable auditLog = context.getBean(AuditLogTable.class);

							people.insertAndReload(new PersonData("person-1"));
							people.insertAndReload(new PersonData("person-2"));
							users.insertAndReload(new UserData("user-1", "pass-1"));
							auditLog.insertAndReload(new AuditLogData("audit-1"));

							Assertions.assertThat(people.count()).isEqualTo(people.truncate());
							Assertions.assertThat(users.count()).isEqualTo(users.truncate());
							Assertions.assertThat(auditLog.count()).isEqualTo(auditLog.truncate());

							PCLibDBSpringTest.assertPersonQueryMethods(people);
							PCLibDBSpringTest.assertUserAndAuditQueryMethods(users, auditLog);

							Assertions.assertThat(context).hasSingleBean(MoreTypeFactory.class);
							PCLibDBSpringTest
									.assertMoreTypeRegistered(protocol, context.getBean(MoreTypeFactory.class), people, users, auditLog);
						} finally {
							PCLibDBSpringTest.dropAll(context.getBeansOfType(AbstractDBTable.class),
									context.getBeansOfType(DataBase.class));
						}
					});
		} finally {
			protocol.cleanup();
		}
	}

	private static void assertMoreTypeRegistered(
			final ProtocolConfig config,
			final MoreTypeFactory typeFactory,
			final PersonTable people,
			final UserTable users,
			final AuditLogTable auditLog) {
		if (typeFactory.matches(config.protocol)) {
			for (final SQLQueryable<?> sqlQueryable : new SQLQueryable<?>[] { people, users, auditLog }) {
				Assertions.assertThat(sqlQueryable.getDataBaseEntryUtils()
						.getColumnTypeProvider()
						.computeType(Age.class, Collections.emptyMap())
						.distinct()
						.count()).isGreaterThanOrEqualTo(1);
				Assertions.assertThat(sqlQueryable.getDataBaseEntryUtils()
						.getColumnTypeProvider()
						.getTypeFor(Age.class, Optional.empty(), Collections.emptyMap())).isInstanceOf(AgeType.class);
			}
		} else {
			Assertions.assertThat(people.getDataBaseEntryUtils()
					.getColumnTypeProvider()
					.computeType(Age.class, Collections.emptyMap())
					.distinct()
					.count()).isEqualTo(0);
			Assertions.assertThatThrownBy(() -> people.getDataBaseEntryUtils()
					.getColumnTypeProvider()
					.getTypeFor(Age.class, Optional.empty(), Collections.emptyMap())).isInstanceOf(IllegalArgumentException.class);
		}
	}

}
