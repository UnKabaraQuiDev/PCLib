package lu.kbra.pclib.db.dbms;

import lu.kbra.pclib.db.autobuild.dialect.MySQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

public class MySQLDbmsProvider implements DbmsProvider {

	@Override
	public String getProtocol() {
		return "mysql";
	}

	@Override
	public ColumnTypeRegistry createColumnTypeRegistry() {
		return new MySQLColumnTypeRegistry();
	}

	@Override
	public SQLStructureVisitor createStructureVisitor(final DataBaseConnector connector) {
		return new MySQLStructureVisitor(connector);
	}

}
