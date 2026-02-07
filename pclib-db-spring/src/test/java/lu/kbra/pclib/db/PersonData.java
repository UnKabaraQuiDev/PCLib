package lu.kbra.pclib.db;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class PersonData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	protected long id;

	@Column(length = 35)
	@Unique
	protected String name;

	public PersonData() {
	}

	public PersonData(long id) {
		this.id = id;
	}

	public PersonData(String name) {
		this.name = name;
	}

	public PersonData(long id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "PersonData@" + System.identityHashCode(this) + " [id=" + id + ", name=" + name + "]";
	}

}
