package lu.kbra.pclib.db.domain.table;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.impl.MapConvertible;

public interface TreeStringConvertible extends MapConvertible {

	default String toTreeString() {
		return PCUtils.printTree(toMap());
	}

}
