package lu.kbra.pclib.db.domain.dialect;

import lu.kbra.pclib.db.connector.impl.DatabaseConnector;
import lu.kbra.pclib.db.dbms.DbmsProviders;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public final class SQLFunctionResolvers {

	public static SQLFunctionResolver forConnector(final DatabaseConnector connector) {
		return SQLFunctionResolvers.forProtocol(connector.getProtocol());
	}

	public static SQLFunctionResolver forProtocol(final String protocol) {
		return DbmsProviders.functionResolverFor(protocol);
	}

	public static <B extends SQLQueryable<T>, T extends DatabaseEntry> SQLFunctionResolver forQueryable(final B table) {
		return SQLFunctionResolvers.forConnector(table.getConnector());
	}

	private SQLFunctionResolvers() {
	}

}
