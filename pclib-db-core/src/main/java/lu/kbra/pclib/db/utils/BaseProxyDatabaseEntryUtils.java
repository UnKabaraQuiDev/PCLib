package lu.kbra.pclib.db.utils;

import java.util.Map;

import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider;
import lu.kbra.pclib.db.utils.impl.ProxyDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.SQLColumnTypeProvider;
import lu.kbra.pclib.db.utils.impl.SQLQueryFunctionProvider;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseProxyDatabaseEntryUtils extends BaseDatabaseEntryUtils implements ProxyDatabaseEntryUtils {

	protected SQLQueryFunctionProvider queryFunctionProvider;

	public BaseProxyDatabaseEntryUtils(final String protocol) {
		super(protocol);
		this.queryFunctionProvider = new DefaultSQLQueryFunctionProvider(this);
	}

	public BaseProxyDatabaseEntryUtils(final ColumnTypeRegistry typeRegistry, final String protocolName) {
		super(typeRegistry, protocolName);
		this.queryFunctionProvider = new DefaultSQLQueryFunctionProvider(this);
	}

	public BaseProxyDatabaseEntryUtils(
			final ColumnTypeRegistry typeRegistry,
			final String protocol,
			final SQLStructureVisitor structureVisitor,
			final SQLFunctionResolver functionResolver) {
		super(typeRegistry, protocol, structureVisitor, functionResolver);
		this.queryFunctionProvider = new DefaultSQLQueryFunctionProvider(this);
	}

	public BaseProxyDatabaseEntryUtils(
			final String protocolName,
			final HintScanner hintScanner,
			final SQLColumnTypeProvider columnTypeProvider,
			final EntryInstanceProvider entryInstanceProvider,
			final SQLFunctionResolver functionResolver,
			final SQLStructureVisitor structureVisitor,
			final Map<String, Object> options,
			final SQLQueryFunctionProvider queryFunctionProvider) {
		super(protocolName, hintScanner, columnTypeProvider, entryInstanceProvider, functionResolver, structureVisitor, options);
		this.queryFunctionProvider = queryFunctionProvider;
	}

}
