package lu.kbra.pclib.db.dbms;

import java.util.Map;

import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.query.SQLQueryVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public interface DbmsProvider {

	ColumnTypeRegistry createColumnTypeRegistry();

	default DataBaseConnectorFactory createConnectorFactory(final Map<String, Object> properties) {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support connector creation.");
	}

	SQLQueryVisitor createQueryVisitor();

	// TODO: remove connector from here
	SQLStructureVisitor createStructureVisitor(DataBaseConnector connector);

	default int getPriority() {
		return 0;
	}

	String getProtocol();

	default boolean supports(final String protocol) {
		return protocol != null && this.getProtocol().equalsIgnoreCase(protocol);
	}

}
