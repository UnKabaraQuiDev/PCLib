package test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.migration.DatabaseMigration;
import lu.kbra.pclib.db.migration.MigrationPhase;
import lu.kbra.pclib.db.migration.SchemaHashCalculator;

import shared.migration.initial.data.CityData1;
import shared.migration.initial.data.GarageData1;
import shared.migration.initial.data.PersonData1;
import shared.migration.initial.table.CityTable1;
import shared.migration.initial.table.GarageTable1;
import shared.migration.initial.table.PersonTable1;
import shared.migration.second.data.CountryData2;
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

public interface DBMigrationTest extends GenericDBTest {

	@Test
	default void testSchemaMigration() throws SQLException {
		final Database database = this.getDatabase();

		/*
		 * STEP 1 — INITIAL SCHEMA
		 */

		final GarageTable1 garages = new GarageTable1(database);
		final CityTable1 cities = new CityTable1(database);
		final PersonTable1 people = new PersonTable1(database);

		System.out.println("========== INITIAL ==========");
		database.clearBeans().register(garages, cities, people).initMigrationSupport(true).scanFromBeans();

//		System.out.println("Structure:\n" + database.getStructure().toTreeString());

		assert !garages.exists();
		assert !cities.exists();
		assert !people.exists();

//		database.createBeans((t, b) -> {
//			if (t instanceof SQLQueryable<?>) {
//				assert b : "Couldn't create" + ((SQLQueryable<?>) t).getName();
//				System.out.println((b ? "Created: " : "Existed: " )+ ((SQLQueryable<?>) t).getName());
//			}
//		});

		database.migrate(Collections.emptyList(), (t, b) -> {
			if (t instanceof SQLQueryable<?>) {
				assert ((SQLQueryable<?>) t).exists() : "Doesn't exist: " + ((SQLQueryable<?>) t).getName();
				System.out.println((b ? "Created: " : "Existed: ") + ((SQLQueryable<?>) t).getName());
			}
		});

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

		System.out.println("========== SECOND ==========");
		database.register(garages2, countries2, cities2, people2).initMigrationSupport(true).scanFromBeans();

//		System.out.println("Structure:\n" + database.getStructure().toTreeString());

		/*
		 * The migration support should compare the stored INITIAL snapshot against this newly scanned
		 * structure and execute the required migration.
		 */
		final List<DatabaseMigration> migrations = new ArrayList<>(Arrays.asList(new DatabaseMigration() {

			DatabaseMigrationPhase[] phases = new DatabaseMigrationPhase[] {
					new ManualMigrationPhase("update-country-code-col", null, 0, MigrationPhase.ADD_COLUMNS) {

						@Override
						public void up(Statement stmt) throws SQLException {

							stmt.execute("UPDATE city SET country_code = 'AA';");

						}

					},
					new ManualMigrationPhase("update-address-col", null, 0, MigrationPhase.ADD_COLUMNS) {

						@Override
						public void up(Statement stmt) throws SQLException {

							stmt.execute("UPDATE garage SET address = 'something not null';");

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

		}));
		database.migrate(migrations, (t, b) -> {
			if (t instanceof SQLQueryable<?>) {
				assert ((SQLQueryable<?>) t).exists() : "Doesn't exist: " + ((SQLQueryable<?>) t).getName();
				System.out.println((b ? "Created: " : "Existed: ") + ((SQLQueryable<?>) t).getName());
			}
		});

		/*
		 * Verify that the actual tables exist.
		 */
		assert garages2.exists();
		assert countries2.exists();
		assert cities2.exists();
		assert people2.exists();

		countries2.insert(new CountryData2(null, "XX", "XX-name"));

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

		System.out.println("========== THIRD ==========");
		database.register(garages3, countries3, cities3, people3, addresses3).initMigrationSupport(true).scanFromBeans();

		/*
		 * Execute 2 -> 3 migration.
		 */
		migrations.add(new DatabaseMigration() {

			DatabaseMigrationPhase[] phases = new DatabaseMigrationPhase[] {
					new ManualMigrationPhase("update-phone-col",
							"Update Phone column, fill with name column.",
							0,
							MigrationPhase.ADD_COLUMNS) {

						@Override
						public void up(Statement stmt) throws SQLException {

							stmt.execute("UPDATE person SET phone = name;");

						}

					},
					new ManualMigrationPhase("update-garage-id", null, 0, MigrationPhase.ADD_COLUMNS) {

						@Override
						public void up(Statement stmt) throws SQLException {

							stmt.execute("UPDATE person SET garage_id = (SELECT id FROM garage LIMIT 1);");

						}

					},
					new ManualMigrationPhase("update-zip-code", null, 0, MigrationPhase.ADD_COLUMNS) {

						@Override
						public void up(Statement stmt) throws SQLException {

							stmt.execute("UPDATE cities SET zip_code = 'XX-1234';");

						}

					},
					new ManualMigrationPhase("update-country-id", null, 0, MigrationPhase.ADD_COLUMNS) {

						@Override
						public void up(Statement stmt) throws SQLException {

							stmt.execute("UPDATE cities SET country_id = (SELECT id FROM country LIMIT 1);");

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
				return "V3 Bump";
			}

			@Override
			public String id() {
				return "v3";
			}

		});
		database.migrate(migrations, (t, b) -> {
			if (t instanceof SQLQueryable<?>) {
				assert ((SQLQueryable<?>) t).exists() : "Doesn't exist: " + ((SQLQueryable<?>) t).getName();
				System.out.println((b ? "Created: " : "Existed: ") + ((SQLQueryable<?>) t).getName());
			}
		});

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
		database.migrate(migrations, (t, b) -> {
			if (t instanceof SQLQueryable<?>) {
				assert ((SQLQueryable<?>) t).exists() : "Doesn't exist: " + ((SQLQueryable<?>) t).getName();
				System.out.println((b ? "Created: " : "Existed: ") + ((SQLQueryable<?>) t).getName());
			}
		});
	}

}
