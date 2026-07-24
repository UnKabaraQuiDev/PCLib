package mysql;

import lu.kbra.pclib.db.connector.MySQLDatabaseConnector;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;

import test.BaseDBTest;

public class MySqlDbTest extends BaseDBTest {

	static {
		MySQL.start();
	}

	@Override
	public DatabaseConnector createConnector() {
		return new MySQLDatabaseConnector(MySQL.USER, MySQL.PASS, "localhost", MySQL.getPort());
	}

}
