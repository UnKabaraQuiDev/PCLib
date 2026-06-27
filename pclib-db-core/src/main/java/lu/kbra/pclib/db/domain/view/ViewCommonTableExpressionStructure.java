package lu.kbra.pclib.db.domain.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ViewCommonTableExpressionStructure {

	private String name;
	private String condition;

	private final List<ViewTableStructure> tables = new ArrayList<>();
	private final List<ViewColumnStructure> columns = new ArrayList<>();
	private final List<String> groupBy = new ArrayList<>();
	private final List<ViewOrderStructure> orderBy = new ArrayList<>();

	public List<ViewColumnStructure> getColumns() {
		return this.columns;
	}

	public String getCondition() {
		return this.condition;
	}

	public List<String> getGroupBy() {
		return this.groupBy;
	}

	public List<ViewTableStructure> getJoinTables() {
		return this.tables.stream().filter(t -> t.getJoinType() != ViewJoinType.MAIN).collect(Collectors.toList());
	}

	public ViewTableStructure getMainTable() {
		return this.tables.stream()
				.filter(t -> t.getJoinType() == ViewJoinType.MAIN)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("CTE has no main table."));
	}

	public String getName() {
		return this.name;
	}

	public List<ViewOrderStructure> getOrderBy() {
		return this.orderBy;
	}

	public List<ViewTableStructure> getTables() {
		return this.tables;
	}

	public void setCondition(final String condition) {
		this.condition = condition;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "ViewCommonTableExpressionStructure@" + System.identityHashCode(this) + " [name=" + this.name + ", condition="
				+ this.condition + ", tables=" + this.tables + ", columns=" + this.columns + ", groupBy=" + this.groupBy + ", orderBy="
				+ this.orderBy + "]";
	}

}
