package lu.kbra.pclib.db.impl;

public interface DataBaseEntry extends Cloneable {

	public interface ReadOnlyDataBaseEntry extends DataBaseEntry {

	}

	DataBaseEntry clone();

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

}
