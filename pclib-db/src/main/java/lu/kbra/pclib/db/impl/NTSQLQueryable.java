package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.async.NextTask;

@Deprecated
public interface NTSQLQueryable<T extends DataBaseEntry> extends SQLQueryable<T> {

	@Deprecated
	NextTask<Void, ?, Integer> ntCount();

	@Deprecated
	<B> NextTask<Void, ?, B> ntQuery(SQLQuery<T, B> query);

	@Deprecated
	@Override
	Class<? extends NTSQLQueryable<T>> getTargetClass();

}
