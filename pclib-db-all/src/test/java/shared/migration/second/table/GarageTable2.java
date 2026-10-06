package shared.migration.second.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.second.data.GarageData2;
import shared.migration.table.GarageTable;

public class GarageTable2 extends DatabaseTable<GarageData2> implements GarageTable<GarageData2> {

	public GarageTable2(Database database) {
		super(database, "garage");
	}

}
