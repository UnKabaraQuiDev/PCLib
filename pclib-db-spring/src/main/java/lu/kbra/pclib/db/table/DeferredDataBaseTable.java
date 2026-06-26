package lu.kbra.pclib.db.table;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class DeferredDataBaseTable<T extends DataBaseEntry> extends DataBaseTable<T> implements DeferredSQLQueryable<T> {

	public DeferredDataBaseTable(final DataBase dataBase) {
		super(dataBase);
	}

	public DeferredDataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	@Deprecated
	public DeferredDataBaseTable(
			final DataBase dataBase,
			final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBTable<T>> tableClass) {
		super(dataBase, dbEntryUtils);
		this.init(tableClass);
	}

	@Override
	protected void gen() {
		// do nothing
	}

	protected void gen_() {
		this.tableStructure = this.dataBaseEntryUtils.scanTable(super.tableClass);
	}

	public void init(final Class<? extends AbstractDBTable<T>> viewClass) {
		super.tableClass = viewClass;
		this.gen_();
	}

	@Deprecated
	public void init(final DataBase dataBase) {
		this.init(dataBase, dataBase.getDataBaseEntryUtils());
	}

	@Deprecated
	public void init(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super.database = dataBase;
		super.dataBaseEntryUtils = dbEntryUtils;
		super.tableClass = (Class<? extends AbstractDBTable<T>>) this.getClass();

		this.gen_();
	}

	@Deprecated
	public void init(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils, final Class<? extends AbstractDBTable<T>> tableClass) {
		super.database = dataBase;
		super.dataBaseEntryUtils = dbEntryUtils;
		super.tableClass = tableClass;

		this.gen_();
	}

}
