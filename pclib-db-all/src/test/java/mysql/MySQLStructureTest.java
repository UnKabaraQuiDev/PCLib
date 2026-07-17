package mysql;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.MySQLDatabaseConnector;
import lu.kbra.pclib.db.utils.DatabaseScanner;
import lu.kbra.pclib.db.utils.impl.VersionDbRule;

import shared.PrintDbRule;

public class MySQLStructureTest {

	@Test
	public void testTable() throws SQLException {
		Database db = new Database(new MySQLDatabaseConnector(), "dbName");
		db.getDatabaseEntryUtils().getQueryableHookManager().add(new PrintDbRule());
		final PersonTable people = new PersonTable(db);
		people.getDatabaseEntryUtils().getQueryableHookManager().add(new VersionDbRule());
		new DatabaseScanner(db, null).register(people).doScan();
		System.err.println(people.getStructure().toTreeString());
		System.err.println(Arrays.toString(people.getCreateSQL()));
	}

}
