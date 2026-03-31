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
	@DefaultValue("CAST((julianday('now') - julianday(birth_date)) / 365.25 AS INTEGER)")
	protected int age;

	public PersonData() {
	}

	public PersonData(final int id) {
		this.id = id;
	}

	public PersonData(final String name, final Date birthDate) {
		this.name = name;
		this.birthDate = birthDate;
	}

	@Override
	public String toString() {
		return "PersonData@" + System.identityHashCode(this) + " [id=" + this.id + ", name=" + this.name + ", birthDate=" + this.birthDate
				+ ", age=" + this.age + "]";
	}

}
