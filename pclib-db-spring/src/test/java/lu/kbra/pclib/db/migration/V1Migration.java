package lu.kbra.pclib.db.migration;

import java.sql.SQLException;
import java.sql.Statement;

public class V1Migration implements DatabaseMigration {

	DatabaseMigrationPhase[] phases = new DatabaseMigrationPhase[] {
			new ManualMigrationPhase("xxx-should-never-run", null, 0, MigrationPhase.ADD_COLUMNS) {

				@Override
				public void up(Statement stmt) throws SQLException {

					throw new IllegalStateException("Should never run.");

				}

			} };

	@Override
	public DatabaseMigrationPhase[] phases() {
		return phases;
	}

	@Override
	public int order() {
		return 0;
	}

	@Override
	public String name() {
		return "V1 Bump";
	}

	@Override
	public String id() {
		return "v1";
	}

}
