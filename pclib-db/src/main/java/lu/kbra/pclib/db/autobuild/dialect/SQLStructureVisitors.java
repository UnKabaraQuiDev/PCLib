package lu.kbra.pclib.db.autobuild.dialect;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

public final class SQLStructureVisitors {

	private SQLStructureVisitors() {
	}

	public static SQLStructureVisitor forConnector(final DataBaseConnector connector) {
		if ("sqlite".equalsIgnoreCase(connector.getProtocol())) {
			return new SQLiteStructureVisitor(connector);
		}
		return new MySQLStructureVisitor(connector);
	}

}
