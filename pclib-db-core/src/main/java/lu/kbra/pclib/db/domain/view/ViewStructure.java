package lu.kbra.pclib.db.domain.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.view.AbstractDBView;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewStructure {

	public static String viewClassNameToTableName(final Class<? extends AbstractDBView<?>> simpleName) {
		return ViewStructure.viewClassNameToTableName(simpleName.getSimpleName());
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

	private String name;
	private String customSQL;
	private Class<? extends DataBaseEntry> entryClass;
	private final List<ViewCommonTableExpressionStructure> withTables = new ArrayList<>();
	private final List<ViewTableStructure> tables = new ArrayList<>();
	private final List<UnionTableStructure> unionTables = new ArrayList<>();
	private final List<String> groupBy = new ArrayList<>();
	private final List<ViewOrderStructure> orderBy = new ArrayList<>();
	private Map<String, Object> viewHints;
	private String condition;
	private boolean distinct;

	public String accept(final SQLStructureVisitor visitor) {
		return visitor.create(this);
	}

	public List<ViewTableStructure> getJoinTables() {
		return this.tables.stream()
				.filter(t -> t.getJoinType() != ViewJoinType.MAIN && t.getJoinType() != ViewJoinType.MAIN_UNION
						&& t.getJoinType() != ViewJoinType.MAIN_UNION_ALL)
				.collect(Collectors.toList());
	}

	public ViewTableStructure getMainTable() {
		return this.tables.stream()
				.filter(t -> t.getJoinType() == ViewJoinType.MAIN || t.getJoinType() == ViewJoinType.MAIN_UNION
						|| t.getJoinType() == ViewJoinType.MAIN_UNION_ALL)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No main table defined."));
	}

}
