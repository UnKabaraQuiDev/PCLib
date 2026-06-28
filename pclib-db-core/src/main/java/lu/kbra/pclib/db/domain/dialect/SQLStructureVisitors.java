package lu.kbra.pclib.db.domain.dialect;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.dbms.DbmsProviders;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public final class SQLStructureVisitors {

	public static SQLStructureVisitor forConnector(final DataBaseConnector connector) {
		return SQLStructureVisitors.forProtocol(connector.getProtocol());
	}

	public static SQLStructureVisitor forProtocol(final String protocol) {
		return DbmsProviders.structureVisitorFor(protocol);
	}

	public static <B extends SQLQueryable<T>, T extends DataBaseEntry> SQLStructureVisitor forQueryable(final B table) {
		return SQLStructureVisitors.forConnector(table.getConnector());
	}

	private SQLStructureVisitors() {
	}

}
