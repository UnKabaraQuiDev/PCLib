package lu.kbra.pclib.db.migration.schema.table;

import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.migration.schema.data.MigrationData;
import lu.kbra.pclib.db.query.SelectQueryBuilder;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationTable extends DatabaseTable<MigrationData> {

	public MigrationTable(Database database, String name) {
		super(database, name + "_migrations");
		super.customHints.put(DefaultQueryableHints.INTERNAL, true);
	}

	public MigrationData findLatestMigration() {
		return super.query(SelectQueryBuilder.<MigrationData>select().orderBy("version", OrderBy.Type.DESC).limit(1).firstThrow());
	}

}
