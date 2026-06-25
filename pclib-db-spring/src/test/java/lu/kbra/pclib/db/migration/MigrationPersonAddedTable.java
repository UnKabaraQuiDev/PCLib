package lu.kbra.pclib.db.migration;

import lu.kbra.pclib.db.autobuild.table.meta.TableName;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DataBaseTable;

@TableName(MigrationTestConstants.TABLE_NAME)
public class MigrationPersonAddedTable extends DataBaseTable<MigrationPersonAddedData> {

	public MigrationPersonAddedTable(final DataBase dataBase) {
		super(dataBase);
	}

}
