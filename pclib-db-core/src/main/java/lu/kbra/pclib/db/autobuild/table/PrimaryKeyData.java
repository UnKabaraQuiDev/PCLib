package lu.kbra.pclib.db.autobuild.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PrimaryKeyData extends ConstraintData {

	private final String name;
	private final String[] columns;

	public PrimaryKeyData(final TableStructure table, final String[] columns) {
		String name = "pk_" + table.getName() + "_" + String.join("_", columns);
		if (name.length() > ConstraintData.NAME_MAX_LENGTH) {
			this.name = "pk_" + table.getName() + "_" + columns.length;
		} else {
			this.name = name;
		}
		this.columns = columns;
	}
}
