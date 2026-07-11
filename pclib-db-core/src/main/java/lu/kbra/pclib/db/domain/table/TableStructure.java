package lu.kbra.pclib.db.domain.table;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TableStructure implements HintsOwner, EntryHintsOwner, DBStructure {

	private final StructureName structureName;
	private final Class<? extends AbstractDBTable<? extends DataBaseEntry>> tableClass;
	private final Class<? extends DataBaseEntry> entryClass;
	private final Map<String, Object> hints;
	private final Map<String, Object> entryHints;
	private ColumnData[] columns;
	private ConstraintData[] constraints;

	@Override
	public Class<? extends SQLQueryable<?>> getTargetClass() {
		return tableClass;
	}

}
