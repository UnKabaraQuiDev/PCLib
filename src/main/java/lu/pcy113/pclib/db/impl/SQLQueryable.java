package lu.pcy113.pclib.db.impl;

import lu.pcy113.pclib.async.NextTask;

public interface SQLQueryable<T extends DataBaseEntry> extends SQLNamed {

	<B> NextTask<Void, B> query(SQLQuery<T, B> query);

}
