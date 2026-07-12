package lu.kbra.pclib.db.domain.table;

import java.util.Arrays;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.domain.column.ColumnData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UniqueData extends ConstraintData {

	private final String name;
	private final ColumnData[] columns;

	public UniqueData(final TableStructure table, final ColumnData[] columns) {
		final String name = "uq_" + table.getName() + "_"
				+ Arrays.stream(columns).map(ColumnData::getLocalName).collect(Collectors.joining("_"));
		if (name.length() > ConstraintData.NAME_MAX_LENGTH) {
			this.name = "uq_" + table.getName() + "_" + columns[0] + "_" + columns.length;
		} else {
			this.name = name;
		}
		this.columns = columns;
	}

}
