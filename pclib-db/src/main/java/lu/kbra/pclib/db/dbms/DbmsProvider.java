package lu.kbra.pclib.db.dbms;

import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public interface DbmsProvider {

	String getProtocol();

	ColumnTypeRegistry createColumnTypeRegistry();

	SQLStructureVisitor createStructureVisitor(DataBaseConnector connector);

	default int getPriority() {
		return 0;
	}

	default boolean supports(final String protocol) {
		return protocol != null && this.getProtocol().equalsIgnoreCase(protocol);
	}

}
