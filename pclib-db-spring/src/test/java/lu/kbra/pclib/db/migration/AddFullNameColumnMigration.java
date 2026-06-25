package lu.kbra.pclib.db.migration;

import java.sql.Connection;
import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;

@Component
public class AddFullNameColumnMigration implements DataBaseMigration {

	@Override
	public String name() {
		return "add_full_name_column";
	}

	@Override
	public int order() {
		return 1;
	}

	@Override
	public boolean shouldRun(final DataBase dataBase) {
		return dataBase.getDataBaseName().startsWith(MigrationTestConstants.DATABASE_PREFIX);
	}

	@Override
	public void up(final DataBase dataBase, final Connection connection) throws DBException {
		new DataBaseSchemaMigrator(dataBase.getConnector())
				.migrate(connection, List.of(new MigrationPersonAddedTable(dataBase)), new SchemaMigrationOptions(true, false));
	}

}
