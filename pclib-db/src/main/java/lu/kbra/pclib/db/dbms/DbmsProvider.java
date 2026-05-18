package lu.kbra.pclib.db.dbms;

import java.util.Map;

import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public interface DbmsProvider {

	String getProtocol();

	ColumnTypeRegistry createColumnTypeRegistry();

	SQLStructureVisitor createStructureVisitor(DataBaseConnector connector);

	default DataBaseConnectorFactory createConnectorFactory(final Map<String, Object> properties) {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support connector creation.");
	}

	default int getPriority() {
		return 0;
	}

	default boolean supports(final String protocol) {
		return protocol != null && this.getProtocol().equalsIgnoreCase(protocol);
	}

}
