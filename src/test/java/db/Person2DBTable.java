package db;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.Column;
import lu.pcy113.pclib.db.annotations.DB_Table;

@DB_Table(name = "test", columns = {
		@Column(name = "id", type = "int", autoIncrement = true, primaryKey = true),
		@Column(name = "name", type = "varchar(255)", unique = true),
		@Column(name = "date", type = "timestamp", default_ = "CURRENT_TIMESTAMP")
})
public class Person2DBTable extends DataBaseTable<Person2> {

	public Person2DBTable(DBTest dbTest) {
		super(dbTest);
	}

}
