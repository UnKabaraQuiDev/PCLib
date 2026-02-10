package lu.kbra.pclib.db.table;

import lu.kbra.pclib.db.DeferredSQLQueryable;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.view.AbstractDBView;

public class DeferredDataBaseTable<T extends DataBaseEntry> extends DataBaseTable<T>
		implements DeferredSQLQueryable<T> {

	public DeferredDataBaseTable(DataBase dataBase) {
		super(dataBase);
	}

	public DeferredDataBaseTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	@Deprecated
	public DeferredDataBaseTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils,
			Class<? extends AbstractDBTable<T>> tableClass) {
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
	public void init(DataBase dataBase, DataBaseEntryUtils dbEntryUtils,
			Class<? extends AbstractDBTable<T>> tableClass) {
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
