package shared.migration.second.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.second.data.PersonData2;
import shared.migration.table.PersonTable;

public class PersonTable2 extends DatabaseTable<PersonData2> implements PersonTable<PersonData2> {

	public PersonTable2(Database database) {
		super(database, "person");
	}

}
