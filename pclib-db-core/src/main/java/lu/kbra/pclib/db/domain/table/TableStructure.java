package lu.kbra.pclib.db.domain.table;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.table.AbstractDBTable;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TableStructure implements HintsOwner, EntryHintsOwner, DBStructure {

	// first pass
	private final StructureName structureName;
	private final Class<? extends AbstractDBTable<?>> targetClass;
	private final Class<? extends DataBaseEntry> entryClass;
	private final Map<String, Object> hints;
	private final Map<String, Object> entryHints;
	private ColumnData[] columns;

	// second pass
	private ConstraintData[] constraints;
	private Set<SQLQueryableDependency> dependencies;

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("structureName", structureName.toMap());
		map.put("targetClass", targetClass);
		map.put("entryClass", entryClass);
		map.put("hints", hints);
		map.put("entryHints", entryHints);
		map.put("columns", columns);
		map.put("constraints", constraints);
		map.put("dependencies", dependencies);

		return map;
	}

}
