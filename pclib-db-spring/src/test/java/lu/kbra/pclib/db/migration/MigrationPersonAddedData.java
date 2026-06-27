package lu.kbra.pclib.db.migration;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.AutoIncrement;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.Nullable;
import lu.kbra.pclib.db.annotations.entry.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.meta.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationPersonAddedData implements DataBaseEntry {

	@Column
	@PrimaryKey
	@AutoIncrement
	protected long id;

	@Column(name = "first_name")
	protected @MaxLength(50) String firstName;

	@Column(name = "last_name")
	protected @MaxLength(50) String lastName;

	@Column(name = "obsolete_note")
	protected @MaxLength(50) String obsoleteNote;

	@Column(name = "full_name")
	@Nullable
	protected @MaxLength(120) String fullName;

	@Override
	public MigrationPersonAddedData clone() {
		return PCUtils.safeClone(super::clone);
	}

}
