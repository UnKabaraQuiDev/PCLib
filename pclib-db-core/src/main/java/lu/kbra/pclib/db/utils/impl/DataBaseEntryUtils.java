package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.SQLColumnTypeProvider;
import lu.kbra.pclib.db.utils.registry.ColumnTypeFactory;

public interface DataBaseEntryUtils extends DataBaseEntryUtilsOptionsOwner {

	String TABLE_NAME_KEY = "NAME";
	String FIELD_NAME_KEY = "FIELD";
	String QUALIFIER_KEY = "Q:";
	String FUNCTION_KEY = "F:";
	/**
	 * {M:...} or {M:...:...}
	 */
	String MEMBER_KEY = "M:";

	Stream<ColumnTypeFactory> computeType(Class<?> rawType, Map<String, Object> hints);

	/**
	 * Only reload generated keys
	 */
	<B extends SQLQueryable<T>, T extends DataBaseEntry> void fillInsert(B table, T data, ResultSet rs) throws SQLException;

	/**
	 * Full reload
	 */
	<B extends SQLQueryable<T>, T extends DataBaseEntry> void fillLoad(B table, T data, ResultSet rs) throws SQLException;

	<B extends SQLQueryable<T>, T extends DataBaseEntry> void
			fillLoadAll(B table, Class<T> entryClazz, ResultSet result, Consumer<T> listExporter) throws SQLException;

	<B extends SQLQueryable<T>, T extends DataBaseEntry> void fillUpdate(B table, T data, ResultSet rs) throws SQLException;

	String getDbmsQualifierName();

	<T extends DataBaseEntry> Class<T> getEntryType(Class<? extends SQLQueryable<?>> tableClass);

	<T extends DataBaseEntry> Field getFieldFor(Class<T> entryClazz, String sqlName);

	SQLFunctionResolver getFunctionResolver();

	<B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[] getGeneratedKeys(Class<T> entryClazz, Class<B> tableClazz);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String[]
			getGeneratedColumnNames(final Class<T> entryClazz, final Class<B> tableClazz);

	<T extends DataBaseEntry> Method getInsertMethod(Class<T> data);

	<T extends DataBaseEntry> Method getLoadMethod(Class<T> data);

	/**
	 * returns the names of the columns that aren't null. ignored primary keys, generated and OnUpdate
	 * columns.
	 */
	<B extends SQLQueryable<T>, T extends DataBaseEntry> String[] getNonNullKeys(B instance, T data);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> Map<String, Object> getNonNullValues(B instance, T data);

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> String getPreparedDeleteSQL(B table, T data);

	/*
	 * data entry
	 */
	<B extends AbstractDBTable<T>, T extends DataBaseEntry> String getPreparedInsertSQL(B table, T data);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String getPreparedSelectCountNotNullSQL(B instance, String[] notNullKeys, T data);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String getPreparedSelectCountUniqueSQL(B instance, String[][] uniqueKeys, T data);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String getPreparedSelectSQL(B table, T data);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String getPreparedSelectUniqueSQL(B instance, String[][] uniqueKeys, T data);

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> String getPreparedUpdateSQL(B table, T data);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> ColumnData[] getPrimaryKeys(Class<T> entryClazz, Class<B> tableClazz);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String[] getPrimaryKeysNames(Class<T> entryClazz, Class<B> tableClazz);

	String getQueryableName(final Class<? extends SQLQueryable<?>> tableClass);

	String getReferencedColumnName(final ColumnData columnData);

	SQLColumnTypeProvider getColumnTypeProvider();
	
	default ColumnType getTypeFor(Field field) {
		return getColumnTypeProvider().getTypeFor(field.getAnnotatedType());
	}

	default ColumnType getTypeFor(Parameter param) {
		return this.getTypeFor(param.getAnnotatedType());
	}
	
	default ColumnType getTypeFor(AnnotatedType type) {
		return getTypeFor(type, Collections.emptyMap());
	}

	
	SQLStructureVisitor getStructureVisitor();

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> String getTruncateSQL(B queryable);

	Map<String, Object> getTypeHints(AnnotatedType type);

	<T extends DataBaseEntry> String[][] getUniqueKeys(ConstraintData[] allConstraints, T data);

	<T extends DataBaseEntry> Map<String, Object>[] getUniqueValues(ConstraintData[] allConstraints, T data);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String[] getUpdateColumnsNames(Class<T> entryClazz, Class<B> tableClazz);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String[]
			getUpdateGeneratedColumnsNames(final Class<T> entryClazz, final Class<B> tableClazz);

	<T extends DataBaseEntry> Method getUpdateMethod(Class<T> data);

	<T extends DataBaseEntry> Method getUpdateMethod(T data);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> T instance(Class<T> entryClazz, Class<B> tableClazz);

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> void prepareDeleteSQL(PreparedStatement stmt, B instance, T data)
			throws SQLException;

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> void prepareInsertSQL(PreparedStatement stmt, B instance, T data)
			throws SQLException;

	<B extends SQLQueryable<T>, T extends DataBaseEntry> void
			prepareSelectCountNotNullSQL(PreparedStatement stmt, B instance, String[] notNullKeys, T data) throws SQLException;

	<B extends SQLQueryable<T>, T extends DataBaseEntry> void
			prepareSelectCountUniqueSQL(PreparedStatement stmt, B instance, String[][] uniqueKeys, T data) throws SQLException;

	<B extends SQLQueryable<T>, T extends DataBaseEntry> void prepareSelectSQL(PreparedStatement stmt, B instance, T data)
			throws SQLException;

	<B extends SQLQueryable<T>, T extends DataBaseEntry> void
			prepareSelectUniqueSQL(PreparedStatement stmt, B instance, String[][] uniqueKeys, T data) throws SQLException;

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> void prepareUpdateSQL(PreparedStatement stmt, B instance, T data)
			throws SQLException;

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String qualifiedName(Class<B> typeName);

	String replaceSQLQualifiers(final Class<? extends SQLQueryable<?>> tableClazz, final String input, final Map<String, String> data);

	default <B extends SQLQueryable<T>, T extends DataBaseEntry> String replaceSQLQualifiers(Class<B> tableClazz, String value) {
		return replaceSQLQualifiers(tableClazz, value, Collections.emptyMap());
	}

	DataBaseStructure scanDataBase(DataBase dataBase, Map<String, Object> baseHints);

	TableStructure getTableStructure(Class<? extends SQLQueryable<?>> clazz);

}
