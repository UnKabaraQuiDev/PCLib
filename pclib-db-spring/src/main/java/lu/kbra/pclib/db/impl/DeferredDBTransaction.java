package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.base.transaction.DBTransaction;
import lu.kbra.pclib.db.table.DatabaseTable;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;

public interface DeferredDBTransaction extends DBTransaction {

	<X extends DatabaseEntry, V extends DeferredDatabaseTable<X>> V use(final V inst);

	@Override
	<X extends DatabaseEntry, V extends DatabaseTable<X>> V use(final V inst);

}
