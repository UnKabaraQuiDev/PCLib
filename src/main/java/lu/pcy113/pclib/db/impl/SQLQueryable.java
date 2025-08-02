package lu.pcy113.pclib.db.impl;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.utils.DataBaseEntryUtils;

public interface SQLQueryable<T extends DataBaseEntry> extends SQLNamed {

	<B> NextTask<Void, B> query(SQLQuery<T, B> query);

	Class<? extends SQLQueryable<T>> getTargetClass();
	
	DataBaseEntryUtils getDbEntryUtils();

}
