package lu.kbra.pclib.db.dbms;

import java.util.Map;

import lu.kbra.pclib.db.connector.impl.DataBaseConnectorFactory;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.query.SQLQueryVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public interface DbmsProvider {

	ColumnTypeRegistry createColumnTypeRegistry();

	default DataBaseConnectorFactory createConnectorFactory(final Map<String, Object> properties) {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support connector creation.");
	}

	SQLQueryVisitor createQueryVisitor();

	SQLStructureVisitor createStructureVisitor();

	default int getPriority() {
		return 0;
	}

	String getProtocol();

	default boolean supports(final String protocol) {
		return protocol != null && this.getProtocol().equalsIgnoreCase(protocol);
	}

}
