package lu.kbra.pclib.db.table;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class DeferredNTDataBaseTable<T extends DataBaseEntry> extends NTDataBaseTable<T>
		implements DeferredNTSQLQueryable<T> {

	public DeferredNTDataBaseTable() {
		super(null);
	}

	@Override
	protected void gen() {
		// do nothing
	}

	public void init(DataBase dataBase) {
		init(dataBase, new BaseDataBaseEntryUtils());
	}

	public void init(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.tableClass = (Class<? extends AbstractDBTable<T>>) getClass();

		gen_();
	}

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
