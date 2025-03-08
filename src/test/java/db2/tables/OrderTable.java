package db2.tables;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.table.Column;
import lu.pcy113.pclib.db.annotations.table.Constraint;
import lu.pcy113.pclib.db.annotations.table.DB_Table;

import db2.datas.OrderData;

//@formatter:off
@DB_Table(name = "orders", columns = {
		@Column(name = "id", type = "int", autoIncrement = true),
		@Column(name = "customer_id", type = "int"),
		@Column(name = "description", type = "text"),
		@Column(name = "price", type = "int"),
		@Column(name = "delivered", type = "bit")
}, constraints = {
		@Constraint(name = "pk_id", type = Constraint.Type.PRIMARY_KEY, columns = "id"),
		@Constraint(name = "fk_customer_id", type = Constraint.Type.FOREIGN_KEY, referenceTable = "customers", referenceColumn = "id", columns = "customer_id")
})
//@formatter:on
public class OrderTable extends DataBaseTable<OrderData> {

	public OrderTable(DataBase dbTest) {
		super(dbTest);
	}

}
