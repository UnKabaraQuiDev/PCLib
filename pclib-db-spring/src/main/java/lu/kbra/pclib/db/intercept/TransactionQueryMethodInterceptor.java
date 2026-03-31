package lu.kbra.pclib.db.intercept;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.springframework.cglib.proxy.MethodProxy;

import lu.kbra.pclib.db.connector.AbstractConnection;

public class TransactionQueryMethodInterceptor extends QueryMethodInterceptor {

	protected final Supplier<AbstractConnection> connection;

	public TransactionQueryMethodInterceptor(final Supplier<AbstractConnection> connection) {
		this.connection = connection;
	}

	@Override
	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
		if ("use".equals(method.getName()) && method.getReturnType() == AbstractConnection.class && method.getParameterCount() == 0) {
			return this.connection.get();
		}
		return super.intercept(obj, method, args, proxy);
	}

}
