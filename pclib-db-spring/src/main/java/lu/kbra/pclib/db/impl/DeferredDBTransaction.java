package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.base.transaction.DBTransaction;
import lu.kbra.pclib.db.table.DatabaseTable;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;

public interface DeferredDBTransaction extends DBTransaction {

	<T extends DatabaseEntry, V extends DeferredDatabaseTable<T>> V use(final V inst);

	@Override
	<T extends DatabaseEntry, V extends DatabaseTable<T>> V use(final V inst);

}
