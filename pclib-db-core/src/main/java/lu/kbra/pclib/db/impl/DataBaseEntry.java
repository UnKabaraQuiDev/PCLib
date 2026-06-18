package lu.kbra.pclib.db.impl;

import java.io.Serializable;

public interface DataBaseEntry extends Cloneable, Serializable {

	public interface ReadOnlyDataBaseEntry extends DataBaseEntry {

	}

}
