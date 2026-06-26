package lu.kbra.pclib.db.config.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.connector.DataBaseConnectorFactory;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.dbms.DbmsProvider;
import lu.kbra.pclib.db.dbms.DbmsProviders;
import lu.kbra.pclib.db.query.SQLQueryVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public class SpringDbmsProviders {

	private final List<DbmsProvider> providers = new ArrayList<>();

	public SpringDbmsProviders(final Collection<DbmsProvider> beanProviders) {
		if (beanProviders != null) {
			this.providers.addAll(beanProviders);
			beanProviders.forEach(DbmsProviders::registerProvider);
		}

		final ServiceLoader<DbmsProvider> loader = ServiceLoader.load(DbmsProvider.class);
		for (final DbmsProvider provider : loader) {
			this.providers.add(provider);
		}

		this.providers.sort(Comparator.comparingInt(DbmsProvider::getPriority).reversed());
	}

	public ColumnTypeRegistry columnTypeRegistryFor(final String protocol) {
		return this.findRequired(protocol).createColumnTypeRegistry();
	}

	public DataBaseConnectorFactory connectorFactoryFor(final String protocol, final java.util.Map<String, Object> properties) {
		return this.findRequired(protocol).createConnectorFactory(properties);
	}

	public DbmsProvider find(final String protocol) {
		if (protocol == null || protocol.isBlank()) {
			return null;
		}
		return this.providers.stream().filter(provider -> provider.supports(protocol)).findFirst().orElse(null);
	}

	public DbmsProvider findRequired(final String protocol) {
		final DbmsProvider provider = this.find(protocol);
		if (provider == null) {
			throw new IllegalArgumentException("No DBMS provider registered for protocol: " + protocol);
		}
		return provider;
	}

	public List<DbmsProvider> getProviders() {
		return List.copyOf(this.providers);
	}

	public SQLQueryVisitor queryVisitorFor(final DataBaseConnector connector) {
		return this.findRequired(connector.getProtocol()).createQueryVisitor();
	}

	public SQLStructureVisitor structureVisitorFor(final DataBaseConnector connector) {
		return this.findRequired(connector.getProtocol()).createStructureVisitor();
	}

}
