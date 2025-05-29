package db;

import java.io.File;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.config.ConfigLoader;
import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.utils.BaseDataBaseEntryUtils;

public class SQLBuilderTest {

	@Test
	public void test() throws Exception {
		System.out.println(new BaseDataBaseEntryUtils().scanTable(CustomerTable.class).build());
		System.out.println(new BaseDataBaseEntryUtils().scanTable(OrderTable.class).build());

		final DBTest db = new DBTest(ConfigLoader.loadFromJSONFile(new DataBaseConnector(), new File("./src/test/resources/config/db_connector.json")));

		final CustomerTable customers = new CustomerTable(db);
		final OrderTable orders = new OrderTable(db);
		
		db.create().runThrow();
		customers.create().runThrow();
		orders.create().runThrow();
	}
}
