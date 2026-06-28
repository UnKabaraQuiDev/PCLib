package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import lu.kbra.pclib.db.annotations.entry.ForeignKey;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.table.ConstraintData;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.registry.ColumnTypeFactory;

public interface DataBaseEntryUtils extends DataBaseEntryUtilsOptionsOwner {

	Stream<ColumnTypeFactory> computeType(final Class<?> rawType, Map<String, Object> hints);

	String fieldToColumnName(final Field field);

	String fieldToColumnName(final String name);

	/**
	 * Only reload generated keys
	 */
	<T extends DataBaseEntry> void fillInsert(final T data, final ResultSet rs) throws SQLException;

	/**
	 * Full reload
	 */
	<T extends DataBaseEntry> void fillLoad(final T data, final ResultSet rs) throws SQLException;

	<T extends DataBaseEntry> void fillLoadAll(final Class<T> entryClazz, final ResultSet result, final Consumer<T> listExporter)
			throws SQLException;

	<T extends DataBaseEntry> void fillUpdate(final T data, final ResultSet rs) throws SQLException;

	String getDbmsQualifierName();

	<T extends DataBaseEntry> Class<T> getEntryType(final Class<? extends SQLQueryable<?>> tableClass);

	<T extends DataBaseEntry> Field getFieldFor(final Class<T> entryClazz, final String sqlName);

	<T extends DataBaseEntry> ColumnData[] getGeneratedKeys(final Class<T> entryClazz);

	<T extends DataBaseEntry> ColumnData[] getGeneratedKeys(final T data);

	<T extends DataBaseEntry> Method getInsertMethod(final Class<T> data);

	<T extends DataBaseEntry> Method getInsertMethod(final T data);

	<T extends DataBaseEntry> Method getLoadMethod(final Class<T> data);

	<T extends DataBaseEntry> Method getLoadMethod(final T data);

	/**
	 * returns the names of the columns that aren't null. ignored primary keys, generated and OnUpdate
	 * columns.
	 */
	<T extends DataBaseEntry> String[] getNonNullKeys(final T data);

	<T extends DataBaseEntry> Map<String, Object> getNonNullValues(final T data);

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> String getPreparedDeleteSQL(final B table, final T data);

	/*
	 * data entry
	 */
	<T extends DataBaseEntry> String getPreparedInsertSQL(final AbstractDBTable<T> table, final T data);

	<T extends DataBaseEntry> String
			getPreparedSelectCountNotNullSQL(final SQLQueryable<? extends T> instance, final String[] notNullKeys, final T data);

	<T extends DataBaseEntry> String
			getPreparedSelectCountUniqueSQL(final SQLQueryable<? extends T> instance, final String[][] uniqueKeys, final T data);

	<T extends DataBaseEntry> String getPreparedSelectSQL(final SQLQueryable<T> table, final T data);

	<T extends DataBaseEntry> String
			getPreparedSelectUniqueSQL(final AbstractDBTable<T> instance, final String[][] uniqueKeys, final T data);

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> String getPreparedUpdateSQL(final B table, final T data);

	<T extends DataBaseEntry> ColumnData[] getPrimaryKeys(final Class<T> entryType);

	<T extends DataBaseEntry> ColumnData[] getPrimaryKeys(final T data);

	<T extends DataBaseEntry> String[] getPrimaryKeysNames(final Class<T> entryClazz);

	Map<String, Object> getQueryableHints(Class<?> tableClazz);

	<V extends SQLQueryable<T>, T extends DataBaseEntry> String getQueryableName(final Class<V> tableClass);

	String getReferencedColumnName(final ForeignKey fk);

	SQLStructureVisitor getStructureVisitor();

	ColumnType getTypeFor(final AnnotatedType type);

	ColumnType getTypeFor(AnnotatedType annotatedType, Map<String, Object> typeHints);

	ColumnType getTypeFor(final Class<?> clazz, final Optional<AnnotatedType> type, final Map<String, Object> typeHints);

	ColumnType getTypeFor(final Field field);

	default ColumnType getTypeFor(final Parameter param) {
		return this.getTypeFor(param.getAnnotatedType());
	}

	Map<String, Object> getTypeHints(final AnnotatedType type);

	<T extends DataBaseEntry> String[][] getUniqueKeys(final ConstraintData[] allConstraints, final T data);

	<T extends DataBaseEntry> Map<String, Object>[] getUniqueValues(final ConstraintData[] allConstraints, final T data);

	<T extends DataBaseEntry> String[] getUpdateColumnsNames(final Class<T> entryClazz);

	<T extends DataBaseEntry> Method getUpdateMethod(final Class<T> data);

	<T extends DataBaseEntry> Method getUpdateMethod(final T data);

	<T extends DataBaseEntry> T instance(final Class<T> clazz);

	<T extends DataBaseEntry> T instance(final T data);

	<T extends DataBaseEntry> void prepareDeleteSQL(final PreparedStatement stmt, final T data) throws SQLException;

	<T extends DataBaseEntry> void prepareInsertSQL(final PreparedStatement stmt, final T data) throws SQLException;

	<T extends DataBaseEntry> void prepareSelectCountNotNullSQL(final PreparedStatement stmt, final String[] notNullKeys, final T data)
			throws SQLException;

	<T extends DataBaseEntry> void prepareSelectCountUniqueSQL(final PreparedStatement stmt, String[][] uniqueKeys, T data)
			throws SQLException;

	<T extends DataBaseEntry> void prepareSelectSQL(final PreparedStatement stmt, final T data) throws SQLException;

	<T extends DataBaseEntry> void prepareSelectUniqueSQL(final PreparedStatement stmt, final String[][] uniqueKeys, final T data)
			throws SQLException;

	<T extends DataBaseEntry> void prepareUpdateSQL(final PreparedStatement stmt, final T data) throws SQLException;

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String replaceQualifiers(String input, B instance);

	DataBaseStructure scanDataBase(DataBase dataBase, Map<String, Object> baseHints);

	<T extends DataBaseEntry> TableStructure scanEntry(Class<? extends AbstractDBTable<T>> tableClazz, final Class<T> data);

	/*
	 * scanning
	 */
	<T extends DataBaseEntry> TableStructure scanTable(final Class<? extends AbstractDBTable<T>> data);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String qualifiedName(Class<B> typeName);

}
