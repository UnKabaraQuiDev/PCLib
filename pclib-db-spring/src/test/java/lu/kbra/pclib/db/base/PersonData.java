package lu.kbra.pclib.db.base;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.Unique;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
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

	public PersonData() {
	}

	public PersonData(final long id) {
		this.id = id;
	}

	public PersonData(final long id, final String name) {
		this.id = id;
		this.name = name;
	}

	public PersonData(final String name) {
		this.name = name;
	}

	@Override
	public PersonData clone() {
		return PCUtils.safeClone(super::clone);
	}

	@Override
	public String toString() {
		return "PersonData@" + System.identityHashCode(this) + " [id=" + this.id + ", name=" + this.name + "]";
	}

}
