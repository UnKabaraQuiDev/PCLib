package mysql;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.base.transaction.DBTransaction;
import lu.kbra.pclib.db.connector.MySQLDatabaseConnector;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.hook.VersionDbRule;
import lu.kbra.pclib.db.utils.DatabaseScanner;
import shared.PersonData;
import shared.PersonTable;
import shared.PrintDbRule;

@TestInstance(Lifecycle.PER_CLASS)
public class MySQLTest {

	static {
		MySQL.start();
	}

	private MySQLDatabaseConnector connector;
	private Database db;

	@BeforeAll
	public void createDb() throws IOException, SQLException, ClassNotFoundException {
		this.connector = new MySQLDatabaseConnector(MySQL.USER, MySQL.PASS, "localhost", MySQL.getPort());
		this.db = new Database(this.connector, MySQL.DB_NAME);
		this.db.getDatabaseEntryUtils().getQueryableHookManager().add(new PrintDbRule());
		this.db.clearBeans().scanFromBeans();

		assert !this.db.exists() : "Db shouldn't exist.";
		assert this.db.create().created() : "Couldn't create database.";
	}

	@AfterAll
	public void deleteDb() throws IOException, SQLException {
		final PersonTable people = new PersonTable(this.db);
		new DatabaseScanner(this.db, null).register(people).doScan();
		this.db.updateDatabaseConnector();
		assert !people.drop().exists();

		this.db.drop();

		this.connector.reset();
	}

	@Test
	public void testTable() throws SQLException {
		final PersonTable people = new PersonTable(this.db);
		people.getDatabaseEntryUtils().getQueryableHookManager().add(new VersionDbRule());
		System.err.println("Hooks:\n" + people.getDatabaseEntryUtils().getQueryableHookManager().toTreeString());
		new DatabaseScanner(this.db, null).register(people).doScan();
		System.err.println(people.getStructure().toTreeString());
		System.err.println(Arrays.toString(people.getCreateSQL()));
		assert !people.exists() : "Table shouldn't exists.";
		assert people.create().created() : "Failed to create table";
		assert people.truncate() == 0 : "There shouldn't be any entries";

		Date date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)));
		final PersonData p1 = new PersonData("Name1", date);
		people.insertAndReload(p1);
		assert p1.getBirthYear() == date.getYear() + 1900 : p1.getBirthYear() + " <> " + date.getYear() + " (" + p1.getBirthDate() + ")";
		date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 590_000_000)));
		final PersonData p2 = new PersonData("Name2", date);
		people.insertAndReload(p2);
		assert p2.getBirthYear() == date.getYear() + 1900 : p2.getBirthYear() + " <> " + date.getYear() + " (" + p2.getBirthDate() + ")";

		System.err.println("Hooks:\n" + people.getDatabaseEntryUtils().getQueryableHookManager().toTreeString());

		{
			final PersonData p1Duplicate = people.load(p1.clone());
			assert p1Duplicate != p1 : "Clone returned same instance.";
			// edit p1 and update
			System.err.println("before: " + p1);
			p1.setName("Name1-Changed");
			people.updateAndReload(p1);
			System.err.println("after: " + p1);
			System.err.println("other: " + p1Duplicate);
			assert p1.getVersion() > p1Duplicate.getVersion();
			// will cause p1Duplicate to be outdated
			Assertions.assertThrows(DBException.class, () -> people.updateAndReload(p1Duplicate));
		}

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

		final PersonData p3 = new PersonData("Name3", p1.getBirthDate());
		people.insertAndReload(p3);
		assert p3.getBirthYear() == date.getYear() + 1900
				: p3.getBirthDate() + " <> " + p1.getBirthDate().getYear() + " (" + p3.getBirthDate() + ")";

		final PersonData agePerson = new PersonData();
		agePerson.setBirthDate(p1.getBirthDate());

		System.err.println(agePerson + " matching: " + people.countNotNull(agePerson) + " people");
		assert people.countNotNull(agePerson) == 2;

		assert people.loadUniqueIfExists(p3).isPresent();
		assert people.loadIfExists(p3).isPresent();
		assert people.loadIfExistsElseInsert(p3) == p3;
		assert people.deleteIfExists(p3).isPresent();

		// default value
		{
			final PersonData pp = new PersonData("name only");
			final PersonData returned = people.insertAndReload(pp);
			assert returned == pp;
			assert returned.getBirthDate() != null;
			people.delete(returned);
		}

		{
			final Collection<PersonData> persons = Arrays.asList(
					new PersonData("name1", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(2, TimeUnit.DAYS))),
					new PersonData("name2", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(3, TimeUnit.DAYS))),
					new PersonData("name3", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(4, TimeUnit.DAYS))),
					new PersonData("name4", new Date(System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(5, TimeUnit.DAYS))),
					new PersonData("name5"));
			final Collection<PersonData> returned = people.insertAndReloadAll(persons);
			assert returned.size() == persons.size();
			returned.forEach(c -> {
				assert c.getId() != 0 : c;
				assert c.getBirthDate() != null : c;
			});
		}
	}

	@Test
	public void testTransaction() throws SQLException {
		final PersonTable people = new PersonTable(this.db);
		new DatabaseScanner(this.db, null).register(people).doScan();
		System.err.println(Arrays.toString(people.getCreateSQL()));
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

}
