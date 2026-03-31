package lu.kbra.pclib.db.autobuild.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.SQLBuildable;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.view.AbstractDBView;

public class ViewStructure implements SQLBuildable {

	private String name;
	private String customSQL;

	private final List<ViewCommonTableExpressionStructure> withTables = new ArrayList<>();
	private final List<ViewTableStructure> tables = new ArrayList<>();
	private final List<UnionTableStructure> unionTables = new ArrayList<>();
	private final List<String> groupBy = new ArrayList<>();
	private final List<ViewOrderStructure> orderBy = new ArrayList<>();

	private String condition;
	private boolean distinct;

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getCustomSQL() {
		return this.customSQL;
	}

	public void setCustomSQL(final String customSQL) {
		this.customSQL = customSQL;
	}

	public List<ViewCommonTableExpressionStructure> getWithTables() {
		return this.withTables;
	}

	public List<ViewTableStructure> getTables() {
		return this.tables;
	}

	public List<UnionTableStructure> getUnionTables() {
		return this.unionTables;
	}

	public List<String> getGroupBy() {
		return this.groupBy;
	}

	public List<ViewOrderStructure> getOrderBy() {
		return this.orderBy;
	}

	public String getCondition() {
		return this.condition;
	}

	public void setCondition(final String condition) {
		this.condition = condition;
	}

	public boolean isDistinct() {
		return this.distinct;
	}

	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	public ViewTableStructure getMainTable() {
		return this.tables.stream()
				.filter(t -> t.getJoinType() == ViewJoinType.MAIN || t.getJoinType() == ViewJoinType.MAIN_UNION
						|| t.getJoinType() == ViewJoinType.MAIN_UNION_ALL)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No main table defined."));
	}

	public List<ViewTableStructure> getJoinTables() {
		return this.tables.stream()
				.filter(t -> t.getJoinType() != ViewJoinType.MAIN && t.getJoinType() != ViewJoinType.MAIN_UNION
						&& t.getJoinType() != ViewJoinType.MAIN_UNION_ALL)
				.collect(Collectors.toList());
	}

	public static String viewClassNameToTableName(String className) {
		if (className == null || className.isEmpty() || className.trim().isEmpty()) {
			return className;
		}

		if (className.toLowerCase().startsWith("ro")) {
			className = "ro" + className.substring(2);
		}

		if (className.toLowerCase().endsWith("roview")) {
			className = "ro" + className.substring(0, className.length() - 6);
		} else if (className.toLowerCase().endsWith("view")) {
			className = className.substring(0, className.length() - 4);
		}

		return PCUtils.camelCaseToSnakeCase(className);
	}

	public static String viewClassNameToTableName(final Class<? extends AbstractDBView<?>> simpleName) {
		return ViewStructure.viewClassNameToTableName(simpleName.getSimpleName());
	}

	@Override
	public String build(final DataBaseConnector connector) {
		return new ViewSQLBuilder(connector.getDatabase(), this).buildCreateSQL();
	}

	@Override
	public String toString() {
		return "ViewStructure@" + System.identityHashCode(this) + " [name=" + this.name + ", customSQL=" + this.customSQL + ", withTables="
				+ this.withTables + ", tables=" + this.tables + ", unionTables=" + this.unionTables + ", groupBy=" + this.groupBy
				+ ", orderBy=" + this.orderBy + ", condition=" + this.condition + ", distinct=" + this.distinct + "]";
	}

}
