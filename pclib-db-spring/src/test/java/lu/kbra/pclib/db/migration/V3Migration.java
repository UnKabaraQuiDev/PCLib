package lu.kbra.pclib.db.migration;

import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import shared.migration.table.CountryTable;

public class V3Migration implements DatabaseMigration {

	@Lazy
	@Autowired
	private CountryTable countries;

	DatabaseMigrationPhase[] phases = new DatabaseMigrationPhase[] {
			new ManualMigrationPhase("update-phone-col", "Update Phone column, fill with name column.", 0, MigrationPhase.ADD_COLUMNS) {

				@Override
				public void up(Statement stmt) throws SQLException {

					stmt.execute("UPDATE person SET phone = name;");

				}

			},
			new ManualMigrationPhase("update-garage-id", null, 0, MigrationPhase.ADD_COLUMNS) {

				@Override
				public void up(Statement stmt) throws SQLException {

					stmt.execute("UPDATE person SET garage_id = (SELECT id FROM garage LIMIT 1);");

				}

			},
			new ManualMigrationPhase("update-zip-code", null, 0, MigrationPhase.ADD_COLUMNS) {

				@Override
				public void up(Statement stmt) throws SQLException {

					stmt.execute("UPDATE cities SET zip_code = 'XX-1234';");

				}

			},
			new ManualMigrationPhase("update-country-id", null, 0, MigrationPhase.ADD_COLUMNS) {

				@Override
				public void up(Statement stmt) throws SQLException {

					stmt.execute("UPDATE cities SET country_id = 1;");

				}

			} };

	@Override
	public DatabaseMigrationPhase[] phases() {
		return phases;
	}

	@Override
	public int order() {
		return 1;
	}

	@Override
	public String name() {
		return "V3 Bump";
	}

	@Override
	public String id() {
		return "v3";
	}

}
