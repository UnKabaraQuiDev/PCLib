package lu.kbra.pclib.db.migration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.annotations.queryable.def.NameOverride;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.table.DatabaseTable;

@Component
@NameOverride(MigrationTestConstants.TABLE_NAME)
public class MigrationPersonInitialTable extends DatabaseTable<MigrationPersonInitialData> {

	public MigrationPersonInitialTable(@Qualifier("migrationDb") final Database database) {
		super(database);
	}

}
