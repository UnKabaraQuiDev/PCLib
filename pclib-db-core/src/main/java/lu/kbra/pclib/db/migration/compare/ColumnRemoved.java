package lu.kbra.pclib.db.migration.compare;

import lu.kbra.pclib.db.domain.column.ColumnData;

public class ColumnRemoved implements SchemaChange {

	private final ColumnData column;

	public ColumnRemoved(final ColumnData column) {
		this.column = column;
	}

	public ColumnData getColumn() {
		return this.column;
	}

}
