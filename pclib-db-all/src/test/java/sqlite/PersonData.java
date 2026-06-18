package sqlite;

import java.sql.Date;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.DefaultValue;
import lu.kbra.pclib.db.autobuild.column.Generated;
import lu.kbra.pclib.db.autobuild.column.Generated.Type;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class PersonData implements DataBaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column
	@Unique
	protected @MaxLength(30) String name;

	@Column
	protected Date birthDate;

	@Column
	@Generated(Type.VIRTUAL)
	@DefaultValue("CAST(strftime('%Y', birth_date) AS INTEGER)")
	protected Integer birthYear;

	public PersonData() {
	}

	public PersonData(final int id) {
		this.id = id;
	}

	public PersonData(final String name, final Date birthDate) {
		this.name = name;
		this.birthDate = birthDate;
	}

}
