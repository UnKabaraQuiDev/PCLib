package lu.kbra.pclib.db.registrar;

import java.lang.reflect.Field;
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

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.annotations.view.DBView;
import lu.kbra.pclib.db.annotations.view.UnionTable;
import lu.kbra.pclib.db.annotations.view.ViewTable;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.view.AbstractDBView;

final class PCLibDBRegistrarUtils {

	public static <T extends DatabaseEntry> Class<? extends SQLQueryable<?>>[]
			resolveDependencies(final Class<? extends SQLQueryable<T>> queryableType) {

		Objects.requireNonNull(queryableType);

		if (AbstractDBView.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBView<T>> viewType = (Class<? extends AbstractDBView<T>>) queryableType;

			final Class<T> entryType = getEntryType(viewType);

			return PCUtils.combineArrays(resolveViewDependencies(viewType), resolveEntryDependencies(entryType));
		}

		if (AbstractDBTable.class.isAssignableFrom(queryableType)) {
			final Class<? extends AbstractDBTable<T>> tableType = (Class<? extends AbstractDBTable<T>>) queryableType;

			final Class<T> entryType = getEntryType(tableType);

			return resolveEntryDependencies(entryType);
		}

		throw new IllegalArgumentException("Unknown class type: " + queryableType.getName());
	}

	public static Class<?> findEntryType(final Type type, final Map<TypeVariable<?>, Type> resolvedTypes) {
		if (type instanceof final Class<?> clazz) {
			if (DatabaseEntry.class.isAssignableFrom(clazz)) {
				return clazz;
			}

			for (final Type iface : clazz.getGenericInterfaces()) {
				final Class<?> result = findEntryType(iface, new HashMap<>(resolvedTypes));
				if (result != null) {
					return result;
				}
			}

			final Type genericSuperclass = clazz.getGenericSuperclass();
			if (genericSuperclass != null && genericSuperclass != Object.class) {
				return findEntryType(genericSuperclass, new HashMap<>(resolvedTypes));
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
				localResolvedTypes.put(typeParameters[i], resolveType(actualTypeArguments[i], resolvedTypes));
			}

			if (SQLQueryable.class.isAssignableFrom(rawClass) && actualTypeArguments.length > 0) {
				final Class<?> entryType = resolveToClass(actualTypeArguments[0], localResolvedTypes);

				if (entryType != null && DatabaseEntry.class.isAssignableFrom(entryType)) {
					return entryType;
				}
			}

			for (final Type iface : rawClass.getGenericInterfaces()) {
				final Class<?> result = findEntryType(iface, new HashMap<>(localResolvedTypes));
				if (result != null) {
					return result;
				}
			}

			final Type genericSuperclass = rawClass.getGenericSuperclass();
			if (genericSuperclass != null && genericSuperclass != Object.class) {
				return findEntryType(genericSuperclass, new HashMap<>(localResolvedTypes));
			}

			return null;
		}

		if (type instanceof final TypeVariable<?> typeVariable) {
			final Type resolvedType = resolvedTypes.get(typeVariable);

			if (resolvedType == null) {
				return null;
			}

			return findEntryType(resolvedType, resolvedTypes);
		}

		if (type instanceof final WildcardType wildcardType) {
			for (final Type upperBound : wildcardType.getUpperBounds()) {
				final Class<?> result = findEntryType(upperBound, resolvedTypes);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Deprecated
	public static <T extends DatabaseEntry> Class<T> findEntryTypeInInterfaces(final Class<?> clazz) {
		if (DatabaseEntry.class.isAssignableFrom(clazz)) {
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

					final Class<T> result = findEntryTypeInInterfaces(rawClass);
					if (result != null) {
						return result;
					}
				}
			}

			if (iface instanceof final Class<?> ifaceClass) {
				final Class<T> result = findEntryTypeInInterfaces(ifaceClass);
				if (result != null) {
					return result;
				}
			}
		}

		final Class<?> superclass = clazz.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			return findEntryTypeInInterfaces(superclass);
		}

		throw new IllegalArgumentException("Could not determine DatabaseEntry type from " + clazz.getName());
	}

	@SuppressWarnings("unchecked")
	public static <T extends DatabaseEntry> Class<T> getEntryType(final Class<? extends SQLQueryable<?>> type) {
		final Class<?> entryType = findEntryType(type, new HashMap<>());

		if (entryType == null) {
			throw new IllegalArgumentException("Could not determine DatabaseEntry type from " + type.getName());
		}

		if (!DatabaseEntry.class.isAssignableFrom(entryType)) {
			throw new IllegalArgumentException("Resolved type is not a DatabaseEntry: " + entryType.getName() + " from " + type.getName());
		}

		return (Class<T>) entryType;
	}

	public static <T extends DatabaseEntry> Class<? extends SQLQueryable<?>>[] resolveEntryDependencies(final Class<T> entryType) {
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

	public static Class<?> resolveToClass(final Type type, final Map<TypeVariable<?>, Type> resolvedTypes) {
		final Type resolvedType = resolveType(type, resolvedTypes);

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

			return resolveToClass(resolved, resolvedTypes);
		}

		if (resolvedType instanceof final WildcardType wildcardType) {
			for (final Type upperBound : wildcardType.getUpperBounds()) {
				final Class<?> result = resolveToClass(upperBound, resolvedTypes);
				if (result != null) {
					return result;
				}
			}
		}

		return null;
	}

	public static Type resolveType(final Type type, final Map<TypeVariable<?>, Type> resolvedTypes) {
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

	public static <T extends DatabaseEntry> Class<? extends SQLQueryable<?>>[]
			resolveViewDependencies(final Class<? extends AbstractDBView<T>> viewType) {

		if (!viewType.isAnnotationPresent(DBView.class)) {
			return new Class[0];
		}

		final DBView dbView = viewType.getAnnotation(DBView.class);

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

		return PCUtils.combineArrays(baseClasses, unionClasses);
	}

}
