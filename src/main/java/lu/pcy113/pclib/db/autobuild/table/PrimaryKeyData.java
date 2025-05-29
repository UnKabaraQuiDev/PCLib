package lu.pcy113.pclib.db.autobuild.table;

import java.util.Arrays;
import java.util.stream.Collectors;

import lu.pcy113.pclib.PCUtils;

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
		return "CONSTRAINT " + getEscapedName() + " PRIMARY KEY (" + Arrays.stream(columns).map(PCUtils::sqlEscapeIdentifier).collect(Collectors.joining(", ")) + ")";
	}
}
