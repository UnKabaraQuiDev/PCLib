import java.io.File;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.config.ConfigLoader;
import lu.pcy113.pclib.db.DataBaseConnector;

import db.DBTest;
import db.Person;

public class DBMain {

	@Test
	public void dbTest() throws Exception {
		try {
			DBTest dbTest = new DBTest(ConfigLoader.loadFromJSONFile(new DataBaseConnector(), new File("./src/test/resources/config/db_connector.json")));

			dbTest.create().thenConsume(System.out::println).run();
			dbTest.updateDataBaseConnector();
			dbTest.getConnector().reset();
			dbTest.TABLE.create().thenConsume(System.out::println).run();

			// @formatter:off
			dbTest.TABLE
					.insert(new Person("person1"))
					.thenApply(i -> {
						System.out.println(i);
						return i.getData();
					})
					.thenCompose((i) -> dbTest.TABLE.delete(i))
					.thenConsume((i) -> System.out.println(i))
					.run();

			dbTest.TABLE
					.insertAndReload(new Person("person2"))
					.thenApply(i -> {
						System.out.println(i);
						return i.getData();
					})
					/*.thenApply(i -> {
						i.setName("NAAAAMééé 2");
						return i;
					})*/
					.thenCompose((i) -> dbTest.TABLE.update(i))
					.thenConsume((i) -> System.out.println("update: " + i))
					.run();
			
			dbTest.TABLE
					.load(new Person(6))
					.thenConsume((i) -> System.out.println("load: "+i))
					.run();
			
			dbTest.TABLE
					.query(Person.byName("NAAAAMééé 2"))
					.thenConsume((i) -> System.out.println(i))
					.run();
			
			dbTest.TABLE.drop().thenConsume((i) -> System.out.println(i)).run();
			
			dbTest.drop().thenConsume((i) -> System.out.println(i)).run();
			// @formatter:on
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
