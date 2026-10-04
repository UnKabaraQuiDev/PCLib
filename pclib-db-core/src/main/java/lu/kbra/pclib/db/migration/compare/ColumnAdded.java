package lu.kbra.pclib.db.migration.compare;

import lu.kbra.pclib.db.domain.column.ColumnData;

public class ColumnAdded implements SchemaChange {

	private final ColumnData column;

	public ColumnAdded(final ColumnData column) {
		this.column = column;
	}

	public ColumnData getColumn() {
		return this.column;
	}

}
