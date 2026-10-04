package lu.kbra.pclib.db.migration;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.migration.schema.table.MigrationColumnTable;
import lu.kbra.pclib.db.migration.schema.table.MigrationConstraintTable;
import lu.kbra.pclib.db.migration.schema.table.MigrationTable;
import lu.kbra.pclib.db.migration.schema.table.MigrationTableTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MigrationSupport {

	private MigrationTable migrationTable;
	private MigrationTableTable migrationTableTable;
	private MigrationColumnTable migrationColumnTable;
	private MigrationConstraintTable migrationConstraintTable;

	public MigrationSupport(Database db, String migrationName) {
		this.migrationTable = new MigrationTable(db, migrationName);
		this.migrationTableTable = new MigrationTableTable(db, migrationName);
		this.migrationColumnTable = new MigrationColumnTable(db, migrationName);
		this.migrationConstraintTable = new MigrationConstraintTable(db, migrationName);
	}

	public SQLQueryable<?>[] getTables() {
		return new SQLQueryable<?>[] { migrationTable, migrationTableTable, migrationColumnTable, migrationConstraintTable };
	}

}
