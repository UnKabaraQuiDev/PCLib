package lu.kbra.pclib.db.base.transaction;

import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.table.DatabaseTable;

public interface DBTransaction extends AutoCloseable {

	@Override
	void close() throws DBException;

	void commit() throws DBException;

	AbstractConnection getConnection();

	boolean isClosed();

	void rollback() throws DBException;

	<X extends DatabaseEntry, V extends DatabaseTable<X>> DatabaseTable<X> use(final V inst);

}
