package test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.migration.DatabaseMigration;
import lu.kbra.pclib.db.migration.SchemaHashCalculator;

import shared.migration.initial.data.CityData1;
import shared.migration.initial.data.GarageData1;
import shared.migration.initial.data.PersonData1;
import shared.migration.initial.table.CityTable1;
import shared.migration.initial.table.GarageTable1;
import shared.migration.initial.table.PersonTable1;
import shared.migration.second.data.GarageData2;
import shared.migration.second.table.CityTable2;
import shared.migration.second.table.CountryTable2;
import shared.migration.second.table.GarageTable2;
import shared.migration.second.table.PersonTable2;
import shared.migration.third.table.AddressTable3;
import shared.migration.third.table.CityTable3;
import shared.migration.third.table.CountryTable3;
import shared.migration.third.table.GarageTable3;
import shared.migration.third.table.PersonTable3;

public interface DbMigrationTest extends GenericDBTest {

	@Test
	default void testSchemaMigration() throws SQLException {
		final Database database = this.getDatabase();

		/*
		 * STEP 1 — INITIAL SCHEMA
		 */

		final GarageTable1 garages = new GarageTable1(database);
		final CityTable1 cities = new CityTable1(database);
		final PersonTable1 people = new PersonTable1(database);

		database.clearBeans().register(garages, cities, people).initMigrationSupport(true).scanFromBeans();

		System.out.println("========== INITIAL ==========");
//		System.out.println("Structure:\n" + database.getStructure().toTreeString());

		assert !garages.exists();
		assert !cities.exists();
		assert !people.exists();

		database.createBeans((t, b) -> {
			if (t instanceof SQLQueryable<?>) {
				assert b : "Couldn't create" + ((SQLQueryable<?>) t).getName();
				System.out.println("Created: " + ((SQLQueryable<?>) t).getName());
			}
		});

		database.migrate(Collections.emptyList());

		assert garages.exists();
		assert cities.exists();
		assert people.exists();

		/*
		 * Insert some data so the migration test also proves that migrations preserve existing data.
		 */

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

		/*
		 * Capture the initial structure/hash before changing the registered Java model.
		 */

		final DatabaseStructure initialStructure = database.getStructure();

//		System.out.println("Initial structure:\n" + initialStructure.toTreeString());

		final String initialHash = SchemaHashCalculator.calculate(initialStructure);

		System.out.println("Initial schema hash: " + initialHash);

		/*
		 * STEP 2 — CHANGE SCHEMA
		 *
		 * Rebuild the Database bean registry with 2 tables.
		 */

		database.getConnector().reset();
		database.clearBeans();

		final GarageTable2 garages2 = new GarageTable2(database);
		final CountryTable2 countries2 = new CountryTable2(database);
		final CityTable2 cities2 = new CityTable2(database);
		final PersonTable2 people2 = new PersonTable2(database);

		database.setMigrationSupport(null);
		database.register(garages2, countries2, cities2, people2).initMigrationSupport(true).scanFromBeans().createBeans(null);

		System.out.println("========== SECOND ==========");
//		System.out.println("Structure:\n" + database.getStructure().toTreeString());

		final DatabaseStructure secondStructure = database.getStructure();

		final String secondHash = SchemaHashCalculator.calculate(secondStructure);

		System.out.println("Second schema hash: " + secondHash);

		assert !initialHash.equals(secondHash) : "Schema hash should change after modifying the schema.";

		/*
		 * The migration support should compare the stored INITIAL snapshot against this newly scanned
		 * structure and execute the required migration.
		 */

		database.migrate(Collections.emptyList());

		final DatabaseStructure migratedSecondStructure = database.getStructure();

//		System.out.println("Migrated second structure:\n" + migratedSecondStructure.toTreeString());

		final String migratedSecondHash = SchemaHashCalculator.calculate(migratedSecondStructure);

		assert secondHash.equals(migratedSecondHash) : "Database structure does not match 2 structure after migration.";

		/*
		 * Verify that the actual tables exist.
		 */
		assert garages2.exists();
		assert countries2.exists();
		assert cities2.exists();
		assert people2.exists();

		/*
		 * Verify data survived the migration.
		 *
		 * The exact assertions here should be adjusted depending on whether postal_code -> zip_code is
		 * intentionally treated as REMOVE + ADD or as an explicit rename.
		 */

		assert garages2.count() == 2 : "Garage data was lost during migration.";
		assert cities2.count() == 2 : "City data was lost during migration.";
		assert people2.count() == 2 : "Person data was lost during migration.";

		/*
		 * Verify the actual migrated values where the column still exists unchanged.
		 */
		final GarageData2 migratedGarage = garages2.load(new GarageData2(garage1.getId()));

		assert migratedGarage != null;
		assert "Garage 1".equals(migratedGarage.getName());

		/*
		 * STEP 3 — CHANGE SCHEMA AGAIN
		 */

		database.getConnector().reset();
		database.clearBeans();

		final GarageTable3 garages3 = new GarageTable3(database);
		final CountryTable3 countries3 = new CountryTable3(database);
		final CityTable3 cities3 = new CityTable3(database);
		final PersonTable3 people3 = new PersonTable3(database);
		final AddressTable3 addresses3 = new AddressTable3(database);

		database.setMigrationSupport(null);
		database.register(garages3, countries3, cities3, people3, addresses3).initMigrationSupport(true).scanFromBeans();

		System.out.println("========== THIRD ==========");
//		System.out.println("Structure:\n" + database.getStructure().toTreeString());

		final DatabaseStructure thirdStructure = database.getStructure();

		final String thirdHash = SchemaHashCalculator.calculate(thirdStructure);

		System.out.println("Third schema hash: " + thirdHash);

		assert !secondHash.equals(thirdHash) : "Schema hash should change after the second schema modification.";

		/*
		 * Execute 2 -> 3 migration.
		 */
		database.migrate(Arrays.asList(new DatabaseMigration() {

			@Override
			public void up(Connection connection) throws DBException {
				try {
					connection.createStatement().execute("UPDATE person SET phone = name;");
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			@Override
			public int order() {
				return 0;
			}

			@Override
			public String name() {
				return "Add phone";
			}

			@Override
			public String id() {
				return "add_phone";
			}
		}));

		final DatabaseStructure migratedThirdStructure = database.getStructure();

//		System.out.println("Migrated third structure:\n" + migratedThirdStructure.toTreeString());

		final String migratedThirdHash = SchemaHashCalculator.calculate(migratedThirdStructure);

		/*
		 * The physical database should now describe exactly the 3 Java structure.
		 */
		assert thirdHash.equals(migratedThirdHash) : "Database structure does not match 3 structure after migration.";

		/*
		 * Verify tables.
		 */
		assert garages3.exists();
		assert countries3.exists();
		assert cities3.exists();
		assert people3.exists();
		assert addresses3.exists();

		/*
		 * Verify data survived the second migration.
		 */
		assert garages3.count() == 2 : "Garage data was lost during 2 -> 3 migration.";
		assert cities3.count() == 2 : "City data was lost during 2 -> 3 migration.";
		assert people3.count() == 2 : "Person data was lost during 2 -> 3 migration.";

		/*
		 * Final structure must be stable.
		 *
		 * Running migration again against the same 3 structure should produce no changes.
		 */
		database.migrate(Collections.emptyList());

		final DatabaseStructure finalStructure = database.getStructure();

		final String finalHash = SchemaHashCalculator.calculate(finalStructure);

		assert thirdHash.equals(finalHash) : "Running migration twice changed the schema.";
	}

}
