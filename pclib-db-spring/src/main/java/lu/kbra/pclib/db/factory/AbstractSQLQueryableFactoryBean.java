
package lu.kbra.pclib.db.factory;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.MethodParameter;

import lu.kbra.pclib.db.impl.ProxySQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.db.view.DeferredDatabaseView;

public class AbstractSQLQueryableFactoryBean<T extends SQLQueryable<?>> implements FactoryBean<T> {

	private final AutowireCapableBeanFactory beanFactory;
	private final Class<T> repositoryClass;
	private final QueryMethodInterceptor interceptor;

	public AbstractSQLQueryableFactoryBean(
			final Class<T> repositoryClass,
			final QueryMethodInterceptor interceptor,
			final AutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		this.repositoryClass = repositoryClass;
		this.interceptor = interceptor;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public T getObject() throws Exception {
		final boolean isView = AbstractDBView.class.isAssignableFrom(this.repositoryClass);
		final boolean isTable = AbstractDBTable.class.isAssignableFrom(this.repositoryClass);

		if (!isView && !isTable) {
			throw new IllegalArgumentException(
					"Repository class must implement AbstractDBView or AbstractDBTable: " + this.repositoryClass.getName());
		}

		final Class<?> superclass = isView ? DeferredDatabaseView.class : DeferredDatabaseTable.class;

		final Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(superclass);
		enhancer.setInterfaces(new Class<?>[] { this.repositoryClass });
		enhancer.setCallback(this.interceptor);

		final Method constr = Arrays.stream(this.repositoryClass.getMethods())
				.filter(method -> method.getName().equals(ProxySQLQueryable.METHOD_NAME))
				.findFirst()
				.orElse(null);

		final Constructor<?> ctor = constr == null ? superclass.getDeclaredConstructors()[0]
				: Arrays.stream(superclass.getDeclaredConstructors())
						.filter(constructor -> constructor.getParameterCount() == constr.getParameterCount())
						.findFirst()
						.orElseThrow(() -> new IllegalStateException(
								"No constructor in " + superclass.getName() + " has " + constr.getParameterCount() + " parameters"));

		final Parameter[] params = ctor.getParameters();
		final Parameter[] constrParams = constr == null ? ctor.getParameters() : constr.getParameters();
		final Object[] args = new Object[params.length];

		for (int i = 0; i < params.length; i++) {
			final Parameter parameter = params[i];
			final Type genericType = parameter.getParameterizedType();

			if (genericType instanceof final ParameterizedType pt) {
				final Type arg = pt.getActualTypeArguments()[0];

				if (arg instanceof final Class<?> clazz && SQLQueryable.class.isAssignableFrom(clazz)) {
					args[i] = this.repositoryClass;
					continue;
				}
			}

			final Parameter constrParam = constrParams[i];

			final DependencyDescriptor descriptor = new DependencyDescriptor(new MethodParameter(ctor, i), true) {
				@Override
				public Annotation[] getAnnotations() {
					return constrParam.getAnnotations();
				}

				@Override
				public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
					return constrParam.getAnnotation(annotationType);
				}
			};

			args[i] = this.beanFactory.resolveDependency(descriptor, null);
		}

		final Object proxy = enhancer.create(Arrays.stream(params).map(Parameter::getType).toArray(Class<?>[]::new), args);

		if (isView) {
			((DeferredDatabaseView) proxy).init(this.repositoryClass, this.interceptor);
		} else {
			((DeferredDatabaseTable) proxy).init(null, this.repositoryClass, this.interceptor);
		}

		this.beanFactory.autowireBean(proxy);
		this.beanFactory.initializeBean(proxy, Introspector.decapitalize(this.repositoryClass.getSimpleName()));

		return (T) proxy;
	}

	@Override
	public Class<?> getObjectType() {
		return this.repositoryClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
