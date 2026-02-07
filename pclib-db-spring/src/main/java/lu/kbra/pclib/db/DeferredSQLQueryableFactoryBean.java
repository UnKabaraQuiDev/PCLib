package lu.kbra.pclib.db;

import java.beans.Introspector;
import java.lang.reflect.Modifier;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.ApplicationContext;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;
import lu.kbra.pclib.db.table.DeferredSQLQueryable;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.view.DeferredDataBaseView;
import lu.kbra.pclib.db.view.DeferredNTDataBaseView;

public class DeferredSQLQueryableFactoryBean<T extends DeferredSQLQueryable<? extends DataBaseEntry>>
		implements FactoryBean<T> {

	private final DataBase database;
	private final DataBaseEntryUtils dbEntryutils;
	private final AutowireCapableBeanFactory beanFactory;
	private final Class<T> repositoryClass;
	private final ApplicationContext context;

	public DeferredSQLQueryableFactoryBean(Class<T> repositoryClass, DataBase db,
			AutowireCapableBeanFactory beanFactory, ApplicationContext context) {
		this.repositoryClass = repositoryClass;
		this.database = db;
		this.dbEntryutils = db.getDataBaseEntryUtils();
		this.beanFactory = beanFactory;
		this.context = context;
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

		final T dbProxy = (T) enhancer.create();
		if (DeferredDataBaseView.class.isAssignableFrom(repositoryClass)) {
			((DeferredDataBaseView) dbProxy).init(database, dbEntryutils, repositoryClass);
		} else if (DeferredDataBaseTable.class.isAssignableFrom(repositoryClass)) {
			((DeferredDataBaseTable) dbProxy).init(database, dbEntryutils, repositoryClass);
		} else if (DeferredNTDataBaseView.class.isAssignableFrom(repositoryClass)) {
			((DeferredNTDataBaseView) dbProxy).init(database, dbEntryutils, repositoryClass);
		} else if (DeferredNTDataBaseTable.class.isAssignableFrom(repositoryClass)) {
			((DeferredNTDataBaseTable) dbProxy).init(database, dbEntryutils, repositoryClass);
		} else {
			throw new IllegalArgumentException(
					"Repository class must extend DeferredDataBaseView or DeferredDataBaseTable: " + repositoryClass);
		}

		interceptor.registerDelegate((T) dbProxy);

		beanFactory.autowireBean(dbProxy);

//		if (dbProxy instanceof AbstractDBTable) {
//			((AbstractDBTable) dbProxy).create();
//		} else if (dbProxy instanceof AbstractDBView) {
//			((AbstractDBView) dbProxy).create();
//		} else {
//			throw new IllegalArgumentException("Cannot create proxy for type: " + repositoryClass);
//		}

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
