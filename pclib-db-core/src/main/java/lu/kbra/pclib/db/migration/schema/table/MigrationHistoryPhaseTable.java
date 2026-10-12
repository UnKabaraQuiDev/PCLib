package lu.kbra.pclib.db.migration.schema.table;

import java.util.Optional;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.migration.schema.data.MigrationHistoryPhaseData;
import lu.kbra.pclib.db.query.SelectQueryBuilder;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationHistoryPhaseTable extends DatabaseTable<MigrationHistoryPhaseData> {

	public MigrationHistoryPhaseTable(final Database db, final String migrationName) {
		super(db, migrationName + "_history_phase", "_dbmhp");
		super.customHints.put(DefaultQueryableHints.INTERNAL, true);
	}

	public Optional<MigrationHistoryPhaseData> findAppliedMigration(long migrationId, String phaseId) {
		return super.query(getFindAppliedMigration(migrationId, phaseId));
	}

	public TransformingQuery<MigrationHistoryPhaseData, Optional<MigrationHistoryPhaseData>>
			getFindAppliedMigration(long migrationId, String phaseId) {
		return SelectQueryBuilder.<MigrationHistoryPhaseData>select()
				.where(cb -> cb.match("migration_id", "=", migrationId).match("phase_id", "=", phaseId))
				.firstOptional();
	}

}
