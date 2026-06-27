package lu.kbra.pclib.db.base.transaction;

import java.sql.Connection;

import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.table.DataBaseTable;

public interface DBTransaction extends AutoCloseable {

	@Override
	void close() throws DBException;

	void commit() throws DBException;

	Connection getConnection();

	boolean isClosed();

	void rollback() throws DBException;

	<X extends DataBaseEntry, V extends DataBaseTable<X>> DataBaseTable<X> use(final V inst);

}
