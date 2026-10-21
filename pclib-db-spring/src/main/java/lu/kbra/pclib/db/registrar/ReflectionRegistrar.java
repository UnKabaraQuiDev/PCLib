package lu.kbra.pclib.db.registrar;

import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
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

//	private boolean AOT_PROCESSING = false;

	@Setter
	private ResourceLoader resourceLoader;

	@Override
	public void registerHints(final RuntimeHints hints, @Nullable final ClassLoader classLoader) {
		this.forAllInclEntries(bd -> {
			final String className = bd.getBeanClassName();

			if (className == null) {
				System.out.println("classname null: " + bd);
				return;
			}

			try {
				final Class<?> type = Class.forName(className, false, classLoader);

				if (type.equals(DeferredSQLQueryable.class) || type.equals(DeferredDatabaseView.class)
						|| type.equals(DeferredDatabaseTable.class) || type.equals(AbstractDBView.class)
						|| type.equals(AbstractDBTable.class) || type.equals(ProxyDatabaseView.class)
						|| type.equals(ProxyDatabaseTable.class) || type.equals(SQLQueryable.class) || type.equals(ProxySQLQueryable.class)
						|| type.equals(DatabaseView.class) || type.equals(DatabaseTable.class) || type.equals(DBTableProxy.class)
						|| type.equals(DatabaseTable.class) /* || type.equals(SyntheticSQLQueryable.class) */) {
					System.out.println("not matching: " + className);
					return;
				}

				if (SQLQueryable.class.isAssignableFrom(type)) {
					hints.reflection()
							.registerType(type, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
					System.out.println("including: " + className);
				} else if (DatabaseEntry.class.isAssignableFrom(type)) {
					hints.reflection()
							.registerType(type,
									MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
									MemberCategory.INVOKE_DECLARED_METHODS,
									MemberCategory.ACCESS_DECLARED_FIELDS);
					System.out.println("including: " + className);
				}
			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException("Could not load " + className, e);
			}
		});
	}

//	@Override
//	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//		if (AotDetector.useGeneratedArtifacts()) {
//			return;
//		}
//
//		this.AOT_PROCESSING = Boolean.getBoolean(AbstractAotProcessor.AOT_PROCESSING);
//
//		if (!this.AOT_PROCESSING) {
//			return;
//		}
//
//		final ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
//
//		this.forAllInclEntries(bd -> {
//			final String className = bd.getBeanClassName();
//
//			if (className == null) {
//				System.out.println("classname null: " + bd);
//				return;
//			}
//
//			try {
//				final Class<?> type = Class.forName(className, false, classLoader);
//
//				if (type.equals(DeferredSQLQueryable.class) || type.equals(DeferredDatabaseView.class)
//						|| type.equals(DeferredDatabaseTable.class) || type.equals(AbstractDBView.class)
//						|| type.equals(AbstractDBTable.class) || type.equals(ProxyDatabaseView.class)
//						|| type.equals(ProxyDatabaseTable.class) || type.equals(SQLQueryable.class) || type.equals(ProxySQLQueryable.class)
//						|| type.equals(DatabaseView.class) || type.equals(DatabaseTable.class) || type.equals(DBTableProxy.class)
//						|| type.equals(DatabaseTable.class) /* || type.equals(SyntheticSQLQueryable.class) */) {
//					System.out.println("not matching: " + className);
//					return;
//				}
//
//				if (!SQLQueryable.class.isAssignableFrom(type)) {
//					return;
//				}
//
//			} catch (final ClassNotFoundException e) {
//				throw new IllegalStateException("Could not load " + className, e);
//			}
//		});
//	}

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

		for (final BeanDefinition bd : scanner.findCandidateComponents("**")) {
			consumer.accept(bd);
		}
	}

//	protected void forAllGenerated(final Consumer<BeanDefinition> consumer) {
//		final Environment environment = new StandardEnvironment();
//
//		final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false, environment) {
//
//			@Override
//			protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
//				if (super.isCandidateComponent(beanDefinition)) {
//					return true;
//				}
//
//				final AnnotationMetadata metadata = beanDefinition.getMetadata();
//
//				final boolean isSpringComponent = metadata.hasAnnotation("org.springframework.stereotype.Component")
//						|| metadata.hasMetaAnnotation("org.springframework.stereotype.Component");
//
//				try {
//					final Class<?> clazz = Class.forName(metadata.getClassName());
//					return isSpringComponent && SQLQueryable.class.isAssignableFrom(clazz) || DatabaseEntry.class.isAssignableFrom(clazz);
//				} catch (final ClassNotFoundException e) {
//					return false;
//				}
//			}
//		};
//
//		scanner.setResourceLoader(this.resourceLoader);
//
//		scanner.addIncludeFilter(new AssignableTypeFilter(SQLQueryable.class));
//
//		for (final BeanDefinition bd : scanner.findCandidateComponents("**")) {
//			consumer.accept(bd);
//		}
//	}

}
