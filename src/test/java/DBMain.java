import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.db.DataBaseConnector;

import db.DBTest;
import db.Person;

public class DBMain {

	@Test
	public void dbTest() {
		try {
			DBTest dbTest = new DBTest(new DataBaseConnector("user", "pass", "localhost", DataBaseConnector.DEFAULT_PORT));

			System.out.println(dbTest.create().join());
			dbTest.updateDataBaseConnector();
			dbTest.getConnector().reset();
			System.out.println(dbTest.TABLE.create().join());

			// @formatter:off
			dbTest.TABLE
					.insert(new Person("person1"))
					.thenApply(i -> {
						System.out.println(i);
						return i.getData();
					})
					.thenCompose((i) -> dbTest.TABLE.delete(i))
					.thenAccept((i) -> System.out.println(i))
					.join();

			dbTest.TABLE
					.insertAndReload(new Person("person2"))
					.thenApply(i -> {
						System.out.println(i);
						return i.getData();
					})
					.thenApply(i -> {
						i.setName("NAAAAMééé 2");
						return i;
					})
					.thenCompose((i) -> dbTest.TABLE.update(i))
					.thenAccept((i) -> System.out.println(i))
					.join();
			
			dbTest.TABLE
					.load(new Person("person2"))
					.thenAccept((i) -> System.out.println(i))
					.join();
			
			dbTest.TABLE
					.query(Person.byName("NAAAAMééé 2"))
					.thenAccept((i) -> System.out.println(i))
					.join();
			
			dbTest.TABLE.drop().thenAccept((i) -> System.out.println(i)).join();
			
			dbTest.drop().thenAccept((i) -> System.out.println(i)).join();
			// @formatter:on
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
