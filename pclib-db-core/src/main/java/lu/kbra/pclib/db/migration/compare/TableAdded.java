package lu.kbra.pclib.db.migration.compare;

import lu.kbra.pclib.db.domain.table.TableStructure;

public class TableAdded implements SchemaChange {

	private final TableStructure table;

	public TableAdded(final TableStructure table) {
		this.table = table;
	}

	public TableStructure getTable() {
		return this.table;
	}

}
