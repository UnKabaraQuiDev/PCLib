package lu.kbra.pclib.db.domain.table;

import lu.kbra.pclib.impl.MapConvertible;

public interface ConstraintData extends MapConvertible, Cloneable {

	int NAME_MAX_LENGTH = 64;

	String getName();

}
