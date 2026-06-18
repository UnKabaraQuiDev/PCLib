package lu.kbra.pclib.db.registrar;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AssignableTypeFilter;

import lu.kbra.pclib.db.annotations.view.DB_View;
import lu.kbra.pclib.db.annotations.view.UnionTable;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.factory.DeferredSQLQueryableFactoryBean;
import lu.kbra.pclib.db.factory.SQLQueryableFactoryBean;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.DeferredSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.intercept.QueryMethodInterceptor;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.table.DataBaseTable;
import lu.kbra.pclib.db.table.DeferredDataBaseTable;
import lu.kbra.pclib.db.view.AbstractDBView;
import lu.kbra.pclib.db.view.DataBaseView;
import lu.kbra.pclib.db.view.DeferredDataBaseView;

public class DeferredSQLQueryableRegistrar
		implements
			BeanDefinitionRegistryPostProcessor,
			EnvironmentAware,
			ResourceLoaderAware,
			BeanFactoryAware {

	private Environment environment;
	private ResourceLoader resourceLoader;
	private BeanFactory beanFactory;

	private final QueryMethodInterceptor interceptor = new QueryMethodInterceptor();

	@SafeVarargs
	private final Class<? extends SQLQueryable<?>>[] combineArrays(final Class<? extends SQLQueryable<?>>[]... arrays) {
		return Stream.of(arrays).flatMap(Arrays::stream).toArray(Class[]::new);
	}

	protected void createDeferred(final BeanDefinitionRegistry registry) {
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
					return DeferredSQLQueryable.class.isAssignableFrom(clazz);
				} catch (final ClassNotFoundException e) {
					return false;
				}
			}
		};

		scanner.setResourceLoader(this.resourceLoader);
		scanner.addIncludeFilter(new AssignableTypeFilter(DeferredSQLQueryable.class));

		final String basePackage = this.resolveRootPackage();

		for (final BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
			try {
				final Class<?> repoClass = Class.forName(bd.getBeanClassName());

				if (repoClass.equals(DeferredSQLQueryable.class) || repoClass.equals(DeferredDataBaseView.class)
						|| repoClass.equals(DeferredDataBaseTable.class)) {
					continue;
				}

				this.registerDeferredFactoryBean(registry, repoClass);

			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	protected void createNormal(final BeanDefinitionRegistry registry) {
		for (final String beanName : registry.getBeanDefinitionNames()) {
			final BeanDefinition bd = registry.getBeanDefinition(beanName);

			final String className = bd.getBeanClassName();
			if (className == null) {
				continue;
			}

			try {
				final Class<?> repoClass = Class.forName(className);

				if (!SQLQueryable.class.isAssignableFrom(repoClass) || repoClass.equals(SQLQueryable.class)
						|| repoClass.equals(DataBaseView.class) || repoClass.equals(DataBaseTable.class)) {
					continue;
				}

				registry.removeBeanDefinition(beanName);
				this.registerNormalFactoryBean(registry, repoClass);

			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	private Class<?> findEntryType(final Type type, final Map<TypeVariable<?>, Type> resolvedTypes) {
		if (type instanceof final Class<?> clazz) {
			if (DataBaseEntry.class.isAssignableFrom(clazz)) {
				return clazz;
			}

			for (final Type iface : clazz.getGenericInterfaces()) {
				final Class<?> result = this.findEntryType(iface, new HashMap<>(resolvedTypes));
				if (result != null) {
					return result;
				}
			}

			final Type genericSuperclass = clazz.getGenericSuperclass();
			if (genericSuperclass != null && genericSuperclass != Object.class) {
				return this.findEntryType(genericSuperclass, new HashMap<>(resolvedTypes));
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
				localResolvedTypes.put(typeParameters[i], this.resolveType(actualTypeArguments[i], resolvedTypes));
			}

			if (SQLQueryable.class.isAssignableFrom(rawClass) && actualTypeArguments.length > 0) {
				final Class<?> entryType = this.resolveToClass(actualTypeArguments[0], localResolvedTypes);

				if (entryType != null && DataBaseEntry.class.isAssignableFrom(entryType)) {
					return entryType;
				}
			}

			for (final Type iface : rawClass.getGenericInterfaces()) {
				final Class<?> result = this.findEntryType(iface, new HashMap<>(localResolvedTypes));
				if (result != null) {
					return result;
				}
			}

			final Type genericSuperclass = rawClass.getGenericSuperclass();
			if (genericSuperclass != null && genericSuperclass != Object.class) {
				return this.findEntryType(genericSuperclass, new HashMap<>(localResolvedTypes));
			}

			return null;
		}

		if (type instanceof final TypeVariable<?> typeVariable) {
			final Type resolvedType = resolvedTypes.get(typeVariable);

			if (resolvedType == null) {
				return null;
			}

			return this.findEntryType(resolvedType, resolvedTypes);
		}

		if (type instanceof final WildcardType wildcardType) {
			for (final Type upperBound : wildcardType.getUpperBounds()) {
				final Class<?> result = this.findEntryType(upperBound, resolvedTypes);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	private <T extends DataBaseEntry> Class<T> findEntryTypeInInterfaces(final Class<?> clazz) {
		if (DataBaseEntry.class.isAssignableFrom(clazz)) {
			return (Class<T>) clazz;
		}

		for (final Type iface : clazz.getGenericInterfaces()) {
			if (iface instanceof final ParameterizedType parameterizedType) {
				final Type rawType = parameterizedType.getRawType();

				if (rawType instanceof final Class<?> rawClass) {
					if (SQLQueryable.class.isAssignableFrom(rawClass)) {
						final Type typeArg = parameterizedType.getActualTypeArguments()[0];

						if (typeArg instanceof final Class<?> entryClass) {
							return (Class<T>) entryClass;
						}
					}

					final Class<T> result = this.findEntryTypeInInterfaces(rawClass);
					if (result != null) {
						return result;
					}
				}
			}

			if (iface instanceof final Class<?> ifaceClass) {
				final Class<T> result = this.findEntryTypeInInterfaces(ifaceClass);
				if (result != null) {
					return result;
				}
			}
		}

		final Class<?> superclass = clazz.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			return this.findEntryTypeInInterfaces(superclass);
		}

		throw new IllegalArgumentException("Could not determine DataBaseEntry type from " + clazz.getName());
	}

	@SuppressWarnings("unchecked")
	private <T extends DataBaseEntry> Class<T> getEntryType(final Class<? extends SQLQueryable<?>> type) {
		final Class<?> entryType = this.findEntryType(type, new HashMap<>());

		if (entryType == null) {
			throw new IllegalArgumentException("Could not determine DataBaseEntry type from " + type.getName());
		}

		if (!DataBaseEntry.class.isAssignableFrom(entryType)) {
			throw new IllegalArgumentException("Resolved type is not a DataBaseEntry: " + entryType.getName() + " from " + type.getName());
		}

		return (Class<T>) entryType;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry registry) {
		this.createDeferred(registry);
		this.createNormal(registry);
	}

	@SuppressWarnings("unchecked")
	protected void registerDeferredFactoryBean(final BeanDefinitionRegistry registry, final Class<?> repositoryClass) {
		if (!Modifier.isAbstract(repositoryClass.getModifiers())) {
			return;
		}

		final String beanName = Introspector.decapitalize(repositoryClass.getSimpleName());

		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DeferredSQLQueryableFactoryBean.class);

		builder.addConstructorArgValue(repositoryClass);
		builder.addConstructorArgValue(this.interceptor);
		builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

		final Class<? extends SQLQueryable<?>>[] dependencies = this
				.resolveDependencies((Class<? extends SQLQueryable<DataBaseEntry>>) repositoryClass);

		final String[] dependencyBeanNames = Arrays.stream(dependencies)
				.map(Class::getSimpleName)
				.map(Introspector::decapitalize)
				.toArray(String[]::new);

		final BeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.setDependsOn(dependencyBeanNames);

		registry.registerBeanDefinition(beanName, beanDefinition);
	}

	@SuppressWarnings("unchecked")
	protected void registerNormalFactoryBean(final BeanDefinitionRegistry registry, final Class<?> repositoryClass) {
		if (Modifier.isAbstract(repositoryClass.getModifiers())) {
			throw new IllegalStateException(
					"SQLQueryable cannot be abstract, use DeferredSQLQueryable instead: " + repositoryClass.getName());
		}

		final String beanName = Introspector.decapitalize(repositoryClass.getSimpleName());

		final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(SQLQueryableFactoryBean.class);

		builder.addConstructorArgValue(repositoryClass);
		builder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

		final Class<? extends SQLQueryable<?>>[] dependencies = this
				.resolveDependencies((Class<? extends SQLQueryable<DataBaseEntry>>) repositoryClass);

		final String[] dependencyBeanNames = Arrays.stream(dependencies)
				.map(Class::getSimpleName)
				.map(Introspector::decapitalize)
				.toArray(String[]::new);

		final BeanDefinition beanDefinition = builder.getBeanDefinition();
		beanDefinition.setDependsOn(dependencyBeanNames);

		registry.registerBeanDefinition(beanName, beanDefinition);
	}

	public <T extends DataBaseEntry> Class<? extends SQLQueryable<?>>[]
			resolveDependencies(final Class<? extends SQLQueryable<T>> queryableType) {

		Objects.requireNonNull(queryableType);

		if (AbstractDBView.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBView<T>> viewType = (Class<? extends AbstractDBView<T>>) queryableType;

			final Class<T> entryType = this.getEntryType(viewType);

			return this.combineArrays(this.resolveViewDependencies(viewType), this.resolveEntryDependencies(entryType));
		}

		if (AbstractDBTable.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBTable<T>> tableType = (Class<? extends AbstractDBTable<T>>) queryableType;

			final Class<T> entryType = this.getEntryType(tableType);

			return this.resolveEntryDependencies(entryType);
		}

		throw new IllegalArgumentException("Unknown class type: " + queryableType.getName());
	}

	private <T extends DataBaseEntry> Class<? extends SQLQueryable<?>>[] resolveEntryDependencies(final Class<T> entryType) {
		final List<Class<? extends SQLQueryable<?>>> deps = new ArrayList<>();

		for (final Field field : entryType.getDeclaredFields()) {
			if (!field.isAnnotationPresent(Column.class) || !field.isAnnotationPresent(ForeignKey.class)) {
				continue;
			}

			final ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
			deps.add(foreignKey.table());
		}

		return deps.toArray(new Class[0]);
	}

	protected String resolveRootPackage() {
		return AutoConfigurationPackages.get(this.beanFactory).get(0);
	}

	private Class<?> resolveToClass(final Type type, final Map<TypeVariable<?>, Type> resolvedTypes) {
		final Type resolvedType = this.resolveType(type, resolvedTypes);

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

			return this.resolveToClass(resolved, resolvedTypes);
		}

		if (resolvedType instanceof final WildcardType wildcardType) {
			for (final Type upperBound : wildcardType.getUpperBounds()) {
				final Class<?> result = this.resolveToClass(upperBound, resolvedTypes);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	private Type resolveType(final Type type, final Map<TypeVariable<?>, Type> resolvedTypes) {
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

	private <T extends DataBaseEntry> Class<? extends SQLQueryable<?>>[]
			resolveViewDependencies(final Class<? extends AbstractDBView<T>> viewType) {

		if (!viewType.isAnnotationPresent(DB_View.class)) {
			return new Class[0];
		}

		final DB_View dbView = viewType.getAnnotation(DB_View.class);

		final Class<? extends SQLQueryable<?>>[] baseClasses = Arrays.stream(dbView.tables())
				.filter(t -> !ViewTable.Type.MAIN_UNION_ALL.equals(t.join()))
				.filter(t -> !ViewTable.Type.MAIN_UNION.equals(t.join()))
				.filter(t -> !t.typeName().equals(Class.class))
				.map(ViewTable::typeName)
				.toArray(Class[]::new);

		final Class<? extends SQLQueryable<?>>[] unionClasses = Arrays.stream(dbView.unionTables())
				.filter(t -> !t.typeName().equals(Class.class))
				.map(UnionTable::typeName)
				.toArray(Class[]::new);

		return this.combineArrays(baseClasses, unionClasses);
	}

	@Override
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setEnvironment(final Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setResourceLoader(final ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
}
