package sqlite;

import java.io.IOException;
import java.nio.file.Path;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.base.transaction.DBTransaction;
import lu.kbra.pclib.db.connector.SQLiteDatabaseConnector;
import lu.kbra.pclib.db.dbms.SQLiteStructureVisitor;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.hook.VersionDbRule;

import shared.PersonData;
import shared.PersonTable;
import shared.PrintDbRule;

@TestInstance(Lifecycle.PER_CLASS)
public class SQLiteTest {

	private Path dir;
	private SQLiteDatabaseConnector connector;
	private Database db;

	@BeforeAll
	public void createDb() throws IOException, SQLException, ClassNotFoundException {
		this.dir = SQLite.createTempDirectory();
		this.connector = new SQLiteDatabaseConnector(this.dir.toString());
		this.db = new Database(this.connector, SQLite.DB_NAME);
		this.db.getDatabaseEntryUtils().getQueryableHookManager().add(new PrintDbRule());
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
		SQLite.deleteDirectory(this.dir);
	}

	@Test
	public void testTable() throws SQLException {
		this.recreateDb();

		final PersonTable people = new PersonTable(this.db);
		people.getDatabaseEntryUtils().getQueryableHookManager().add(new VersionDbRule());
		this.db.clearBeans().register(people).scanFromBeans();
		System.out.println(Arrays.toString(people.getCreateSQL()));
		System.err.println(people.getStructure().toTreeString());
		assert !people.exists() : "Table shouldn't exists.";
		assert people.create().created() : "Failed to create table";
		assert people.clear() == 0 : "There shouldn't be any entries";

		Date date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)));
		final PersonData p1 = new PersonData("Name1", date);
		people.insertAndReload(p1);
		assert p1.getBirthYear() == date.getYear() + 1900 : p1.getBirthYear() + " <> " + date.getYear() + " (" + p1.getBirthDate() + ")";
		date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 590_000_000)));
		final PersonData p2 = new PersonData("Name2", date);
		people.insertAndReload(p2);
		assert p2.getBirthYear() == date.getYear() + 1900 : p2.getBirthYear() + " <> " + date.getYear() + " (" + p2.getBirthDate() + ")";

		final PersonData p1Duplicate = people.load(p1.clone());
		// edit p1 and update
		System.err.println("before: " + p1);
		p1.setName("Name1-Changed");
		people.updateAndReload(p1);
		System.err.println("after: " + p1);
		assert p1.getVersion() > p1Duplicate.getVersion();
		// will cause p1Duplicate to be outdated
		Assertions.assertThrows(DBException.class, () -> people.updateAndReload(p1Duplicate));

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
		assert p3.getBirthYear() == p1.getBirthDate().getYear() + 1900
				: p3.getBirthYear() + " <> " + p1.getBirthDate().getYear() + " (" + p3.getBirthDate() + ")";

		final PersonData agePerson = new PersonData();
		agePerson.setBirthDate(p1.getBirthDate());

		assert people.countNotNull(agePerson) == 2;

		assert people.loadUniqueIfExists(p3).isPresent();
		assert people.loadIfExists(p3).isPresent();
		assert people.loadIfExistsElseInsert(p3) == p3;
		assert people.deleteIfExists(p3).isPresent();
	}

	@Test
	public void testTransaction() throws SQLException {
		this.recreateDb();

		final PersonTable people = new PersonTable(this.db);
		this.db.clearBeans().register(people).scanFromBeans();

		people.getDatabaseEntryUtils().getStructureVisitor().setOption(SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE_PROPERTY, true);
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

	private void recreateDb() {
		this.db.clearBeans().scanFromBeans();
		this.db.drop();
		this.db.create();
	}

}
