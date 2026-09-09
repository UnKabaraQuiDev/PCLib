package lu.kbra.pclib.db.base.transaction;

import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.table.DatabaseTable;
import lu.kbra.pclib.db.transaction.TransactionOption;

public interface DBTransaction extends AutoCloseable {

	@Override
	void close() throws DBException;

	void commit() throws DBException;

	AbstractConnection getConnection();

	boolean isClosed();

	void rollback() throws DBException;

	<T extends DatabaseEntry, V extends DatabaseTable<T>> DatabaseTable<T> use(final V inst);

	public interface TransactionCustomizer {

		default TransactionCustomizer enable(final TransactionOption option) {
			return this.setEnabled(option, true);
		}

		default TransactionCustomizer disable(final TransactionOption option) {
			return this.setDisabled(option, true);
		}

		TransactionCustomizer setEnabled(TransactionOption option, boolean v);

		default TransactionCustomizer setDisabled(final TransactionOption option, final boolean v) {
			return this.setEnabled(option, !v);
		}

	}

}
