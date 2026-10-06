package lu.kbra.pclib.db.migration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lu.kbra.pclib.db.base.Database;

import shared.migration.initial.table.CityTable1;
import shared.migration.initial.table.GarageTable1;
import shared.migration.initial.table.PersonTable1;

@Configuration
public class V1Config {

	@Bean
	PersonTable1 personTable1(Database database) {
		return new PersonTable1(database);
	}

	@Bean
	GarageTable1 garageTable1(Database database) {
		return new GarageTable1(database);
	}

	@Bean
	CityTable1 cityTable1(Database database) {
		return new CityTable1(database);
	}

}
