package lu.kbra.pclib.db.view;

import org.springframework.cglib.proxy.MethodInterceptor;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;

import lombok.Getter;

@Getter
public abstract class DeferredDatabaseView<T extends DatabaseEntry> extends DatabaseView<T> implements DeferredSQLQueryable<T> {

	protected MethodInterceptor interceptor;

	protected DeferredDatabaseView(final DeferredDatabase database) {
		super(database);
	}

	protected DeferredDatabaseView(final DeferredDatabase database, final String name) {
		super(database, name);
	}

	public void init(final Class<? extends AbstractDBView<T>> targetClass, final MethodInterceptor interceptor) {
		super.customHints.put(DefaultQueryableHints.TARGET_CLASS, targetClass);
		this.interceptor = interceptor;
	}

	@Override
	public DeferredDatabase getDatabase() {
		return (DeferredDatabase) super.database;
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
