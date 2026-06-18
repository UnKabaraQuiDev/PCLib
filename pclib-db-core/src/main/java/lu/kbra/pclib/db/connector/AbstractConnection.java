package lu.kbra.pclib.db.connector;

import java.sql.Connection;

import lu.kbra.pclib.db.exception.DBException;

public interface AbstractConnection extends AutoCloseable, Connection {

	@Override
	void close() throws DBException;

}
