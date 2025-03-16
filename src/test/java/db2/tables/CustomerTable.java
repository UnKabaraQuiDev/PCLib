package db2.tables;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.SQLRequestType;
import lu.pcy113.pclib.db.annotations.table.Column;
import lu.pcy113.pclib.db.annotations.table.Constraint;
import lu.pcy113.pclib.db.annotations.table.DB_Table;

import db2.datas.CustomerData;

//@formatter:off
@DB_Table(name = "customers", columns = {
		@Column(name = "id", type = "int", autoIncrement = true),
		@Column(name = "name", type = "varchar(35)"),
		@Column(name = "register_time", type = "timestamp", default_ = "CURRENT_TIMESTAMP")
}, constraints = {
		@Constraint(name = "pk_id", type = Constraint.Type.PRIMARY_KEY, columns = "id"),
		@Constraint(name = "uq_name", type = Constraint.Type.UNIQUE, columns = "name")
})
//@formatter:on
public class CustomerTable extends DataBaseTable<CustomerData> {

	public CustomerTable(DataBase dbTest) {
		super(dbTest);
	}
	
	@Override
	public void requestHook(SQLRequestType type, Object query) {
		System.out.println(query);
	}

}
