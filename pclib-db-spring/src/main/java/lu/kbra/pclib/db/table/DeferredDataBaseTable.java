package lu.kbra.pclib.db.table;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

public class DeferredDataBaseTable<T extends DataBaseEntry> extends DataBaseTable<T> implements DeferredSQLQueryable<T> {

	public DeferredDataBaseTable(final DataBase dataBase) {
		super(dataBase);
	}

	public DeferredDataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public void init(final Class<? extends AbstractDBTable<T>> viewClass) {
		super.tableClass = viewClass;
		this.gen_();
	}

	@Override
	protected void gen() {
		// do nothing
	}

	protected void gen_() {
		this.structure = this.dataBaseEntryUtils.scanTable(super.tableClass);
	}

}
