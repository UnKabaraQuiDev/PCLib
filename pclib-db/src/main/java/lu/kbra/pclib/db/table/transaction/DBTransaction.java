package lu.kbra.pclib.db.table.transaction;

import java.sql.Connection;

import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.table.NTDataBaseTable;

public interface DBTransaction extends AutoCloseable {

	Connection getConnection();

	void commit() throws DBException;

	void rollback() throws DBException;

	boolean isClosed();

	@Override
	void close() throws DBException;

	<X extends DataBaseEntry, V extends DataBaseTable<X>> DataBaseTable<X> use(final V inst);

	<X extends DataBaseEntry, V extends NTDataBaseTable<X>> NTDataBaseTable<X> use(final V inst);

}
