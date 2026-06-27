package lu.kbra.pclib.db.migration;

import lu.kbra.pclib.db.annotations.queryable.def.NameOverride;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DataBaseTable;

@NameOverride(MigrationTestConstants.TABLE_NAME)
public class MigrationPersonFinalTable extends DataBaseTable<MigrationPersonFinalData> {

	public MigrationPersonFinalTable(final DataBase dataBase) {
		super(dataBase);
	}

}
