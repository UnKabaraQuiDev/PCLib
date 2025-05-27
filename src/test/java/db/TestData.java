package db;


import java.sql.Timestamp;

import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.ForeignKey;
import lu.pcy113.pclib.db.autobuild.column.Generated;
import lu.pcy113.pclib.db.autobuild.column.NColumn;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.autobuild.column.Unique;
import lu.pcy113.pclib.db.impl.SQLEntry;

public class TestData implements SQLEntry {

	@NColumn(name = "id")
	@PrimaryKey
	@AutoIncrement
	private long id;
	
	@NColumn(name = "name", length = 64)
	@Unique(0)
	private String name;
	
	@NColumn(name = "instant")
	@Generated
	private Timestamp instant;
	
	@NColumn(name = "customer_id")
	@ForeignKey(table = CustomerTable.class)
	private int customerId;
	
	private CustomerData customerData;
	
}
