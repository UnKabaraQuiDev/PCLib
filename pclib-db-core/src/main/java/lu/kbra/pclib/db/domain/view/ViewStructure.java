package lu.kbra.pclib.db.domain.view;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.PCUtils;
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
	public String toString() {
		final StringBuilder sb = new StringBuilder();

		sb.append("structureName=").append(this.structureName).append('\n');
		sb.append("targetClass=").append(this.targetClass).append('\n');
		sb.append("entryClass=").append(this.entryClass).append('\n');
		sb.append("hints=");
		if (!this.hints.isEmpty()) {
			sb.append("\n");
		}
		sb.append(PCUtils.printTree(this.hints)).append('\n');
		sb.append("entryHints=");
		if (!this.entryHints.isEmpty()) {
			sb.append("\n");
		}
		sb.append(PCUtils.printTree(this.entryHints)).append('\n');
		sb.append("columns=");
		Arrays.stream(columns).collect(PCUtils.joining(sb, "\n - "));

		sb.append("withTables=");
		Arrays.stream(withTables).collect(PCUtils.joining(sb, "\n - "));
		sb.append("tables=");
		Arrays.stream(tables).collect(PCUtils.joining(sb, "\n - "));
		sb.append("unionTables=");
		Arrays.stream(unionTables).collect(PCUtils.joining(sb, "\n - "));
		sb.append("groupBy=");
		Arrays.stream(groupBy).collect(PCUtils.joining(sb, "\n - "));
		sb.append("orderBy=");
		Arrays.stream(orderBy).collect(PCUtils.joining(sb, "\n - "));
		sb.append("condition=").append(this.condition).append('\n');
		sb.append("distinct=").append(this.distinct).append('\n');
		sb.append("customSQL=").append(this.customSQL).append('\n');
		sb.append("dependencies=");
		dependencies.stream().collect(PCUtils.joining(sb, "\n - "));

		return sb.toString();
	}
}
