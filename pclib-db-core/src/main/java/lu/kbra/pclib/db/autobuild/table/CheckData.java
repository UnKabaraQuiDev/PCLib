package lu.kbra.pclib.db.autobuild.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CheckData extends ConstraintData {

	private final TableStructure table;

	private final String name;
	private final String expression;

	public CheckData(final TableStructure table, final String expression) {
		this.table = table;
		this.name = "ck_" + table.getName() + "_" + Integer.toHexString(expression.hashCode());
		this.expression = expression;
	}

	@Override
	public String build(final DataBaseConnector conn) {
		return "CONSTRAINT " + this.getEscapedName() + " CHECK (" + this.expression + ")";
	}

}
