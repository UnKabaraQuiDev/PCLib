package lu.kbra.pclib.db.connector.impl;

import java.net.URI;
import java.sql.Connection;

import lu.kbra.pclib.db.connector.AbstractDataBaseConnector.CachedConnection.ConnectionHolder;
import lu.kbra.pclib.db.exception.DBException;

public interface DataBaseConnector extends Cloneable {

	DataBaseConnector clone();

	Connection connect() throws DBException;

	Connection createConnection() throws DBException;

	String getDatabase();

	String getProtocol();

	URI getURI();

	/**
	 * @param timeoutSeconds
	 * @return false if the connection was still alive or isn't created.<br>
	 *         true if the connection needed to be reset
	 */
	boolean keepAlive(int timeoutSeconds);

	default void preDelete() {
	}

	void reset() throws DBException;

	void setDatabase(String database);

	ConnectionHolder use() throws DBException;

}
