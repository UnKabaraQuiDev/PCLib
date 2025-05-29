package db;

import lu.pcy113.pclib.db.autobuild.column.AutoIncrement;
import lu.pcy113.pclib.db.autobuild.column.Column;
import lu.pcy113.pclib.db.autobuild.column.PrimaryKey;
import lu.pcy113.pclib.db.autobuild.column.Unique;
import lu.pcy113.pclib.db.impl.DataBaseEntry;

public class CustomerData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	private long id;

	@Column(length = 64)
	@Unique
	private String name;

	@Column(length = 320)
	@Unique(1)
	private String email;

	public CustomerData() {
	}

	public CustomerData(String name, String email) {
		this.name = name;
		this.email = email;
	}

	@Override
	public String toString() {
		return "CustomerData [id=" + id + ", name=" + name + ", email=" + email + "]";
	}

}
