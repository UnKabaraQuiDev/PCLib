package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.table.DeferredSQLQueryable;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class DeferredDataBaseView<T extends DataBaseEntry> extends DataBaseView<T> implements DeferredSQLQueryable<T> {

	public DeferredDataBaseView() {
	}

	public void init(DataBase dataBase) {
		init(dataBase, dataBase.getDataBaseEntryUtils());
	}

	public void init(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.viewClass = (Class<? extends AbstractDBView<T>>) getClass();
	}

	public void init(DataBase dataBase, DataBaseEntryUtils dbEntryUtils, Class<? extends AbstractDBView<T>> viewClass) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.viewClass = viewClass;
	}

}
