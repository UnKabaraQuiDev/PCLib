package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;

public interface DeferredSQLQueryable<T extends DatabaseEntry> extends SQLQueryable<T> {

	QueryMethodInterceptor getInterceptor();

}
