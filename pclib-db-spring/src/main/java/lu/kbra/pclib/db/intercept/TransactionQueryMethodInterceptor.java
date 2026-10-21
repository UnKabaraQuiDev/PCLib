package lu.kbra.pclib.db.intercept;

import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import lu.kbra.pclib.db.connector.impl.AbstractConnection;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransactionQueryMethodInterceptor implements MethodInterceptor {

	protected final QueryMethodInterceptor parent;
	protected final Supplier<AbstractConnection> useMethod;

	@Override
	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
		if ("use".equals(method.getName()) && method.getReturnType() == AbstractConnection.class && method.getParameterCount() == 0) {
			return this.useMethod.get();
		}
		return parent.intercept(obj, method, args, proxy);
	}

}
