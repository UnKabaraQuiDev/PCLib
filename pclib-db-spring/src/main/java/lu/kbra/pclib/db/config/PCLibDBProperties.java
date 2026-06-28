package lu.kbra.pclib.db.config;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PCLibDBProperties {

	@Getter
	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Connector {

		public static Connector from(final String sectionName, final Map<String, Object> raw) {
			final Connector connector = new Connector();
			connector.qualifier = PCLibDBProperties.string(PCLibDBProperties.value(raw, "qualifier"), sectionName);
			connector.protocol = PCLibDBProperties.string(PCLibDBProperties.value(raw, "protocol"), null);
			connector.name = PCLibDBProperties.string(PCLibDBProperties.value(raw, "name"), connector.qualifier);
			connector.exposeConnector = PCLibDBProperties.optionalBool(PCLibDBProperties.value(raw, "exposeConnector"));
			connector.exposeDatabase = PCLibDBProperties.optionalBool(PCLibDBProperties.value(raw, "exposeDatabase"));
			connector.autoCreate = PCLibDBProperties.optionalBool(PCLibDBProperties.value(raw, "autoCreate"));
			connector.autoMigrate = PCLibDBProperties.optionalBool(PCLibDBProperties.value(raw, "autoMigrate"));
			connector.autoAddColumns = PCLibDBProperties.optionalBool(PCLibDBProperties.value(raw, "autoAddColumns"));
			connector.autoRemoveColumns = PCLibDBProperties.optionalBool(PCLibDBProperties.value(raw, "autoRemoveColumns"));
			connector.migrationSchemaName = PCLibDBProperties.string(PCLibDBProperties.value(raw, "migrationSchemaName"), null);

			for (final Map.Entry<String, Object> entry : raw.entrySet()) {
				final String key = entry.getKey();
				if (PCLibDBProperties.isConnectorMetaKey(key)) {
					continue;
				}
				connector.properties.put(key, entry.getValue());
			}
			return connector;
		}

		private String qualifier;
		private String protocol;
		private String name;
		private Boolean exposeConnector;
		private Boolean exposeDatabase;
		private Boolean autoCreate;
		private Boolean autoMigrate;
		private Boolean autoAddColumns;
		private Boolean autoRemoveColumns;
		private String migrationSchemaName;

		private final Map<String, Object> properties = new LinkedHashMap<>();

	}

	private static final Set<String> GLOBAL_KEYS = Set.of("enabled",
			"exposeconnector",
			"exposedatabase",
			"autocreate",
			"automigrate",
			"autoaddcolumns",
			"autoremovecolumns",
			"migrationschemaname",
			"protocol");

	public static PCLibDBProperties bind(final Environment environment) {
		final Map<String, Object> raw = Binder.get(environment)
				.bind("pclib.db", Bindable.mapOf(String.class, Object.class))
				.orElse(Collections.emptyMap());

		return PCLibDBProperties.from(raw);
	}

	@SuppressWarnings("unchecked")
	public static PCLibDBProperties from(final Map<String, Object> raw) {
		final PCLibDBProperties properties = new PCLibDBProperties();

		properties.enabled = PCLibDBProperties.bool(raw.get("enabled"), properties.enabled);
		properties.exposeConnector = PCLibDBProperties.bool(PCLibDBProperties.value(raw, "exposeConnector"), properties.exposeConnector);
		properties.exposeDatabase = PCLibDBProperties.bool(PCLibDBProperties.value(raw, "exposeDatabase"), properties.exposeDatabase);
		properties.autoCreate = PCLibDBProperties.bool(PCLibDBProperties.value(raw, "autoCreate"), properties.autoCreate);
		properties.autoMigrate = PCLibDBProperties.bool(PCLibDBProperties.value(raw, "autoMigrate"), properties.autoMigrate);
		properties.autoAddColumns = PCLibDBProperties.bool(PCLibDBProperties.value(raw, "autoAddColumns"), properties.autoAddColumns);
		properties.autoRemoveColumns = PCLibDBProperties.bool(PCLibDBProperties.value(raw, "autoRemoveColumns"),
				properties.autoRemoveColumns);
		properties.migrationSchemaName = PCLibDBProperties.string(PCLibDBProperties.value(raw, "migrationSchemaName"),
				properties.migrationSchemaName);

		for (final Map.Entry<String, Object> entry : raw.entrySet()) {
			final String key = entry.getKey();
			final Object value = entry.getValue();

			if (PCLibDBProperties.GLOBAL_KEYS.contains(PCLibDBProperties.normalize(key)) || !(value instanceof Map<?, ?>)) {
				continue;
			}

			final Connector connector = Connector.from(key, (Map<String, Object>) value);
			if (connector.getProtocol() != null && !connector.getProtocol().isBlank()) {
				properties.connectors.put(connector.getQualifier(), connector);
			}
		}

		return properties;
	}

	private static boolean bool(final Object value, final boolean fallback) {
		final Boolean parsed = PCLibDBProperties.optionalBool(value);
		return parsed == null ? fallback : parsed;
	}

	private static boolean isConnectorMetaKey(final String key) {
		final String normalized = PCLibDBProperties.normalize(key);
		return Objects.equals(normalized, "qualifier") || Objects.equals(normalized, "protocol") || Objects.equals(normalized, "name")
				|| Objects.equals(normalized, "exposeconnector") || Objects.equals(normalized, "exposedatabase")
				|| Objects.equals(normalized, "autocreate") || Objects.equals(normalized, "automigrate")
				|| Objects.equals(normalized, "autoaddcolumns") || Objects.equals(normalized, "autoremovecolumns")
				|| Objects.equals(normalized, "migrationschemaname");
	}

	private static String normalize(final String key) {
		return key == null ? "" : key.replace("-", "").replace("_", "").toLowerCase(Locale.ROOT);
	}

	private static Boolean optionalBool(final Object value) {
		if (value == null) {
			return null;
		}
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		return Boolean.valueOf(String.valueOf(value));
	}

	private static String string(final Object value, final String fallback) {
		return value == null ? fallback : String.valueOf(value);
	}

	private static Object value(final Map<String, Object> map, final String key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}
		final String normalizedKey = PCLibDBProperties.normalize(key);
		return map.entrySet()
				.stream()
				.filter(entry -> PCLibDBProperties.normalize(entry.getKey()).equals(normalizedKey))
				.map(Map.Entry::getValue)
				.findFirst()
				.orElse(null);
	}

	private boolean enabled = true;
	private boolean exposeConnector = true;
	private boolean exposeDatabase = true;
	private boolean autoCreate = true;
	private boolean autoMigrate = true;
	private boolean autoAddColumns = false;
	private boolean autoRemoveColumns = false;
	private String migrationSchemaName = "pclib_schema_migrations";
	private final Map<String, Connector> connectors = new LinkedHashMap<>();

	public String getMigrationSchemaName(final Connector connector) {
		return connector.getMigrationSchemaName() == null || connector.getMigrationSchemaName().isBlank() ? this.migrationSchemaName
				: connector.getMigrationSchemaName();
	}

	public Connector getRequiredConnector(final String connectorKey) {
		final Connector connector = this.connectors.get(connectorKey);
		if (connector == null) {
			throw new IllegalArgumentException("No PCLib DB connector configuration named: " + connectorKey);
		}
		return connector;
	}

	public boolean isAutoAddColumns(final Connector connector) {
		return connector.getAutoAddColumns() == null ? this.autoAddColumns : connector.getAutoAddColumns();
	}

	public boolean isAutoCreate(final Connector connector) {
		return connector.getAutoCreate() == null ? this.autoCreate : connector.getAutoCreate();
	}

	public boolean isAutoMigrate(final Connector connector) {
		return connector.getAutoMigrate() == null ? this.autoMigrate : connector.getAutoMigrate();
	}

	public boolean isAutoRemoveColumns(final Connector connector) {
		return connector.getAutoRemoveColumns() == null ? this.autoRemoveColumns : connector.getAutoRemoveColumns();
	}

	public boolean isExposeConnector(final Connector connector) {
		return connector.getExposeConnector() == null ? this.exposeConnector : connector.getExposeConnector();
	}

	public boolean isExposeDatabase(final Connector connector) {
		return connector.getExposeDatabase() == null ? this.exposeDatabase : connector.getExposeDatabase();
	}

}
