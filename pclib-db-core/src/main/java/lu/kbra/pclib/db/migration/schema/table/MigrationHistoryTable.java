package lu.kbra.pclib.db.migration.schema.table;

import java.util.Optional;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.migration.schema.data.MigrationHistoryData;
import lu.kbra.pclib.db.query.SelectQueryBuilder;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationHistoryTable extends DatabaseTable<MigrationHistoryData> {

	public MigrationHistoryTable(final Database db, final String migrationName) {
		super(db, migrationName + "_history");
		super.customHints.put(DefaultQueryableHints.INTERNAL, true);
	}

	public Optional<MigrationHistoryData> findAppliedMigration(String migrationId) {
		return super.query(
				SelectQueryBuilder.<MigrationHistoryData>select().where(cb -> cb.match("migration_id", "=", migrationId)).firstOptional());
	}

}
