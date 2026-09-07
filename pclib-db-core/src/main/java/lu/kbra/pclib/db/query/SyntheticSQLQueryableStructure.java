package lu.kbra.pclib.db.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class SyntheticSQLQueryableStructure implements SQLQueryableStructure {

	private final StructureName structureName;
	private ColumnData[] columns;
	private ConstraintData[] constraints;
	private final Class<? extends DatabaseEntry> entryClass;
	private final Class<? extends SQLQueryable<?>> targetClass;
	private Set<SQLQueryableDependency> dependencies;
	private final Map<String, Object> hints;

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();

		map.put("structureName", this.structureName);
		map.put("columns", this.columns);
		map.put("constraints", this.constraints);
		map.put("entryClass", this.entryClass);
		map.put("targetClass", this.targetClass);
		map.put("dependencies", this.dependencies);
		map.put("hints", this.hints);

		return map;
	}

}
