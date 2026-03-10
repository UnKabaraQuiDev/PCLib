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
import lu.kbra.pclib.db.impl.DeferredNTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;
import lu.kbra.pclib.db.intercept.TransactionQueryMethodInterceptor;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

@Deprecated
public class DeferredNTDataBaseTable<T extends DataBaseEntry> extends NTDataBaseTable<T> implements DeferredNTSQLQueryable<T> {

	public DeferredNTDataBaseTable(DataBase dataBase) {
		super(dataBase);
	}

	public DeferredNTDataBaseTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public DeferredNTDataBaseTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils, Class<? extends AbstractDBTable<T>> tableClass) {
		super(dataBase, dbEntryUtils, tableClass);
		gen_();
	}

	@Override
	protected void gen() {
		// do nothing
	}

	public void init(Class<? extends AbstractDBTable<T>> viewClass) {
		super.tableClass = viewClass;
		gen_();
	}

	@Deprecated
	public void init(DataBase dataBase) {
		init(dataBase, new BaseDataBaseEntryUtils());
	}

	@Deprecated
	public void init(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.tableClass = (Class<? extends AbstractDBTable<T>>) getClass();

		gen_();
	}

	@Deprecated
	public void init(DataBase dataBase, DataBaseEntryUtils dbEntryUtils, Class<? extends AbstractDBTable<T>> tableClass) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.tableClass = tableClass;

		gen_();
	}

	protected void gen_() {
		structure = dbEntryUtils.scanTable((Class<? extends DataBaseTable<T>>) super.tableClass);
		structure.update(dataBase.getConnector());
	}

}
