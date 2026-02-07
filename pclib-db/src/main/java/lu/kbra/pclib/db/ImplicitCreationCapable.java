package lu.kbra.pclib.db;

import java.sql.SQLException;

public interface ImplicitCreationCapable {

	boolean exists() throws SQLException;

	boolean create() throws SQLException;

}
