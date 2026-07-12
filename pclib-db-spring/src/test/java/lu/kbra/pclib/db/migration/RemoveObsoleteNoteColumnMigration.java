package lu.kbra.pclib.db.migration;

import java.sql.Connection;

import org.springframework.stereotype.Component;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.exception.DBException;

@Component
public class RemoveObsoleteNoteColumnMigration implements DatabaseMigration {

	@Override
	public String name() {
		return "remove_obsolete_note_column";
	}

	@Override
	public int order() {
		return 3;
	}

	@Override
	public boolean shouldRun(final Database database) {
		return database.getDatabaseName().startsWith(MigrationTestConstants.DATABASE_PREFIX);
	}

	@Override
	public void up(final Database database, final Connection connection) throws DBException {
	}

}
