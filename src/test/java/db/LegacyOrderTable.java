package db;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.SQLRequestType;
import lu.pcy113.pclib.db.annotations.table.Column;
import lu.pcy113.pclib.db.annotations.table.Constraint;
import lu.pcy113.pclib.db.annotations.table.DB_Table;

//@formatter:off
@DB_Table(name = "legacy_orders", columns = {
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
public class LegacyOrderTable extends DataBaseTable<OrderData> {

	public LegacyOrderTable(DataBase dbTest) {
		super(dbTest);
	}

	@Override
	public void requestHook(SQLRequestType type, Object query) {
	}

}