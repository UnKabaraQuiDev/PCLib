package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.table.transaction.DBTableTransaction;
import lu.kbra.pclib.db.utils.SQLRequestType;

public interface TransactionSQLHookable<T extends DataBaseEntry> {

	void requestHook(DBTableTransaction<T> transaction, SQLRequestType type, Object query);

}
