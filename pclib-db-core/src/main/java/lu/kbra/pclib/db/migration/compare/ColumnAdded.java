package lu.kbra.pclib.db.migration.compare;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.TableStructure;

import lombok.Data;

@Data
public class ColumnAdded implements SchemaChange {

	private final TableStructure tableStructure;
	private final ColumnData column;

}
