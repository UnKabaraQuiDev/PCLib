package postgres;

import lu.kbra.pclib.db.connector.PostgreSQLDatabaseConnector;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;

import test.BaseDBTest;

public class PostgreSqlDbTest extends BaseDBTest {

	static {
		PostgreSQL.start();
	}

	@Override
	public DatabaseConnector createConnector() {
		return new PostgreSQLDatabaseConnector(PostgreSQL.USER, PostgreSQL.PASS, "localhost", PostgreSQL.getPort());
	}

}
