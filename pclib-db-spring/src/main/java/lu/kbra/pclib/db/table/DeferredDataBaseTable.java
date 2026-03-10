package lu.kbra.pclib.db.table;

import java.beans.Introspector;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.core.MethodParameter;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.connector.DelegatingConnection;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;
import lu.kbra.pclib.db.intercept.TransactionQueryMethodInterceptor;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class DeferredDataBaseTable<T extends DataBaseEntry> extends DataBaseTable<T> implements DeferredSQLQueryable<T> {

	public DeferredDataBaseTable(final DataBase dataBase) {
		super(dataBase);
	}

	public DeferredDataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	@Deprecated
	public DeferredDataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBTable<T>> tableClass) {
		super(dataBase, dbEntryUtils);
		init(tableClass);
	}

	@Override
	protected void gen() {
		// do nothing
	}

	public void init(final Class<? extends AbstractDBTable<T>> viewClass) {
		super.tableClass = viewClass;
		gen_();
	}

	@Deprecated
	public void init(final DataBase dataBase) {
		init(dataBase, new BaseDataBaseEntryUtils());
	}

	@Deprecated
	public void init(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.tableClass = (Class<? extends AbstractDBTable<T>>) getClass();

		gen_();
	}

	@Deprecated
	public void init(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils, final Class<? extends AbstractDBTable<T>> tableClass) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.tableClass = tableClass;

		gen_();
	}

	protected void gen_() {
		structure = dbEntryUtils.scanTable(super.tableClass);
		structure.update(dataBase.getConnector());
	}

}
