package shared.migration.third.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.table.CityTable;
import shared.migration.third.data.CityData3;

public class CityTable3 extends DatabaseTable<CityData3> implements CityTable<CityData3> {

	public CityTable3(Database database) {
		super(database, "cities", "city-table");
	}

}
