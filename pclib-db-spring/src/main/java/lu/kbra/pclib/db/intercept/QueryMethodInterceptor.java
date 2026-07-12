package lu.kbra.pclib.db.intercept;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.annotation.AnnotatedElementUtils;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.ProxyDataBaseEntryUtils;

import lombok.Getter;

@Getter
public class QueryMethodInterceptor implements MethodInterceptor {

	protected final Map<Method, Function<List<Object>, ?>> queries = new HashMap<>();

	@Override
	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
		if (this.queries.containsKey(method)) {
			try {
				return this.queries.get(method).apply(Arrays.asList(args));
			} catch (final Exception e) {
				throw new DBException(method.toString(), e);
			}
		}
		return proxy.invokeSuper(obj, args);
	}

	public void build(final SQLQueryable<?> delegate) {
		final Class<? extends SQLQueryable<?>> repositoryInterface = delegate.getTargetClass();

		final DataBaseEntryUtils dataBaseEntryUtils = delegate.getDataBaseEntryUtils();
		if (!(dataBaseEntryUtils instanceof final ProxyDataBaseEntryUtils proxyDataBaseEntryUtils)) {
			throw new IllegalArgumentException(
					"Delegate must use ProxyDataBaseEntryUtils to be able to build query functions: " + repositoryInterface.getName());
		}

		for (final Method method : repositoryInterface.getDeclaredMethods()) {
			if (AnnotatedElementUtils.hasAnnotation(method, Query.class)) {
				final Function<List<Object>, ?> f = proxyDataBaseEntryUtils.getQueryFunctionProvider()
						.buildMethodQueryFunction(delegate, method);
				this.queries.put(method, f);
			}
		}
		for (final Class<?> topiface : repositoryInterface.getInterfaces()) {
			for (final Method method : topiface.getDeclaredMethods()) {
				if (AnnotatedElementUtils.hasAnnotation(method, Query.class)) {
					final Function<List<Object>, ?> f = proxyDataBaseEntryUtils.getQueryFunctionProvider()
							.buildMethodQueryFunction(delegate, method);
					this.queries.put(method, f);
				}
			}
		}
	}

}
