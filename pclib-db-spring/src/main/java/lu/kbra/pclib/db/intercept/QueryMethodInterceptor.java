package lu.kbra.pclib.db.intercept;

import org.springframework.cglib.proxy.MethodInterceptor;

import lu.kbra.pclib.db.impl.SQLQueryable;

public interface QueryMethodInterceptor extends MethodInterceptor {

	void build(final SQLQueryable<?> delegate);

}
