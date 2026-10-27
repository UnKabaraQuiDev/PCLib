package lu.kbra.pclib.db.migration.schema.table;

import java.util.List;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.migration.schema.data.MigrationConstraintData;
import lu.kbra.pclib.db.query.QueryBuilder;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationConstraintTable extends DatabaseTable<MigrationConstraintData> {

	public MigrationConstraintTable(final Database database, final String name) {
		super(database, name + "_constraints", "_dbmcs");
		super.customHints.put(DefaultQueryableHints.INTERNAL, true);
	}

	public List<MigrationConstraintData> findByTableId(final Long id) {
		return super.query(QueryBuilder.<MigrationConstraintData>select().where(cb -> cb.match("table_id", "=", id)).list());
	}

}
