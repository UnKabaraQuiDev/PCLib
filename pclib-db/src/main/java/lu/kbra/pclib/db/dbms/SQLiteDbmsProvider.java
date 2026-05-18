package lu.kbra.pclib.db.dbms;

import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.dialect.SQLiteStructureVisitor;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.SQLiteColumnTypeRegistry;

public class SQLiteDbmsProvider implements DbmsProvider {

	@Override
	public String getProtocol() {
		return "sqlite";
	}

	@Override
	public ColumnTypeRegistry createColumnTypeRegistry() {
		return new SQLiteColumnTypeRegistry();
	}

	@Override
	public SQLStructureVisitor createStructureVisitor(final DataBaseConnector connector) {
		return new SQLiteStructureVisitor(connector);
	}

}
