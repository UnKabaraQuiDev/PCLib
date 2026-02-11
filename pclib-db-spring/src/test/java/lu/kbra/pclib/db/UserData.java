package lu.kbra.pclib.db;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class UserData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	protected long id;

	@Column(length = 35)
	@Unique
	protected String name;

	@Column
	protected String pass;

	public UserData() {
	}

	public UserData(long id) {
		this.id = id;
	}

	public UserData(String name) {
		this.name = name;
	}

	public UserData(String name, String pass) {
		this.name = name;
		this.pass = pass;
	}

	public UserData(long id, String name, String pass) {
		this.id = id;
		this.name = name;
		this.pass = pass;
	}

	@Override
	public String toString() {
		return "UserData@" + System.identityHashCode(this) + " [id=" + id + ", name=" + name + ", pass=" + pass + "]";
	}

}
