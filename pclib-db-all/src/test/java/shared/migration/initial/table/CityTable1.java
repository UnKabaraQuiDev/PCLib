package shared.migration.initial.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.initial.data.CityData1;
import shared.migration.table.CityTable;

public class CityTable1 extends DatabaseTable<CityData1> implements CityTable<CityData1> {

	public CityTable1(Database database) {
		super(database, "city", "city-table");
	}

}
