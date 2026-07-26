package test;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.connector.impl.DatabaseConnector;

public interface GenericDBTest {

	Database getDatabase();

	DatabaseConnector getConnector();

	DatabaseConnector createConnector() throws Exception;

}
