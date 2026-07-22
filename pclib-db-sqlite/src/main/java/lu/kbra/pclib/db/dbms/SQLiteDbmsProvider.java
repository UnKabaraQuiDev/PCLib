package lu.kbra.pclib.db.dbms;

import java.util.Locale;
import java.util.Map;

import lu.kbra.pclib.db.connector.SQLiteDatabaseConnector;
import lu.kbra.pclib.db.connector.impl.DatabaseConnectorFactory;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;
import lu.kbra.pclib.db.utils.registry.SQLiteColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.SQLiteEncodingTypeRegistry;

public class SQLiteDbmsProvider implements DbmsProvider {

	public static final String DBMS_QUALIFIER_NAME = "sqlite";

	private static String normalize(final String key) {
		return key == null ? "" : key.replace("-", "").replace("_", "").toLowerCase(Locale.ROOT);
	}

	private static String string(final Map<String, Object> properties, final String key, final String fallback) {
		final Object value = SQLiteDbmsProvider.value(properties, key);
		return value == null ? fallback : String.valueOf(value);
	}

	private static Object value(final Map<String, Object> properties, final String key) {
		if (properties.containsKey(key)) {
			return properties.get(key);
		}
		final String normalizedKey = SQLiteDbmsProvider.normalize(key);
		for (final Map.Entry<String, Object> entry : properties.entrySet()) {
			if (SQLiteDbmsProvider.normalize(entry.getKey()).equals(normalizedKey)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public ColumnTypeRegistry createColumnTypeRegistry() {
		return new SQLiteColumnTypeRegistry();
	}

	@Override
	public EncodingTypeRegistry createEncodingTypeRegistry() {
		return new SQLiteEncodingTypeRegistry();
	}

	@Override
	public DatabaseConnectorFactory createConnectorFactory(final Map<String, Object> properties) {
		final SQLiteDatabaseConnector connector = new SQLiteDatabaseConnector();
		connector.dirPath = SQLiteDbmsProvider.string(properties, "dirPath", ".");
		return connector::clone;
	}

	@Override
	public SQLFunctionResolver createFunctionResolver() {
		return new SQLiteFunctionResolver();
	}

	@Override
	public SQLStructureVisitor createStructureVisitor() {
		return new SQLiteStructureVisitor();
	}

	@Override
	public String getProtocol() {
		return SQLiteDbmsProvider.DBMS_QUALIFIER_NAME;
	}

}
