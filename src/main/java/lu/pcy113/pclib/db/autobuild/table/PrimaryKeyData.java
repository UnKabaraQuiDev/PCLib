package lu.pcy113.pclib.db.autobuild.table;

public class PrimaryKeyData extends ConstraintData {

	private final TableStructure table;
	private final String[] columns;

	public PrimaryKeyData(TableStructure table, String[] columns) {
		this.table = table;
		this.columns = columns;
	}

	public String getFinalName() {
		return "pk_" + table.getFinalName();
	}

	public String[] getColumns() {
		return columns;
	}

	@Override
	public String build() {
		return "CONSTRAINT " + getFinalName() + " PRIMARY KEY (" + String.join(", ", "`" + columns + "`") + ")";
	}
}
