package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public interface SQLQueryable<T extends DataBaseEntry> extends SQLNamed {

	NextTask<Void, ?, Integer> count();

	<B> NextTask<Void, ?, B> query(SQLQuery<T, B> query);

	Class<? extends SQLQueryable<T>> getTargetClass();

	DataBaseEntryUtils getDbEntryUtils();

}
