import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

public class PersonTable extends DatabaseTable<PersonData> {

	public PersonTable(final Database database) {
		super(database);
	}

	public PersonTable(final Database database, final DatabaseEntryUtils dbEntryUtils) {
		super(database, dbEntryUtils);
	}

}
