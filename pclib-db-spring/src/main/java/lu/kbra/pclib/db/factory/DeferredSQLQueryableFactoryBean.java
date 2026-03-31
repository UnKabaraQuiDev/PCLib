package lu.kbra.pclib.db.factory;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.MethodParameter;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;
import lu.kbra.pclib.db.view.DeferredDataBaseView;
import lu.kbra.pclib.db.view.DeferredNTDataBaseView;

public class DeferredSQLQueryableFactoryBean<X extends DataBaseEntry, T extends DeferredSQLQueryable<X>> implements FactoryBean<T> {

	private final AutowireCapableBeanFactory beanFactory;
	private final Class<T> repositoryClass;
	private final QueryMethodInterceptor interceptor;

	public DeferredSQLQueryableFactoryBean(
			final Class<T> repositoryClass,
			final QueryMethodInterceptor interceptor,
			final AutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
		this.repositoryClass = repositoryClass;
		this.interceptor = interceptor;
	}

	@Override
	public T getObject() throws Exception {
		final Enhancer enhancer = new Enhancer();
		if (!Modifier.isAbstract(this.repositoryClass.getModifiers())) {
			throw new IllegalArgumentException("Repository class must be abstract to be proxied: " + this.repositoryClass);
		}
		enhancer.setSuperclass(this.repositoryClass);
		enhancer.setCallback(this.interceptor);

		final T dbProxy;

		final Constructor<?> ctor = this.repositoryClass.getDeclaredConstructors()[0];
		if (ctor == null || ctor.getParameterCount() == 0) {
			throw new UnsupportedOperationException(this.repositoryClass + " doesn't define a constructor.");
		} else {
			final Parameter[] params = ctor.getParameters();
			final Object[] args = new Object[params.length];

			for (int i = 0; i < params.length; i++) {
				final Parameter p = params[i];
				final Type genericType = p.getParameterizedType();

				if (genericType instanceof final ParameterizedType pt) {
					final Type arg = pt.getActualTypeArguments()[0];
					if (arg instanceof final Class<?> clazz && SQLQueryable.class.isAssignableFrom(clazz)) {
						args[i] = this.repositoryClass;
						continue;
					}
				}

				final DependencyDescriptor desc = new DependencyDescriptor(new MethodParameter(ctor, i), true);
				final Qualifier qual = p.getAnnotation(Qualifier.class);
				final String name = qual != null ? qual.value() : null;
				args[i] = this.beanFactory.resolveDependency(desc, name);
			}

			dbProxy = (T) enhancer.create(Arrays.stream(params).map(Parameter::getType).toArray(Class<?>[]::new), args);
		}

		if (DeferredDataBaseView.class.isAssignableFrom(this.repositoryClass)) {
			((DeferredDataBaseView) dbProxy).init(this.repositoryClass);
		} else if (DeferredDataBaseTable.class.isAssignableFrom(this.repositoryClass)) {
			((DeferredDataBaseTable) dbProxy).init(this.repositoryClass);
		} else if (DeferredNTDataBaseView.class.isAssignableFrom(this.repositoryClass)) {
			((DeferredNTDataBaseView) dbProxy).init(this.repositoryClass);
		} else if (DeferredNTDataBaseTable.class.isAssignableFrom(this.repositoryClass)) {
			((DeferredNTDataBaseTable) dbProxy).init(this.repositoryClass);
		} else {
			throw new IllegalArgumentException("Repository class must extend Deferred[NT]DataBase(View|Table): " + this.repositoryClass);
		}

		this.interceptor.registerDelegate(dbProxy, this.repositoryClass);

		this.beanFactory.autowireBean(dbProxy);
		this.beanFactory.initializeBean(dbProxy, Introspector.decapitalize(this.repositoryClass.getSimpleName()));

		if (dbProxy instanceof final AbstractDBTable<?> adbt) {
			adbt.getDataBase().registerTableBean(adbt);
		}

		return dbProxy;
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
