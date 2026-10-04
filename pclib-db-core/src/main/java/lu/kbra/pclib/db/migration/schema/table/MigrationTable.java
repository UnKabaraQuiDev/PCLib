package lu.kbra.pclib.db.migration.schema.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.migration.schema.data.MigrationData;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationTable extends DatabaseTable<MigrationData> {

	public MigrationTable(Database database, String name) {
		super(database, name + "_migrations");
	}

}
