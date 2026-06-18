package lu.kbra.pclib.db.connector.impl;

import lu.kbra.pclib.db.exception.DBException;

public interface ImplicitCreationCapable {

	boolean create() throws DBException;

	boolean exists() throws DBException;

}
