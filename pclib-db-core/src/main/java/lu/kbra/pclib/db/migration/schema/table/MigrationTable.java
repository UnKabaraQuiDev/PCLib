package lu.kbra.pclib.db.migration.schema.table;

import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.migration.schema.data.MigrationData;
import lu.kbra.pclib.db.query.SelectQueryBuilder;
import lu.kbra.pclib.db.table.DatabaseTable;

public class MigrationTable extends DatabaseTable<MigrationData> {

	public MigrationTable(Database database, String name) {
		super(database, name + "_migrations", "_dbm");
		super.customHints.put(DefaultQueryableHints.INTERNAL, true);
	}

	public Optional<MigrationData> findLatestMigration() {
		return super.query(SelectQueryBuilder.<MigrationData>select().orderBy("version", OrderBy.Type.DESC).limit(1).firstOptional());
	}

	public List<MigrationData> findAll() {
		return super.query(SelectQueryBuilder.<MigrationData>select().orderBy("version", OrderBy.Type.DESC).list());
	}

}
