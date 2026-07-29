package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.Type;

import lu.kbra.pclib.db.exception.DataAccessException;
import lu.kbra.pclib.db.impl.DatabaseEntry;

public interface StorageBinding {

	Object get(DatabaseEntry entry) throws DataAccessException;

	void set(DatabaseEntry entry, Object val) throws DataAccessException;

	Type getGenericType();

}
