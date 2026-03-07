package lu.kbra.pclib.db.autobuild.view;

import java.util.Objects;

import lu.kbra.pclib.db.annotations.view.DB_View;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.annotations.view.UnionTable;
import lu.kbra.pclib.db.annotations.view.ViewColumn;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.annotations.view.ViewWithTable;
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

		for (final UnionTable union : annotation.unionTables()) {
			structure.getUnionTables().add(this.buildUnionTable(union));
		}

		return structure;
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

}