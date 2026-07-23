package lu.kbra.pclib.db.domain.table;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ForeignKeyData implements ConstraintData {

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

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();

		map.put("name", name);
		map.put("columns", columns);
		map.put("referencedTable", referencedTable);
		map.put("referencedColumns", referencedColumns);
		map.put("onDeleteAction", onDeleteAction);
		map.put("onUpdateAction", onUpdateAction);

		return map;
	}

}
