package sqlite;

import java.io.IOException;

import lu.kbra.pclib.db.connector.SQLiteDatabaseConnector;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.dbms.SQLiteStructureVisitor;

import test.BaseDBTest;

public class SqliteDbTest extends BaseDBTest {

	static {
		SQLiteStructureVisitor.CLEAR_INSTEAD_OF_TRUNCATE = true;
	}

	@Override
	public DatabaseConnector createConnector() throws IOException {
		return new SQLiteDatabaseConnector(SQLite.createTempDirectory().toUri());
	}

}
