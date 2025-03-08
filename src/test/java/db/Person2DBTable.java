package db;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.Column;
import lu.pcy113.pclib.db.annotations.Constraint;
import lu.pcy113.pclib.db.annotations.DB_Table;

@DB_Table(name = "test", columns = {
		@Column(name = "id", type = "int", autoIncrement = true),
		@Column(name = "name", type = "varchar(255)"),
		@Column(name = "date", type = "timestamp", default_ = "CURRENT_TIMESTAMP")
}, constraints = {
		@Constraint(name = "pk_test_id", type = Constraint.Type.PRIMARY_KEY, columns = { "id" }),
		@Constraint(name = "uq_test_name", type = Constraint.Type.UNIQUE, columns = { "name" })
})
public class Person2DBTable extends DataBaseTable<Person2> {

	public Person2DBTable(DataBase dbTest) {
		super(dbTest);
	}

}
