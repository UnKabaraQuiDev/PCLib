package lu.kbra.pclib.db.domain.view;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.DBStructure;
import lu.kbra.pclib.db.domain.table.EntryHintsOwner;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.view.AbstractDBView;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class ViewStructure implements HintsOwner, EntryHintsOwner, DBStructure {

	// first pass
	private final StructureName structureName;
	private final Class<? extends AbstractDBView<? extends DataBaseEntry>> targetClass;
	private final Class<? extends DataBaseEntry> entryClass;
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
		return Arrays.stream(tables)
				.filter(t -> t.getJoinType() != ViewTable.Type.MAIN && t.getJoinType() != ViewTable.Type.MAIN_UNION
						&& t.getJoinType() != ViewTable.Type.MAIN_UNION_ALL)
				.collect(Collectors.toList());
	}

	public ViewTableStructure getMainTable() {
		return Arrays.stream(tables)
				.filter(t -> t.getJoinType() == ViewTable.Type.MAIN || t.getJoinType() == ViewTable.Type.MAIN_UNION
						|| t.getJoinType() == ViewTable.Type.MAIN_UNION_ALL)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No main table defined."));
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();
		map.put("structureName", structureName.toMap());
		map.put("targetClass", targetClass);
		map.put("entryClass", entryClass);
		map.put("hints", hints);
		map.put("columns", columns);
		map.put("withTables", withTables);
		map.put("tables", tables);
		map.put("unionTables", unionTables);
		map.put("groupBy", groupBy);
		map.put("orderBy", orderBy);
		map.put("condition", condition);
		map.put("distinct", distinct);
		map.put("customSQL", customSQL);
		map.put("dependencies", dependencies);

		return map;
	}

}
