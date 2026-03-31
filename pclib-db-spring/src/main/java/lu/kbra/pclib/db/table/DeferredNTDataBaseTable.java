package lu.kbra.pclib.db.table;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredNTSQLQueryable;
import lu.kbra.pclib.db.utils.BaseDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

@Deprecated
public class DeferredNTDataBaseTable<T extends DataBaseEntry> extends NTDataBaseTable<T> implements DeferredNTSQLQueryable<T> {

	@Deprecated
	public DeferredNTDataBaseTable(final DataBase dataBase) {
		super(dataBase);
	}

	@Deprecated
	public DeferredNTDataBaseTable(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	@Deprecated
	public DeferredNTDataBaseTable(
			final DataBase dataBase,
			final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends AbstractDBTable<T>> tableClass) {
		super(dataBase, dbEntryUtils, tableClass);
		this.gen_();
	}

	@Deprecated
	@Override
	protected void gen() {
		// do nothing
	}

	@Deprecated
	public void init(final Class<? extends AbstractDBTable<T>> viewClass) {
		super.tableClass = viewClass;
		this.gen_();
	}

	@Deprecated
	public void init(final DataBase dataBase) {
		this.init(dataBase, new BaseDataBaseEntryUtils());
	}

	@Deprecated
	public void init(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.tableClass = (Class<? extends AbstractDBTable<T>>) this.getClass();

		this.gen_();
	}

	@Deprecated
	public void init(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils, final Class<? extends AbstractDBTable<T>> tableClass) {
		super.dataBase = dataBase;
		super.dbEntryUtils = dbEntryUtils;
		super.tableClass = tableClass;

		this.gen_();
	}

	@Deprecated
	protected void gen_() {
		this.tableStructure = this.dbEntryUtils.scanTable(super.tableClass);
		this.tableStructure.update(this.dataBase.getConnector());
	}

}
