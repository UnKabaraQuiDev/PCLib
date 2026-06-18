package lu.kbra.pclib.db.migration;

import lu.kbra.pclib.db.autobuild.table.TableName;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DataBaseTable;

@TableName(MigrationTestConstants.TABLE_NAME)
public class MigrationPersonFinalTable extends DataBaseTable<MigrationPersonFinalData> {

	public MigrationPersonFinalTable(final DataBase dataBase) {
		super(dataBase);
	}

}
