package lu.pcy113.pclib.db.autobuild.table;

public class CheckData extends ConstraintData {

	private final TableStructure table;

	private final String name;
	private final String expression;

	public CheckData(TableStructure table, String expression) {
		this.table = table;
		this.name = "ck_" + table.getName() + "_" + Integer.toHexString(expression.hashCode());
		this.expression = expression;
	}

	public CheckData(TableStructure table, String name, String expression) {
		this.table = table;
		this.name = name;
		this.expression = expression;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String build() {
		return "CONSTRAINT " + getEscapedName() + " CHECK (" + expression + ")";
	}

}
