package lu.kbra.pclib.db.migration.compare;

import lu.kbra.pclib.db.domain.table.TableStructure;

import lombok.Data;

@Data
public class TableRemoved implements SchemaChange {

	private final TableStructure table;

}
