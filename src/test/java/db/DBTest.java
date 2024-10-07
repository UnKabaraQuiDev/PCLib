package db;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseConnector;

public class DBTest extends DataBase {

	public final TestDBTable TABLE = new TestDBTable(this);

	public DBTest(DataBaseConnector connector) {
		super(connector);
	}

}
