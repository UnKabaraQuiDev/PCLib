package lu.kbra.pclib.db.connector.impl;

import lu.kbra.pclib.db.exception.DBException;

public interface ImplicitDeletionCapable {

	boolean delete() throws DBException;

}
