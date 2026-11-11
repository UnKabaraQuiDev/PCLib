package lu.kbra.pclib.db.dbms;

import java.util.Map;

import lu.kbra.pclib.db.connector.impl.DatabaseConnectorFactory;
import lu.kbra.pclib.db.dbms.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.dbms.registry.EncodingTypeRegistry;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;

public interface DbmsProvider {

	@Deprecated
	ColumnTypeRegistry createColumnTypeRegistry();

	@Deprecated
	EncodingTypeRegistry createEncodingTypeRegistry();

	DatabaseConnectorFactory createConnectorFactory(final Map<String, Object> properties);

	SQLFunctionResolver createFunctionResolver();

	SQLStructureVisitor createStructureVisitor();

	default int getPriority() {
		return 0;
	}

	String getProtocol();

	default boolean supports(final String protocol) {
		return this.getProtocol().equalsIgnoreCase(protocol);
	}

}
