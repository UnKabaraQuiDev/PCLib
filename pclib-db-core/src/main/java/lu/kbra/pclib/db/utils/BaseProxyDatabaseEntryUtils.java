package lu.kbra.pclib.db.utils;

import java.util.Map;

import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider;
import lu.kbra.pclib.db.utils.impl.ProxyDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.SQLColumnTypeProvider;
import lu.kbra.pclib.db.utils.impl.SQLEncodingTypeProvider;
import lu.kbra.pclib.db.utils.impl.SQLQueryFunctionProvider;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

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

	public BaseProxyDatabaseEntryUtils(
			final ColumnTypeRegistry columnTypeRegistry,
			final EncodingTypeRegistry encodingTypeRegistry,
			final String protocolName) {
		super(columnTypeRegistry, encodingTypeRegistry, protocolName);
		this.queryFunctionProvider = new DefaultSQLQueryFunctionProvider(this);
	}

	public BaseProxyDatabaseEntryUtils(
			final ColumnTypeRegistry columnTypeRegistry,
			final EncodingTypeRegistry encodingTypeRegistry,
			final String protocol,
			final SQLStructureVisitor structureVisitor,
			final SQLFunctionResolver functionResolver) {
		super(columnTypeRegistry, encodingTypeRegistry, protocol, structureVisitor, functionResolver);
		this.queryFunctionProvider = new DefaultSQLQueryFunctionProvider(this);
	}

	public BaseProxyDatabaseEntryUtils(
			String dbmsQualifierName,
			HintScanner hintScanner,
			SQLColumnTypeProvider columnTypeProvider,
			SQLEncodingTypeProvider encodingTypeProvider,
			EntryInstanceProvider entryInstanceProvider,
			SQLFunctionResolver functionResolver,
			SQLStructureVisitor structureVisitor,
			SQLQueryableHookManager queryableHookManager,
			Map<String, Object> options) {
		super(dbmsQualifierName,
				hintScanner,
				columnTypeProvider,
				encodingTypeProvider,
				entryInstanceProvider,
				functionResolver,
				structureVisitor,
				queryableHookManager,
				options);
	}

}
