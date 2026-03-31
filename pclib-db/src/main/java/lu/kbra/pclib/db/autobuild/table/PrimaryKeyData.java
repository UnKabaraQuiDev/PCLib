package lu.kbra.pclib.db.autobuild.table;

import java.util.Arrays;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public class PrimaryKeyData extends ConstraintData {

	private final TableStructure table;

	private String name;
	private final String[] columns;

	public PrimaryKeyData(final TableStructure table, final String[] columns) {
		this.table = table;
		this.name = "pk_" + table.getName() + "_" + String.join("_", columns);
		if (this.name.length() > ConstraintData.NAME_MAX_LENGTH) {
			this.name = "pk_" + table.getName() + "_" + columns.length;
		}
		this.columns = columns;
	}

	public PrimaryKeyData(final TableStructure table, final String name, final String[] columns) {
		this.table = table;
		this.name = name;
		this.columns = columns;
	}

	@Override
	public String getName() {
		return this.name;
	}

	public String[] getColumns() {
		return this.columns;
	}

	@Override
	public String build(final DataBaseConnector conn) {
		return "CONSTRAINT " + this.getEscapedName() + " PRIMARY KEY ("
				+ Arrays.stream(this.columns).map(PCUtils::sqlEscapeIdentifier).collect(Collectors.joining(", ")) + ")";
	}
}
