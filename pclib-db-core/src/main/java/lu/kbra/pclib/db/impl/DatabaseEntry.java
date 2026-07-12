package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public interface DatabaseEntry extends Cloneable {

	public interface ReadOnlyDatabaseEntry extends DatabaseEntry {

	}

	static <T extends DatabaseEntry> T safeClone(final ThrowingSupplier<Object, CloneNotSupportedException> obj) {
		try {
			return (T) obj.get();
		} catch (final CloneNotSupportedException e) {
			throw new InternalError(e);
		}
	}

	DatabaseEntry clone();

	@Override
	boolean equals(Object obj);

	@Override
	int hashCode();

}
