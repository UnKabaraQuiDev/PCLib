package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredNTSQLQueryable;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class DeferredNTDataBaseView<T extends DataBaseEntry> extends NTDataBaseView<T>
		implements DeferredNTSQLQueryable<T> {

	public DeferredNTDataBaseView(DataBase dataBase) {
		super(dataBase);
	}

	public DeferredNTDataBaseView(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public DeferredNTDataBaseView(DataBase dataBase, DataBaseEntryUtils dbEntryUtils,
			Class<? extends NTAbstractDBView<T>> viewClass) {
		super(dataBase, dbEntryUtils, viewClass);
	}

	public void init(Class<? extends AbstractDBView<T>> viewClass) {
		super.viewClass = viewClass;
	}

	@Deprecated
	public void init(DataBase dataBase) {
		init(dataBase, new BaseDataBaseEntryUtils());
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
