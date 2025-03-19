package lu.pcy113.pclib.db.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import lu.pcy113.pclib.db.annotations.entry.GeneratedKey;
import lu.pcy113.pclib.db.annotations.entry.GeneratedKeyUpdate;
import lu.pcy113.pclib.db.annotations.entry.Reload;
import lu.pcy113.pclib.db.annotations.entry.UniqueKey;
import lu.pcy113.pclib.db.annotations.table.Constraint;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;

public class CachedSQLEntryUtils implements SQLEntryUtils.SQLEntryUtilsImpl {

	private HashMap<Class<?>, Method> generatedKeyUpdateCache = new HashMap<>();
	private HashMap<Class<?>, Method> reloadCache = new HashMap<>();
	private HashMap<Class<?>, String> generatedKeyNameCache = new HashMap<>();
	private HashMap<Class<?>, Map<String, Method>> uniqueKeysMethodCache = new HashMap<>();

	@Override
	public <T extends SQLEntry> void generatedKeyUpdate(T data, ResultSet rs) {
		final Method m = getGeneratedKeyMethod(data.getClass());
		final GeneratedKeyUpdate generatedKeyUpdate = m.getAnnotation(GeneratedKeyUpdate.class);

		try {
			if (generatedKeyUpdate.type().equals(GeneratedKeyUpdate.Type.RESULT_SET)) {
				m.invoke(data, rs);
			} else if (generatedKeyUpdate.type().equals(GeneratedKeyUpdate.Type.INDEX)) {
				m.invoke(data, m.getParameterTypes()[0].cast(rs.getObject(generatedKeyUpdate.index())));
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Method getGeneratedKeyMethod(Class<?> clazz) {
		if (!generatedKeyUpdateCache.containsKey(clazz)) {
			boolean found = false;
			for (Method m : clazz.getMethods()) {
				if (m.isAnnotationPresent(GeneratedKeyUpdate.class)) {
					generatedKeyUpdateCache.put(clazz, m);
					found = true;
					break;
				}
			}

			if (!found) {
				throw new IllegalStateException("No method annotated with @GeneratedKeyUpdate found.");
			}
		}

		return generatedKeyUpdateCache.get(clazz);
	}

	@Override
	public <T extends SQLEntry> void reload(T data, ResultSet rs) {
		final Method m = getReloadMethod(data.getClass());

		try {
			m.invoke(data, rs);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	private Method getReloadMethod(Class<?> clazz) {
		if (!reloadCache.containsKey(clazz)) {
			boolean found = false;
			for (Method m : clazz.getMethods()) {
				if (m.isAnnotationPresent(Reload.class)) {
					reloadCache.put(clazz, m);
					found = true;
					break;
				}
			}

			if (!found) {
				throw new IllegalStateException("No method annotated with @Reload found.");
			}
		}

		return reloadCache.get(clazz);
	}

	@Override
	public <T extends SQLEntry> String getGeneratedKeyName(T data) {
		final Class<?> clazz = data.getClass();

		if (!generatedKeyNameCache.containsKey(clazz)) {
			if (clazz.isAnnotationPresent(GeneratedKey.class)) {
				generatedKeyNameCache.put(clazz, clazz.getAnnotation(GeneratedKey.class).value());
			} else {
				throw new IllegalStateException("Class not annotated with @GeneratedKey.");
			}
		}

		return generatedKeyNameCache.get(clazz);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends SQLEntry> T copy(T data, ResultSet rs) {
		data = (T) ((SQLEntry) data).clone();
		reload(data, rs);
		return data;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<T> listExporter) throws SQLException {
		final Method reloadMethod = getReloadMethod(data.getClass());

		while (result.next()) {
			T newData = (T) data.clone();
			try {
				reloadMethod.invoke(newData, result);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			listExporter.accept(newData);
		}
	}

	@Override
	public <T extends SQLQuery<B>, B extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<B> listExporter) throws SQLException {
		final Method reloadMethod = getReloadMethod(data.clone().getClass());

		while (result.next()) {
			B newData = (B) data.clone();
			try {
				reloadMethod.invoke(newData, result);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
			listExporter.accept(newData);
		}
	}

	@Override
	public <T extends SQLEntry> Map<String, Object>[] getUniqueKeys(Constraint[] allConstraints, T data) {
		final Class<?> clazz = data.getClass();

		final List<Constraint> uniqueConstraints = Arrays.stream(allConstraints).filter((Constraint c) -> c.type().equals(Constraint.Type.UNIQUE)).collect(Collectors.toList());

		final Set<String> declaredUniquesSet = new HashSet<>();
		Arrays.stream(allConstraints).filter((Constraint c) -> c.type().equals(Constraint.Type.UNIQUE)).map(Constraint::columns).flatMap(Arrays::stream).forEach(declaredUniquesSet::add);

		if (declaredUniquesSet.size() == 0) {
			return null;
		}

		final Map<String, Object> uniqueValues = new HashMap<>();

		try {
			if (!uniqueKeysMethodCache.containsKey(clazz)) {
				uniqueKeysMethodCache.put(clazz, new HashMap<String, Method>());

				for (Method m : data.clone().getClass().getMethods()) {
					if (m.isAnnotationPresent(UniqueKey.class)) {
						final String uniqueKey = m.getAnnotation(UniqueKey.class).value();
						uniqueKeysMethodCache.get(clazz).put(uniqueKey, m);

						if (declaredUniquesSet.contains(uniqueKey)) {
							uniqueValues.put(uniqueKey, m.invoke(data));
							declaredUniquesSet.remove(uniqueKey);
						}
					}
				}

			} else {

				for (final Entry<String, Method> esm : uniqueKeysMethodCache.get(clazz).entrySet()) {
					final String uniqueKey = esm.getKey();
					final Method m = esm.getValue();

					if (declaredUniquesSet.contains(uniqueKey)) {
						uniqueValues.put(uniqueKey, m.invoke(data));
						declaredUniquesSet.remove(uniqueKey);
					}
				}

			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		if (declaredUniquesSet.size() > 0) {
			throw new IllegalStateException("Missing unique keys: " + declaredUniquesSet);
		}

		final Map<String, Object>[] uniques = new HashMap[uniqueConstraints.size()];

		for (int i = 0; i < uniqueConstraints.size(); i++) {
			final Constraint constraint = uniqueConstraints.get(i);
			uniques[i] = new HashMap<String, Object>();

			for (String key : constraint.columns()) {
				uniques[i].put(key, uniqueValues.get(key));
			}
		}

		// remove null values
		for (Map<String, Object> unique : uniques) {
			unique.entrySet().stream().filter(c -> c.getValue() != null).collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		}

		return uniques;
	}

	public void clearCache() {
		generatedKeyNameCache.clear();
		generatedKeyUpdateCache.clear();
		reloadCache.clear();
		uniqueKeysMethodCache.clear();
	}

}