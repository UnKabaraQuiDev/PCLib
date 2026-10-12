package lu.kbra.pclib.db.migration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import lu.kbra.pclib.db.ProtocolConfig;
import lu.kbra.pclib.db.config.DatabaseInitializerAutoConfig;
import lu.kbra.pclib.db.config.PCLibDBAutoConfiguration;
import lu.kbra.pclib.db.config.PCLibDBRegistrarAutoConfiguration;
import lu.kbra.pclib.db.dbms.SQLiteStructureVisitor;
import lu.kbra.pclib.db.query.QueryBuilder;

import lombok.extern.slf4j.Slf4j;
import mysql.MySQL;
import postgres.PostgreSQL;
import shared.migration.initial.data.CityData1;
import shared.migration.initial.data.GarageData1;
import shared.migration.initial.data.PersonData1;
import shared.migration.initial.table.CityTable1;
import shared.migration.initial.table.GarageTable1;
import shared.migration.initial.table.PersonTable1;
import shared.migration.third.data.GarageData3;
import shared.migration.third.table.AddressTable3;
import shared.migration.third.table.CityTable3;
import shared.migration.third.table.CountryTable3;
import shared.migration.third.table.GarageTable3;
import shared.migration.third.table.PersonTable3;
import sqlite.SQLite;

@ParameterizedClass
@MethodSource("queryProtocols")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
/**
 * skip the v2, directly from v1 -> v3
 */
class SpringMigrationTest2 {

	private static final Map<String, String> DB_NAMES = new HashMap<>();
	private static final Set<ProtocolConfig> NEED_CLEANUP = new HashSet<>();

	static {
		MySQL.start();
		PostgreSQL.start();
	}

	private final ProtocolConfig protocol;
	private final String maindb;

	SpringMigrationTest2(final ProtocolConfig protocol) {
		this.protocol = protocol;
		this.maindb = SpringMigrationTest2.DB_NAMES.computeIfAbsent(protocol.getProtocol(), k -> "main" + System.nanoTime());
		SpringMigrationTest2.NEED_CLEANUP.add(protocol);
	}

	static Stream<ProtocolConfig> queryProtocols() throws IOException {
		final Path sqliteDir = Files.createTempDirectory(SQLite.createTempDirectory(), "spring-query-");

		return Stream.of(ProtocolConfig.mysql(), ProtocolConfig.postgres(), ProtocolConfig.sqlite(sqliteDir));
	}

	@BeforeAll
	public static void setUp() {
		System.setProperty(SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY, Boolean.TRUE.toString());
	}

	@AfterAll
	public static void cleanup() {
		SpringMigrationTest2.NEED_CLEANUP.forEach(ProtocolConfig::cleanup);
	}

	@Test
	@Order(0)
	void first() {
		SpringMigrationTest2.log.info("FIRST === {}", this.protocol.getDisplayName());
		new ApplicationContextRunner().withUserConfiguration(DBConfiguration.class)
				.withInitializer(context -> AutoConfigurationPackages.register((BeanDefinitionRegistry) context,
						SpringMigrationTest2.class.getPackageName()))
				.withUserConfiguration(V1Config.class)
				.withConfiguration(AutoConfigurations.of(PCLibDBAutoConfiguration.class,
						PCLibDBRegistrarAutoConfiguration.class,
						DatabaseInitializerAutoConfig.class,
						ConfigurationPropertiesAutoConfiguration.class))
				.withBean(ApplicationConversionService.class, ApplicationConversionService::new)
				.withBean(ObjectMapper.class, ObjectMapper::new)
				.withPropertyValues(this.protocol.properties("main", "main", this.maindb))
				.run(this::assertFirstContext);
	}

	@Test
	@Order(2)
	void third() {
		SpringMigrationTest2.log.info("THIRD === {}", this.protocol.getDisplayName());
		new ApplicationContextRunner().withUserConfiguration(DBConfiguration.class)
				.withInitializer(context -> AutoConfigurationPackages.register((BeanDefinitionRegistry) context,
						SpringMigrationTest2.class.getPackageName()))
				.withUserConfiguration(V3Config.class)
				.withConfiguration(AutoConfigurations.of(PCLibDBAutoConfiguration.class,
						PCLibDBRegistrarAutoConfiguration.class,
						DatabaseInitializerAutoConfig.class,
						ConfigurationPropertiesAutoConfiguration.class))
				.withBean(ApplicationConversionService.class, ApplicationConversionService::new)
				.withBean(ObjectMapper.class, ObjectMapper::new)
				.withPropertyValues(this.protocol.properties("main", "main", this.maindb))
				.run(this::assertThirdContext);
	}

	private void assertFirstContext(final AssertableApplicationContext context) {
		final GarageTable1 garages = context.getBean(GarageTable1.class);
		final PersonTable1 people = context.getBean(PersonTable1.class);
		final CityTable1 cities = context.getBean(CityTable1.class);

		assert garages.exists();
		assert cities.exists();
		assert people.exists();

		final GarageData1 garage1 = new GarageData1();
		garage1.setName("Garage 1");
		garages.insertAndReload(garage1);

		final GarageData1 garage2 = new GarageData1();
		garage2.setName("Garage 2");
		garages.insertAndReload(garage2);

		final CityData1 city1 = new CityData1();
		city1.setGarageId(garage1.getId());
		city1.setName("City 1");
		city1.setPostalCode("10000");
		cities.insertAndReload(city1);

		final CityData1 city2 = new CityData1();
		city2.setGarageId(garage2.getId());
		city2.setName("City 2");
		city2.setPostalCode("20000");
		cities.insertAndReload(city2);

		final PersonData1 person1 = new PersonData1();
		person1.setCityId(city1.getId());
		person1.setEmail("person1@example.com");
		person1.setName("Person 1");
		people.insertAndReload(person1);

		final PersonData1 person2 = new PersonData1();
		person2.setCityId(city2.getId());
		person2.setEmail("person2@example.com");
		person2.setName("Person 2");
		people.insertAndReload(person2);

		assert garages.count() == 2;
		assert cities.count() == 2;
		assert people.count() == 2;
	}

	private void assertThirdContext(final AssertableApplicationContext context) {
		final GarageTable3 garages = context.getBean(GarageTable3.class);
		final PersonTable3 people = context.getBean(PersonTable3.class);
		final CityTable3 cities = context.getBean(CityTable3.class);
		final CountryTable3 countries = context.getBean(CountryTable3.class);
		final AddressTable3 addresses = context.getBean(AddressTable3.class);

		assert garages.exists();
		assert countries.exists();
		assert cities.exists();
		assert people.exists();
		assert addresses.exists();

		final GarageData3 migratedGarage = garages.query(QueryBuilder.<GarageData3>select().limit(1).firstNull());

		assert migratedGarage != null;
		assert "Garage 1".equals(migratedGarage.getName());

		assert garages.count() == 2 : "Garage data was lost during 2 -> 3 migration.";
		assert cities.count() == 2 : "City data was lost during 2 -> 3 migration.";
		assert people.count() == 2 : "Person data was lost during 2 -> 3 migration.";
	}

}
