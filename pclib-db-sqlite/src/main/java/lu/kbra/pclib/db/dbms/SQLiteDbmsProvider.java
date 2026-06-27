package lu.kbra.pclib.db.dbms;

import java.util.Locale;
import java.util.Map;

import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.sqlite.SQLiteStructureVisitor;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.connector.SQLiteDataBaseConnector;
import lu.kbra.pclib.db.query.SQLQueryVisitor;
import lu.kbra.pclib.db.query.SQLiteQueryVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.SQLiteColumnTypeRegistry;

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
	public DataBaseConnectorFactory createConnectorFactory(final Map<String, Object> properties) {
		final SQLiteDataBaseConnector connector = new SQLiteDataBaseConnector();
		connector.dirPath = SQLiteDbmsProvider.string(properties, "dirPath", ".");
		return connector::clone;
	}

	@Override
	public SQLQueryVisitor createQueryVisitor() {
		return new SQLiteQueryVisitor();
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
