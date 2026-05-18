package lu.kbra.pclib.db.dbms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public final class DbmsProviders {

	private static final CopyOnWriteArrayList<DbmsProvider> PROGRAMMATIC_PROVIDERS = new CopyOnWriteArrayList<>();

	private DbmsProviders() {
	}

	public static ColumnTypeRegistry columnTypeRegistryFor(final String protocol) {
		return findRequired(protocol).createColumnTypeRegistry();
	}

	public static SQLStructureVisitor structureVisitorFor(final DataBaseConnector connector) {
		return findRequired(connector.getProtocol()).createStructureVisitor(connector);
	}

	public static DbmsProvider findRequired(final String protocol) {
		final DbmsProvider provider = find(protocol);
		if (provider == null) {
			throw new IllegalArgumentException("No DBMS provider registered for protocol: " + protocol);
		}
		return provider;
	}

	public static DbmsProvider find(final String protocol) {
		if (protocol == null || protocol.trim().isEmpty()) {
			return null;
		}

		final List<DbmsProvider> providers = providers();
		providers.sort(new Comparator<DbmsProvider>() {
			@Override
			public int compare(final DbmsProvider a, final DbmsProvider b) {
				return Integer.compare(b.getPriority(), a.getPriority());
			}
		});

		return providers.stream().filter(provider -> provider.supports(protocol)).findFirst().orElse(null);
	}

	public static void registerProvider(final DbmsProvider provider) {
		if (provider != null && !PROGRAMMATIC_PROVIDERS.contains(provider)) {
			PROGRAMMATIC_PROVIDERS.add(provider);
		}
	}

	public static List<DbmsProvider> providers() {
		final List<DbmsProvider> providers = new ArrayList<>(PROGRAMMATIC_PROVIDERS);
		final ServiceLoader<DbmsProvider> loader = ServiceLoader.load(DbmsProvider.class);
		for (final DbmsProvider provider : loader) {
			providers.add(provider);
		}
		return providers;
	}

}
