package lu.kbra.pclib.db.table;

import java.sql.Connection;

public interface DBTableTransaction extends AutoCloseable {

	Connection getConnection();

	void commit() throws DBException;

	void rollback() throws DBException;

	boolean isClosed();

	@Override
	void close() throws DBException;
}