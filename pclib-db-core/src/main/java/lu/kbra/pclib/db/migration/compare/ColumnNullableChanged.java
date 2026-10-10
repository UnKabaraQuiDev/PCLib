package lu.kbra.pclib.db.migration.compare;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.TableStructure;

import lombok.Data;

@Data
public class ColumnNullableChanged implements SchemaChange {

	private final TableStructure tableStructure;
	private final ColumnData oldColumn;
	private final ColumnData newColumn;

}
