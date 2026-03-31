package lu.kbra.pclib.db.autobuild.table;

import java.util.Arrays;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class ForeignKeyData extends ConstraintData {

	public enum OnAction {

		NO_ACTION(""),
		RESTRICT("RESTRICT"),
		CASCADE("CASCADE"),
		SET_NULL("SET NULL"),
		SET_DEFAULT("SET DEFAULT");

		private final String action;

		OnAction(final String action) {
			this.action = action;
		}

		public String getAction() {
			return this.action;
		}

		@Override
		public String toString() {
			return this.action;
		}

	}

	private final TableStructure table;

	private String name;

	private final String[] columns;
	private final String referencedTable;
	private final String[] referencedColumns;

	private OnAction onDeleteAction;
	private OnAction onUpdateAction;

	public ForeignKeyData(
			final TableStructure table,
			final String[] columns,
			final String referencedTable,
			final String[] referencedColumns) {
		this(table, "fk_" + table.getName() + "_" + String.join("_", columns), columns, referencedTable, referencedColumns);
		if (this.name.length() > ConstraintData.NAME_MAX_LENGTH) {
			this.name = "fk_" + table.getName() + "_" + columns[0] + "_" + columns.length;
		}
	}

	public ForeignKeyData(
			final TableStructure table,
			final String explicitName,
			final String[] columns,
			final String referencedTable,
			final String[] referencedColumns) {
		this.table = table;
		this.name = explicitName;
		this.columns = columns;
		this.referencedTable = referencedTable;
		this.referencedColumns = referencedColumns;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public TableStructure getTable() {
		return this.table;
	}

	public String[] getColumns() {
		return this.columns;
	}

	public String getReferencedTable() {
		return this.referencedTable;
	}

	public String[] getReferencedColumns() {
		return this.referencedColumns;
	}

	public OnAction getOnDeleteAction() {
		return this.onDeleteAction;
	}

	public OnAction getOnUpdateAction() {
		return this.onUpdateAction;
	}

	@Override
	public String build(final DataBaseConnector conn) {
		final StringBuilder sb = new StringBuilder();
		//@formatter:off
		sb.append("CONSTRAINT ")
		.append(this.getEscapedName())
		.append(" FOREIGN KEY (")
		.append(Arrays.stream(this.columns).map(PCUtils::sqlEscapeIdentifier).collect(Collectors.joining(", ")))
		.append(")")
		.append(" REFERENCES ")
		.append(PCUtils.sqlEscapeIdentifier(this.referencedTable))
		.append(" (")
		.append(Arrays.stream(this.referencedColumns).map(PCUtils::sqlEscapeIdentifier).collect(Collectors.joining(", ")))
		.append(")");
		//@formatter:on

		if (this.onDeleteAction != null) {
			sb.append(" ON DELETE ").append(this.onDeleteAction);
		}

		if (this.onUpdateAction != null) {
			sb.append(" ON UPDATE ").append(this.onUpdateAction);
		}

		return sb.toString();
	}

}
