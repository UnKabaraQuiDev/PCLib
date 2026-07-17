package lu.kbra.pclib.db.domain.table;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class CheckData implements ConstraintData {

	private final String name;
	private final String expression;

	public CheckData(final TableStructure table, final String expression) {
		this.name = "ck_" + table.getName() + "_" + Integer.toHexString(expression.hashCode());
		this.expression = expression;
	}

	@Override
	public Map<String, Object> toMap() {
		final Map<String, Object> map = new HashMap<>();

		map.put("name", name);
		map.put("expression", expression);

		return map;
	}

}
