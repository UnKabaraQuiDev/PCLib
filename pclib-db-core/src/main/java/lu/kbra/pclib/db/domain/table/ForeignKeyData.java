package lu.kbra.pclib.db.domain.table;

import java.util.HashMap;
import java.util.Map;

import lu.kbra.pclib.db.domain.Qualified;
import lu.kbra.pclib.db.impl.SQLQueryable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ForeignKeyData implements ConstraintData {

	public enum OnAction {

		NO_ACTION(null),
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
