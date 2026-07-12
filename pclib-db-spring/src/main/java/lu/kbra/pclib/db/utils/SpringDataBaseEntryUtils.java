package lu.kbra.pclib.db.utils;

import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public class SpringDataBaseEntryUtils extends BaseProxyDataBaseEntryUtils {

	public SpringDataBaseEntryUtils(
			final ColumnTypeRegistry typeRegistry,
			final String protocol,
			final SQLStructureVisitor structureVisitor,
			final SQLFunctionResolver functionResolver) {
		super(typeRegistry, protocol, structureVisitor, functionResolver);
	}

	public SpringDataBaseEntryUtils(final ColumnTypeRegistry typeRegistry, final String protocolName) {
		super(typeRegistry, protocolName);
	}

	public SpringDataBaseEntryUtils(final String protocol) {
		super(protocol);
	}

}
