package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public interface SQLQueryable<T extends DataBaseEntry> extends SQLNamed {

	int count() throws DBException;

	DataBaseEntryUtils getDataBaseEntryUtils();

	Class<? extends SQLQueryable<T>> getTargetClass();

	<B> B query(SQLQuery<T, B> query) throws DBException;

	String getQualifiedName();

}
