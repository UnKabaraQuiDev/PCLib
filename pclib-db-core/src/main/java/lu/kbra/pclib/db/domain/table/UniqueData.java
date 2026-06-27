package lu.kbra.pclib.db.domain.table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UniqueData extends ConstraintData {

	private final String name;
	private final String[] columns;

	public UniqueData(final TableStructure table, final String[] columns) {
		final String name = "uq_" + table.getName() + "_" + String.join("_", columns);
		if (name.length() > ConstraintData.NAME_MAX_LENGTH) {
			this.name = "uq_" + table.getName() + "_" + columns[0] + "_" + columns.length;
		} else {
			this.name = name;
		}
		this.columns = columns;
	}

}
