package shared.migration.third.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.third.data.GarageData3;

public class GarageTable3 extends DatabaseTable<GarageData3> {

	public GarageTable3(Database database) {
		super(database, "garage");
	}

}
