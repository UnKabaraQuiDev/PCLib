package lu.kbra.pclib.db.domain.table;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lu.kbra.pclib.db.domain.Qualified;
import lu.kbra.pclib.db.impl.SQLQueryable;

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

	private final @Qualified String[] columns;
	private final @Qualified String[] referencedColumns;

	private final Class<? extends SQLQueryable<?>> resolvedClass;
	private final StructureName resolvedName;

	private final OnAction onDeleteAction;
	private final OnAction onUpdateAction;

	public ForeignKeyData(
			final String name,
			@Qualified final String[] columns,
			@Qualified final String[] referencedColumns,
			final Class<? extends SQLQueryable<?>> resolvedClass,
			final StructureName resolvedName) {
		this.name = name;
		this.columns = columns;
		this.referencedColumns = referencedColumns;
		this.resolvedClass = resolvedClass;
		this.resolvedName = resolvedName;
		this.onDeleteAction = OnAction.RESTRICT;
		this.onUpdateAction = OnAction.RESTRICT;
	}

	public ForeignKeyData(
			final SQLQueryableStructure table,
			final @Qualified String[] columns,
			final @Qualified String[] referencedColumns,
			final Class<? extends SQLQueryable<?>> resolvedClass,
			final StructureName resolvedName) {
		final String name = "fk_" + table.getName() + "_" + String.join("_", columns);
		if (name.length() > ConstraintData.NAME_MAX_LENGTH) {
			this.name = "fk_" + table.getName() + "_" + columns[0] + "_" + columns.length;
		} else {
			this.name = name;
		}
		this.columns = columns;
		this.resolvedClass = resolvedClass;
		this.resolvedName = resolvedName;
		this.referencedColumns = referencedColumns;
		this.onDeleteAction = OnAction.RESTRICT;
		this.onUpdateAction = OnAction.RESTRICT;
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();

		map.put("name", this.name);
		map.put("columns", this.columns);
		map.put("referencedColumns", this.referencedColumns);
		map.put("resolvedClass", this.resolvedClass);
		map.put("resolvedName", this.resolvedName);
		map.put("onDeleteAction", this.onDeleteAction);
		map.put("onUpdateAction", this.onUpdateAction);

		return map;
	}

}
