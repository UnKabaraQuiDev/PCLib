package db;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.Column;
import lu.pcy113.pclib.db.annotations.Constraint;
import lu.pcy113.pclib.db.annotations.DB_Table;

//@formatter:off
@DB_Table(name = "test", columns = {
		@Column(name = "id", type = "int", autoIncrement = true),
		@Column(name = "name", type = "varchar(255)", unique = true),
		@Column(name = "date", type = "timestamp", default_ = "CURRENT_TIMESTAMP"),
		@Column(name = "concatted", type="varchar(100)", generated = true, generator = "CONCAT(`name`,`date`)")
}, constraints = {
		@Constraint(type = Constraint.Type.PRIMARY_KEY, name = "pk_id", columns = { "id" })
})
//@formatter:on
public class PersonDBTable extends DataBaseTable<Person> {

	public PersonDBTable(DBTest dbTest) {
		super(dbTest);
	}

}
