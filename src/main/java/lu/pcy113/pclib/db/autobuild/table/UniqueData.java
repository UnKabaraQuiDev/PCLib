package lu.pcy113.pclib.db.autobuild.table;

public class UniqueData extends ConstraintData {

	private TableStructure table;

	private String explicitName = null;
	private String[] columns;

	public UniqueData(TableStructure table, String[] columns) {
		this.table = table;
		this.columns = columns;
	}

	public UniqueData(TableStructure table, String explicitName, String[] columns) {
		this.table = table;
		this.explicitName = explicitName;
		this.columns = columns;
	}

	public boolean hasExplicitName() {
		return explicitName != null && !explicitName.isEmpty();
	}

	public String getExplicitName() {
		return explicitName;
	}

	public String getFinalName() {
		return hasExplicitName() ? explicitName : "uq_" + table.getFinalName() + "_" + String.join("_", columns);
	}

	@Override
	public String build() {
		return "CONSTRAINT " + getFinalName() + " UNIQUE (" + String.join(", ", "`"+columns+"`") + ")";
	}

}
