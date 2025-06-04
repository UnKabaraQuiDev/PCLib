package lu.pcy113.pclib.db.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.autobuild.column.Column;
import lu.pcy113.pclib.db.autobuild.column.ColumnData;
import lu.pcy113.pclib.db.autobuild.column.ForeignKey;
import lu.pcy113.pclib.db.autobuild.column.type.ColumnType;
import lu.pcy113.pclib.db.autobuild.query.Query;
import lu.pcy113.pclib.db.autobuild.table.ConstraintData;
import lu.pcy113.pclib.db.autobuild.table.TableStructure;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;

public interface DataBaseEntryUtils {

	/**
	 * Only reload generated keys
	 */
	<T extends DataBaseEntry> void fillInsert(T data, ResultSet rs) throws SQLException;

	<T extends DataBaseEntry> Method getInsertMethod(T data);

	<T extends DataBaseEntry> Method getInsertMethod(Class<T> data);

	<T extends DataBaseEntry> void fillUpdate(T data, ResultSet rs) throws SQLException;

	<T extends DataBaseEntry> Method getUpdateMethod(T data);

	<T extends DataBaseEntry> Method getUpdateMethod(Class<T> data);

	/**
	 * Full reload
	 */
	<T extends DataBaseEntry> void fillLoad(T data, ResultSet rs) throws SQLException;

	<T extends DataBaseEntry> Method getLoadMethod(T data);

	<T extends DataBaseEntry> Method getLoadMethod(Class<T> data);

	<T extends DataBaseEntry> T fillLoadCopy(T data, ResultSet rs) throws SQLException;

	<T extends DataBaseEntry> T instance(T data);

	<T extends DataBaseEntry> T instance(Class<T> clazz);

	<T extends DataBaseEntry> void fillLoadAll(T data, ResultSet result, Consumer<T> listExporter) throws SQLException;

	<T extends DataBaseEntry> void fillLoadAllTable(Class<? extends SQLQueryable<T>> tableClazz, SQLQuery<T, ?> query, ResultSet result, Consumer<T> listExporter) throws SQLException;

	<T extends DataBaseEntry> Map<String, Object>[] getUniqueKeys(ConstraintData[] allConstraints, T data);

	/*
	 * scanning
	 */
	<T extends DataBaseEntry> TableStructure scanTable(Class<? extends DataBaseTable<T>> data);

	<T extends DataBaseEntry> TableStructure scanEntry(Class<T> data);

	<T extends DataBaseEntry> void initQueries(SQLQueryable<T> instance);

	ColumnType getTypeFor(Field field);

	ColumnType getTypeFor(Class<?> clazz, Column col);

	String getReferencedColumnName(ForeignKey fk);

	<T extends DataBaseEntry> Class<T> getEntryType(Class<? extends SQLQueryable<T>> tableClass);

	<T extends DataBaseEntry> ColumnData[] getPrimaryKeys(T data);

	<T extends DataBaseEntry> ColumnData[] getPrimaryKeys(Class<? extends T> entryType);

	<T extends DataBaseEntry> String getQueryableName(Class<? extends SQLQueryable<T>> tableClass);

	String fieldToColumnName(String name);

	String fieldToColumnName(Field field);

	<T extends DataBaseEntry> Object buildTableQueryFunction(Class<? extends SQLQueryable<T>> tableClazz, String tableName, SQLQueryable<T> instance, Type type, Query query);

	// <T extends DataBaseEntry> Object buildEntryQueryFunction(Class<T> entryClazz, String tableName, Type type, Query query);

	<T extends DataBaseEntry> Function<List<Object>, ?> buildMethodQueryFunction(String tableName, SQLQueryable<T> instance, Method method);

	/*
	 * data entry
	 */
	<T extends DataBaseEntry> String getPreparedInsertSQL(DataBaseTable<T> table, T data);

	<T extends DataBaseEntry> String getPreparedUpdateSQL(DataBaseTable<T> table, T data);

	<T extends DataBaseEntry> String getPreparedDeleteSQL(DataBaseTable<T> table, T data);

	<T extends DataBaseEntry> String getPreparedSelectSQL(SQLQueryable<T> table, T data);

	<T extends DataBaseEntry> void prepareInsertSQL(PreparedStatement stmt, T data) throws SQLException;

	<T extends DataBaseEntry> void prepareUpdateSQL(PreparedStatement stmt, T data) throws SQLException;

	<T extends DataBaseEntry> void prepareDeleteSQL(PreparedStatement stmt, T data) throws SQLException;

	<T extends DataBaseEntry> void prepareSelectSQL(PreparedStatement stmt, T data) throws SQLException;

}