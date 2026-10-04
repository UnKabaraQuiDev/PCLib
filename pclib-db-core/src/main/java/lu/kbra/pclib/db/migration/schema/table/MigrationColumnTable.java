package lu.kbra.pclib.db.migration.schema.table;

import java.util.List;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.migration.schema.data.MigrationColumnData;
import lu.kbra.pclib.db.query.SelectQueryBuilder;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationColumnTable extends DatabaseTable<MigrationColumnData> {

	public MigrationColumnTable(Database database, String name) {
		super(database, name + "_columns");
		super.customHints.put(DefaultQueryableHints.INTERNAL, true);
	}

	public List<MigrationColumnData> findByTableId(Long id) {
		return super.query(SelectQueryBuilder.<MigrationColumnData>select().where(cb -> cb.match("table_id", "=", id)).list());
	}

}
