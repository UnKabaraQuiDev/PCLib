package db2.views;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseView;
import lu.pcy113.pclib.db.annotations.view.DB_View;
import lu.pcy113.pclib.db.annotations.view.OrderBy;
import lu.pcy113.pclib.db.annotations.view.ViewColumn;
import lu.pcy113.pclib.db.annotations.view.ViewTable;

import db2.datas.OrderData;

//@formatter:off
@DB_View(name = "customer_order_totals", tables = {
		@ViewTable(name = "orders", columns = {
				@ViewColumn(name = "customer_id"),
				@ViewColumn(func = "COUNT(customer_id)", asName = "order_count"),
				@ViewColumn(func = "SUM(price)", asName = "total_price")
		}),
		@ViewTable(name = "customers", join = ViewTable.Type.INNER, columns = {
				@ViewColumn(name = "name", asName = "customer_name"),
		}, on = "`customer_id` = `customers`.`id`")
}, groupBy = "customer_id", orderBy = {
		@OrderBy(column = "total_price", type = OrderBy.Type.DESC),
		@OrderBy(column = "order_count", type = OrderBy.Type.DESC)
})
//@formatter:on
public class CustomerOrderTotalView extends DataBaseView<OrderData> {

	public CustomerOrderTotalView(DataBase dbTest) {
		super(dbTest);
	}

}
