package lu.kbra.pclib.db.table;

import org.springframework.cglib.proxy.MethodInterceptor;

import lu.kbra.pclib.db.base.DeferredDatabase;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;

import lombok.Getter;

@Getter
public abstract class DeferredDatabaseTable<T extends DatabaseEntry> extends DatabaseTable<T> implements DeferredSQLQueryable<T> {

	protected MethodInterceptor interceptor;

	protected DeferredDatabaseTable(final DeferredDatabase database) {
		super(database);
	}

	protected DeferredDatabaseTable(final DeferredDatabase database, final String name) {
		super(database, name);
	}

	public final void init(
			final AbstractDBTable<T> parent,
			final Class<? extends AbstractDBTable<T>> targetClass,
			final MethodInterceptor interceptor) {
		super.customHints.put(DefaultQueryableHints.TARGET_CLASS, targetClass);
		this.interceptor = interceptor;

		if (parent != null) {
			super.structure = parent.getStructure();
		}
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
