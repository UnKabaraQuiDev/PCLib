import org.junit.jupiter.api.Test;

import db.DBTest;
import lu.pcy113.pclib.db.DataBaseConnector;

public class DBMain {

	@Test
	public void dbTest() {
		DBTest dbTest = new DBTest(new DataBaseConnector("user", "pass", "host", "database", 3306));
		System.out.println(dbTest.TABLE.create().join());
	}

}
