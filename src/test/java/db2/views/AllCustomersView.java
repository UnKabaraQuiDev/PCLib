package db2.views;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseView;
import lu.pcy113.pclib.db.annotations.view.DB_View;
import lu.pcy113.pclib.db.annotations.view.ViewColumn;
import lu.pcy113.pclib.db.annotations.view.ViewTable;

import db2.datas.OrderData;

//@formatter:off
@DB_View(name = "customer_order_totals", tables = {
		@ViewTable(name = "customers", columns = {
				@ViewColumn(name = "*"),
		})
})
//@formatter:on
public class AllCustomersView extends DataBaseView<OrderData> {

	public AllCustomersView(DataBase dbTest) {
		super(dbTest);
	}

}
