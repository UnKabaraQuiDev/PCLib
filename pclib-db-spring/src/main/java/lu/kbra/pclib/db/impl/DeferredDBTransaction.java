package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.pclib.db.table.DeferredNTDataBaseTable;
import lu.kbra.pclib.db.table.NTDataBaseTable;
import lu.kbra.pclib.db.table.transaction.DBTransaction;

public interface DeferredDBTransaction extends DBTransaction {

	<X extends DataBaseEntry, V extends DeferredDataBaseTable<X>> V use(final V inst);

	<X extends DataBaseEntry, V extends DeferredNTDataBaseTable<X>> V use(final V inst);

	<X extends DataBaseEntry, V extends DataBaseTable<X>> V use(final V inst);

	<X extends DataBaseEntry, V extends NTDataBaseTable<X>> V use(final V inst);

}
