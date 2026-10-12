package shared.migration.second.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.second.data.CountryData2;
import shared.migration.table.CountryTable;

public class CountryTable2 extends DatabaseTable<CountryData2> implements CountryTable<CountryData2> {

	public CountryTable2(Database database) {
		super(database, "country");
	}

}
