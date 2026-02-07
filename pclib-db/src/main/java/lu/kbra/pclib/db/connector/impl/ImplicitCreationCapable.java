package lu.kbra.pclib.db.connector.impl;

import java.sql.SQLException;

public interface ImplicitCreationCapable {

	boolean exists() throws SQLException;

	boolean create() throws SQLException;

}
