package lu.kbra.pclib.db.autobuild.table;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class CheckData extends ConstraintData {

	private final TableStructure table;

	private final String name;
	private final String expression;

	public CheckData(final TableStructure table, final String expression) {
		this.table = table;
		this.name = "ck_" + table.getName() + "_" + Integer.toHexString(expression.hashCode());
		this.expression = expression;
	}

	public CheckData(final TableStructure table, final String name, final String expression) {
		this.table = table;
		this.name = name;
		this.expression = expression;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String build(final DataBaseConnector conn) {
		return "CONSTRAINT " + this.getEscapedName() + " CHECK (" + this.expression + ")";
	}

}
