package lu.kbra.pclib.db.migration;

import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import lu.kbra.pclib.db.dbms.MySQLDbmsProvider;
import lu.kbra.pclib.db.dbms.PostgreSQLDbmsProvider;
import lu.kbra.pclib.db.dbms.SQLiteDbmsProvider;

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

			},
			new ManualMigrationPhase("update-iso-code", null, 0, MigrationPhase.ADD_COLUMNS) {

				@Override
				public void up(Statement stmt) throws SQLException {

					final String dbms = countries.getDatabaseEntryUtils().getDbmsQualifierName();

					if (dbms.equals(SQLiteDbmsProvider.DBMS_QUALIFIER_NAME)) {
						stmt.execute("UPDATE country SET iso_code = substr(strftime('%f', 'now'), -3);");
					} else if (dbms.equals(MySQLDbmsProvider.DBMS_QUALIFIER_NAME)) {
						stmt.execute("UPDATE country SET iso_code = RIGHT(DATE_FORMAT(NOW(6), '%f'), 3);");
					} else if (dbms.equals(PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME)) {
						stmt.execute("UPDATE country SET iso_code = RIGHT(TO_CHAR(CLOCK_TIMESTAMP(), 'US'), 3);");
					}

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
