package lu.kbra.pclib.db.connector.impl;

import java.sql.Connection;
import java.util.Map;

import lu.kbra.pclib.db.exception.DBException;

public interface AbstractConnection extends AutoCloseable, Connection {

	@Override
	void close() throws DBException;

	Map<String, Object> getAttributes();

	<T> Object setAttribute(String key, T value);

	<T> T getAttribute(String key);

	boolean hasAttribute(String key);

	Object removeAttribute(String key);

}
