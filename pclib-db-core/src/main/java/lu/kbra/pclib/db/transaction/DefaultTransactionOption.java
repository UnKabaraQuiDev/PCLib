package lu.kbra.pclib.db.transaction;

public enum DefaultTransactionOption implements TransactionOption {

	READ_ONLY,
	DEFER_FOREIGN_KEYS,
	IMMEDIATE_FOREIGN_KEYS;

}
