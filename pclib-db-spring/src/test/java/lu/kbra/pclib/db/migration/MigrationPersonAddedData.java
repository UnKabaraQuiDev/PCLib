package lu.kbra.pclib.db.migration;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.Nullable;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

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

}
