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
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;
import lu.kbra.pclib.db.view.DeferredDataBaseView;
import lu.kbra.pclib.db.view.DeferredNTDataBaseView;

public class SQLQueryableFactoryBean<T extends SQLQueryable<? extends DataBaseEntry>> implements FactoryBean<T> {

	private final AutowireCapableBeanFactory beanFactory;
	private final Class<T> repositoryClass;

	public SQLQueryableFactoryBean(Class<T> repositoryClass, AutowireCapableBeanFactory beanFactory) {
		this.repositoryClass = repositoryClass;
		this.beanFactory = beanFactory;
	}

	@Override
	public T getObject() throws Exception {
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

			dbProxy = (T) ctor.newInstance(args);
		}

		if (DeferredDataBaseView.class.isAssignableFrom(repositoryClass)) {
			((DeferredDataBaseView) dbProxy).init(repositoryClass);
		} else if (DeferredDataBaseTable.class.isAssignableFrom(repositoryClass)) {
			((DeferredDataBaseTable) dbProxy).init(repositoryClass);
		} else if (DeferredNTDataBaseView.class.isAssignableFrom(repositoryClass)) {
			((DeferredNTDataBaseView) dbProxy).init(repositoryClass);
		} else if (DeferredNTDataBaseTable.class.isAssignableFrom(repositoryClass)) {
			((DeferredNTDataBaseTable) dbProxy).init(repositoryClass);
		}

		beanFactory.autowireBean(dbProxy);

		beanFactory.initializeBean(dbProxy, Introspector.decapitalize(repositoryClass.getSimpleName()));

		if (dbProxy instanceof AbstractDBTable<?> adbt) {
			adbt.getDataBase().registerTableBean(adbt);
		}

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
