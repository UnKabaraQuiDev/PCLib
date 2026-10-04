package lu.kbra.pclib.db.migration.schema.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.migration.schema.data.MigrationTableData;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationTableTable extends DatabaseTable<MigrationTableData> {

	public MigrationTableTable(Database database, String name) {
		super(database, name + "_tables");
	}

}
