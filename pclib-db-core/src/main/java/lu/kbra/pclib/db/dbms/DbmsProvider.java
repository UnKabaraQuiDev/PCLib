package lu.kbra.pclib.db.dbms;

import java.util.Map;

import lu.kbra.pclib.db.connector.impl.DatabaseConnectorFactory;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public interface DbmsProvider {

	ColumnTypeRegistry createColumnTypeRegistry();

	default DatabaseConnectorFactory createConnectorFactory(final Map<String, Object> properties) {
		throw new UnsupportedOperationException(this.getClass().getName() + " does not support connector creation.");
	}

	SQLFunctionResolver createFunctionResolver();

	SQLStructureVisitor createStructureVisitor();

	default int getPriority() {
		return 0;
	}

	String getProtocol();

	default boolean supports(final String protocol) {
		return protocol != null && this.getProtocol().equalsIgnoreCase(protocol);
	}

}
