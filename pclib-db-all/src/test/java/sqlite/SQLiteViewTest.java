package sqlite;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.SQLiteDatabaseConnector;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.hook.VersionDbRule;

import shared.CarData;
import shared.CarTable;
import shared.CityData;
import shared.CityTable;
import shared.GarageData;
import shared.GarageTable;
import shared.PersonCarGarageCityROData;
import shared.PersonCarGarageCityView;
import shared.PersonCarROData;
import shared.PersonCarView;
import shared.PersonData;
import shared.PersonTable;
import shared.PrintDbRule;

@TestInstance(Lifecycle.PER_CLASS)
public class SQLiteViewTest {

	private Path dir;
	private SQLiteDatabaseConnector connector;
	private Database db;

	@BeforeAll
	public void createDb() throws IOException, SQLException, ClassNotFoundException {
		this.dir = SQLite.createTempDirectory();
		this.connector = new SQLiteDatabaseConnector(this.dir.toUri());
		this.db = new Database(this.connector, SQLite.DB_NAME);
		db.getDatabaseEntryUtils().getQueryableHookManager().add(new PrintDbRule()).add(new VersionDbRule());
		this.db.clearBeans().scanFromBeans();

		assert !this.db.exists() : "Db shouldn't exist.";
		assert this.db.create().created() : "Couldn't create database.";
	}

	@AfterAll
	public void deleteDb() throws IOException, SQLException {
		if (this.db != null) {
			this.db.drop();
		}
		if (this.connector != null) {
			this.connector.reset();
		}
		SQLite.deleteDirectory(this.dir);
	}

	@Test
	public void testViewCreateSQL() {
		final PersonCarView view = new PersonCarView(this.db);
		final PersonTable people = new PersonTable(this.db);
		final CarTable car = new CarTable(this.db);
		this.db.clearBeans().register(view, people, car).scanFromBeans();

		final String sql = Arrays.stream(view.getCreateSQL()).collect(Collectors.joining("\n"));

		Assertions.assertTrue(sql.contains("CREATE VIEW \"person_car\" AS"));
		Assertions.assertTrue(sql.contains("FROM"));
		Assertions.assertTrue(sql.contains("JOIN"));
		Assertions.assertTrue(sql.contains("p.id = c.person_id"));
		Assertions.assertTrue(sql.contains("AS \"person_name\""));
		Assertions.assertTrue(sql.contains("AS \"car_brand\""));
	}

