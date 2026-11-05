package lu.kbra.pclib.db.impl;

import java.sql.SQLException;

import lu.kbra.pclib.db.connector.impl.AbstractConnection;
import lu.kbra.pclib.db.exception.DBException;

public interface SQLCommand<T extends DatabaseEntry, R> {

	R execute(AbstractConnection c, SQLQueryable<T> instance) throws SQLException, DBException;

}
