package lu.kbra.pclib.db.migration;

import java.sql.SQLException;
import java.sql.Statement;

public class V2Migration implements DatabaseMigration {

	DatabaseMigrationPhase[] phases = new DatabaseMigrationPhase[] {
			new ManualMigrationPhase("update-country-code-col", null, 0, MigrationPhase.ADD_COLUMNS) {

				@Override
				public void up(Statement stmt) throws SQLException {

					stmt.execute("UPDATE city SET country_code = 'AA';");

				}

			},
			new ManualMigrationPhase("update-address-col", null, 0, MigrationPhase.ADD_COLUMNS) {

				@Override
				public void up(Statement stmt) throws SQLException {

					stmt.execute("UPDATE garage SET address = 'something not null';");

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
		return "V2 Bump";
	}

	@Override
	public String id() {
		return "v2";
	}

}
