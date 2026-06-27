package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.base.transaction.DBTransaction;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;

public interface DeferredDBTransaction extends DBTransaction {

	<X extends DataBaseEntry, V extends DeferredDataBaseTable<X>> V use(final V inst);

	@Override
	<X extends DataBaseEntry, V extends DataBaseTable<X>> V use(final V inst);

}
