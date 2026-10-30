package lu.kbra.pclib.db.registrar;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.springframework.aot.AotDetector;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.aot.BeanRegistrationAotProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.aot.AbstractAotProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.view.Table;
import lu.kbra.pclib.db.annotations.view.UnionTable;
import lu.kbra.pclib.db.annotations.view.View;
import lu.kbra.pclib.db.factory.AbstractSQLQueryableFactoryBean;
import lu.kbra.pclib.db.factory.DeferredSQLQueryableFactoryBean;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.ProxySQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DeferredDatabaseTable;
import lu.kbra.pclib.db.table.ProxyDatabaseTable;
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.db.view.DeferredDatabaseView;
import lu.kbra.pclib.db.view.ProxyDatabaseView;

import lombok.Setter;

public class SQLQueryableRegistrar implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ResourceLoaderAware, BeanFactoryAware {

	private boolean aotProcessing = false;

	@Setter
	private Environment environment;
	@Setter
	private ResourceLoader resourceLoader;
	@Setter
	private BeanFactory beanFactory;

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) {
		if (AotDetector.useGeneratedArtifacts()) {
			return;
		}

		this.aotProcessing = Boolean.getBoolean(AbstractAotProcessor.AOT_PROCESSING);

		this.createDeferred(registry);
	}

	protected void createDeferred(final BeanDefinitionRegistry registry) {
		this.forAll(bd -> {
			try {
				final Class<?> repoClass = Class.forName(bd.getBeanClassName());

				if (repoClass.equals(DeferredSQLQueryable.class) || repoClass.equals(DeferredDatabaseView.class)
						|| repoClass.equals(DeferredDatabaseTable.class) || repoClass.equals(AbstractDBView.class)
						|| repoClass.equals(AbstractDBTable.class) || repoClass.equals(ProxyDatabaseView.class)
						|| repoClass.equals(ProxyDatabaseTable.class) || repoClass.equals(SQLQueryable.class)
						|| repoClass.equals(ProxySQLQueryable.class)) {
					return;
				}

//				if (this.AOT_PROCESSING && this.registerGeneratedRepository(registry, repoClass)) {
//					return;
//				}
				if (repoClass.isInterface()) {
					this.registerAbstractFactoryBean(registry, repoClass);
				} else {
					this.registerDeferredFactoryBean(registry, repoClass);
				}
			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		});
	}

	protected void forAll(final Consumer<BeanDefinition> consumer) {
		final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false,
				this.environment) {

			@Override
			protected boolean isCandidateComponent(final AnnotatedBeanDefinition beanDefinition) {
				if (super.isCandidateComponent(beanDefinition)) {
					return true;
				}

				final AnnotationMetadata metadata = beanDefinition.getMetadata();

				final boolean isSpringComponent = metadata.hasAnnotation("org.springframework.stereotype.Component")
						|| metadata.hasMetaAnnotation("org.springframework.stereotype.Component");

				if (!isSpringComponent) {
					return false;
				}

				try {
					final Class<?> clazz = Class.forName(metadata.getClassName());
					return SQLQueryable.class.isAssignableFrom(clazz);
				} catch (final ClassNotFoundException e) {
					return false;
				}
			}
		};

		scanner.setResourceLoader(this.resourceLoader);

		scanner.addIncludeFilter(new AssignableTypeFilter(SQLQueryable.class));

		for (final String basePackage : this.resolveRootPackages()) {
			for (final BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
				consumer.accept(bd);
			}
		}
	}

	protected void registerAbstractFactoryBean(final BeanDefinitionRegistry registry, final Class<?> repositoryClass) {
		if (!Modifier.isAbstract(repositoryClass.getModifiers())) {
			return;
		}

		this.registerFactoryBean(registry, repositoryClass, AbstractSQLQueryableFactoryBean.class);
	}

	protected void registerDeferredFactoryBean(final BeanDefinitionRegistry registry, final Class<?> repositoryClass) {
		if (!Modifier.isAbstract(repositoryClass.getModifiers())) {
			return;
		}

		this.registerFactoryBean(registry, repositoryClass, DeferredSQLQueryableFactoryBean.class);
	}

