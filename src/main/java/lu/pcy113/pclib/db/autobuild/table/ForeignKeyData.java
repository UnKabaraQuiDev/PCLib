package lu.pcy113.pclib.db.autobuild.table;

public class ForeignKeyData extends ConstraintData {

	public static enum OnAction {

		NO_ACTION(""),
		RESTRICT("RESTRICT"),
		CASCADE("CASCADE"),
		SET_NULL("SET NULL"),
		SET_DEFAULT("SET DEFAULT");

		private final String action;

		private OnAction(String action) {
			this.action = action;
		}

		public String getAction() {
			return action;
		}

		@Override
		public String toString() {
			return action;
		}

	}

	private final TableStructure table;

	private final String name;

	private final String[] columns;
	private final String referencedTable;
	private final String[] referencedColumns;

	private OnAction onDeleteAction, onUpdateAction;

	public ForeignKeyData(TableStructure table, String[] columns, String referencedTable, String[] referencedColumns) {
		this(table, "fk_" + table.getName() + "_" + String.join("_", columns), columns, referencedTable, referencedColumns);
	}

	public ForeignKeyData(TableStructure table, String explicitName, String[] columns, String referencedTable, String[] referencedColumns) {
		this.table = table;
		this.name = explicitName;
		this.columns = columns;
		this.referencedTable = referencedTable;
		this.referencedColumns = referencedColumns;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append("CONSTRAINT ").append(getName()).append(" FOREIGN KEY (").append(String.join(", ", "`" + columns + "`")).append(")").append(" REFERENCES ").append("`" + referencedTable + "`").append(" (")
				.append(String.join(", ", "`" + referencedColumns + "`")).append(")");

		if (onDeleteAction != null) {
			sb.append(" ON DELETE ").append(onDeleteAction);
		}

		if (onUpdateAction != null) {
			sb.append(" ON UPDATE ").append(onUpdateAction);
		}

		return sb.toString();
	}

}
