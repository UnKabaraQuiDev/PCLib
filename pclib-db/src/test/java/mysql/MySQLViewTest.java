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

		assert !this.db.exists() : "Db shouldn't exist.";
		assert this.db.create().created() : "Couldn't create database.";
	}

	@Test
	public void testViewGenerationAndQuery() throws SQLException {
		final PersonTable people = new PersonTable(this.db);
		final CarTable cars = new CarTable(this.db);
		final PersonCarView personCars = new PersonCarView(this.db);

		assert !people.exists() : "Person table shouldn't exist.";
		assert people.create().created() : "Failed to create person table";

		assert !cars.exists() : "Car table shouldn't exist.";
		assert cars.create().created() : "Failed to create car table";

		assert !personCars.exists() : "View shouldn't exist.";
		assert personCars.create().created() : "Failed to create view";

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

		final List<PersonCarViewData> rows = personCars.loadAll();
		assertEquals(3, rows.size(), "View should contain 3 joined rows");

		assertTrue(rows.stream().anyMatch(r -> "Alice".equals(r.personName) && "Tesla".equals(r.carBrand)));
		assertTrue(rows.stream().anyMatch(r -> "Alice".equals(r.personName) && "Audi".equals(r.carBrand)));
		assertTrue(rows.stream().anyMatch(r -> "Bob".equals(r.personName) && "BMW".equals(r.carBrand)));
	}

	@AfterAll
	public void deleteDb() throws IOException, SQLException {
		this.db.drop();
		this.connector.reset();
	}
}