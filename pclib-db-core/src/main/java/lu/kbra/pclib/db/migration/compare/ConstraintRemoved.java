package lu.kbra.pclib.db.migration.compare;

import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.TableStructure;

import lombok.Data;

@Data
public class ConstraintRemoved implements SchemaChange {

	private final TableStructure table;
	private final ConstraintData oldConstraint;

}
