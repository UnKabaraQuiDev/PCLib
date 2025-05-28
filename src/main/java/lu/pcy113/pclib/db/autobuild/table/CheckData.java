package lu.pcy113.pclib.db.autobuild.table;

public class CheckData extends ConstraintData {

	private final TableStructure table;
	private final String expression;
	private final String explicitName;

	public CheckData(TableStructure table, String expression) {
		this(table, null, expression);
	}

	public CheckData(TableStructure table, String explicitName, String expression) {
		this.table = table;
		this.explicitName = explicitName;
		this.expression = expression;
	}

	public String getFinalName() {
		if (explicitName != null && !explicitName.isEmpty())
			return explicitName;
		return "ck_" + table.getFinalName() + "_" + Integer.toHexString(expression.hashCode());
	}

	@Override
	public String build() {
		return "CONSTRAINT " + getFinalName() + " CHECK (" + expression + ")";
	}
	
}
