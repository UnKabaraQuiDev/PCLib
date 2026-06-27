package lu.kbra.pclib.db.autobuild.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ForeignKeyData extends ConstraintData {

	public enum OnAction {

		NO_ACTION(""),
		RESTRICT("RESTRICT"),
		CASCADE("CASCADE"),
		SET_NULL("SET NULL"),
		SET_DEFAULT("SET DEFAULT");

		@Getter
		private final String action;

		OnAction(final String action) {
			this.action = action;
		}

		@Override
		public String toString() {
			return this.action;
		}

	}

	private final String name;

	private final String[] columns;
	private final String referencedTable;
	private final String[] referencedColumns;

	private final OnAction onDeleteAction;
	private final OnAction onUpdateAction;

	public ForeignKeyData(final String name, final String[] columns, final String referencedTable, final String[] referencedColumns) {
		this.name = name;
		this.columns = columns;
		this.referencedTable = referencedTable;
		this.referencedColumns = referencedColumns;
		this.onDeleteAction = OnAction.RESTRICT;
		this.onUpdateAction = OnAction.RESTRICT;
	}

	public ForeignKeyData(
			final TableStructure table,
			final String[] columns,
			final String referencedTable,
			final String[] referencedColumns) {
		final String name = "fk_" + table.getName() + "_" + String.join("_", columns);
		if (name.length() > ConstraintData.NAME_MAX_LENGTH) {
			this.name = "fk_" + table.getName() + "_" + columns[0] + "_" + columns.length;
		} else {
			this.name = name;
		}
		this.columns = columns;
		this.referencedTable = referencedTable;
		this.referencedColumns = referencedColumns;
		this.onDeleteAction = OnAction.RESTRICT;
		this.onUpdateAction = OnAction.RESTRICT;
	}

}
