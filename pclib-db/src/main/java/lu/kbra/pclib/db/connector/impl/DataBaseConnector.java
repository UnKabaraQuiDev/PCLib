package lu.kbra.pclib.db.connector.impl;

import java.sql.Connection;

import lu.kbra.pclib.db.connector.AbstractDataBaseConnector.CachedConnection.ConnectionHolder;
import lu.kbra.pclib.db.table.DBException;

public interface DataBaseConnector extends Cloneable {

	Connection connect() throws DBException;

	ConnectionHolder use() throws DBException;

	Connection createConnection() throws DBException;

	void setDatabase(String database);

	String getDatabase();

	void reset() throws DBException;

	String getProtocol();

	DataBaseConnector clone();

	/**
	 * @param timeoutSeconds
	 * @return false if the connection was still alive or isn't created.<br>
	 *         true if the connection needed to be reset
	 */
	boolean keepAlive(int timeoutSeconds);

}
