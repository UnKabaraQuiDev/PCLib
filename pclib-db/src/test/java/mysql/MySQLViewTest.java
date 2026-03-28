package mysql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;

@TestInstance(Lifecycle.PER_CLASS)
public class MySQLViewTest {

	static {
		MySQL.start();
	}

	private MySQLDataBaseConnector connector;
	private DataBase db;

	@BeforeAll
	public void createDb() throws IOException, SQLException, ClassNotFoundException {
		this.connector = new MySQLDataBaseConnector(MySQL.USER, MySQL.PASS, "localhost", MySQL.getPort());
		this.db = new DataBase(this.connector, MySQL.DB_NAME);
		((BaseDataBaseEntryUtils) this.db.getDataBaseEntryUtils()).loadMySQLTypes();

//		assert !this.db.exists() : "Db shouldn't exist.";
		assert this.db.create().created() : "Couldn't create database.";
	}

	@Test
	public void testViewGenerationAndQuery() throws SQLException {
		db.create();
		final PersonTable people = new PersonTable(this.db);
		final CarTable cars = new CarTable(this.db);
		final PersonCarView personCars = new PersonCarView(this.db);

		people.create();
		cars.create();
		personCars.create();

		final Date date = new Date(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)).getTime());

		final PersonData p1 = new PersonData("Alice", date);
		people.insertAndReload(p1);
		assertTrue(p1.id > 0, "Person id should be generated");

		final PersonData p2 = new PersonData("Bob", date);
		people.insertAndReload(p2);
		assertTrue(p2.id > 0, "Person id should be generated");

		final CarData c1 = new CarData(p1.id, "Tesla");
		cars.insertAndReload(c1);
		assertTrue(c1.id > 0, "Car id should be generated");

		final CarData c2 = new CarData(p1.id, "Audi");
		cars.insertAndReload(c2);

		final CarData c3 = new CarData(p2.id, "BMW");
		cars.insertAndReload(c3);

		final List<PersonCarROData> rows = personCars.loadAll();
		assertEquals(3, rows.size(), "View should contain 3 joined rows");

		assertTrue(rows.stream().anyMatch(r -> "Alice".equals(r.personName) && "Tesla".equals(r.carBrand)));
		assertTrue(rows.stream().anyMatch(r -> "Alice".equals(r.personName) && "Audi".equals(r.carBrand)));
		assertTrue(rows.stream().anyMatch(r -> "Bob".equals(r.personName) && "BMW".equals(r.carBrand)));

		db.drop();
	}

	@Test
	public void testMultiJoinViewGenerationAndQuery() throws SQLException {
		db.create();
		final PersonTable people = new PersonTable(this.db);
		final CarTable cars = new CarTable(this.db);
		final GarageTable garages = new GarageTable(this.db);
		final CityTable cities = new CityTable(this.db);
		final PersonCarGarageCityView view = new PersonCarGarageCityView(this.db);
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

		final CarData tesla = new CarData(alice.id, "Tesla");
		cars.insertAndReload(tesla);

		final CarData audi = new CarData(alice.id, "Audi");
		cars.insertAndReload(audi);

		final CarData bmw = new CarData(bob.id, "BMW");
		cars.insertAndReload(bmw);

		final GarageData g1 = new GarageData(tesla.id, "Garage North");
		garages.insertAndReload(g1);

		final GarageData g2 = new GarageData(audi.id, "Garage South");
		garages.insertAndReload(g2);

		final GarageData g3 = new GarageData(bmw.id, "Garage East");
		garages.insertAndReload(g3);

		final CityData city1 = new CityData(g1.id, "Luxembourg");
		cities.insertAndReload(city1);

		final CityData city2 = new CityData(g2.id, "Esch");
		cities.insertAndReload(city2);

		final CityData city3 = new CityData(g3.id, "Differdange");
		cities.insertAndReload(city3);

		final List<PersonCarGarageCityROData> rows = view.loadAll();
		assertEquals(3, rows.size(), "View should contain 3 joined rows");

		assertTrue(rows.stream()
				.anyMatch(r -> "Alice".equals(r.personName) && "Tesla".equals(r.carBrand) && "Garage North".equals(r.garageName)
						&& "Luxembourg".equals(r.cityName)));

		assertTrue(rows.stream()
				.anyMatch(r -> "Alice".equals(r.personName) && "Audi".equals(r.carBrand) && "Garage South".equals(r.garageName)
						&& "Esch".equals(r.cityName)));

		assertTrue(rows.stream()
				.anyMatch(r -> "Bob".equals(r.personName) && "BMW".equals(r.carBrand) && "Garage East".equals(r.garageName)
						&& "Differdange".equals(r.cityName)));

		db.drop();
	}

	@AfterAll
	public void deleteDb() throws IOException, SQLException {
		this.db.drop();
		this.connector.reset();
	}

}