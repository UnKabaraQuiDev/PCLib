package lu.kbra.pclib.db.utils;

import java.util.Map;

import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.query.DefaultQueryFunctionProvider;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider;
import lu.kbra.pclib.db.utils.impl.ProxyDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.QueryFunctionProvider;
import lu.kbra.pclib.db.utils.impl.SQLColumnTypeProvider;
import lu.kbra.pclib.db.utils.impl.SQLEncodingTypeProvider;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseProxyDatabaseEntryUtils extends BaseDatabaseEntryUtils implements ProxyDatabaseEntryUtils {

	protected QueryFunctionProvider queryFunctionProvider;

	public BaseProxyDatabaseEntryUtils(final String protocol) {
		super(protocol);
		this.queryFunctionProvider = new DefaultQueryFunctionProvider(this);
	}

	public BaseProxyDatabaseEntryUtils(
			final ColumnTypeRegistry columnTypeRegistry,
			final EncodingTypeRegistry encodingTypeRegistry,
			final String protocolName) {
		super(columnTypeRegistry, encodingTypeRegistry, protocolName);
		this.queryFunctionProvider = new DefaultQueryFunctionProvider(this);
	}

	public BaseProxyDatabaseEntryUtils(
			final ColumnTypeRegistry columnTypeRegistry,
			final EncodingTypeRegistry encodingTypeRegistry,
			final String protocol,
			final SQLStructureVisitor structureVisitor,
			final SQLFunctionResolver functionResolver) {
		super(columnTypeRegistry, encodingTypeRegistry, protocol, structureVisitor, functionResolver);
		this.queryFunctionProvider = new DefaultQueryFunctionProvider(this);
	}

	public BaseProxyDatabaseEntryUtils(
			final String dbmsQualifierName,
			final HintScanner hintScanner,
			final SQLEncodingTypeProvider encodingTypeProvider,
			final SQLColumnTypeProvider columnTypeProvider,
			final EntryInstanceProvider entryInstanceProvider,
			final SQLFunctionResolver functionResolver,
			final SQLStructureVisitor structureVisitor,
			final SQLQueryableHookManager queryableHookManager,
			final DatabaseScanner databaseScanner,
			final Map<String, Object> options) {
		super(dbmsQualifierName,
				hintScanner,
				encodingTypeProvider,
				columnTypeProvider,
				entryInstanceProvider,
				functionResolver,
				structureVisitor,
				queryableHookManager,
				databaseScanner,
				options);
	}

}
