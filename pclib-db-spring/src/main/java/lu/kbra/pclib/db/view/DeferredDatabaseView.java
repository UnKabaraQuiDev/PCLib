package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;

import lombok.Getter;

@Getter
public abstract class DeferredDatabaseView<T extends DatabaseEntry> extends DatabaseView<T> implements DeferredSQLQueryable<T> {

	protected QueryMethodInterceptor interceptor;

	protected DeferredDatabaseView(final Database database) {
		super(database);
	}

	protected DeferredDatabaseView(final Database database, final String name) {
		super(database, name);
	}

	public void init(final Class<? extends AbstractDBView<T>> targetClass, final QueryMethodInterceptor interceptor) {
		super.customHints.put(DefaultQueryableHints.TARGET_CLASS, targetClass);
		this.interceptor = interceptor;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
