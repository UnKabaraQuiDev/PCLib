package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.async.NextTask;

public interface NTSQLQueryable<T extends DataBaseEntry> extends SQLQueryable<T> {

	NextTask<Void, ?, Integer> ntCount();

	<B> NextTask<Void, ?, B> ntQuery(SQLQuery<T, B> query);
	
	@Override
	Class<? extends NTSQLQueryable<T>> getTargetClass();

}
