package db2.views;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseView;
import lu.pcy113.pclib.db.annotations.view.DB_View;
import lu.pcy113.pclib.db.annotations.view.ViewColumn;
import lu.pcy113.pclib.db.annotations.view.ViewTable;

import db2.datas.OrderData;

//@formatter:off
@DB_View(name = "undelivered_orders", tables = {
		@ViewTable(name = "orders", columns = {
				@ViewColumn(name = "id", asName = "order_id"),
				@ViewColumn(name = "customer_id"),
				@ViewColumn(name = "description"),
				@ViewColumn(name = "price")
		}),
		@ViewTable(name = "customers", join = ViewTable.Type.LEFT, columns = {
				@ViewColumn(name = "name", asName = "customer_name"),
		}, on = "`customer_id` = `customers`.`id`")
}, condition = "`delivered` = 0x00")
//@formatter:on
public class UndeliveredOrderView extends DataBaseView<OrderData> {

	public UndeliveredOrderView(DataBase dbTest) {
		super(dbTest);
	}

}
