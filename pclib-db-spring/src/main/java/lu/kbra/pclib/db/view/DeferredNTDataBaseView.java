package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredNTSQLQueryable;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

@Deprecated
public class DeferredNTDataBaseView<T extends DataBaseEntry> extends NTDataBaseView<T> implements DeferredNTSQLQueryable<T> {

	@Deprecated
	public DeferredNTDataBaseView(final DataBase dataBase) {
		super(dataBase);
	}

	@Deprecated
	public DeferredNTDataBaseView(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	@Deprecated
	public DeferredNTDataBaseView(
			final DataBase dataBase,
			final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends NTAbstractDBView<T>> viewClass) {
		super(dataBase, dbEntryUtils, viewClass);
	}

	@Deprecated
	public void init(final Class<? extends AbstractDBView<T>> viewClass) {
		super.viewClass = viewClass;
	}

	@Deprecated
	public void init(final DataBase dataBase) {
		this.init(dataBase, new BaseDataBaseEntryUtils());
	}

	@Deprecated
	public void init(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.viewClass = (Class<? extends AbstractDBView<T>>) this.getClass();
	}

	@Deprecated
	public void init(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils, final Class<? extends AbstractDBView<T>> viewClass) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.viewClass = viewClass;
	}

}
