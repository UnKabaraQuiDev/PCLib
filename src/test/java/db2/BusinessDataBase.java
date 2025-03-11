package db2;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.annotations.base.DB_Base;

import db2.tables.CustomerTable;
import db2.tables.OrderTable;
import db2.views.AllCustomersView;
import db2.views.CustomerOrderTotalView;
import db2.views.DeliveredOrderView;
import db2.views.UndeliveredOrderView;

@DB_Base(name = "pclib_test_business")
public class BusinessDataBase extends DataBase {

	public CustomerTable CUSTOMERS = new CustomerTable(this);
	public OrderTable ORDERS = new OrderTable(this);

	public DeliveredOrderView DELIVERED_ORDERS = new DeliveredOrderView(this);
	public UndeliveredOrderView UNDELIVERED_ORDERS = new UndeliveredOrderView(this);
	public CustomerOrderTotalView CUSTOMER_ORDER_TOTAL = new CustomerOrderTotalView(this);
	public AllCustomersView ALL_CUSTOMERS = new AllCustomersView(this);
	
	public BusinessDataBase(DataBaseConnector connector) {
		super(connector);
	}

}
