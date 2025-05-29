package db;

import java.io.File;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.config.ConfigLoader;
import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.TableHelper;
import lu.pcy113.pclib.db.utils.BaseDataBaseEntryUtils;

public class DBTest {

	public static void main(String[] args) throws Exception {
		new DBTest().test();
	}

	@Test
	public void test() throws Exception {
		System.out.println(new BaseDataBaseEntryUtils().scanTable(CustomerTable.class).build());
		System.out.println(new BaseDataBaseEntryUtils().scanTable(OrderTable.class).build());

		new BaseDataBaseEntryUtils().initQueries(CustomerTable.class);
		
		CustomerData.BY_NAME_OFFSET.apply("qsd", 0);
		
		final TestDB db = new TestDB(ConfigLoader.loadFromJSONFile(new DataBaseConnector(), new File("./src/test/resources/config/db_connector.json")));

		final CustomerTable customers = new CustomerTable(db);
		final OrderTable orders = new OrderTable(db);

		db.create().runThrow();
		customers.create().runThrow();
		orders.create().runThrow();

		if (!customers.exists(new CustomerData("name1", "email1")).runThrow()) {
			System.out.println(customers.insertAndReload(new CustomerData("name1", "email1")).runThrow());
		}

		final long id = customers.query(CustomerData.byNameAndEmail("name1", "email1")).thenApply(PCUtils.first(cd -> cd.getId(), -1L)).runThrow();

		CustomerData customer = customers.load(new CustomerData(id)).runThrow();
		System.out.println(customer);

		customer = TableHelper.insertOrLoad(customers, new CustomerData("name1", "email1"), CustomerData.byNameAndEmail("name1", "email1")).runThrow();
		System.out.println(customer);

		customer = customers.loadUnique(new CustomerData("name1", "email1")).runThrow();
		System.out.println(customer);

		OrderData order1 = orders.insertAndReload(new OrderData(customer)).runThrow();
		System.out.println(order1);
		order1.setCustomerId(null);
		System.out.println(order1);
		orders.update(order1).runThrow();
		System.out.println(order1);
		orders.updateAndLoad(order1).runThrow();
		System.out.println(order1);

		System.out.println(customers.query(CustomerData.BY_NAME.apply("name1")).runThrow());
	}
}
