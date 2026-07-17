package lu.kbra.pclib.db.domain.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.EntryHintsOwner;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.view.AbstractDBView;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ViewStructure implements EntryHintsOwner, SQLQueryableStructure {

	// first pass
	private final StructureName structureName;
	private final Class<? extends AbstractDBView<? extends DatabaseEntry>> targetClass;
	private final Class<? extends DatabaseEntry> entryClass;
	private final Map<String, Object> hints;
	private final Map<String, Object> entryHints;
	private ColumnData[] columns;

	// second pass
	private ViewCommonTableExpressionStructure[] withTables;
	private ViewTableStructure[] tables;
	private UnionTableStructure[] unionTables;
	private String[] groupBy;
	private ViewOrderStructure[] orderBy;
	private String condition;
	private boolean distinct;
	private String customSQL;
	private Set<SQLQueryableDependency> dependencies;

	public List<ViewTableStructure> getJoinTables() {
		return Arrays.stream(this.tables)
				.filter(t -> t.getJoinType() != ViewTable.Type.MAIN && t.getJoinType() != ViewTable.Type.MAIN_UNION
						&& t.getJoinType() != ViewTable.Type.MAIN_UNION_ALL)
				.collect(Collectors.toList());
	}

	public ViewTableStructure getMainTable() {
		return Arrays.stream(this.tables)
				.filter(t -> t.getJoinType() == ViewTable.Type.MAIN || t.getJoinType() == ViewTable.Type.MAIN_UNION
						|| t.getJoinType() == ViewTable.Type.MAIN_UNION_ALL)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No main table defined."));
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("structureName", this.structureName);
		map.put("targetClass", this.targetClass);
		map.put("entryClass", this.entryClass);
		map.put("hints", this.hints);
		map.put("columns", this.columns);
		map.put("withTables", this.withTables);
		map.put("tables", this.tables);
		map.put("unionTables", this.unionTables);
		map.put("groupBy", this.groupBy);
		map.put("orderBy", this.orderBy);
		map.put("condition", this.condition);
		map.put("distinct", this.distinct);
		map.put("customSQL", this.customSQL);
		map.put("dependencies", this.dependencies);

		return map;
	}

	@Override
	public String toString() {
		return targetClass + "<" + entryClass + "> (" + structureName.getName() + ")";
	}

}
