package lu.kbra.pclib.db.dbms;

import java.util.Locale;
import java.util.Map;

import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;
import lu.kbra.pclib.db.connector.impl.DataBaseConnectorFactory;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

public class MySQLDbmsProvider implements DbmsProvider {

	public static final String DBMS_QUALIFIER_NAME = "mysql";

	public static final String DEFAULT_CHARACTER_SET = "utf8mb4";
	public static final String DEFAULT_COLLATION = "utf8mb4_general_ci";
	public static final String DEFAULT_ENGINE = "InnoDB";

	private static int integer(final Map<String, Object> properties, final String key, final int fallback) {
		final Object value = MySQLDbmsProvider.value(properties, key);
		if (value == null) {
			return fallback;
		}
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		return Integer.parseInt(String.valueOf(value));
	}

	private static String normalize(final String key) {
		return key == null ? "" : key.replace("-", "").replace("_", "").toLowerCase(Locale.ROOT);
	}

	private static String string(final Map<String, Object> properties, final String key, final String fallback) {
		final Object value = MySQLDbmsProvider.value(properties, key);
		return value == null ? fallback : String.valueOf(value);
	}

	private static Object value(final Map<String, Object> properties, final String key) {
		if (properties.containsKey(key)) {
			return properties.get(key);
		}
		final String normalizedKey = MySQLDbmsProvider.normalize(key);
		for (final Map.Entry<String, Object> entry : properties.entrySet()) {
			if (MySQLDbmsProvider.normalize(entry.getKey()).equals(normalizedKey)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public ColumnTypeRegistry createColumnTypeRegistry() {
		return new MySQLColumnTypeRegistry();
	}

	@Override
	public DataBaseConnectorFactory createConnectorFactory(final Map<String, Object> properties) {
		final MySQLDataBaseConnector connector = new MySQLDataBaseConnector(MySQLDbmsProvider.string(properties, "username", null),
				MySQLDbmsProvider.string(properties, "password", null),
				MySQLDbmsProvider.string(properties, "host", "localhost"),
				MySQLDbmsProvider.integer(properties, "port", MySQLDataBaseConnector.DEFAULT_PORT),
				null);
		return connector::clone;
	}

	@Override
	public SQLFunctionResolver createFunctionResolver() {
		return new MySQLFunctionResolver();
	}

	@Override
	public SQLStructureVisitor createStructureVisitor() {
		return new MySQLStructureVisitor();
	}

	@Override
	public String getProtocol() {
		return MySQLDbmsProvider.DBMS_QUALIFIER_NAME;
	}

}
