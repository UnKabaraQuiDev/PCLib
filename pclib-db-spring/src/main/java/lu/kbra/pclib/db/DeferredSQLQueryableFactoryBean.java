package lu.kbra.pclib.db;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.MethodParameter;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;
import lu.kbra.pclib.db.view.DeferredDataBaseView;
import lu.kbra.pclib.db.view.DeferredNTDataBaseView;

public class DeferredSQLQueryableFactoryBean<T extends DeferredSQLQueryable<? extends DataBaseEntry>>
		implements FactoryBean<T> {

	private final AutowireCapableBeanFactory beanFactory;
	private final Class<T> repositoryClass;

	public DeferredSQLQueryableFactoryBean(Class<T> repositoryClass, AutowireCapableBeanFactory beanFactory) {
		this.repositoryClass = repositoryClass;
		this.beanFactory = beanFactory;
	}

	@Override
	public T getObject() throws Exception {
		final QueryMethodInterceptor<T> interceptor = new QueryMethodInterceptor<>(repositoryClass);

		final Enhancer enhancer = new Enhancer();
		if (!Modifier.isAbstract(repositoryClass.getModifiers())) {
			throw new IllegalArgumentException("Repository class must be abstract to be proxied: " + repositoryClass);
		}
		enhancer.setSuperclass(repositoryClass);
		enhancer.setCallback(interceptor);

//		final RootBeanDefinition rbd = new RootBeanDefinition(repositoryClass,
//				AbstractAutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR, true);
		final T dbProxy;
//		if (rbd.hasConstructorArgumentValues()) {
//			final ConstructorArgumentValues cav = rbd.getConstructorArgumentValues();
//			dbProxy = (T) enhancer.create(cav.getargument);
//		} else {
//			dbProxy = (T) enhancer.create();
//		}

		final Constructor<?> ctor = repositoryClass.getDeclaredConstructors()[0];
		if (ctor == null || ctor.getParameterCount() == 0) {
			throw new UnsupportedOperationException(repositoryClass + " doesn't define a constructor.");
//			dbProxy = (T) enhancer.create();
		} else {
			final Parameter[] params = ctor.getParameters();
			final Object[] args = new Object[params.length];

			for (int i = 0; i < params.length; i++) {
				final Parameter p = params[i];

				final DependencyDescriptor desc = new DependencyDescriptor(new MethodParameter(ctor, i), true);

				final Qualifier qual = p.getAnnotation(Qualifier.class);
				final String name = (qual != null ? qual.value() : null);

				args[i] = beanFactory.resolveDependency(desc, name);
			}

			dbProxy = (T) enhancer.create(Arrays.stream(params).map(p -> p.getType()).toArray(Class<?>[]::new), args);
		}

		System.err.println(dbProxy);

		if (DeferredDataBaseView.class.isAssignableFrom(repositoryClass)) {
			((DeferredDataBaseView) dbProxy).init(repositoryClass);
		} else if (DeferredDataBaseTable.class.isAssignableFrom(repositoryClass)) {
			((DeferredDataBaseTable) dbProxy).init(repositoryClass);
		} else if (DeferredNTDataBaseView.class.isAssignableFrom(repositoryClass)) {
			((DeferredNTDataBaseView) dbProxy).init(repositoryClass);
		} else if (DeferredNTDataBaseTable.class.isAssignableFrom(repositoryClass)) {
			((DeferredNTDataBaseTable) dbProxy).init(repositoryClass);
		} else {
			throw new IllegalArgumentException(
					"Repository class must extend Deferred[NT]DataBase(View|Table): " + repositoryClass);
		}

		interceptor.registerDelegate((T) dbProxy);

		beanFactory.autowireBean(dbProxy);

		beanFactory.initializeBean(dbProxy, Introspector.decapitalize(repositoryClass.getSimpleName()));

		return dbProxy;
	}

	@Override
	public Class<?> getObjectType() {
		return repositoryClass;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
