package lu.kbra.pclib.db.dbms;

import java.util.Locale;
import java.util.Map;

import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.autobuild.postgres.PostgreSQLStructureVisitor;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.connector.PostgreSQLDataBaseConnector;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.query.PostgreSQLQueryVisitor;
import lu.kbra.pclib.db.query.SQLQueryVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;
import lu.kbra.pclib.db.utils.registry.PostgreSQLColumnTypeRegistry;

public class PostgreSQLDbmsProvider implements DbmsProvider {

	public static final String DBMS_QUALIFIER_NAME = "postgres";

	public static final String DEFAULT_MAINTENANCE_DATABASE = "postgres";

	private static int integer(final Map<String, Object> properties, final String key, final int fallback) {
		final Object value = PostgreSQLDbmsProvider.value(properties, key);
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
		final Object value = PostgreSQLDbmsProvider.value(properties, key);
		return value == null ? fallback : String.valueOf(value);
	}

	private static Object value(final Map<String, Object> properties, final String key) {
		if (properties.containsKey(key)) {
			return properties.get(key);
		}
		final String normalizedKey = PostgreSQLDbmsProvider.normalize(key);
		for (final Map.Entry<String, Object> entry : properties.entrySet()) {
			if (PostgreSQLDbmsProvider.normalize(entry.getKey()).equals(normalizedKey)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public ColumnTypeRegistry createColumnTypeRegistry() {
		return new PostgreSQLColumnTypeRegistry();
	}

	@Override
	public DataBaseConnectorFactory createConnectorFactory(final Map<String, Object> properties) {
		final PostgreSQLDataBaseConnector connector = new PostgreSQLDataBaseConnector(
				PostgreSQLDbmsProvider.string(properties, "username", null),
				PostgreSQLDbmsProvider.string(properties, "password", null),
				PostgreSQLDbmsProvider.string(properties, "host", "localhost"),
				PostgreSQLDbmsProvider.integer(properties, "port", PostgreSQLDataBaseConnector.DEFAULT_PORT));
		return connector::clone;
	}

	@Override
	public SQLQueryVisitor createQueryVisitor() {
		return new PostgreSQLQueryVisitor();
	}

	@Override
	public SQLStructureVisitor createStructureVisitor(final DataBaseConnector connector) {
		return new PostgreSQLStructureVisitor(connector);
	}

	@Override
	public String getProtocol() {
		return PostgreSQLDbmsProvider.DBMS_QUALIFIER_NAME;
	}

}
