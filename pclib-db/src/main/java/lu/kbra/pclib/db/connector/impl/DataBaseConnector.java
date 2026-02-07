package lu.kbra.pclib.db.connector.impl;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataBaseConnector {

	Connection connect() throws SQLException;

	Connection createConnection() throws SQLException;

	void setDatabase(String database);

	String getDatabase();

	void reset() throws SQLException;

	String getProtocol();
	

}
