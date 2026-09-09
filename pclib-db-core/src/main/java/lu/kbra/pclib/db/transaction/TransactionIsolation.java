package lu.kbra.pclib.db.transaction;

public enum TransactionIsolation implements SingleTransactionOption {

	READ_UNCOMMITTED,
	READ_COMMITTED,
	REPEATABLE_READ,
	SERIALIZABLE;

}
