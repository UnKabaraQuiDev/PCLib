package shared.migration.third.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.table.CountryTable;
import shared.migration.third.data.CountryData3;

public class CountryTable3 extends DatabaseTable<CountryData3> implements CountryTable<CountryData3> {

	public CountryTable3(Database database) {
		super(database, "country");
	}

}
