package lu.kbra.pclib.db.migration;

import java.sql.Connection;
import java.util.List;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;

@Component
public class RemoveObsoleteNoteColumnMigration implements DataBaseMigration {

	@Override
	public String name() {
		return "remove_obsolete_note_column";
	}

	@Override
	public int order() {
		return 3;
	}

	@Override
	public boolean shouldRun(final DataBase dataBase) {
		return dataBase.getDataBaseName().startsWith(MigrationTestConstants.DATABASE_PREFIX);
	}

	@Override
	public void up(final DataBase dataBase, final Connection connection) throws DBException {
		new DataBaseSchemaMigrator()
				.migrate(connection, List.of(new MigrationPersonFinalTable(dataBase)), new SchemaMigrationOptions(false, true));
	}

}
