package lu.pcy113.pclib.db;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

import lu.pcy113.pclib.db.annotations.GeneratedKey;
import lu.pcy113.pclib.db.annotations.GeneratedKeyUpdate;
import lu.pcy113.pclib.db.annotations.Reload;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;

public final class SQLEntryUtils {

	/**
	 * Only reload generated keys
	 */
	public static <T extends SQLEntry> void generatedInsertUpdate(T data, ResultSet rs) {
		for (Method m : data.getClass().getMethods()) {
			if (m.isAnnotationPresent(GeneratedKeyUpdate.class)) {
				try {
					m.invoke(data, rs);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
				break;
			}
		}
	}

	/**
	 * Full reload
	 */
	public static <T extends SQLEntry> void reload(T data, ResultSet rs) {
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

	public static <T extends SQLEntry> String getGeneratedKeyName(T data) {
		return data.getClass().getAnnotation(GeneratedKey.class).value();
	}

	@SuppressWarnings("unchecked")
	public static <T extends SQLEntry> T copy(T data, ResultSet rs) {
		data = (T) ((SQLEntry) data).clone();
		reload(data, rs);
		return data;
	}

	@SuppressWarnings("unchecked")
	public static <T extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<T> listExporter) throws SQLException {
		Method reloadMethod = null;
		for (Method m : data.getClass().getMethods()) {
			if (m.isAnnotationPresent(Reload.class)) {
				reloadMethod = m;
				break;
			}
		}
		if(reloadMethod == null) {
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
	
	public static <T extends SQLQuery<B>, B extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<B> listExporter) throws SQLException {
		Method reloadMethod = null;
		for (Method m : data.clone().getClass().getMethods()) {
			if (m.isAnnotationPresent(Reload.class)) {
				reloadMethod = m;
				break;
			}
		}
		if(reloadMethod == null) {
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


}
