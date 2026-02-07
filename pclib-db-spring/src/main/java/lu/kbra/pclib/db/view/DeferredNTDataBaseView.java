package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class DeferredNTDataBaseView<T extends DataBaseEntry> extends NTDataBaseView<T> {

	public DeferredNTDataBaseView() {
	}

	public void init(DataBase dataBase) {
		init(dataBase, new BaseDataBaseEntryUtils());
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
