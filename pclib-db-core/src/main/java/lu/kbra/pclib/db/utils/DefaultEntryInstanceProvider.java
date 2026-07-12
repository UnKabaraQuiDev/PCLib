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
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;
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

	protected final DataBaseEntryUtils dataBaseEntryUtils;

	protected final EntryInstanceFactories factories;

	public DefaultEntryInstanceProvider(final DataBaseEntryUtils dataBaseEntryUtils) {
		this.dataBaseEntryUtils = dataBaseEntryUtils;
		this.factories = new CHMEntryInstanceFactories();
	}

	@Override
	public <T extends DataBaseEntry> T instance(final SQLQueryable<T> table) {
		Objects.requireNonNull(table, "table is null.");

		return (T) this.factories.computeIfAbsent(table, this::computeInstanceFactories)
				.get(DataBaseEntryUtils.EMPTY_SET)
				.getFunction()
				.apply(DataBaseEntryUtils.EMPTY_ARRAY);
	}

	@Override
	public <T extends DataBaseEntry> FactoryMethod getFactoryMethod(final SQLQueryable<T> table, final String[] columns) {
		return this.factories.computeIfAbsent(table, this::computeInstanceFactories).get(new HashSet<>(Arrays.asList(columns)));
	}

	protected <T extends DataBaseEntry> Map<Set<String>, FactoryMethod> computeInstanceFactories(final SQLQueryable<T> table) {
		final Class<T> entryClazz = table.getEntryClass();

		final Map<Set<String>, FactoryMethod> factories = new HashMap<>();

		for (final Constructor<?> constructor : entryClazz.getConstructors()) {
			final Set<String> args = new HashSet<>(constructor.getParameterCount());
			final List<ArgData> mapping = new ArrayList<>(constructor.getParameterCount());
			for (int i = 0; i < constructor.getParameterCount(); i++) {
				final Parameter p = constructor.getParameters()[i];
				final String name = this.dataBaseEntryUtils.parameterToColumnName(p);
				args.add(name);
				mapping.add(
						new ArgData(name, this.dataBaseEntryUtils.getColumnFor(table, name), constructor.getGenericParameterTypes()[i], i));
			}
			constructor.setAccessible(true);

			factories.put(Collections.unmodifiableSet(args),
					new FactoryMethod(Collections.unmodifiableList(mapping),
							(ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>) (final Object[] params) -> {
								try {
									return (DataBaseEntry) constructor.newInstance(params);
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
				final String name = this.dataBaseEntryUtils.parameterToColumnName(p);
				args.add(name);
				mapping.add(new ArgData(name, this.dataBaseEntryUtils.getColumnFor(table, name), method.getGenericParameterTypes()[i], i));
			}
			method.setAccessible(true);

			if (factories.containsKey(args)) {
				if (this.dataBaseEntryUtils.isFailOnDuplicateFactoryMethod()) {
					throw new DBException("Method with parameters: " + args + " registered at least twice on: " + entryClazz);
				} else if (this.dataBaseEntryUtils.isWarnOnDuplicateFactoryMethod()) {
					System.out.println("Method with parameters: " + args + " registered at least twice on: " + entryClazz);
				}
				// prefer constructor instead of factory
				continue;
			}
			factories.put(Collections.unmodifiableSet(args),
					new FactoryMethod(Collections.unmodifiableList(mapping),
							(ThrowingFunction<Object[], ? extends DataBaseEntry, DBException>) (final Object[] params) -> {
								try {
									return (DataBaseEntry) method.invoke(null, params);
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
