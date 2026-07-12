package postgres;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

public class PersonTable extends DatabaseTable<PersonData> {

	public PersonTable(final Database database) {
		super(database);
	}

}
