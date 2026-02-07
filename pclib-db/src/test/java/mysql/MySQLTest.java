package mysql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;

@TestInstance(Lifecycle.PER_CLASS)
public class MySQLTest {

	private final String dir = ".local/tests/";
	private MySQLDataBaseConnector connector;
	private DataBase db;

	@BeforeAll
	public void createDb() throws IOException, SQLException, ClassNotFoundException {
		Files.createDirectories(Paths.get(dir));
		connector = new MySQLDataBaseConnector("user", "pass", "localhost", MySQLDataBaseConnector.DEFAULT_PORT);
		db = new DataBase(connector, this.getClass().getSimpleName().toLowerCase());
		((BaseDataBaseEntryUtils) db.getDataBaseEntryUtils()).loadMySQLTypes();

		assert !db.exists() : "Db shouldn't exist.";
		assert db.create().created() : "Couldn't create database.";
	}

	@Test
	public void testTable() throws SQLException {
		final PersonTable people = new PersonTable(db);
		assert !people.exists() : "Table shouldn't exists.";
		assert people.create().created() : "Failed to create table";

		final Date date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)));
		final PersonData p1 = new PersonData("Name1", date);
		people.insertAndReload(p1);
		assert p1.birthYear == date.getYear() + 1900 : p1.birthYear + " <> " + date.getYear() + " (" + p1.birthDate + ")";
	}

	@AfterAll
	public void deleteDb() throws IOException, SQLException {
		db.drop();
		connector.reset();
	}

}
