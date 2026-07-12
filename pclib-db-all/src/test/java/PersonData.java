import java.sql.Date;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.DefaultValue;
import lu.kbra.pclib.db.annotations.entry.Generated;
import lu.kbra.pclib.db.annotations.entry.Generated.Type;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.FixedLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonData implements DatabaseEntry {

	@Column
	@AutoIncrement
	@PrimaryKey
	protected int id;

	@Column
	@Unique
	protected @FixedLength(30) String name;

	@Column
	protected Date birthDate;

	@Column
	@Generated(Type.VIRTUAL)
	@DefaultValue("CAST((julianday('now') - julianday(birth_date)) / 365.25 AS INTEGER)")
	protected int age;

	public PersonData(final int id) {
		this.id = id;
	}

	public PersonData(final String name, final Date birthDate) {
		this.name = name;
		this.birthDate = birthDate;
	}

	@Override
	public PersonData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
