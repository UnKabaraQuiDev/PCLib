package lu.kbra.pclib.db.utils;

import java.util.Map;
import java.util.ServiceLoader;

import lu.kbra.pclib.db.dbms.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.dbms.registry.EncodingTypeRegistry;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.query.DefaultQueryFunctionProvider;
import lu.kbra.pclib.db.query.returns.ReturnTypeMapper;
import lu.kbra.pclib.db.utils.impl.ColumnTypeProvider;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider;
import lu.kbra.pclib.db.utils.impl.ProxyDatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.QueryFunctionProvider;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseProxyDatabaseEntryUtils extends BaseDatabaseEntryUtils implements ProxyDatabaseEntryUtils {

	protected QueryFunctionProvider queryFunctionProvider;

	public BaseProxyDatabaseEntryUtils(final String protocol) {
		super(protocol);
		this.queryFunctionProvider = new DefaultQueryFunctionProvider(this);
		this.loadClasspathReturnTypeMappers();
	}

	public BaseProxyDatabaseEntryUtils(
			final ColumnTypeRegistry columnTypeRegistry,
			final EncodingTypeRegistry encodingTypeRegistry,
			final String protocolName) {
		super(columnTypeRegistry, encodingTypeRegistry, protocolName);
		this.queryFunctionProvider = new DefaultQueryFunctionProvider(this);
		this.loadClasspathReturnTypeMappers();
	}

	public BaseProxyDatabaseEntryUtils(String protocol, SQLStructureVisitor structureVisitor, SQLFunctionResolver functionResolver) {
		super(protocol, structureVisitor, functionResolver);
		this.queryFunctionProvider = new DefaultQueryFunctionProvider(this);
		this.loadClasspathReturnTypeMappers();
	}

	public BaseProxyDatabaseEntryUtils(
			final ColumnTypeRegistry columnTypeRegistry,
			final EncodingTypeRegistry encodingTypeRegistry,
			final String protocol,
			final SQLStructureVisitor structureVisitor,
			final SQLFunctionResolver functionResolver) {
		super(columnTypeRegistry, encodingTypeRegistry, protocol, structureVisitor, functionResolver);
		this.queryFunctionProvider = new DefaultQueryFunctionProvider(this);
		this.loadClasspathReturnTypeMappers();
	}

	public BaseProxyDatabaseEntryUtils(
			final String dbmsQualifierName,
			final HintScanner hintScanner,
			final ColumnTypeProvider columnTypeProvider,
			final EntryInstanceProvider entryInstanceProvider,
			final SQLFunctionResolver functionResolver,
			final SQLStructureVisitor structureVisitor,
			final SQLQueryableHookManager queryableHookManager,
			final DatabaseScanner databaseScanner,
			final Map<String, Object> options) {
		super(dbmsQualifierName,
				hintScanner,
				columnTypeProvider,
				entryInstanceProvider,
				functionResolver,
				structureVisitor,
				queryableHookManager,
				databaseScanner,
				options);
		this.queryFunctionProvider = new DefaultQueryFunctionProvider(this);
		this.loadClasspathReturnTypeMappers();
	}

	public BaseProxyDatabaseEntryUtils(
			String dbmsQualifierName,
			HintScanner hintScanner,
			ColumnTypeProvider columnTypeProvider,
			EntryInstanceProvider entryInstanceProvider,
			SQLFunctionResolver functionResolver,
			SQLStructureVisitor structureVisitor,
			SQLQueryableHookManager queryableHookManager,
			DatabaseScanner databaseScanner,
			Map<String, Object> options,
			QueryFunctionProvider queryFunctionProvider) {
		super(dbmsQualifierName,
				hintScanner,
				columnTypeProvider,
				entryInstanceProvider,
				functionResolver,
				structureVisitor,
				queryableHookManager,
				databaseScanner,
				options);
		this.queryFunctionProvider = queryFunctionProvider;
	}

	protected void loadClasspathReturnTypeMappers() {
		final DefaultQueryFunctionProvider f = (DefaultQueryFunctionProvider) this.queryFunctionProvider;
		ServiceLoader.load(ReturnTypeMapper.class).forEach(t -> {
			if (!t.shouldRegister()) {
				return;
			}

			f.registerReturnTypeMapper(t);
		});
	}

}
