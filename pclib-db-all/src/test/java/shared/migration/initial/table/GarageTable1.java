package shared.migration.initial.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

import shared.migration.initial.data.GarageData1;
import shared.migration.table.GarageTable;

public class GarageTable1 extends DatabaseTable<GarageData1> implements GarageTable<GarageData1> {

	public GarageTable1(Database database) {
		super(database, "garage");
	}

}
