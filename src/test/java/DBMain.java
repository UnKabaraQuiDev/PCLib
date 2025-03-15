import java.io.File;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.config.ConfigLoader;
import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.ReturnData;
import lu.pcy113.pclib.db.TableHelper;
import lu.pcy113.pclib.db.utils.CachedSQLEntryUtils;
import lu.pcy113.pclib.db.utils.SQLEntryUtils;

import db.DBTest;
import db.Person;
import db.Person2;

public class DBMain {

	public static void main(String[] args) throws Exception {
		new DBMain().dbTest();
	}
	
	@Test
	public void dbTest() throws Exception {
		try {
			SQLEntryUtils.register(new CachedSQLEntryUtils());
			
			DBTest dbTest = new DBTest(ConfigLoader.loadFromJSONFile(new DataBaseConnector(), new File("./src/test/resources/config/db_connector.json")));

			dbTest.create().thenConsume(System.out::println).run();
			dbTest.TABLE.create().thenConsume(System.out::println).run();
			dbTest.TABLE2.create().thenConsume(System.out::println).run();
			
			System.out.println(dbTest.TABLE.getCreateSQL());
			
			// @formatter:off
			TableHelper.insertOrLoad(dbTest.TABLE2, new Person2("person1"), () -> Person2.byName("person1"))
					.catch_(Exception::printStackTrace)
					.thenConsume(v -> System.out.println("person: " + v))
					.run();
			
			TableHelper.insertOrLoad(dbTest.TABLE, new Person("person1"), () -> Person.byName("person1"))
					.catch_(Exception::printStackTrace)
					.thenConsume(v -> System.out.println("person: " + v))
					.run();
			// @formatter:on

			System.out.println("Count: " + dbTest.TABLE.count().thenApply(ReturnData::getData).run());

			// dbTest.TABLE.drop().thenConsume((i) -> System.out.println(i)).run();
			
			int result = dbTest.TABLE.update(Person.deleteByName("person1")).thenApply(ReturnData::getData).run();
			System.out.println("Rows deleted: " + result);
			
			dbTest.TABLE.drop().thenConsume((i) -> System.out.println(i)).run();

			dbTest.drop().thenConsume((i) -> System.out.println(i)).run();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
