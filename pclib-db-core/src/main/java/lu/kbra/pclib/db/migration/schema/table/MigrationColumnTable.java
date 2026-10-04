package lu.kbra.pclib.db.migration.schema.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.migration.schema.data.MigrationColumnData;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationColumnTable extends DatabaseTable<MigrationColumnData> {

	public MigrationColumnTable(Database database, String name) {
		super(database, name + "_columns");
	}

}
