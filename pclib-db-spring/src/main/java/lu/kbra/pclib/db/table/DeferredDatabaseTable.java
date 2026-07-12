package lu.kbra.pclib.db.table;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;

import lombok.Getter;

@Getter
public abstract class DeferredDatabaseTable<T extends DatabaseEntry> extends DatabaseTable<T> implements DeferredSQLQueryable<T> {

	protected QueryMethodInterceptor interceptor;

	protected DeferredDatabaseTable(final Database database, final String name) {
		super(database, name);
	}

	protected DeferredDatabaseTable(final Database database) {
		super(database);
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
