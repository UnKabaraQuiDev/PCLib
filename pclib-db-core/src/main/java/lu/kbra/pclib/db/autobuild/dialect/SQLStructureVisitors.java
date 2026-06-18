package lu.kbra.pclib.db.autobuild.dialect;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.dbms.DbmsProviders;

public final class SQLStructureVisitors {

	public static SQLStructureVisitor forConnector(final DataBaseConnector connector) {
		return DbmsProviders.structureVisitorFor(connector);
	}

	private SQLStructureVisitors() {
	}

}
