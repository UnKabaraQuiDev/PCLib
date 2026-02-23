package lu.kbra.pclib.db.connector.impl;

import java.sql.Connection;

import lu.kbra.pclib.db.table.DBException;

public interface DataBaseConnector extends Cloneable {

	Connection connect() throws DBException;

	Connection createConnection() throws DBException;

	void setDatabase(String database);

	String getDatabase();

	void reset() throws DBException;

	String getProtocol();

	DataBaseConnector clone();

}
