package shared.migration.initial.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.initial.data.PersonData1;

public class PersonTable1 extends DatabaseTable<PersonData1> {

	public PersonTable1(Database database) {
		super(database, "person");
	}

}
