package lu.kbra.pclib.db.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import lu.kbra.pclib.db.DataBaseTable;
import lu.kbra.pclib.db.autobuild.column.Column;
import lu.kbra.pclib.db.autobuild.column.ColumnData;
import lu.kbra.pclib.db.autobuild.column.ForeignKey;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType;
import lu.kbra.pclib.db.autobuild.table.ConstraintData;
import lu.kbra.pclib.db.autobuild.table.TableStructure;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface DataBaseEntryUtils {

	/**
	 * Only reload generated keys
	 */
	<T extends DataBaseEntry> void fillInsert(final T data, final ResultSet rs) throws SQLException;

	<T extends DataBaseEntry> Method getInsertMethod(final T data);

	<T extends DataBaseEntry> Method getInsertMethod(final Class<T> data);

	<T extends DataBaseEntry> void fillUpdate(final T data, final ResultSet rs) throws SQLException;

	<T extends DataBaseEntry> Method getUpdateMethod(final T data);

	<T extends DataBaseEntry> Method getUpdateMethod(final Class<T> data);

	/**
	 * Full reload
	 */
	<T extends DataBaseEntry> void fillLoad(final T data, final ResultSet rs) throws SQLException;

	<T extends DataBaseEntry> Method getLoadMethod(final T data);

	<T extends DataBaseEntry> Method getLoadMethod(final Class<T> data);

	<T extends DataBaseEntry> T fillLoadCopy(final T data, final ResultSet rs) throws SQLException;

	<T extends DataBaseEntry> T instance(final T data);

	<T extends DataBaseEntry> T instance(final Class<T> clazz);

	<T extends DataBaseEntry> void fillLoadAll(final T data, final ResultSet result, final Consumer<T> listExporter) throws SQLException;

	<T extends DataBaseEntry> void fillLoadAllTable(
			final Class<? extends SQLQueryable<T>> tableClazz,
			final SQLQuery<T, ?> query,
			final ResultSet result,
			final Consumer<T> listExporter) throws SQLException;

	<T extends DataBaseEntry> Map<String, Object>[] getUniqueValues(final ConstraintData[] allConstraints, final T data);

	<T extends DataBaseEntry> List<String>[] getUniqueKeys(final ConstraintData[] allConstraints, final T data);

	<T extends DataBaseEntry> Map<String, Object> getNotNullValues(final T data);

	<T extends DataBaseEntry> List<String> getNotNullKeys(final T data);

	/*
	 * scanning
	 */
	<T extends DataBaseEntry> TableStructure scanTable(final Class<? extends DataBaseTable<T>> data);

	<T extends DataBaseEntry> TableStructure scanEntry(final Class<T> data);

	ColumnType getTypeFor(final Field field);

	ColumnType getTypeFor(final Class<?> clazz, final Column col);

	String getReferencedColumnName(final ForeignKey fk);

	<T extends DataBaseEntry> Class<T> getEntryType(final Class<? extends SQLQueryable<? extends DataBaseEntry>> tableClass);

	<T extends DataBaseEntry> ColumnData[] getPrimaryKeys(final T data);

	<T extends DataBaseEntry> ColumnData[] getGeneratedKeys(final T data);

	<T extends DataBaseEntry> ColumnData[] getPrimaryKeys(final Class<? extends T> entryType);

	<T extends DataBaseEntry> ColumnData[] getGeneratedKeys(final Class<? extends T> entryType);

	String getQueryableName(final Class<? extends SQLQueryable<? extends DataBaseEntry>> tableClass);

	String fieldToColumnName(final String name);

	String fieldToColumnName(final Field field);

	<T extends DataBaseEntry> Field getFieldFor(final Class<T> entryClazz, final String sqlName);

	/*
	 * data entry
	 */
	<T extends DataBaseEntry> String getPreparedInsertSQL(final DataBaseTable<T> table, final T data);

	<T extends DataBaseEntry> String getPreparedUpdateSQL(final DataBaseTable<T> table, final T data);

	<T extends DataBaseEntry> String getPreparedDeleteSQL(final DataBaseTable<T> table, final T data);

	<T extends DataBaseEntry> String getPreparedSelectSQL(final SQLQueryable<T> table, final T data);

	<T extends DataBaseEntry> void prepareInsertSQL(final PreparedStatement stmt, final T data) throws SQLException;

	<T extends DataBaseEntry> void prepareUpdateSQL(final PreparedStatement stmt, final T data) throws SQLException;

	<T extends DataBaseEntry> void prepareDeleteSQL(final PreparedStatement stmt, final T data) throws SQLException;

	<T extends DataBaseEntry> void prepareSelectSQL(final PreparedStatement stmt, final T data) throws SQLException;

	<T extends DataBaseEntry> String getPreparedSelectCountUniqueSQL(
			final SQLQueryable<? extends T> instance,
			final List<String>[] uniqueKeys,
			final T data);

	<T extends DataBaseEntry> void prepareSelectCountUniqueSQL(final PreparedStatement stmt, List<String>[] uniqueKeys, T data)
			throws SQLException;

	<T extends DataBaseEntry> String getPreparedSelectCountNotNullSQL(
			final SQLQueryable<? extends T> instance,
			final List<String> notNullKeys,
			final T data);

	<T extends DataBaseEntry> void prepareSelectCountNotNullSQL(final PreparedStatement stmt, final List<String> notNullKeys, final T data)
			throws SQLException;

	<T extends DataBaseEntry> String getPreparedSelectUniqueSQL(
			final DataBaseTable<T> instance,
			final List<String>[] uniqueKeys,
			final T data);

	<T extends DataBaseEntry> void prepareSelectUniqueSQL(final PreparedStatement stmt, final List<String>[] uniqueKeys, final T data)
			throws SQLException;

}