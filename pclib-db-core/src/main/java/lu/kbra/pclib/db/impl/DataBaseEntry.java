package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public interface DataBaseEntry extends Cloneable {

	public interface ReadOnlyDataBaseEntry extends DataBaseEntry {

	}

	static <T extends DataBaseEntry> T safeClone(final ThrowingSupplier<Object, CloneNotSupportedException> obj) {
		try {
			return (T) obj.get();
		} catch (final CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

	DataBaseEntry clone();

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

}
