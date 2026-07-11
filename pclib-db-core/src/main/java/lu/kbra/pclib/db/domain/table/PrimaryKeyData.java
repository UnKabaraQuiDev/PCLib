package lu.kbra.pclib.db.domain.table;

import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lu.kbra.pclib.db.domain.column.ColumnData;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PrimaryKeyData extends ConstraintData {

	private final String name;
	private final ColumnData[] columns;

	public PrimaryKeyData(final TableStructure table, final ColumnData[] columns) {
		final String name = "pk_" + table.getName() + "_"
				+ Arrays.stream(columns).map(ColumnData::getLocalName).collect(Collectors.joining("_"));
		if (name.length() > ConstraintData.NAME_MAX_LENGTH) {
			this.name = "pk_" + table.getName() + "_" + columns.length;
		} else {
			this.name = name;
		}
		this.columns = columns;
	}

}
