package lu.kbra.pclib.db.autobuild.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CheckData extends ConstraintData {

	private final String name;
	private final String expression;

	public CheckData(final TableStructure table, final String expression) {
		this.name = "ck_" + table.getName() + "_" + Integer.toHexString(expression.hashCode());
		this.expression = expression;
	}

}
