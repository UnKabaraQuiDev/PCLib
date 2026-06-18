package lu.kbra.pclib.db.migration;

import lu.kbra.pclib.db.autobuild.column.AutoIncrement;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.PrimaryKey;
import lu.kbra.pclib.db.autobuild.column.type.meta.MaxLength;
import lu.kbra.pclib.db.impl.DataBaseEntry;

public class MigrationPersonInitialData implements DataBaseEntry {

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

	public MigrationPersonInitialData() {
	}

	public MigrationPersonInitialData(final String firstName, final String lastName, final String obsoleteNote) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.obsoleteNote = obsoleteNote;
	}

}
