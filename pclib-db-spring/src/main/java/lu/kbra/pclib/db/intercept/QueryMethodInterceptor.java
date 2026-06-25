package lu.kbra.pclib.db.intercept;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.core.annotation.AnnotatedElementUtils;

import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.ProxyDataBaseEntryUtils;

public class QueryMethodInterceptor implements MethodInterceptor {

	protected final Map<Method, Function<List<Object>, ?>> queries = new HashMap<>();

	@Override
	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
		if (this.queries.containsKey(method)) {
			return this.queries.get(method).apply(Arrays.asList(args));
		}
		return proxy.invokeSuper(obj, args);
	}

	private Object invokeImplMethod(final Object proxy, final Method method, final Object[] args) throws Throwable {
		try {
			final Class<?> declaringClass = method.getDeclaringClass();

			// ensure proxy implements the interface
			if (!declaringClass.isAssignableFrom(proxy.getClass())) {
				throw new IllegalArgumentException("Proxy does not implement interface: " + declaringClass.getName());
			}

			final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(declaringClass, MethodHandles.lookup());

			final MethodHandle handle = lookup.findSpecial(declaringClass,
					method.getName(),
					MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
					declaringClass);

			return handle.bindTo(proxy).invokeWithArguments(args);
		} catch (final IllegalAccessException e) {
			throw new RuntimeException("Tried calling " + method + " on " + proxy, e);
		}
	}

	private boolean isDeclaredInSuperclass(final Method method, final Class<?> superClass) {
		try {
			superClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
			return true;
		} catch (final NoSuchMethodException e) {
			return false;
		}
	}

	public <X extends DataBaseEntry, T extends SQLQueryable<X>> void
			registerDelegate(final T delegate, final Class<T> repositoryInterface) {
		if (!(delegate.getDbEntryUtils() instanceof ProxyDataBaseEntryUtils)) {
			throw new IllegalArgumentException(
					"Delegate must use ProxyDataBaseEntryUtils to be able to build query functions: " + repositoryInterface.getName());
		}

		final ProxyDataBaseEntryUtils dbEntryUtils = (ProxyDataBaseEntryUtils) delegate.getDbEntryUtils();

		for (final Method method : repositoryInterface.getDeclaredMethods()) {
			if (AnnotatedElementUtils.hasAnnotation(method, Query.class)) {
				final Function<List<Object>, ?> f = dbEntryUtils.buildMethodQueryFunction(delegate, method);
				this.queries.put(method, f);
			}
		}
		for (final Class<?> topiface : repositoryInterface.getInterfaces()) {
			for (final Method method : topiface.getDeclaredMethods()) {
				if (AnnotatedElementUtils.hasAnnotation(method, Query.class)) {
					final Function<List<Object>, ?> f = dbEntryUtils.buildMethodQueryFunction(delegate, method);
					this.queries.put(method, f);
				}
			}
		}
	}

}