//	protected void registerNormalFactoryBean(final BeanDefinitionRegistry registry, final Class<?> repositoryClass) {
//		if (Modifier.isAbstract(repositoryClass.getModifiers())) {
//			throw new IllegalStateException(
//					"SQLQueryable cannot be abstract, use DeferredSQLQueryable instead: " + repositoryClass.getName());
//		}
//
//		registerFactoryBean(registry, repositoryClass, SQLQueryableFactoryBean.class);
//	}

	protected void
			registerFactoryBean(final BeanDefinitionRegistry registry, final Class<?> repositoryClass, final Class<?> factoryBeanClass) {
		final String beanName = Introspector.decapitalize(repositoryClass.getSimpleName());

		final RootBeanDefinition beanDefinition = new RootBeanDefinition(factoryBeanClass);

		beanDefinition.setTargetType(ResolvableType.forClassWithGenerics(factoryBeanClass, repositoryClass));
		beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, repositoryClass);
		beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
		if (aotProcessing) {
			beanDefinition.setAttribute(BeanRegistrationAotProcessor.IGNORE_REGISTRATION_ATTRIBUTE, true);
		}

		registry.registerBeanDefinition(beanName, beanDefinition);
	}

	protected boolean registerGeneratedRepository(final BeanDefinitionRegistry registry, final Class<?> repositoryClass) {
		final String beanName = Introspector.decapitalize(repositoryClass.getSimpleName());

		final String generatedClassName = repositoryClass.getName() + "_SqlQueryable";

//		try {
//			Class.forName(generatedClassName);
//		} catch (final ClassNotFoundException e) {
//			System.out.println("class not there: " + generatedClassName);
//			return false;
//		}

//		final Class<?> generatedClass = loadGeneratedClass(repositoryClass);
//
//		if (!repositoryClass.isAssignableFrom(generatedClass)) {
//			throw new IllegalStateException(generatedClass.getName() + " must extend or implement " + repositoryClass.getName());
//		}
//		if (Modifier.isAbstract(generatedClass.getModifiers())) {
//			throw new IllegalStateException("Generated repository must be concrete: " + generatedClass.getName());
//		}

		final RootBeanDefinition definition = new RootBeanDefinition();

		definition.setBeanClassName(generatedClassName);
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
		definition.setTargetType(ResolvableType.forClass(repositoryClass));

		registry.registerBeanDefinition(beanName, definition);

		return true;
	}

