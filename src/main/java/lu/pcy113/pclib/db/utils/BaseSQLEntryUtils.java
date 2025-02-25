package lu.pcy113.pclib.db.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import lu.pcy113.pclib.db.annotations.Constraint;
import lu.pcy113.pclib.db.annotations.GeneratedKey;
import lu.pcy113.pclib.db.annotations.GeneratedKeyUpdate;
import lu.pcy113.pclib.db.annotations.Reload;
import lu.pcy113.pclib.db.annotations.UniqueKey;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;

public class BaseSQLEntryUtils implements SQLEntryUtils.SQLEntryUtilsImpl {

	@Override
	public <T extends SQLEntry> void generatedKeyUpdate(T data, ResultSet rs) {
		for (Method m : data.getClass().getMethods()) {
			if (m.isAnnotationPresent(GeneratedKeyUpdate.class)) {
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
				break;
			}
		}
	}

	@Override
	public <T extends SQLEntry> void reload(T data, ResultSet rs) {
		for (Method m : data.getClass().getMethods()) {
			if (m.isAnnotationPresent(Reload.class)) {
				try {
					m.invoke(data, rs);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
				break;
			}
		}
	}

	@Override
	public <T extends SQLEntry> String getGeneratedKeyName(T data) {
		return data.getClass().getAnnotation(GeneratedKey.class).value();
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
		Method reloadMethod = null;
		for (Method m : data.getClass().getMethods()) {
			if (m.isAnnotationPresent(Reload.class)) {
				reloadMethod = m;
				break;
			}
		}
		if (reloadMethod == null) {
			throw new IllegalStateException("No method annotated with @Reload found");
		}

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
		Method reloadMethod = null;
		for (Method m : data.clone().getClass().getMethods()) {
			if (m.isAnnotationPresent(Reload.class)) {
				reloadMethod = m;
				break;
			}
		}
		if (reloadMethod == null) {
			throw new IllegalStateException("No method annotated with @Reload found");
		}

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
	public <T extends SQLEntry> Map<String, Object> getUniqueKeys(Constraint[] allConstraints, T data) {
		final Set<String> declaredUniquesSet = new HashSet<>();
		Arrays.stream(allConstraints).filter((Constraint c) -> c.type().equals(Constraint.Type.UNIQUE)).map(Constraint::columns).flatMap(Arrays::stream).forEach(declaredUniquesSet::add);
		
		if (declaredUniquesSet.size() == 0) {
			return null;
		}

		final Map<String, Object> uniques = new HashMap<>();

		try {
			for (Method m : data.clone().getClass().getMethods()) {
				if (m.isAnnotationPresent(UniqueKey.class)) {
					final UniqueKey uniqueValue = m.getAnnotation(UniqueKey.class);

					if (declaredUniquesSet.contains(uniqueValue.value())) {
						uniques.put(uniqueValue.value(), m.invoke(data));
						declaredUniquesSet.remove(uniqueValue.value());
					}
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

		if (declaredUniquesSet.size() > 0) {
			throw new IllegalStateException("Missing unique keys: " + declaredUniquesSet);
		}

		return uniques;
	}

}