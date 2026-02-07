import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.SQLiteDataBaseConnector;

@TestInstance(Lifecycle.PER_CLASS)
public class SQLiteTest {

	private final String dir = ".local/tests/";
	private SQLiteDataBaseConnector connector;
	private DataBase db;

	@BeforeAll
	public void createDb() throws IOException, SQLException, ClassNotFoundException {
		Files.createDirectories(Paths.get(dir));
		connector = new SQLiteDataBaseConnector(dir);
		db = new DataBase(connector, "test");

		assert connector.getPath().toFile().exists() == connector.exists() : "File already existed.";
		assert connector.getPath().toFile().exists() ^ connector.create() : "Database couldn't be created.";
	}

	@Test
	public void testTable() throws SQLException {
		final PersonTable people = new PersonTable(db);
		assert !people.exists() : "Table shouldn't exists.";
		assert people.create().created() : "Failed to create table";

		final Date date = PCUtils.toDate(Timestamp.from(Instant.ofEpochMilli(System.currentTimeMillis() - 100_000_000)));
		final PersonData p1 = new PersonData("Name1", date);
//		people.insertAndReload(p1);
		assert p1.age == ChronoUnit.YEARS.between(date.toLocalDate(), LocalDate.now());
	}

	@AfterAll
	public void deleteDb() throws IOException, SQLException {
//		db.drop();
		connector.reset();
//		PCUtils.deleteFile(connector.getPath().toFile());
//		assert !connector.exists() : "Couldn't delete file.";
	}

}
