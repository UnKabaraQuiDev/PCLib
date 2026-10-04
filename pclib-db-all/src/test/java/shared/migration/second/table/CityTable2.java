package shared.migration.second.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.second.data.CityData2;

public class CityTable2 extends DatabaseTable<CityData2> {

	public CityTable2(Database database) {
		super(database, "city");
	}

}
