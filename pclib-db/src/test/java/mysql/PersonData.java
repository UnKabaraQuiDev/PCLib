package mysql;

import java.sql.Date;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.DefaultValue;
import lu.kbra.pclib.db.autobuild.column.Generated;
import lu.kbra.pclib.db.autobuild.column.Generated.Type;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class PersonData implements DataBaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column(length = 30)
	@Unique
	protected String name;

	@Column
	protected Date birthDate;

	@Column
	@Generated(Type.VIRTUAL)
	@DefaultValue("YEAR(birth_date)")
	protected int birthYear;

	public PersonData() {
	}

	public PersonData(int id) {
		this.id = id;
	}

	public PersonData(String name, Date birthDate) {
		this.name = name;
		this.birthDate = birthDate;
	}

	@Override
	public String toString() {
		return "PersonData@" + System.identityHashCode(this) + " [id=" + id + ", name=" + name + ", birthDate=" + birthDate + ", birthYear="
				+ birthYear + "]";
	}

}