//	private Class<?> loadGeneratedClass(final Class<?> repositoryClass) {
//		final String generatedName = repositoryClass.getName() + "__Generated";
//
//		try {
//			return Class.forName(generatedName, true, repositoryClass.getClassLoader());
//		} catch (final ClassNotFoundException e) {
//			throw new IllegalStateException(
//					"No generated repository implementation found: " + generatedName + ". Generate and compile repository subclasses "
//							+ "before Spring process-aot runs.",
//					e);
//		}
//	}

	protected String[] resolveRootPackages() {
		return AutoConfigurationPackages.get(this.beanFactory).toArray(String[]::new);
	}

	private static Class<?> findEntryType(final Type type, final Map<TypeVariable<?>, Type> resolvedTypes) {
		if (type instanceof final Class<?> clazz) {
			if (DatabaseEntry.class.isAssignableFrom(clazz)) {
				return clazz;
			}

			for (final Type iface : clazz.getGenericInterfaces()) {
				final Class<?> result = SQLQueryableRegistrar.findEntryType(iface, new HashMap<>(resolvedTypes));
				if (result != null) {
					return result;
				}
			}

			final Type genericSuperclass = clazz.getGenericSuperclass();
			if (genericSuperclass != null && genericSuperclass != Object.class) {
				return SQLQueryableRegistrar.findEntryType(genericSuperclass, new HashMap<>(resolvedTypes));
			}

			return null;
		}

		if (type instanceof final ParameterizedType parameterizedType) {
			final Type rawType = parameterizedType.getRawType();

			if (!(rawType instanceof final Class<?> rawClass)) {
				return null;
			}

			final Map<TypeVariable<?>, Type> localResolvedTypes = new HashMap<>(resolvedTypes);

			final TypeVariable<?>[] typeParameters = rawClass.getTypeParameters();
			final Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

			for (int i = 0; i < typeParameters.length; i++) {
				localResolvedTypes.put(typeParameters[i], SQLQueryableRegistrar.resolveType(actualTypeArguments[i], resolvedTypes));
			}

			if (SQLQueryable.class.isAssignableFrom(rawClass) && actualTypeArguments.length > 0) {
				final Class<?> entryType = SQLQueryableRegistrar.resolveToClass(actualTypeArguments[0], localResolvedTypes);

				if (entryType != null && DatabaseEntry.class.isAssignableFrom(entryType)) {
					return entryType;
				}
			}

			for (final Type iface : rawClass.getGenericInterfaces()) {
				final Class<?> result = SQLQueryableRegistrar.findEntryType(iface, new HashMap<>(localResolvedTypes));
				if (result != null) {
					return result;
				}
			}

			final Type genericSuperclass = rawClass.getGenericSuperclass();
			if (genericSuperclass != null && genericSuperclass != Object.class) {
				return SQLQueryableRegistrar.findEntryType(genericSuperclass, new HashMap<>(localResolvedTypes));
			}

			return null;
		}

		if (type instanceof final TypeVariable<?> typeVariable) {
			final Type resolvedType = resolvedTypes.get(typeVariable);

			if (resolvedType == null) {
				return null;
			}

			return SQLQueryableRegistrar.findEntryType(resolvedType, resolvedTypes);
		}

		if (type instanceof final WildcardType wildcardType) {
			for (final Type upperBound : wildcardType.getUpperBounds()) {
				final Class<?> result = SQLQueryableRegistrar.findEntryType(upperBound, resolvedTypes);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends DatabaseEntry> Class<T> getEntryType(final Class<?> type) {
		final Class<?> entryType = SQLQueryableRegistrar.findEntryType(type, new HashMap<>());

		if (entryType == null) {
			throw new IllegalArgumentException("Could not determine DatabaseEntry type from " + type.getName());
		}

		if (!DatabaseEntry.class.isAssignableFrom(entryType)) {
			throw new IllegalArgumentException("Resolved type is not a DatabaseEntry: " + entryType.getName() + " from " + type.getName());
		}

		return (Class<T>) entryType;
	}

	private <T extends DatabaseEntry> Class<? extends SQLQueryable<?>>[] resolveEntryDependencies(final Class<T> entryType) {
		final Set<Class<? extends SQLQueryable<?>>> deps = new LinkedHashSet<>();

		for (final Field field : entryType.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Column.class) || !field.isAnnotationPresent(ForeignKey.class)) {
				continue;
			}

			final ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
			deps.add(foreignKey.table());
		}

		return deps.toArray(Class[]::new);
	}

	private static Class<?> resolveToClass(final Type type, final Map<TypeVariable<?>, Type> resolvedTypes) {
		final Type resolvedType = SQLQueryableRegistrar.resolveType(type, resolvedTypes);

		if (resolvedType instanceof final Class<?> clazz) {
			return clazz;
		}

		if (resolvedType instanceof final ParameterizedType parameterizedType
				&& parameterizedType.getRawType() instanceof final Class<?> rawClass) {
			return rawClass;
		}

		if (resolvedType instanceof final TypeVariable<?> typeVariable) {
			final Type resolved = resolvedTypes.get(typeVariable);

			if (resolved == null) {
				return null;
			}

			return SQLQueryableRegistrar.resolveToClass(resolved, resolvedTypes);
		}

		if (resolvedType instanceof final WildcardType wildcardType) {
			for (final Type upperBound : wildcardType.getUpperBounds()) {
				final Class<?> result = SQLQueryableRegistrar.resolveToClass(upperBound, resolvedTypes);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	private static Type resolveType(final Type type, final Map<TypeVariable<?>, Type> resolvedTypes) {
		Type current = type;

		while (current instanceof final TypeVariable<?> typeVariable) {
			final Type resolved = resolvedTypes.get(typeVariable);

			if (resolved == null || resolved.equals(current)) {
				return current;
			}

			current = resolved;
		}

		return current;
	}

	private <T extends DatabaseEntry> Class<? extends SQLQueryable<?>>[]
			resolveViewDependencies(final Class<? extends AbstractDBView<T>> viewType) {
		if (!viewType.isAnnotationPresent(View.class)) {
			return new Class[0];
		}

		final View dbView = viewType.getAnnotation(View.class);

		final Class<? extends SQLQueryable<?>>[] baseClasses = Arrays.stream(dbView.tables())
				.filter(t -> !Table.Type.MAIN_UNION_ALL.equals(t.join()))
				.filter(t -> !Table.Type.MAIN_UNION.equals(t.join()))
				.filter(t -> !t.typeName().equals(Class.class))
				.map(Table::typeName)
				.toArray(Class[]::new);

		final Class<? extends SQLQueryable<?>>[] unionClasses = Arrays.stream(dbView.unionTables())
				.filter(t -> !t.typeName().equals(Class.class))
				.map(UnionTable::typeName)
				.toArray(Class[]::new);

		return PCUtils.combineArrays(baseClasses, unionClasses);
	}

	public <T extends DatabaseEntry> Class<? extends SQLQueryable<?>>[]
			resolveDependencies(final Class<? extends SQLQueryable<T>> queryableType) {
		Objects.requireNonNull(queryableType);

		if (AbstractDBView.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBView<T>> viewType = (Class<? extends AbstractDBView<T>>) queryableType;

			final Class<T> entryType = SQLQueryableRegistrar.getEntryType(viewType);

			return PCUtils.combineArrays(this.resolveViewDependencies(viewType), this.resolveEntryDependencies(entryType));
		}

		if (AbstractDBTable.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBTable<T>> tableType = (Class<? extends AbstractDBTable<T>>) queryableType;

			final Class<T> entryType = SQLQueryableRegistrar.getEntryType(tableType);

			return this.resolveEntryDependencies(entryType);
		}

		throw new IllegalArgumentException("Unknown class type: " + queryableType.getName());
	}

}
