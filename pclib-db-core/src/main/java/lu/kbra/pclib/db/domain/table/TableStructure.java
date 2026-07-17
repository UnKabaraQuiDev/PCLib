package lu.kbra.pclib.db.domain.table;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.table.AbstractDBTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class TableStructure implements EntryHintsOwner, SQLQueryableStructure {

	// first pass
	private final StructureName structureName;
	private final Class<? extends AbstractDBTable<?>> targetClass;
	private final Class<? extends DatabaseEntry> entryClass;
	private final Map<String, Object> hints;
	private final Map<String, Object> entryHints;
	private ColumnData[] columns;

	// second pass
	private ConstraintData[] constraints;
	private Set<SQLQueryableDependency> dependencies;

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("structureName", this.structureName);
		map.put("targetClass", this.targetClass);
		map.put("entryClass", this.entryClass);
		map.put("hints", this.hints);
		map.put("entryHints", this.entryHints);
		map.put("columns", this.columns);
		map.put("constraints", this.constraints);
		map.put("dependencies", this.dependencies);

		return map;
	}

	@Override
	public String toString() {
		return targetClass + "<" + entryClass + "> (" + structureName.getName() + ")";
	}

}
