package lu.kbra.pclib.db.impl;

import org.springframework.cglib.proxy.MethodInterceptor;

public interface DeferredSQLQueryable<T extends DatabaseEntry> extends SQLQueryable<T> {

	MethodInterceptor getInterceptor();

}
