package lu.kbra.pclib.db.query;

import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.dbms.DbmsProviders;
import lu.kbra.pclib.db.impl.SQLNamed;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.view.AbstractDBView;

public final class SQLQueryVisitors {

	private static final SQLQueryVisitor DEFAULT_VISITOR = new MySQLQueryVisitor();

	private SQLQueryVisitors() {
	}

	public static SQLQueryVisitor defaultVisitor() {
		return SQLQueryVisitors.DEFAULT_VISITOR;
	}

	public static SQLQueryVisitor forConnector(final DataBaseConnector connector) {
		if (connector == null) {
			return SQLQueryVisitors.defaultVisitor();
		}
		return DbmsProviders.queryVisitorFor(connector);
	}

	public static SQLQueryVisitor forNamed(final SQLNamed named) {
		if (named instanceof AbstractDBTable<?>) {
			return SQLQueryVisitors.forConnector(((AbstractDBTable<?>) named).getDataBase().getConnector());
		}
		if (named instanceof AbstractDBView<?>) {
			return SQLQueryVisitors.forConnector(((AbstractDBView<?>) named).getDataBase().getConnector());
		}
		if (named != null && named.getQualifiedName() != null && named.getQualifiedName().startsWith("\"")) {
			return new PostgreSQLQueryVisitor();
		}
		return SQLQueryVisitors.defaultVisitor();
	}

}
