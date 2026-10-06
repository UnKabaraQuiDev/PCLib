package lu.kbra.pclib.db.migration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.base.Database;

import shared.migration.second.table.CityTable2;
import shared.migration.second.table.CountryTable2;
import shared.migration.second.table.GarageTable2;
import shared.migration.second.table.PersonTable2;

@Configuration
public class V2Config {

	@Bean
	PersonTable2 personTable2(Database database) {
		return new PersonTable2(database);
	}

	@Bean
	GarageTable2 garageTable2(Database database) {
		return new GarageTable2(database);
	}

	@Bean
	CountryTable2 countryTable2(Database database) {
		return new CountryTable2(database);
	}

	@Bean
	CityTable2 cityTable2(Database database) {
		return new CityTable2(database);
	}

	@Bean
	V2Migration migration2() {
		return new V2Migration();
	}

}
