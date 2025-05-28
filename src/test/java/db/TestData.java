package db;

import java.sql.Timestamp;

import lu.pcy113.pclib.db.annotations.entry.GeneratedKeyUpdate;
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
	@Unique
	private String name;

	@NColumn(name = "instant")
	@Generated
	private Timestamp instant;

	@NColumn
	@ForeignKey(table = CustomerTable.class)
	private int customerId;

	private CustomerData customerData;
	
	@GeneratedKeyUpdate
	public void update(long id, Timestamp instant) {
		this.id = id;
		this.instant = instant;
	}

}
