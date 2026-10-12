package lu.kbra.pclib.db.migration.schema.table;

import java.util.List;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.migration.schema.data.MigrationTableData;
import lu.kbra.pclib.db.query.SelectQueryBuilder;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationTableTable extends DatabaseTable<MigrationTableData> {

	public MigrationTableTable(Database database, String name) {
		super(database, name + "_tables", "_dbmt");
		super.customHints.put(DefaultQueryableHints.INTERNAL, true);
	}

	public List<MigrationTableData> findByMigrationId(Long id) {
		return super.query(SelectQueryBuilder.<MigrationTableData>select().where(cb -> cb.match("migration_id", "=", id)).list());
	}

}
