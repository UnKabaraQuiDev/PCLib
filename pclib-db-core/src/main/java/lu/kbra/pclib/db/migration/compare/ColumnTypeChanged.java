package lu.kbra.pclib.db.migration.compare;

import lu.kbra.pclib.db.domain.column.ColumnData;

public class ColumnTypeChanged implements SchemaChange {

	private final ColumnData oldColumn;
	private final ColumnData newColumn;

	public ColumnTypeChanged(final ColumnData oldColumn, final ColumnData newColumn) {
		this.oldColumn = oldColumn;
		this.newColumn = newColumn;
	}

	public ColumnData getOldColumn() {
		return this.oldColumn;
	}

	public ColumnData getNewColumn() {
		return this.newColumn;
	}

}
