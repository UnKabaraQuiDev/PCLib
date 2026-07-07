package lu.kbra.pclib.db.domain.table;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.HintsOwner;
import lu.kbra.pclib.db.table.AbstractDBTable;

@Data
@AllArgsConstructor
public class TableStructure implements HintsOwner {

	private final String name;
	private final Class<? extends AbstractDBTable<? extends DataBaseEntry>> tableClass;
	private final Class<? extends DataBaseEntry> entryClass;
	private final Map<String, Object> hints;
	private ColumnData[] columns;
	private ConstraintData[] constraints;

	public <T extends DataBaseEntry> TableStructure(
			final String name,
			final Class<? extends AbstractDBTable<T>> tableClass,
			final Class<T> entryClass,
			final Map<String, Object> tableHints) {
		this.name = name;
		this.tableClass = tableClass;
		this.entryClass = entryClass;
		this.hints = tableHints;
	}

	public TableStructure(
			final String name,
			final Map<String, Object> tableHints,
			final ColumnData[] columns,
			final ConstraintData[] constraints) {
		this.name = name;
		this.tableClass = null;
		this.entryClass = null;
		this.hints = tableHints;
		this.columns = columns;
		this.constraints = constraints;
	}

}
