package db;

import java.util.concurrent.CompletableFuture;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.annotations.Column;
import lu.pcy113.pclib.db.annotations.DB_Table;

@DB_Table(name = "test", columns = { @Column(name = "id", type = "int"), @Column(name = "name", type = "varchar(255)") })
public class TestDBTable extends DataBaseTable<String> {

	public TestDBTable(DBTest dbTest) {
		super(dbTest);
	}

}
