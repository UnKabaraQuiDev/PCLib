package lu.kbra.pclib.db.connector.impl;

import java.sql.SQLException;

public interface ImplicitDeletionCapable {

	boolean delete() throws SQLException;

}
