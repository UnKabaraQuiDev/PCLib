package lu.kbra.pclib.db.base;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.annotations.entry.def.MaxLength;
import lu.kbra.pclib.db.impl.DatabaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserData implements DatabaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	protected long id;

	@Column
	@Unique
	protected @MaxLength(35) String name;

	@Column
	protected String pass;

	public UserData(final long id) {
		this.id = id;
	}

	public UserData(final String name) {
		this.name = name;
	}

	public UserData(final String name, final String pass) {
		this.name = name;
		this.pass = pass;
	}

	@Override
	public UserData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
