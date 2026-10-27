package lu.kbra.pclib.db.migration.schema.table;

import java.util.List;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.migration.schema.data.MigrationTableData;
import lu.kbra.pclib.db.query.QueryBuilder;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationTableTable extends DatabaseTable<MigrationTableData> {

	public MigrationTableTable(final Database database, final String name) {
		super(database, name + "_tables", "_dbmt");
		super.customHints.put(DefaultQueryableHints.INTERNAL, true);
	}

	public List<MigrationTableData> findByMigrationId(final Long id) {
		return super.query(QueryBuilder.<MigrationTableData>select().where(cb -> cb.match("migration_id", "=", id)).list());
	}

}
