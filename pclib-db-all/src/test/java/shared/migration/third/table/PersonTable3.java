package shared.migration.third.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.table.PersonTable;
import shared.migration.third.data.PersonData3;

public class PersonTable3 extends DatabaseTable<PersonData3> implements PersonTable<PersonData3> {

	public PersonTable3(Database database) {
		super(database, "person");
	}

}
