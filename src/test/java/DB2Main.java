import java.io.File;

import org.junit.jupiter.api.Test;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.config.ConfigLoader;
import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.ReturnData;
import lu.pcy113.pclib.db.TableHelper;
import lu.pcy113.pclib.db.utils.CachedSQLEntryUtils;
import lu.pcy113.pclib.db.utils.SQLEntryUtils;

import db2.BusinessDataBase;
import db2.datas.CustomerData;
import db2.datas.OrderData;

public class DB2Main {

	public static void main(String[] args) throws Exception {
		new DB2Main().dbTest();
	}

	@Test
	public void dbTest() throws Exception {
		try {
			SQLEntryUtils.register(new CachedSQLEntryUtils());

			BusinessDataBase db = new BusinessDataBase(ConfigLoader.loadFromJSONFile(new DataBaseConnector(), new File("./src/test/resources/config/db_connector.json")));

			System.out.println(db.getCreateSQL());
			System.out.println(db.CUSTOMERS.getCreateSQL());
			System.out.println(db.ORDERS.getCreateSQL());

			db.create().thenConsume(System.out::println).run();
			db.CUSTOMERS.create().thenConsume(System.out::println).run();
			db.ORDERS.create().thenConsume(System.out::println).run();

			db.ORDERS.clear().catch_(Exception::printStackTrace).thenApply(PCUtils.single2SingleMultiMap()).thenParallel(v -> System.out.println("Cleared " + v + " orders")).run();

			// @formatter:off
			final CustomerData customer1 = TableHelper.insertOrLoad(db.CUSTOMERS, new CustomerData("person1"), () -> CustomerData.byName("person1"))
					.catch_(Exception::printStackTrace)
					.thenParallel(v -> System.out.println("Created customer (1): " + v))
					.run();
			
			final CustomerData customer2 = TableHelper.insertOrLoad(db.CUSTOMERS, new CustomerData("person2"), () -> CustomerData.byName("person2"))
					.catch_(Exception::printStackTrace)
					.thenParallel(v -> System.out.println("Created customer (2): " + v))
					.run();
			// @formatter:on

			System.out.println("Customer count: " + db.CUSTOMERS.count().thenApply(ReturnData::getData).run());

			// @formatter:off
			final OrderData customer1order1 = db.ORDERS.insertAndReload(new OrderData(customer1, "order1", 12, false))
					.catch_(Exception::printStackTrace)
					.thenApply(PCUtils.single2SingleMultiMap())
					.thenParallel(v -> System.out.println("Created order (1): " + v))
					.run();
			
			final OrderData customer1order2 = db.ORDERS.insertAndReload(new OrderData(customer1, "order2", 13, true))
					.catch_(Exception::printStackTrace)
					.thenApply(PCUtils.single2SingleMultiMap())
					.thenParallel(v -> System.out.println("Created order (2): " + v))
					.run();
			
			final OrderData customer1order3 = db.ORDERS.insertAndReload(new OrderData(customer1, "order3", 1, false))
					.catch_(Exception::printStackTrace)
					.thenApply(PCUtils.single2SingleMultiMap())
					.thenParallel(v -> System.out.println("Created order (3): " + v))
					.run();
			
			
			final OrderData customer2order1 = db.ORDERS.insertAndReload(new OrderData(customer2, "order1", 13, true))
					.catch_(Exception::printStackTrace)
					.thenApply(PCUtils.single2SingleMultiMap())
					.thenParallel(v -> System.out.println("Created order (4): " + v))
					.run();
			
			final OrderData customer2order2 = db.ORDERS.insertAndReload(new OrderData(customer2, "order2", 1, true))
					.catch_(Exception::printStackTrace)
					.thenApply(PCUtils.single2SingleMultiMap())
					.thenParallel(v -> System.out.println("Created order (5): " + v))
					.run();
			
			final OrderData customer2order3 = db.ORDERS.insertAndReload(new OrderData(customer2, "order3", 42, false))
					.catch_(Exception::printStackTrace)
					.thenApply(PCUtils.single2SingleMultiMap())
					.thenParallel(v -> System.out.println("Created order (6): " + v))
					.run();
			// @formatter:on

			System.out.println("Order count: " + db.ORDERS.count().thenApply(ReturnData::getData).run());

			System.out.println(db.DELIVERED_ORDERS.getCreateSQL());
			System.out.println(db.UNDELIVERED_ORDERS.getCreateSQL());
			System.out.println(db.CUSTOMER_ORDER_TOTAL.getCreateSQL());
			System.out.println(db.ALL_CUSTOMERS.getCreateSQL());
			
			db.DELIVERED_ORDERS.create().thenConsume(System.out::println).run();
			db.UNDELIVERED_ORDERS.create().thenConsume(System.out::println).run();
			db.CUSTOMER_ORDER_TOTAL.create().thenConsume(System.out::println).run();
			db.ALL_CUSTOMERS.create().thenConsume(System.out::println).run();
			
			db.drop().thenConsume((i) -> System.out.println(i)).run();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
