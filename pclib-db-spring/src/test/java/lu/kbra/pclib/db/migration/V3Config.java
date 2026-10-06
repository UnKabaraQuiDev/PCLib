package lu.kbra.pclib.db.migration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.base.Database;

import shared.migration.third.table.AddressTable3;
import shared.migration.third.table.CityTable3;
import shared.migration.third.table.CountryTable3;
import shared.migration.third.table.GarageTable3;
import shared.migration.third.table.PersonTable3;

@Configuration
public class V3Config {

	@Bean
	PersonTable3 personTable3(Database database) {
		return new PersonTable3(database);
	}

	@Bean
	GarageTable3 garageTable3(Database database) {
		return new GarageTable3(database);
	}

	@Bean
	CountryTable3 countryTable3(Database database) {
		return new CountryTable3(database);
	}

	@Bean
	CityTable3 cityTable3(Database database) {
		return new CityTable3(database);
	}

	@Bean
	AddressTable3 addressTable3(Database database) {
		return new AddressTable3(database);
	}

	@Bean
	V1Migration migration1() {
		return new V1Migration();
	}

	@Bean
	V2Migration migration3() {
		return new V2Migration();
	}

	@Bean
	V3Migration migration2() {
		return new V3Migration();
	}

}
