package lu.kbra.pclib.db.domain.view;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.DBStructure;
import lu.kbra.pclib.db.domain.table.EntryHintsOwner;
import lu.kbra.pclib.db.domain.table.StructureName;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.view.AbstractDBView;

@Data
@AllArgsConstructor
public class ViewStructure implements HintsOwner, EntryHintsOwner, DBStructure {

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

	private final StructureName structureName;
	private final String customSQL;
	private final Class<? extends AbstractDBView<? extends DataBaseEntry>> viewClass;
	private final Class<? extends DataBaseEntry> entryClass;
	private final List<ViewCommonTableExpressionStructure> withTables;
	private final List<ViewTableStructure> tables;
	private final List<UnionTableStructure> unionTables;
	private final List<String> groupBy;
	private final List<ViewOrderStructure> orderBy;
	private final Map<String, Object> hints;
	private final String condition;
	private final boolean distinct;
	private final Map<String, Object> entryHints;

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

	@Override
	public Class<? extends SQLQueryable<?>> getTargetClass() {
		return viewClass;
	}

	@Override
	public ColumnData[] getPrimaryKeys() {
		return new ColumnData[0];
	}

}
