package lu.pcy113.pclib.db.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;

import lu.pcy113.pclib.db.annotations.Column;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;

public final class SQLEntryUtils {

	public static SQLEntryUtilsImpl INSTANCE = new BaseSQLEntryUtils();

	public static void register(SQLEntryUtilsImpl impl) {
		INSTANCE = impl;
	}

	/**
	 * Only reload generated keys
	 */
	public static <T extends SQLEntry> void generatedKeyUpdate(T data, ResultSet rs) {
		INSTANCE.generatedKeyUpdate(data, rs);
	}

	/**
	 * Full reload
	 */
	public static <T extends SQLEntry> void reload(T data, ResultSet rs) {
		INSTANCE.reload(data, rs);
	}

	public static <T extends SQLEntry> String getGeneratedKeyName(T data) {
		return INSTANCE.getGeneratedKeyName(data);
	}

	public static <T extends SQLEntry> T copy(T data, ResultSet rs) {
		return INSTANCE.copy(data, rs);
	}

	public static <T extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<T> listExporter) throws SQLException {
		INSTANCE.copyAll(data, result, listExporter);
	}

	public static <T extends SQLQuery<B>, B extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<B> listExporter) throws SQLException {
		INSTANCE.copyAll(data, result, listExporter);
	}

	public static <T extends SQLEntry> Map<String, Object> getUniqueKeys(Column[] allColumns, T data) {
		return INSTANCE.getUniqueKeys(allColumns, data);
	}

	public interface SQLEntryUtilsImpl {

		/**
		 * Only reload generated keys
		 */
		<T extends SQLEntry> void generatedKeyUpdate(T data, ResultSet rs);

		/**
		 * Full reload
		 */
		<T extends SQLEntry> void reload(T data, ResultSet rs);

		<T extends SQLEntry> String getGeneratedKeyName(T data);

		<T extends SQLEntry> T copy(T data, ResultSet rs);

		<T extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<T> listExporter) throws SQLException;

		<T extends SQLQuery<B>, B extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<B> listExporter) throws SQLException;

		<T extends SQLEntry> Map<String, Object> getUniqueKeys(Column[] allColumns, T data);

	}

}