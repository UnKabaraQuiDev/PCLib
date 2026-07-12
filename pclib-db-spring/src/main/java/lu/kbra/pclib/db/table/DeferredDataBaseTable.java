package lu.kbra.pclib.db.table;

import lombok.Getter;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;

@Getter
public abstract class DeferredDataBaseTable<T extends DataBaseEntry> extends DataBaseTable<T> implements DeferredSQLQueryable<T> {

	protected QueryMethodInterceptor interceptor;

	protected DeferredDataBaseTable(final DataBase dataBase, final String name) {
		super(dataBase, name);
	}

	protected DeferredDataBaseTable(final DataBase dataBase) {
		super(dataBase);
	}

	public void init(final Class<? extends AbstractDBTable<T>> targetClass, final QueryMethodInterceptor interceptor) {
		super.customHints.put(DefaultQueryableHints.TARGET_CLASS, targetClass);
		this.interceptor = interceptor;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
