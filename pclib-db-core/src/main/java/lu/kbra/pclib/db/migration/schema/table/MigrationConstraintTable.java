package lu.kbra.pclib.db.migration.schema.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.migration.schema.data.MigrationConstraintData;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationConstraintTable extends DatabaseTable<MigrationConstraintData> {

	public MigrationConstraintTable(Database database, String name) {
		super(database, name + "_constraints");
	}

}
