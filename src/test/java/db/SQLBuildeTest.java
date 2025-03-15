package db;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.SQLBuilder;

public class SQLBuildeTest {
	
	@Test
	public void test() {
		System.out.println(SQLBuilder.safeSelect(new DBTest(new DataBaseConnector()).TABLE, new String[] {"a", null, "b"}));
	}
}
