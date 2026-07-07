package lu.kbra.pclib.db.domain.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.view.AbstractDBView;

@Data
@AllArgsConstructor
public class ViewStructure implements HintsOwner {

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

	private final String name;
	private final String customSQL;
	private final Class<? extends DataBaseEntry> entryClass;
	private final List<ViewCommonTableExpressionStructure> withTables = new ArrayList<>();
	private final List<ViewTableStructure> tables = new ArrayList<>();
	private final List<UnionTableStructure> unionTables = new ArrayList<>();
	private final List<String> groupBy = new ArrayList<>();
	private final List<ViewOrderStructure> orderBy = new ArrayList<>();
	private final Map<String, Object> viewHints;
	private final String condition;
	private final boolean distinct;
	private final Map<String, Object> hints;

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
