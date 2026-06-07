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

public class PCLibDBProperties {

	public static class Connector {

		public static Connector from(final String sectionName, final Map<String, Object> raw) {
			final Connector connector = new Connector();
			connector.qualifier = PCLibDBProperties.string(PCLibDBProperties.value(raw, "qualifier"), sectionName);
			connector.protocol = PCLibDBProperties.string(PCLibDBProperties.value(raw, "protocol"), null);
			connector.name = PCLibDBProperties.string(PCLibDBProperties.value(raw, "name"), connector.qualifier);
			connector.exposeConnector = PCLibDBProperties.optionalBool(PCLibDBProperties.value(raw, "exposeConnector"));
			connector.exposeDatabase = PCLibDBProperties.optionalBool(PCLibDBProperties.value(raw, "exposeDatabase"));
			connector.autoCreate = PCLibDBProperties.optionalBool(PCLibDBProperties.value(raw, "autoCreate"));

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

		private final Map<String, Object> properties = new LinkedHashMap<>();

		public Boolean getAutoCreate() {
			return this.autoCreate;
		}

		public Boolean getExposeConnector() {
			return this.exposeConnector;
		}

		public Boolean getExposeDatabase() {
			return this.exposeDatabase;
		}

		public String getName() {
			return this.name;
		}

		public Map<String, Object> getProperties() {
			return this.properties;
		}

		public String getProtocol() {
			return this.protocol;
		}

		public String getQualifier() {
			return this.qualifier;
		}

		public void setAutoCreate(final Boolean autoCreate) {
			this.autoCreate = autoCreate;
		}

		public void setExposeConnector(final Boolean exposeConnector) {
			this.exposeConnector = exposeConnector;
		}

		public void setExposeDatabase(final Boolean exposeDatabase) {
			this.exposeDatabase = exposeDatabase;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public void setProtocol(final String protocol) {
			this.protocol = protocol;
		}

		public void setQualifier(final String qualifier) {
			this.qualifier = qualifier;
		}

		@Override
		public String toString() {
			return "Connector@" + System.identityHashCode(this) + " [qualifier=" + this.qualifier + ", protocol=" + this.protocol
					+ ", name=" + this.name + ", exposeConnector=" + this.exposeConnector + ", exposeDatabase=" + this.exposeDatabase
					+ ", autoCreate=" + this.autoCreate + ", properties=" + this.properties + "]";
		}

	}

	private static final Set<String> GLOBAL_KEYS = Set.of("enabled", "expose-connector", "expose-database", "auto-create", "protocol");

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

		for (final Map.Entry<String, Object> entry : raw.entrySet()) {
			final String key = entry.getKey();
			final Object value = entry.getValue();

			if (PCLibDBProperties.GLOBAL_KEYS.contains(key) || !(value instanceof Map<?, ?>)) {
				continue;
			}

			final Connector connector = Connector.from(key, (Map<String, Object>) value);
			if (connector.getProtocol() != null && !connector.getProtocol().isBlank()) {
				properties.connectors.put(key, connector);
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
				|| Objects.equals(normalized, "autocreate");
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

	private final Map<String, Connector> connectors = new LinkedHashMap<>();

	public Map<String, Connector> getConnectors() {
		return this.connectors;
	}

	@Deprecated
	public String getProtocol() {
		return this.connectors.size() == 1 ? this.connectors.values().iterator().next().getProtocol() : null;
	}

	public Connector getRequiredConnector(final String name) {
		final Connector connector = this.connectors.get(name);
		if (connector == null) {
			throw new IllegalArgumentException("No PCLib DB connector configuration named: " + name);
		}
		return connector;
	}

	public boolean isAutoCreate() {
		return this.autoCreate;
	}

	public boolean isAutoCreate(final Connector connector) {
		return connector.getAutoCreate() == null ? this.autoCreate : connector.getAutoCreate();
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public boolean isExposeConnector() {
		return this.exposeConnector;
	}

	public boolean isExposeConnector(final Connector connector) {
		return connector.getExposeConnector() == null ? this.exposeConnector : connector.getExposeConnector();
	}

	public boolean isExposeDatabase() {
		return this.exposeDatabase;
	}

	public boolean isExposeDatabase(final Connector connector) {
		return connector.getExposeDatabase() == null ? this.exposeDatabase : connector.getExposeDatabase();
	}

	public void setAutoCreate(final boolean autoCreate) {
		this.autoCreate = autoCreate;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public void setExposeConnector(final boolean exposeConnector) {
		this.exposeConnector = exposeConnector;
	}

	public void setExposeDatabase(final boolean exposeDatabase) {
		this.exposeDatabase = exposeDatabase;
	}

	@Override
	public String toString() {
		return "PCLibDBProperties@" + System.identityHashCode(this) + " [enabled=" + this.enabled + ", exposeConnector="
				+ this.exposeConnector + ", exposeDatabase=" + this.exposeDatabase + ", autoCreate=" + this.autoCreate + ", connectors="
				+ this.connectors + "]";
	}

}
