package postgres;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.base.transaction.DBTransaction;
import lu.kbra.pclib.db.connector.PostgreSQLDataBaseConnector;
import lu.kbra.pclib.db.exception.DBException;

@TestInstance(Lifecycle.PER_CLASS)
public class PostgreSQLTest {

	static {
		PostgreSQL.start();
	}

	private PostgreSQLDataBaseConnector connector;
	private DataBase db;

	@BeforeAll
	public void createDb() throws IOException, SQLException, ClassNotFoundException {
		this.connector = new PostgreSQLDataBaseConnector(PostgreSQL.USER, PostgreSQL.PASS, "localhost", PostgreSQL.getPort());
		this.db = new DataBase(this.connector, PostgreSQL.DB_NAME);
		this.db.clearBeans().scanFromBeans();

		assert !this.db.exists() : "Db shouldn't exist.";
		assert this.db.create().created() : "Couldn't create database.";
	}

	@AfterAll
	public void deleteDb() throws IOException, SQLException {
		final PersonTable people = new PersonTable(this.db);
		this.db.clearBeans().register(people).scanFromBeans();
		assert people.exists();
		assert !people.drop().exists();

		this.db.drop();
		this.connector.reset();
	}

	@Test
	public void testTable() throws SQLException {
		final PersonTable people = new PersonTable(this.db);
		this.db.clearBeans().register(people).scanFromBeans();
		assert !people.exists() : "Table shouldn't exists.";
		assert people.create().created() : "Failed to create table";
		assert people.truncate() == 0 : "There shouldn't be any entries";

		Date date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)));
		final PersonData p1 = new PersonData("Name1", date);
		people.insertAndReload(p1);
		assert p1.birthYear == date.getYear() + 1900 : p1.birthYear + " <> " + date.getYear() + " (" + p1.birthDate + ")";
		date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 590_000_000)));
		final PersonData p2 = new PersonData("Name2", date);
		people.insertAndReload(p2);
		assert p2.birthYear == date.getYear() + 1900 : p2.birthYear + " <> " + date.getYear() + " (" + p2.birthDate + ")";

		Assertions.assertThrows(DBException.class, () -> people.insertAndReload(p1));

		assert people.exists(p2);
		assert people.existsUnique(p2);
		people.delete(p2);
		assert !people.exists(p2);
		assert !people.existsUnique(p2);

		assert !people.deleteIfExists(p2).isPresent();
		assert people.countUniques(p1) == 1;
		assert people.countUniques(p2) == 0;
		assert people.countNotNull(p1) == 1;

		final PersonData p3 = new PersonData("Name3", p1.birthDate);
		people.insertAndReload(p3);
		assert p3.birthYear == p1.birthDate.getYear() + 1900 : p3.birthYear + " <> " + p1.birthDate.getYear() + " (" + p3.birthDate + ")";

		final PersonData agePerson = new PersonData();
		agePerson.birthDate = p1.birthDate;

		assert people.countNotNull(agePerson) == 2;

		assert people.loadUniqueIfExists(p3).isPresent();
		assert people.loadIfExists(p3).isPresent();
		assert people.loadIfExistsElseInsert(p3) == p3;
		assert people.deleteIfExists(p3).isPresent();
	}

	@Test
	public void testTransaction() throws SQLException {
		final PersonTable people = new PersonTable(this.db);
		this.db.clearBeans().register(people).scanFromBeans();
		people.create();
		people.truncate();

		final Date date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)));
		final PersonData p1 = new PersonData("Name1", date);

		try (DBTransaction tt = this.db.createTransaction()) {
			tt.use(people).insertAndReload(p1);
			assert tt.use(people).exists(p1);

			assert !people.exists(p1);

			tt.rollback();
		}
		assert !people.exists(p1);

		try (DBTransaction tt = this.db.createTransaction()) {
			tt.use(people).insertAndReload(p1);
			assert tt.use(people).exists(p1);

			assert !people.exists(p1);
		}
		assert !people.exists(p1);

		try (DBTransaction tt = this.db.createTransaction()) {
			tt.use(people).insertAndReload(p1);
			assert tt.use(people).exists(p1);

			assert !people.exists(p1);

			tt.commit();
		}
		assert people.exists(p1);

		people.delete(p1);
	}

	@Test
	public void testViewCreateSQL() {
		final PersonCarView view = new PersonCarView(this.db);
		final PersonTable people = new PersonTable(this.db);
		final CarTable car = new CarTable(this.db);
		this.db.clearBeans().register(view, car, people).scanFromBeans();

		final String sql = Arrays.stream(view.getCreateSQL()).collect(Collectors.joining("\n"));
		System.err.println(sql);
		Assertions.assertTrue(sql.contains("CREATE VIEW \"public\".\"person_car\" AS"));
		Assertions.assertTrue(sql.contains("FROM"));
		Assertions.assertTrue(sql.contains("JOIN"));
		Assertions.assertTrue(sql.contains("p.id = c.person_id"));
		Assertions.assertTrue(sql.contains("AS \"person_name\""));
		Assertions.assertTrue(sql.contains("AS \"car_brand\""));
	}

}
