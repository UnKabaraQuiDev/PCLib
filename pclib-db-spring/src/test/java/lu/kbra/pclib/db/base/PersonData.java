package lu.kbra.pclib.db.base;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.annotations.entry.Unique;
import lu.kbra.pclib.db.autobuild.column.meta.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	protected long id;

	@Column
	@Unique
	protected @MaxLength(35) String name;

	public PersonData(final long id) {
		this.id = id;
	}

	public PersonData(final String name) {
		this.name = name;
	}

	@Override
	public PersonData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
