package lu.kbra.pclib.db.domain.view;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.impl.SQLQueryableDependencyOwner;

import lombok.Data;

@Data
public class ViewCommonTableExpressionStructure implements SQLQueryableDependencyOwner {

	private final String name;
	private final String condition;

	private ViewTableStructure[] tables;
	private ViewColumnStructure[] columns;
	private String[] groupBy;
	private ViewOrderStructure[] orderBy;

	private Set<SQLQueryableDependency> dependencies;

	public List<ViewTableStructure> getJoinTables() {
		return Arrays.stream(this.tables)
				.filter(t -> t.getJoinType() != ViewTable.Type.MAIN && t.getJoinType() != ViewTable.Type.MAIN_UNION
						&& t.getJoinType() != ViewTable.Type.MAIN_UNION_ALL)
				.collect(Collectors.toList());
	}

	public ViewTableStructure getMainTable() {
		return Arrays.stream(this.tables)
				.filter(t -> t.getJoinType() == ViewTable.Type.MAIN)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("CTE has no main table."));
	}

	@Override
	public SQLQueryableDependency getKey() {
		throw new UnsupportedOperationException();
	}

}
