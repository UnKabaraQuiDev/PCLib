package lu.kbra.pclib.db.domain.dialect;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.dbms.DbmsProviders;

public final class SQLStructureVisitors {

	public static SQLStructureVisitor forConnector(final DataBaseConnector connector) {
		return SQLStructureVisitors.forProtocol(connector.getProtocol());
	}

	public static SQLStructureVisitor forProtocol(final String protocol) {
		return DbmsProviders.structureVisitorFor(protocol);
	}

	private SQLStructureVisitors() {
	}

}
