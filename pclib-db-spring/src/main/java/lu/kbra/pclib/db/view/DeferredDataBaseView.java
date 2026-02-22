package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class DeferredDataBaseView<T extends DataBaseEntry> extends DataBaseView<T> implements DeferredSQLQueryable<T> {

	public DeferredDataBaseView(DataBase dataBase) {
		super(dataBase);
	}

	public DeferredDataBaseView(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public DeferredDataBaseView(DataBase dataBase, DataBaseEntryUtils dbEntryUtils,
			Class<? extends AbstractDBView<T>> viewClass) {
		super(dataBase, dbEntryUtils, viewClass);
	}

	public void init(Class<? extends AbstractDBView<T>> viewClass) {
		super.viewClass = viewClass;
	}

	@Deprecated
	public void init(DataBase dataBase) {
		init(dataBase, dataBase.getDataBaseEntryUtils());
	}

	@Deprecated
	public void init(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.viewClass = (Class<? extends AbstractDBView<T>>) getClass();
	}

	@Deprecated
	public void init(DataBase dataBase, DataBaseEntryUtils dbEntryUtils, Class<? extends AbstractDBView<T>> viewClass) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.viewClass = viewClass;
	}

}
