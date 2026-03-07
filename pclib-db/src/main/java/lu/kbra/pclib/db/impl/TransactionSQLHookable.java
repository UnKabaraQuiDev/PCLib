package lu.kbra.pclib.db.impl;

import lu.kbra.pclib.db.table.DBTableTransaction;
import lu.kbra.pclib.db.utils.SQLRequestType;

public interface TransactionSQLHookable {

	void requestHook(DBTableTransaction transaction, SQLRequestType type, Object query);

}
