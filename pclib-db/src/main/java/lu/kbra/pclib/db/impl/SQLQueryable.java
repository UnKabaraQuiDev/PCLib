package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.table.DBException;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public interface SQLQueryable<T extends DataBaseEntry> extends SQLNamed {

	int count() throws DBException;

	<B> B query(SQLQuery<T, B> query) throws DBException;

	Class<? extends SQLQueryable<T>> getTargetClass();

	DataBaseEntryUtils getDbEntryUtils();

}
