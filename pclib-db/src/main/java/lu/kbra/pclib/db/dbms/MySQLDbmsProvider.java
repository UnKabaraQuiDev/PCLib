package lu.kbra.pclib.db.dbms;

import java.util.Locale;
import java.util.Map;

import lu.kbra.pclib.db.autobuild.dialect.MySQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.connector.MySQLDataBaseConnector;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.query.SQLQueryVisitor;
import lu.kbra.pclib.db.query.MySQLQueryVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

public class MySQLDbmsProvider implements DbmsProvider {

	@Override
	public String getProtocol() {
		return "mysql";
	}

	@Override
	public ColumnTypeRegistry createColumnTypeRegistry() {
		return new MySQLColumnTypeRegistry();
	}

	@Override
	public SQLStructureVisitor createStructureVisitor(final DataBaseConnector connector) {
		return new MySQLStructureVisitor(connector);
	}

	@Override
	public SQLQueryVisitor createQueryVisitor(final DataBaseConnector connector) {
		return new MySQLQueryVisitor();
	}

	@Override
	public DataBaseConnectorFactory createConnectorFactory(final Map<String, Object> properties) {
		final MySQLDataBaseConnector connector = new MySQLDataBaseConnector();
		connector.host = MySQLDbmsProvider.string(properties, "host", "localhost");
		connector.port = MySQLDbmsProvider.integer(properties, "port", MySQLDataBaseConnector.DEFAULT_PORT);
		connector.username = MySQLDbmsProvider.string(properties, "username", null);
		connector.password = MySQLDbmsProvider.string(properties, "password", null);
		connector.characterSet = MySQLDbmsProvider.string(properties, "characterSet", MySQLDataBaseConnector.DEFAULT_CHARSET);
		connector.collation = MySQLDbmsProvider.string(properties, "collation", MySQLDataBaseConnector.DEFAULT_COLLATION);
		connector.engine = MySQLDbmsProvider.string(properties, "engine", MySQLDataBaseConnector.DEFAULT_ENGINE);
		return connector::clone;
	}

	private static String string(final Map<String, Object> properties, final String key, final String fallback) {
		final Object value = MySQLDbmsProvider.value(properties, key);
		return value == null ? fallback : String.valueOf(value);
	}

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

	private static String normalize(final String key) {
		return key == null ? "" : key.replace("-", "").replace("_", "").toLowerCase(Locale.ROOT);
	}

}
