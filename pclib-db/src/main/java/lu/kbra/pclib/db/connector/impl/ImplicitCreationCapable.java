package lu.kbra.pclib.db.connector.impl;

import lu.kbra.pclib.db.exception.DBException;

public interface ImplicitCreationCapable {

	boolean exists() throws DBException;

	boolean create() throws DBException;

}
