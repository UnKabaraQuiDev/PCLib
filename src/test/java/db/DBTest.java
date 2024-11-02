package db;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.annotations.DB_Base;

@DB_Base(name = "database")
public class DBTest extends DataBase {

	public final PersonDBTable TABLE = new PersonDBTable(this);
	public final Person2DBTable TABLE2 = new Person2DBTable(this);

	public DBTest(DataBaseConnector connector) {
		super(connector);
		
		System.out.println(connector);
	}

}
