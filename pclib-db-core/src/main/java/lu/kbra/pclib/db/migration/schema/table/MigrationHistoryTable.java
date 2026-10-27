package lu.kbra.pclib.db.migration.schema.table;

import java.util.Optional;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.migration.schema.data.MigrationHistoryData;
import lu.kbra.pclib.db.query.QueryBuilder;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationHistoryTable extends DatabaseTable<MigrationHistoryData> {

	public MigrationHistoryTable(final Database db, final String migrationName) {
		super(db, migrationName + "_history", "_dbmh");
		super.customHints.put(DefaultQueryableHints.INTERNAL, true);
	}

	public Optional<MigrationHistoryData> findAppliedMigration(final String migrationId) {
		return super.query(
				QueryBuilder.<MigrationHistoryData>select().where(cb -> cb.match("migration_id", "=", migrationId)).firstOptional());
	}

}
