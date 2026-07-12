package lu.kbra.pclib.db.migration;

import lu.kbra.pclib.db.annotations.queryable.def.NameOverride;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

@NameOverride(MigrationTestConstants.TABLE_NAME)
public class MigrationPersonFinalTable extends DatabaseTable<MigrationPersonFinalData> {

	public MigrationPersonFinalTable(final Database database) {
		super(database);
	}

}
