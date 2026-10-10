package lu.kbra.pclib.db.migration;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import shared.migration.second.data.CountryData2;
import shared.migration.table.CityTable;
import shared.migration.table.CountryTable;
import shared.migration.table.GarageTable;
import shared.migration.third.data.CountryData3;

public class V2Migration implements DatabaseMigration {

	@Lazy
	@Autowired
	private CityTable<?> cities;
	@Lazy
	@Autowired
	private GarageTable<?> garages;
	@Lazy
	@Autowired
	private CountryTable countries;

	DatabaseMigrationPhase[] phases = new DatabaseMigrationPhase[] {
			new ManualMigrationPhase("update-country-code-col", null, 0, MigrationPhase.ADD_COLUMNS) {

				@Override
				public void up(Statement stmt) throws SQLException {

					if (Arrays.stream(cities.getStructure().getColumns()).anyMatch(c -> "country_code".equals(c.getLocalName()))) {
						stmt.execute("UPDATE " + cities.getQualifiedName() + " SET country_code = 'CC';");
					}

				}

			},
			new ManualMigrationPhase("update-address-col", null, 0, MigrationPhase.ADD_COLUMNS) {

				@Override
				public void up(Statement stmt) throws SQLException {

					if (Arrays.stream(garages.getStructure().getColumns()).anyMatch(c -> "address".equals(c.getLocalName()))) {
						stmt.execute("UPDATE " + garages.getQualifiedName() + " SET address = 'something not null';");
					}

				}

			},
			new ManualMigrationPhase("insert-default-country", null, 0, MigrationPhase.ADD_TABLE) {

				@Override
				public void up(Statement stmt) throws SQLException {

					if (countries.getEntryClass() == CountryData2.class) {
						countries.insert(new CountryData2(null, "CC", "CC's name"));
					} else if (countries.getEntryClass() == CountryData3.class) {
						countries.insert(new CountryData3(null, "CC", "CC's name"));
					}

				}

			} };

	@Override
	public DatabaseMigrationPhase[] phases() {
		return phases;
	}

	@Override
	public int order() {
		return 0;
	}

	@Override
	public String name() {
		return "V2 Bump";
	}

	@Override
	public String id() {
		return "v2";
	}

}
