package lu.kbra.pclib.db.domain.table;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface DBStructure extends StructureNameOwner {

	Class<? extends SQLQueryable<?>> getTargetClass();

	Class<? extends DataBaseEntry> getEntryClass();

	ColumnData[] getColumns();

}
