package lu.kbra.pclib.db.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import lu.kbra.pclib.db.annotations.entry.Factory;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;
import lu.kbra.pclib.db.utils.impl.EntryInstanceProvider;
import lu.kbra.pclib.impl.function.ThrowingFunction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DefaultEntryInstanceProvider implements EntryInstanceProvider {

	public static class CHMEntryInstanceFactories extends ConcurrentHashMap<SQLQueryable<?>, Map<Set<String>, FactoryMethod>>
			implements
				EntryInstanceFactories {

		private static final long serialVersionUID = 5984418945927200165L;

	}

	protected final DatabaseEntryUtils databaseEntryUtils;

	protected final EntryInstanceFactories factories;

	public DefaultEntryInstanceProvider(final DatabaseEntryUtils databaseEntryUtils) {
		this.databaseEntryUtils = databaseEntryUtils;
		this.factories = new CHMEntryInstanceFactories();
	}

	@Override
	public <T extends DatabaseEntry> T instance(final SQLQueryable<T> table) {
		Objects.requireNonNull(table, "table is null.");

		return (T) this.factories.computeIfAbsent(table, this::computeInstanceFactories)
				.get(DatabaseEntryUtils.EMPTY_SET)
				.getFunction()
				.apply(DatabaseEntryUtils.EMPTY_ARRAY);
	}

	@Override
	public <T extends DatabaseEntry> FactoryMethod getFactoryMethod(final SQLQueryable<T> table, final String[] columns) {
		return this.factories.computeIfAbsent(table, this::computeInstanceFactories).get(new HashSet<>(Arrays.asList(columns)));
	}

	protected <T extends DatabaseEntry> Map<Set<String>, FactoryMethod> computeInstanceFactories(final SQLQueryable<T> table) {
		final Class<T> entryClazz = table.getEntryClass();

		final Map<Set<String>, FactoryMethod> factories = new HashMap<>();

		for (final Constructor<?> constructor : entryClazz.getConstructors()) {
			final Set<String> args = new HashSet<>(constructor.getParameterCount());
			final List<ArgData> mapping = new ArrayList<>(constructor.getParameterCount());
			for (int i = 0; i < constructor.getParameterCount(); i++) {
				final Parameter p = constructor.getParameters()[i];
				final String name = this.databaseEntryUtils.parameterToColumnName(p);
				args.add(name);
				mapping.add(
						new ArgData(name, this.databaseEntryUtils.getColumnFor(table, name), constructor.getGenericParameterTypes()[i], i));
			}
			constructor.setAccessible(true);

			factories.put(Collections.unmodifiableSet(args),
					new FactoryMethod(Collections.unmodifiableList(mapping),
							(ThrowingFunction<Object[], ? extends DatabaseEntry, DBException>) (final Object[] params) -> {
								try {
									return (DatabaseEntry) constructor.newInstance(params);
								} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
										| InvocationTargetException e) {
									throw new DBException(
											"Failed to instantiate " + entryClazz.getName() + " through constructor: " + constructor,
											e);
								}
							}));
		}

		for (final Method method : entryClazz.getDeclaredMethods()) {
			if (method.isAnnotationPresent(Factory.class)) {
				if (!Modifier.isStatic(method.getModifiers())) {
					throw new IllegalArgumentException("Factory method not static: " + method);
				}
			} else {
				continue;
			}
			if (!method.getReturnType().equals(entryClazz)) {
				throw new IllegalArgumentException(
						"Factory method returns wrong type: " + entryClazz.getName() + " returns " + method.getReturnType().getName());
			}

			final Set<String> args = new HashSet<>(method.getParameterCount());
			final List<ArgData> mapping = new ArrayList<>(method.getParameterCount());
			for (int i = 0; i < method.getParameterCount(); i++) {
				final Parameter p = method.getParameters()[i];
				final String name = this.databaseEntryUtils.parameterToColumnName(p);
				args.add(name);
				mapping.add(new ArgData(name, this.databaseEntryUtils.getColumnFor(table, name), method.getGenericParameterTypes()[i], i));
			}
			method.setAccessible(true);

			if (factories.containsKey(args)) {
				if (this.databaseEntryUtils.isFailOnDuplicateFactoryMethod()) {
					throw new DBException("Method with parameters: " + args + " registered at least twice on: " + entryClazz);
				} else if (this.databaseEntryUtils.isWarnOnDuplicateFactoryMethod()) {
					System.out.println("Method with parameters: " + args + " registered at least twice on: " + entryClazz);
				}
				// prefer constructor instead of factory
				continue;
			}
			factories.put(Collections.unmodifiableSet(args),
					new FactoryMethod(Collections.unmodifiableList(mapping),
							(ThrowingFunction<Object[], ? extends DatabaseEntry, DBException>) (final Object[] params) -> {
								try {
									return (DatabaseEntry) method.invoke(null, params);
								} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
									throw new DBException(
											"Failed to instantiate " + entryClazz.getName() + " through factory method: " + method,
											e);
								}
							}));
		}

		return Collections.unmodifiableMap(factories);
	}

}
