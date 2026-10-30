package lu.kbra.pclib.db.registrar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.Set;
import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;

import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.DatabaseEntry.ReadOnlyDatabaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.ProxySQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DBTableProxy;
import lu.kbra.pclib.db.table.DatabaseTable;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.pclib.db.table.ProxyDatabaseTable;
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.db.view.DatabaseView;
import lu.kbra.pclib.db.view.DeferredDatabaseView;
import lu.kbra.pclib.db.view.ProxyDatabaseView;

import lombok.Setter;

public class ReflectionRegistrar implements RuntimeHintsRegistrar, /* ImportBeanDefinitionRegistrar, */ResourceLoaderAware {

	private static final String INCLUDE_RESOURCE = "META-INF/lu/kbra/pclib/db/graal-vm-include";

	@Setter
	private ResourceLoader resourceLoader;

	@Override
	public void registerHints(final RuntimeHints hints, @Nullable final ClassLoader classLoader) {
		this.forAllInclEntries(bd -> {
			final String className = bd.getBeanClassName();

			if (className == null) {
				return;
			}

			try {
				final Class<?> type = Class.forName(className, false, classLoader);

				if (type.equals(DeferredSQLQueryable.class) || type.equals(DeferredDatabaseView.class)
						|| type.equals(DeferredDatabaseTable.class) || type.equals(AbstractDBView.class)
						|| type.equals(AbstractDBTable.class) || type.equals(ProxyDatabaseView.class)
						|| type.equals(ProxyDatabaseTable.class) || type.equals(SQLQueryable.class) || type.equals(ProxySQLQueryable.class)
						|| type.equals(DatabaseView.class) || type.equals(DatabaseTable.class) || type.equals(DBTableProxy.class)
						|| type.equals(DatabaseTable.class) /* || type.equals(SyntheticSQLQueryable.class) */
						|| type.equals(DatabaseEntry.class) || type.equals(ReadOnlyDatabaseEntry.class)) {
					return;
				}

				if (SQLQueryable.class.isAssignableFrom(type)) {
					hints.reflection()
							.registerType(type, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
				} else if (DatabaseEntry.class.isAssignableFrom(type)) {
					hints.reflection()
							.registerType(type,
									MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
									MemberCategory.INVOKE_DECLARED_METHODS,
									MemberCategory.ACCESS_DECLARED_FIELDS);
				}
			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException("Could not load " + className, e);
			}
		});

		try {
			final Enumeration<URL> resources = classLoader.getResources(ReflectionRegistrar.INCLUDE_RESOURCE);

			while (resources.hasMoreElements()) {
				final URL resource = resources.nextElement();

				try (final BufferedReader reader = new BufferedReader(
						new InputStreamReader(resource.openStream(), StandardCharsets.UTF_8))) {

					String line;
					while ((line = reader.readLine()) != null) {
						line = line.trim();

						if (line.isEmpty() || line.startsWith("#")) {
							continue;
						}

						final String[] parts = line.split("\\s+");
						final String name = parts[0];

						if (parts.length == 1) {
							hints.resources().registerResourceBundle(name);
							continue;
						}

						final Set<MemberCategory> categories = EnumSet.noneOf(MemberCategory.class);

						for (int i = 1; i < parts.length; i++) {
							try {
								categories.add(MemberCategory.valueOf(parts[i]));
							} catch (final IllegalArgumentException e) {
								throw new IllegalArgumentException(
										"Unknown MemberCategory '" + parts[i] + "' in GraalVM include: " + resource + ": " + line,
										e);
							}
						}

						hints.reflection().registerType(TypeReference.of(name), categories.toArray(new MemberCategory[0]));
					}
				}
			}
		} catch (final IOException e) {
			throw new IllegalStateException("Failed to load GraalVM includes from classpath", e);
		}
	}

	protected void forAllInclEntries(final Consumer<BeanDefinition> consumer) {
		final Environment environment = new StandardEnvironment();

		final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false, environment) {

			@Override
			protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
				if (super.isCandidateComponent(beanDefinition)) {
					return true;
				}

				final AnnotationMetadata metadata = beanDefinition.getMetadata();

				final boolean isSpringComponent = metadata.hasAnnotation("org.springframework.stereotype.Component")
						|| metadata.hasMetaAnnotation("org.springframework.stereotype.Component");

				try {
					final Class<?> clazz = Class.forName(metadata.getClassName());
					return isSpringComponent && SQLQueryable.class.isAssignableFrom(clazz) || DatabaseEntry.class.isAssignableFrom(clazz);
				} catch (final ClassNotFoundException e) {
					return false;
				}
			}
		};

		scanner.setResourceLoader(this.resourceLoader);

		scanner.addIncludeFilter(new AssignableTypeFilter(SQLQueryable.class));
		scanner.addIncludeFilter(new AssignableTypeFilter(DatabaseEntry.class));

		for (final BeanDefinition bd : scanner.findCandidateComponents("**")) {
			consumer.accept(bd);
		}
	}

}
