package lu.kbra.pclib.db.migration;

import lu.kbra.pclib.db.migration.DatabaseMigration.DatabaseMigrationPhase;

import lombok.Data;

@Data
public class MigratedPhase {

	private final DatabaseMigration migration;
	private final DatabaseMigrationPhase phase;

}
