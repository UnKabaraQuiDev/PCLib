package lu.pcy113.pclib.db.utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Consumer;

import lu.pcy113.pclib.db.annotations.table.Constraint;
import lu.pcy113.pclib.db.autobuild.column.ColumnData;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;

public interface SQLEntryUtils {

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

	<T extends SQLEntry> T instance(T data);

	<T extends SQLEntry> T instance(Class<T> clazz);

	<T extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<T> listExporter) throws SQLException;

	<T extends SQLQuery<B>, B extends SQLEntry> void copyAll(T data, ResultSet result, Consumer<B> listExporter) throws SQLException;

	<T extends SQLEntry> Map<String, Object>[] getUniqueKeys(Constraint[] allConstraints, T data);
	
	<T extends SQLEntry> ColumnData[] getColumns(Class<T> data);

}