package lu.kbra.pclib.db.table.transaction;

import java.sql.Connection;

import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;

public interface DBTableTransaction<T extends DataBaseEntry> extends AutoCloseable, SQLQueryable<T>, AbstractDBTable<T> {

	Connection getConnection();

	void commit() throws DBException;

	void rollback() throws DBException;

	boolean isClosed();

	@Override
	void close() throws DBException;
}