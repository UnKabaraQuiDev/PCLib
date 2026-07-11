package lu.kbra.pclib.db.utils;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider;
import lu.kbra.pclib.db.utils.impl.ProxyDataBaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.SQLColumnTypeProvider;
import lu.kbra.pclib.db.utils.impl.SQLQueryFunctionProvider;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

@Getter
@Setter
public class BaseProxyDataBaseEntryUtils extends BaseDataBaseEntryUtils implements ProxyDataBaseEntryUtils {

	protected SQLQueryFunctionProvider queryFunctionProvider;

	public BaseProxyDataBaseEntryUtils(final String protocol) {
		super(protocol);
		queryFunctionProvider = new DefaultSQLQueryFunctionProvider(this);
	}

	public BaseProxyDataBaseEntryUtils(final ColumnTypeRegistry typeRegistry, final String protocolName) {
		super(typeRegistry, protocolName);
		queryFunctionProvider = new DefaultSQLQueryFunctionProvider(this);
	}

	public BaseProxyDataBaseEntryUtils(
			ColumnTypeRegistry typeRegistry,
			String protocol,
			SQLStructureVisitor structureVisitor,
			SQLFunctionResolver functionResolver) {
		super(typeRegistry, protocol, structureVisitor, functionResolver);
		queryFunctionProvider = new DefaultSQLQueryFunctionProvider(this);
	}

	public BaseProxyDataBaseEntryUtils(
			String protocolName,
			HintScanner hintScanner,
			SQLColumnTypeProvider columnTypeProvider,
			EntryInstanceProvider entryInstanceProvider,
			SQLFunctionResolver functionResolver,
			SQLStructureVisitor structureVisitor,
			Map<String, Object> options,
			SQLQueryFunctionProvider queryFunctionProvider) {
		super(protocolName, hintScanner, columnTypeProvider, entryInstanceProvider, functionResolver, structureVisitor, options);
		this.queryFunctionProvider = queryFunctionProvider;
	}

}
