package lu.pcy113.pclib.db.autobuild.table;

public class PrimaryKeyData extends ConstraintData {

	private final TableStructure table;

	private final String name;
	private final String[] columns;

	public PrimaryKeyData(TableStructure table, String[] columns) {
		this.table = table;
		this.name = "pk_" + table.getName() + "_" + String.join("_", columns);
		this.columns = columns;
	}

	public PrimaryKeyData(TableStructure table, String name, String[] columns) {
		this.table = table;
		this.name = name;
		this.columns = columns;
	}

	@Override
	public String getName() {
		return name;
	}

	public String[] getColumns() {
		return columns;
	}

	@Override
	public String build() {
		return "CONSTRAINT " + getName() + " PRIMARY KEY (" + String.join(", ", "`" + columns + "`") + ")";
	}
}
