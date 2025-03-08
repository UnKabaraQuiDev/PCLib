package db;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.table.Column;
import lu.pcy113.pclib.db.annotations.table.Constraint;
import lu.pcy113.pclib.db.annotations.table.DB_Table;

//@formatter:off
@DB_Table(name = "test", columns = {
		@Column(name = "id", type = "int", autoIncrement = true),
		@Column(name = "name", type = "varchar(255)"),
		@Column(name = "date", type = "timestamp", default_ = "CURRENT_TIMESTAMP"),
		@Column(name = "concatted", type="varchar(100)", generated = true, generator = "CONCAT(`name`,`date`)")
}, constraints = {
		@Constraint(type = Constraint.Type.PRIMARY_KEY, name = "pk_test_id", columns = { "id" }),
		@Constraint(type = Constraint.Type.UNIQUE, name = "uq_test_name", columns = { "name" })
})
//@formatter:on
public class PersonDBTable extends DataBaseTable<Person> {

	public PersonDBTable(DataBase dbTest) {
		super(dbTest);
	}
	
}
