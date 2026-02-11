package lu.kbra.pclib.db;

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
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.table.NTDataBaseTable;
import lu.kbra.pclib.db.view.DataBaseView;
import lu.kbra.pclib.db.view.NTDataBaseView;

public class SQLQueryableFactoryBean<T extends SQLQueryable<? extends DataBaseEntry>>
		implements FactoryBean<T> {

	private final AutowireCapableBeanFactory beanFactory;
	private final Class<T> repositoryClass;

	public SQLQueryableFactoryBean(Class<T> repositoryClass, AutowireCapableBeanFactory beanFactory) {
		this.repositoryClass = repositoryClass;
		this.beanFactory = beanFactory;
	}

	@Override
	public T getObject() throws Exception {
		final Enhancer enhancer = new Enhancer();
		if (!Modifier.isAbstract(repositoryClass.getModifiers())) {
			throw new IllegalArgumentException("Repository class must be abstract to be proxied: " + repositoryClass);
		}
		enhancer.setSuperclass(repositoryClass);

		final T dbProxy;

		final Constructor<?> ctor = repositoryClass.getDeclaredConstructors()[0];
		if (ctor == null || ctor.getParameterCount() == 0) {
			throw new UnsupportedOperationException(repositoryClass + " doesn't define a constructor.");
		} else {
			final Parameter[] params = ctor.getParameters();
			final Object[] args = new Object[params.length];

			for (int i = 0; i < params.length; i++) {
				final Parameter p = params[i];
				final Type genericType = p.getParameterizedType();

				if (genericType instanceof ParameterizedType pt) {
					final Type arg = pt.getActualTypeArguments()[0];
					if (arg instanceof Class<?> clazz && SQLQueryable.class.isAssignableFrom(clazz)) {
						args[i] = repositoryClass;
						continue;
					}
				}

				final DependencyDescriptor desc = new DependencyDescriptor(new MethodParameter(ctor, i), true);
				final Qualifier qual = p.getAnnotation(Qualifier.class);
				final String name = (qual != null ? qual.value() : null);
				args[i] = beanFactory.resolveDependency(desc, name);
			}

			dbProxy = (T) enhancer.create(Arrays.stream(params).map(p -> p.getType()).toArray(Class<?>[]::new), args);
		}

//		if (DataBaseView.class.isAssignableFrom(repositoryClass)) {
//			((DataBaseView) dbProxy).init(repositoryClass);
//		} else if (DataBaseTable.class.isAssignableFrom(repositoryClass)) {
//			((DataBaseTable) dbProxy).init(repositoryClass);
//		} else if (NTDataBaseView.class.isAssignableFrom(repositoryClass)) {
//			((NTDataBaseView) dbProxy).init(repositoryClass);
//		} else if (NTDataBaseTable.class.isAssignableFrom(repositoryClass)) {
//			((NTDataBaseTable) dbProxy).init(repositoryClass);
//		} else {
//			throw new IllegalArgumentException(
//					"Repository class must extend [NT]DataBase(View|Table): " + repositoryClass);
//		}

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
