package lu.pcy113.pclib.db.autobuild.table;

public class UniqueData extends ConstraintData {

	private final TableStructure table;

	private final String name;
	private final String[] columns;

	public UniqueData(TableStructure table, String[] columns) {
		this.table = table;
		this.name = "uq_" + table.getName() + "_" + String.join("_", columns);
		this.columns = columns;
	}

	public UniqueData(TableStructure table, String name, String[] columns) {
		this.table = table;
		this.name = name;
		this.columns = columns;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String build() {
		return "CONSTRAINT " + getName() + " UNIQUE (" + String.join(", ", "`" + columns + "`") + ")";
	}

}
