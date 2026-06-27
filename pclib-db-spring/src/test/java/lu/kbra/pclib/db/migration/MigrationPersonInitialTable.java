package lu.kbra.pclib.db.migration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.queryable.def.NameOverride;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.table.DataBaseTable;

@Component
@NameOverride(MigrationTestConstants.TABLE_NAME)
public class MigrationPersonInitialTable extends DataBaseTable<MigrationPersonInitialData> {

	public MigrationPersonInitialTable(@Qualifier("migrationDb") final DataBase dataBase) {
		super(dataBase);
	}

}
