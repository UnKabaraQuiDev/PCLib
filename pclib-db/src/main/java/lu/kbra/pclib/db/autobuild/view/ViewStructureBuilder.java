package lu.kbra.pclib.db.autobuild.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.annotations.view.DB_View;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.annotations.view.UnionTable;
import lu.kbra.pclib.db.annotations.view.ViewColumn;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.annotations.view.ViewWithTable;
import lu.kbra.pclib.db.autobuild.table.ForeignKeyData;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.view.AbstractDBView;

public class ViewStructureBuilder<T extends DataBaseEntry> {

	private final DataBaseEntryUtils dataBaseEntryUtils;
	private final Class<? extends AbstractDBView<T>> viewClass;

	public ViewStructureBuilder(final AbstractDBView<T> dataBase) {
		this.viewClass = (Class<? extends AbstractDBView<T>>) dataBase.getClass();
		this.dataBaseEntryUtils = dataBase.getDbEntryUtils();
	}

	public ViewStructureBuilder(final Class<? extends AbstractDBView<T>> dataBase, final DataBaseEntryUtils dbEntryUtils) {
		this.viewClass = dataBase;
		this.dataBaseEntryUtils = dbEntryUtils;
	}

	public ViewStructure build() {
		final DB_View annotation = viewClass.getAnnotation(DB_View.class);
		if (annotation == null) {
			throw new IllegalArgumentException("Class is not annotated with @DB_View: " + viewClass.getName());
		}

		final ViewStructure structure = new ViewStructure();
		structure.setName(annotation.name().trim().isEmpty() ? getTypeName(viewClass) : annotation.name());
		structure.setCustomSQL(annotation.customSQL());
		structure.setCondition(annotation.condition());

		for (final String group : annotation.groupBy()) {
			structure.getGroupBy().add(group);
		}

		for (final OrderBy order : annotation.orderBy()) {
			final ViewOrderStructure os = new ViewOrderStructure();
			os.setColumn(order.column());
			os.setType(order.type().name());
			structure.getOrderBy().add(os);
		}

		for (final ViewWithTable with : annotation.with()) {
			structure.getWithTables().add(this.buildWith(with));
		}

		for (final ViewTable table : annotation.tables()) {
			final ViewTableStructure ts = this.buildTable(table);
			structure.getTables().add(ts);
			if (ts.getJoinType() == ViewJoinType.MAIN || ts.getJoinType() == ViewJoinType.MAIN_UNION
					|| ts.getJoinType() == ViewJoinType.MAIN_UNION_ALL) {
				structure.setDistinct(ts.isDistinct());
			}
		}

		this.resolveMissingJoinConditions(structure.getTables());

		for (final UnionTable union : annotation.unionTables()) {
			structure.getUnionTables().add(this.buildUnionTable(union));
		}

		return structure;
	}

	private void resolveMissingJoinConditions(final List<ViewTableStructure> tables) {
		final List<ViewTableStructure> resolved = new ArrayList<>();

		for (final ViewTableStructure table : tables) {
			if (table.getJoinType() == ViewJoinType.MAIN || table.getJoinType() == ViewJoinType.MAIN_UNION
					|| table.getJoinType() == ViewJoinType.MAIN_UNION_ALL) {
				resolved.add(table);
				continue;
			}

			if (table.getOn() == null || table.getOn().trim().isEmpty()) {
				table.setOn(this.resolveJoinCondition(table, resolved));
			}

			resolved.add(table);
		}
	}

	private String resolveJoinCondition(final ViewTableStructure joinTable, final List<ViewTableStructure> candidates) {
		final List<JoinPath> matches = new ArrayList<>();

		for (final ViewTableStructure candidate : candidates) {
			matches.addAll(this.findJoinPaths(candidate, joinTable));
		}

		if (matches.isEmpty()) {
			throw new IllegalArgumentException("Could not resolve join condition for table '" + joinTable.getEffectiveName()
					+ "'. No foreign key path found to previously declared tables.");
		}

		if (matches.size() > 1) {
			throw new IllegalArgumentException("Could not resolve join condition for table '" + joinTable.getEffectiveName()
					+ "'. Multiple join paths found: " + matches.stream().map(JoinPath::toString).collect(Collectors.joining(", "))
					+ ". Please specify 'on' explicitly.");
		}

		return matches.get(0).toOnClause();
	}

	private List<JoinPath> findJoinPaths(final ViewTableStructure left, final ViewTableStructure right) {
		final List<JoinPath> paths = new ArrayList<>();

		final TableStructure leftStructure = this.dataBaseEntryUtils
				.scanEntry(this.dataBaseEntryUtils.getEntryType((Class<? extends SQLQueryable<?>>) left.getTypeClass()));
		final TableStructure rightStructure = this.dataBaseEntryUtils
				.scanEntry(this.dataBaseEntryUtils.getEntryType((Class<? extends SQLQueryable<?>>) right.getTypeClass()));

		if (leftStructure == null || rightStructure == null) {
			return paths;
		}

		final String leftTableName = leftStructure.getName();
		final String rightTableName = rightStructure.getName();

		final String leftAlias = left.getAlias() == null || left.getAlias().trim().isEmpty() ? left.getEffectiveName() : left.getAlias();
		final String rightAlias = right.getAlias() == null || right.getAlias().trim().isEmpty() ? right.getEffectiveName()
				: right.getAlias();

		// left has FK to right
		for (final ForeignKeyData fk : this.getForeignKeys(leftStructure)) {
			if (rightTableName.equals(fk.getReferencedTable())) {
				paths.add(new JoinPath(leftAlias, fk.getColumns(), rightAlias, fk.getReferencedColumns()));
			}
		}

		// right has FK to left
		for (final ForeignKeyData fk : this.getForeignKeys(rightStructure)) {
			if (leftTableName.equals(fk.getReferencedTable())) {
				paths.add(new JoinPath(leftAlias, fk.getReferencedColumns(), rightAlias, fk.getColumns()));
			}
		}

		return paths;
	}