	@Test
	public void testMultiJoinViewGenerationAndQuery() throws SQLException {
		this.recreateDb();

		final PersonTable people = new PersonTable(this.db);
		final CarTable cars = new CarTable(this.db);
		final GarageTable garages = new GarageTable(this.db);
		final CityTable cities = new CityTable(this.db);
		final PersonCarGarageCityView view = new PersonCarGarageCityView(this.db);
		this.db.clearBeans().register(view, people, cars, garages, cities).scanFromBeans();

		people.create();
		cars.create();
		garages.create();
		cities.create();
		view.create();

		final Date date = new Date(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)).getTime());

		final PersonData alice = new PersonData("Alice", date);
		people.insertAndReload(alice);

		final PersonData bob = new PersonData("Bob", date);
		people.insertAndReload(bob);

		final CarData c1 = new CarData(alice.getId(), "Tesla");
		cars.insertAndReload(c1);

		{
			try {
				final CarData c1Duplicate = cars.load(c1.clone());
				assert c1Duplicate != c1 : "Clone returned same instance.";
				// edit c and update
				System.err.println("before: " + c1);
				c1.setBrand("Name1-Changed");
				Thread.sleep(1_000);
				cars.updateAndReload(c1);
				System.err.println("after: " + c1);
				System.err.println("other: " + c1Duplicate);
				assert c1.getVersion().after(c1Duplicate.getVersion());
				// will cause c1Duplicate to be outdated
				Assertions.assertThrows(DBException.class, () -> cars.updateAndReload(c1Duplicate));
				c1.setBrand(c1Duplicate.getBrand());
				cars.updateAndReload(c1);
			} catch (InterruptedException e) {
			}
		}

		final CarData audi = new CarData(alice.getId(), "Audi");
		cars.insertAndReload(audi);

		final CarData bmw = new CarData(bob.getId(), "BMW");
		cars.insertAndReload(bmw);

		final GarageData g1 = new GarageData(c1.getId(), "Garage North");
		garages.insertAndReload(g1);

		final GarageData g2 = new GarageData(audi.getId(), "Garage South");
		garages.insertAndReload(g2);

		final GarageData g3 = new GarageData(bmw.getId(), "Garage East");
		garages.insertAndReload(g3);

		final CityData city1 = new CityData(g1.getId(), "Luxembourg");
		cities.insertAndReload(city1);

		final CityData city2 = new CityData(g2.getId(), "Esch");
		cities.insertAndReload(city2);

		final CityData city3 = new CityData(g3.getId(), "Differdange");
		cities.insertAndReload(city3);

		final List<PersonCarGarageCityROData> rows = view.loadAll();
		Assertions.assertEquals(3, rows.size(), "View should contain 3 joined rows");

		Assertions.assertTrue(rows.stream()
				.anyMatch(r -> "Alice".equals(r.getPersonName()) && "Tesla".equals(r.getCarBrand())
						&& "Garage North".equals(r.getGarageName()) && "Luxembourg".equals(r.getCityName())));

		Assertions.assertTrue(rows.stream()
				.anyMatch(r -> "Alice".equals(r.getPersonName()) && "Audi".equals(r.getCarBrand())
						&& "Garage South".equals(r.getGarageName()) && "Esch".equals(r.getCityName())));

		Assertions.assertTrue(rows.stream()
				.anyMatch(r -> "Bob".equals(r.getPersonName()) && "BMW".equals(r.getCarBrand()) && "Garage East".equals(r.getGarageName())
						&& "Differdange".equals(r.getCityName())));
	}

	@Test
	public void testViewGenerationAndQuery() throws SQLException {
		this.recreateDb();

		final PersonTable people = new PersonTable(this.db);
		final CarTable cars = new CarTable(this.db);
		final PersonCarView personCars = new PersonCarView(this.db);
		this.db.clearBeans().register(people, cars, personCars).scanFromBeans();

		people.create();
		cars.create();
		personCars.create();

		final Date date = new Date(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)).getTime());

		final PersonData p1 = new PersonData("Alice", date);
		people.insertAndReload(p1);
		Assertions.assertTrue(p1.getId() > 0, "Person getId() should be generated");

		final PersonData p2 = new PersonData("Bob", date);
		people.insertAndReload(p2);
		Assertions.assertTrue(p2.getId() > 0, "Person getId() should be generated");

		final CarData c1 = new CarData(p1.getId(), "Tesla");
		cars.insertAndReload(c1);
		Assertions.assertTrue(c1.getId() > 0, "Car getId() should be generated");

		final CarData c2 = new CarData(p1.getId(), "Audi");
		cars.insertAndReload(c2);

		final CarData c3 = new CarData(p2.getId(), "BMW");
		cars.insertAndReload(c3);

		final List<PersonCarROData> rows = personCars.loadAll();
		Assertions.assertEquals(3, rows.size(), "View should contain 3 joined rows");

		Assertions.assertTrue(rows.stream().anyMatch(r -> "Alice".equals(r.getPersonName()) && "Tesla".equals(r.getCarBrand())));
		Assertions.assertTrue(rows.stream().anyMatch(r -> "Alice".equals(r.getPersonName()) && "Audi".equals(r.getCarBrand())));
		Assertions.assertTrue(rows.stream().anyMatch(r -> "Bob".equals(r.getPersonName()) && "BMW".equals(r.getCarBrand())));
	}

	private void recreateDb() {
		this.db.clearBeans().scanFromBeans();
		this.db.drop();
		this.db.create();
	}

}
