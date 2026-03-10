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

	public <T extends SQLQueryable<? extends DataBaseEntry>> void registerDelegate(final T delegate, final Class<T> repositoryInterface) {
		if (!(delegate.getDbEntryUtils() instanceof ProxyDataBaseEntryUtils)) {
			throw new IllegalArgumentException(
					"Delegate must use ProxyDataBaseEntryUtils to be able to build query functions: " + repositoryInterface.getName());
		}

		final ProxyDataBaseEntryUtils dbEntryUtils = (ProxyDataBaseEntryUtils) delegate.getDbEntryUtils();
		final String repoName = delegate.getName();

		for (final Method method : repositoryInterface.getDeclaredMethods()) {
			if (AnnotatedElementUtils.hasAnnotation(method, Query.class)) {
				final Function<List<Object>, ?> f = dbEntryUtils.buildMethodQueryFunction(repoName, delegate, method);
				queries.put(method, f);
			}
		}
		for (final Class<?> topiface : repositoryInterface.getInterfaces()) {
			for (final Method method : topiface.getDeclaredMethods()) {
				if (AnnotatedElementUtils.hasAnnotation(method, Query.class)) {
					final Function<List<Object>, ?> f = dbEntryUtils.buildMethodQueryFunction(repoName, delegate, method);
					queries.put(method, f);
				}
			}
		}
	}

	@Override
	public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy) throws Throwable {
		if (queries.containsKey(method)) {
			return queries.get(method).apply(Arrays.asList(args));
		}
		if (isDeclaredInSuperclass(method, obj.getClass().getSuperclass())) {
			return proxy.invokeSuper(obj, args);
		}
		return invokeDefaultMethod(obj, method, args);
	}

	private boolean isDeclaredInSuperclass(final Method method, final Class<?> superClass) {
		try {
			superClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
			return true;
		} catch (final NoSuchMethodException e) {
			return false;
		}
	}

	private Object invokeDefaultMethod(final Object proxy, final Method method, final Object[] args) throws Throwable {
		final Class<?> declaringClass = method.getDeclaringClass();

		// If the default method is in a parent interface (not directly implemented)
		if (!declaringClass.isAssignableFrom(proxy.getClass())) {
			throw new IllegalArgumentException("Proxy does not implement declaring interface.");
		}

		// Use a special MethodHandles.Lookup to access default methods
		final MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(declaringClass, MethodHandles.lookup());

		// Bind the method handle to the proxy (as interface default methods are bound
		// to interface)
		final MethodHandle methodHandle = lookup
				.findSpecial(declaringClass,
						method.getName(),
						MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
						declaringClass);

		return methodHandle.bindTo(proxy).invokeWithArguments(args);
	}

}
