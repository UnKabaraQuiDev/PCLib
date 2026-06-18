package lu.kbra.pclib.db.autobuild;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public interface SQLBuildable {

	String build(DataBaseConnector connector);

}
