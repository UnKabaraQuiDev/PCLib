package lu.kbra.pclib.db.intercept;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.annotation.AnnotatedElementUtils;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.domain.query.QueryStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.QueryMethodException;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.ProxyDatabaseEntryUtils;

import lombok.Getter;

@Getter
public class SharedQueryMethodInterceptor implements QueryMethodInterceptor {

	public static final String QUERY_ID = "QA_QUERY_ID";
	protected static final AtomicInteger COUNTER = new AtomicInteger(0);

	protected final Map<Method, Boolean> skip = new IdentityHashMap<>();
	protected final Map<Integer, Map<Method, QueryStructure>> queryStructures = new HashMap<>();
	protected final Map<Integer, Map<Method, Function<Object[], ?>>> queries = new HashMap<>();

	@Override
	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
		{
			Boolean shouldSkip = this.skip.get(method);
			if (shouldSkip == null) {
				this.skip.put(method, shouldSkip = !AnnotatedElementUtils.hasAnnotation(method, Query.class));
			}
			if (shouldSkip != null && shouldSkip) {
				return proxy.invokeSuper(obj, args);
			}
		}

		final Integer id = this.resolveDelegate(obj);

		final Map<Method, Function<Object[], ?>> delegateQueries = this.queries.get(id);

		if (delegateQueries != null && delegateQueries.containsKey(method)) {
			try {
				return delegateQueries.get(method).apply(args);
			} catch (final DBException e) {
				throw e;
			} catch (final Exception e) {
				final QueryStructure structure = this.queryStructures.get(id).get(method);

				throw new QueryMethodException(method.toString(), null, structure, e);
			}
		}

		return proxy.invokeSuper(obj, args);
	}

	protected Integer resolveDelegate(final Object obj) {
		if (obj instanceof final SQLQueryable<?> queryable) {
			return (Integer) queryable.getStructure()
					.getHints()
					.computeIfAbsent(SharedQueryMethodInterceptor.QUERY_ID, k -> SharedQueryMethodInterceptor.COUNTER.incrementAndGet());
		}

		throw new IllegalStateException("Cannot resolve SQLQueryable delegate for proxy: " + obj.getClass().getName());
	}

	@Override
	public synchronized void build(final SQLQueryable<?> delegate) {
		final Integer id = this.resolveDelegate(delegate);

		final Class<? extends SQLQueryable<?>> repositoryInterface = delegate.getTargetClass();

		final DatabaseEntryUtils databaseEntryUtils = delegate.getDatabaseEntryUtils();

		if (!(databaseEntryUtils instanceof final ProxyDatabaseEntryUtils proxyDatabaseEntryUtils)) {
			throw new IllegalArgumentException(
					"Delegate must use ProxyDatabaseEntryUtils to build query functions: " + repositoryInterface.getName());
		}

		final Map<Method, QueryStructure> structures = new HashMap<>();
		final Map<Method, Function<Object[], ?>> functions = new HashMap<>();

		for (final Method method : repositoryInterface.getDeclaredMethods()) {
			if (AnnotatedElementUtils.hasAnnotation(method, Query.class)) {
				final QueryStructure structure = proxyDatabaseEntryUtils.getQueryFunctionProvider()
						.buildMethodQueryStructure(delegate, new HashMap<>(), method);

				structures.put(method, structure);

				final Function<Object[], ?> function = proxyDatabaseEntryUtils.getQueryFunctionProvider()
						.buildMethodQueryFunction(delegate, method, structure);

				functions.put(method, function);
			}
		}

		for (final Class<?> topInterface : repositoryInterface.getInterfaces()) {
			for (final Method method : topInterface.getDeclaredMethods()) {
				if (AnnotatedElementUtils.hasAnnotation(method, Query.class)) {
					final Function<Object[], ?> function = proxyDatabaseEntryUtils.getQueryFunctionProvider()
							.buildMethodQueryFunction(delegate, method);

					functions.put(method, function);
				}
			}
		}

		this.queryStructures.put(id, structures);
		this.queries.put(id, functions);
	}

}
