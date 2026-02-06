package lu.kbra.pclib.db.autobuild.table;

import java.util.Arrays;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;

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

	public String[] getColumns() {
		return columns;
	}

	@Override
	public String build() {
		return "CONSTRAINT " + getEscapedName() + " UNIQUE (" + Arrays.stream(columns).map(PCUtils::sqlEscapeIdentifier).collect(Collectors.joining(", ")) + ")";
	}

}
