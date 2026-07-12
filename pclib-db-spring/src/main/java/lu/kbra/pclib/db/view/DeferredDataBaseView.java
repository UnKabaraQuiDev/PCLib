package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.domain.table.meta.DefaultQueryableHints;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;

import lombok.Getter;

@Getter
public abstract class DeferredDataBaseView<T extends DataBaseEntry> extends DataBaseView<T> implements DeferredSQLQueryable<T> {

	protected QueryMethodInterceptor interceptor;

	protected DeferredDataBaseView(final DataBase dataBase) {
		super(dataBase);
	}

	protected DeferredDataBaseView(final DataBase dataBase, final String name) {
		super(dataBase, name);
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
