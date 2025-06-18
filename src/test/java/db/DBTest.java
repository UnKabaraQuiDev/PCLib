package db;

import java.io.File;
import java.sql.Timestamp;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.config.ConfigLoader;
import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.utils.BaseDataBaseEntryUtils;

public class DBTest {

	public static void main(String[] args) throws Throwable {
		new DBTest().test();
	}

	@Test
	public void test() throws Throwable {
		System.out.println(new BaseDataBaseEntryUtils().scanTable(CustomerTable.class).build());
		System.out.println(new BaseDataBaseEntryUtils().scanTable(OrderTable.class).build());

		final TestDB db = new TestDB(
				ConfigLoader.loadFromJSONFile(new DataBaseConnector(), new File("./src/test/resources/config/db_connector.json")));

		final CustomerTable customers = new CustomerTable(db);
		final OrderTable orders = new OrderTable(db);
		System.out.println(customers.getCreateSQL());
		System.out.println(orders.getCreateSQL());

		System.out.println(new BaseDataBaseEntryUtils().getNotNullValues(new OrderData(02)));
		System.out.println(new BaseDataBaseEntryUtils().getNotNullValues(new OrderData(02, new Timestamp(01))));

		new BaseDataBaseEntryUtils().initQueries(customers);
		new BaseDataBaseEntryUtils().initQueries(orders);
		// new BaseDataBaseEntryUtils().initQueries(legacyOrders);

		System.out.println(((SQLQuery) CustomerData.ALL).getPreparedQuerySQL(customers));
		System.out.println(((SQLQuery) CustomerData.ALL_LIMIT.apply(24)).getPreparedQuerySQL(customers));
		System.out.println(((SQLQuery) CustomerData.ALL_OFFSET.apply(12)).getPreparedQuerySQL(customers));
		System.out.println(((SQLQuery) CustomerData.ALL_OFFSET_LIMIT.apply(12, 24)).getPreparedQuerySQL(customers));

		db.drop().runThrow();

		db.create().runThrow();
		customers.create().runThrow();
		orders.create().runThrow();

		if (!customers.exists(new CustomerData("name1", "email1")).runThrow()) {
			System.out.println(customers.insertAndReload(new CustomerData("name1", "email1")).runThrow());
		}

		final long id = customers
				.query(CustomerData.byNameAndEmail("name1", "email1"))
				.thenApply(PCUtils.first(cd -> cd.getId(), -1L))
				.runThrow();

		CustomerData customer = customers.load(new CustomerData(id)).runThrow();

		customer = customers.loadIfExistsElseInsert(new CustomerData("name1", "email1")).runThrow();

		customer = customers.loadUnique(new CustomerData("name1", "email1")).runThrow();

		OrderData order1 = orders.insertAndReload(new OrderData(customer)).runThrow();

		System.out.println("name: " + customers.query(CustomerData.BY_NAME.apply("name1")).runThrow());
		System.out.println("name & email: " + customers.query(CustomerData.BY_NAME_AND_EMAIL.apply("name1", "email1")).runThrow());
		System.out.println("email: " + customers.query(CustomerData.BY_EMAIL.apply("email1")).runThrow());
		// System.out.println("name offset: " +
		// customers.query(CustomerData.BY_NAME_OFFSET.apply("name1", 1)).runThrow());
		// System.out.println("name & email & age: " +
		// customers.query(CustomerData.BY_NAME_AND_EMAIL_AND_AGE.apply("name1",
		// "email1", 36)).runThrow());
		// System.out.println("others: " +
		// customers.query(CustomerData.BY_OTHERS.apply(PCUtils.hashMap("col1", "a",
		// "col2", "b", "col3", "c", "col4", "d"))).runThrow());

		System.out.println("table name: " + customers.BY_NAME.runThrow("name1"));
		System.out.println("table id sup: " + customers.ID_SUP.runThrow(0));

		System.out.println(orders.query(OrderData.BY_CUSTOMER_ID.apply(customer.getId())).runThrow());

		/*
		 * for (Method m : CustomerTable.class.getDeclaredMethods()) { if
		 * (!m.isAnnotationPresent(Query.class)) continue;
		 * 
		 * final Object obj = customers.getDbEntryUtils().buildMethodQueryFunction(CustomerTable.class,
		 * "customer", customers, m); if (m.getName().equals("all")) {
		 * System.err.println(((Function<List<Object>, List<CustomerData>>) obj).apply(Arrays.asList())); }
		 * else if (m.getName().equals("byName")) { System.err.println(((Function<List<Object>,
		 * List<CustomerData>>) obj).apply(Arrays.asList("name1"))); } else if (m.getName().equals("idSup"))
		 * { System.err.println(((Function<List<Object>, List<CustomerData>>) obj).apply(Arrays.asList(0)));
		 * } }
		 */

	}

}
