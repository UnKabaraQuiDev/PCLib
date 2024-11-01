package db;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.Column;
import lu.pcy113.pclib.db.annotations.DB_Table;

@DB_Table(name = "test", columns = {
		@Column(name = "id", type = "int", autoIncrement = true, primaryKey = true),
		@Column(name = "name", type = "varchar(255)", unique = true),
		@Column(name = "date", type = "timestamp", default_ = "CURRENT_TIMESTAMP")
})
public class TestDBTable extends DataBaseTable<Person> {

	public TestDBTable(DBTest dbTest) {
		super(dbTest);
	}

}