	private List<ForeignKeyData> getForeignKeys(final TableStructure structure) {
		if (structure.getConstraints() == null) {
			return Collections.emptyList();
		}

		return Arrays.stream(structure.getConstraints())
				.filter(ForeignKeyData.class::isInstance)
				.map(ForeignKeyData.class::cast)
				.collect(Collectors.toList());
	}

	private ViewCommonTableExpressionStructure buildWith(final ViewWithTable with) {
		final ViewCommonTableExpressionStructure ws = new ViewCommonTableExpressionStructure();
		ws.setName(with.name());
		ws.setCondition(with.condition());

		for (final ViewColumn column : with.columns()) {
			ws.getColumns().add(this.buildColumn(column));
		}

		for (final ViewTable table : with.tables()) {
			ws.getTables().add(this.buildTable(table));
		}

		for (final String group : with.groupBy()) {
			ws.getGroupBy().add(group);
		}

		for (final OrderBy order : with.orderBy()) {
			final ViewOrderStructure os = new ViewOrderStructure();
			os.setColumn(order.column());
			os.setType(order.type().name());
			ws.getOrderBy().add(os);
		}

		return ws;
	}

	private ViewTableStructure buildTable(final ViewTable table) {
		final ViewTableStructure ts = new ViewTableStructure();
		ts.setName(this.blankToNull(table.name()));
		ts.setTypeClass(table.typeName());
		ts.setAlias(this.blankToNull(table.asName()));
		ts.setOn(this.blankToNull(table.on()));
		ts.setResolvedTypeName(this.getTypeName(table.typeName()));
		ts.setJoinType(this.mapJoinType(table.join()));
		ts.setDistinct(table.distinct());

		for (final ViewColumn column : table.columns()) {
			ts.getColumns().add(this.buildColumn(column));
		}

		if (ts.getEffectiveName() == null || ts.getEffectiveName().trim().isEmpty()) {
			throw new IllegalArgumentException("ViewTable name cannot be empty/undefined.");
		}

		return ts;
	}

	private UnionTableStructure buildUnionTable(final UnionTable table) {
		final UnionTableStructure ts = new UnionTableStructure();
		ts.setName(this.blankToNull(table.name()));
		ts.setResolvedTypeName(this.getTypeName(table.typeName()));

		for (final ViewColumn column : table.columns()) {
			ts.getColumns().add(this.buildColumn(column));
		}

		if (ts.getEffectiveName() == null || ts.getEffectiveName().trim().isEmpty()) {
			throw new IllegalArgumentException("UnionTable name cannot be empty/undefined.");
		}

		return ts;
	}

	private ViewColumnStructure buildColumn(final ViewColumn column) {
		final ViewColumnStructure cs = new ViewColumnStructure();
		cs.setName(this.blankToNull(column.name()));
		cs.setAlias(this.blankToNull(column.asName()));
		cs.setFunc(this.blankToNull(column.func()));
		return cs;
	}

	private ViewJoinType mapJoinType(final ViewTable.Type type) {
		switch (type) {
		case MAIN:
			return ViewJoinType.MAIN;
		case MAIN_UNION:
			return ViewJoinType.MAIN_UNION;
		case MAIN_UNION_ALL:
			return ViewJoinType.MAIN_UNION_ALL;
		case INNER:
			return ViewJoinType.INNER;
		case LEFT:
			return ViewJoinType.LEFT;
		case RIGHT:
			return ViewJoinType.RIGHT;
		case FULL:
			return ViewJoinType.FULL;
		default:
			throw new IllegalArgumentException(Objects.toString(type));
		}
	}

	private String blankToNull(final String value) {
		return value == null || value.trim().isEmpty() ? null : value;
	}

	@SuppressWarnings("unchecked")
	public String getTypeName(final Class<?> clazz) {
		if (SQLQueryable.class.isAssignableFrom(clazz)) {
			return this.dataBaseEntryUtils.getQueryableName((Class<? extends SQLQueryable<T>>) clazz);
		}

		return null;
	}

	private static final class JoinPath {

		private final String leftAlias;
		private final String[] leftColumns;
		private final String rightAlias;
		private final String[] rightColumns;

		private JoinPath(final String leftAlias, final String[] leftColumns, final String rightAlias, final String[] rightColumns) {
			this.leftAlias = leftAlias;
			this.leftColumns = leftColumns;
			this.rightAlias = rightAlias;
			this.rightColumns = rightColumns;
		}

		private String toOnClause() {
			if (this.leftColumns.length != this.rightColumns.length) {
				throw new IllegalStateException("Mismatched join column count.");
			}

			final List<String> parts = new ArrayList<>();
			for (int i = 0; i < this.leftColumns.length; i++) {
				parts.add(this.leftAlias + "." + this.leftColumns[i] + " = " + this.rightAlias + "." + this.rightColumns[i]);
			}
			return String.join(" AND ", parts);
		}

		@Override
		public String toString() {
			return this.toOnClause();
		}
	}

}