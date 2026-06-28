package lu.kbra.pclib.db.dbms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CopyOnWriteArrayList;

import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public final class DbmsProviders {

	private static final CopyOnWriteArrayList<DbmsProvider> PROGRAMMATIC_PROVIDERS = new CopyOnWriteArrayList<>();

	public static ColumnTypeRegistry columnTypeRegistryFor(final String protocol) {
		return DbmsProviders.findRequired(protocol).createColumnTypeRegistry();
	}

	public static DbmsProvider find(final String protocol) {
		if (protocol == null || protocol.trim().isEmpty()) {
			return null;
		}

		final List<DbmsProvider> providers = DbmsProviders.providers();
		providers.sort(Comparator.comparing(DbmsProvider::getPriority).reversed());

		return providers.stream().filter(provider -> provider.supports(protocol)).findFirst().orElse(null);
	}

	public static DbmsProvider findRequired(final String protocol) {
		final DbmsProvider provider = DbmsProviders.find(protocol);
		if (provider == null) {
			throw new IllegalArgumentException("No DBMS provider registered for protocol: " + protocol);
		}
		return provider;
	}

	public static List<DbmsProvider> providers() {
		final List<DbmsProvider> providers = new ArrayList<>(DbmsProviders.PROGRAMMATIC_PROVIDERS);
		final ServiceLoader<DbmsProvider> loader = ServiceLoader.load(DbmsProvider.class);
		for (final DbmsProvider provider : loader) {
			providers.add(provider);
		}
		return providers;
	}

	public static void registerProvider(final DbmsProvider provider) {
		if (provider != null && !DbmsProviders.PROGRAMMATIC_PROVIDERS.contains(provider)) {
			DbmsProviders.PROGRAMMATIC_PROVIDERS.add(provider);
		}
	}

	public static SQLStructureVisitor structureVisitorFor(final String protocol) {
		return DbmsProviders.findRequired(protocol).createStructureVisitor();
	}

	private DbmsProviders() {
	}

}
