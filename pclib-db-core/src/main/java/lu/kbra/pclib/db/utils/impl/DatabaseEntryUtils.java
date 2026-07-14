package lu.kbra.pclib.db.utils.impl;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.entry.Column;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.dialect.SQLFunctionResolver;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.domain.table.SQLQueryableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.utils.HintScanner;
import lu.kbra.pclib.db.utils.registry.ColumnTypeRegistry;

public interface DatabaseEntryUtils extends DatabaseEntryUtilsOptionsOwner {

	String TABLE_NAME_KEY = "NAME";
	String FIELD_NAME_KEY = "FIELD";
	String QUALIFIER_KEY = "Q:";
	String FUNCTION_KEY = "F:";
	/**
	 * for fields {M:...} or {M:...:...}
	 */
	String MEMBER_KEY = "M:";

	String DBMS_FILTER_ALL = "";

	Set<?> EMPTY_SET = Collections.EMPTY_SET;
	Object[] EMPTY_ARRAY = new Object[0];
	Optional<?> EMPTY_OPTIONAL = Optional.empty();
	Map<?, ?> EMPTY_MAP = Collections.EMPTY_MAP;

	default String fieldToColumnName(final Field field) {
		Objects.requireNonNull(field, "field is null.");
		if (!field.isAnnotationPresent(Column.class)) {
			throw new IllegalArgumentException("Field " + field.getName() + " is not annotated with @Column");
		}
		final Column colAnno = field.getAnnotation(Column.class);
		return colAnno.name().isEmpty() ? this.fieldToColumnName(field.getName()) : colAnno.name();
	}

	default String fieldToColumnName(final String name) {
		Objects.requireNonNull(name, "name is null.");
		return this.getStructureVisitor().fieldToColumnName(name);
	}

	/**
	 * Only reload generated keys
	 */
	<T extends DatabaseEntry> void fillInsert(AbstractDBTable<T> table, T data, ResultSet rs) throws SQLException;

	/**
	 * Full reload
	 */
	<T extends DatabaseEntry> void fillLoad(SQLQueryable<T> table, T data, ResultSet rs) throws SQLException;

	<T extends DatabaseEntry> void fillLoadAll(SQLQueryable<T> table, Class<T> entryClazz, ResultSet result, Consumer<T> listExporter)
			throws SQLException;

	<T extends DatabaseEntry> void fillUpdate(AbstractDBTable<T> table, T data, ResultSet rs) throws SQLException;

	default <T extends DatabaseEntry> ColumnData getColumnFor(final SQLQueryable<T> table, final String name) {
		return this.getColumnFor(table.getStructure(), name);
	}

	ColumnData getColumnFor(SQLQueryableStructure structure, String name);

	SQLColumnTypeProvider getColumnTypeProvider();

	String getDbmsQualifierName();

	EntryInstanceProvider getEntryInstanceProvider();

	SQLFunctionResolver getFunctionResolver();

	<T extends DatabaseEntry> String[] getGeneratedColumnNames(final AbstractDBTable<T> table);

	<T extends DatabaseEntry> ColumnData[] getGeneratedKeys(AbstractDBTable<T> table);

	HintScanner getHintScanner();

	<T extends DatabaseEntry> Method getInsertMethod(Class<T> data);

	<T extends DatabaseEntry> Method getLoadMethod(Class<T> data);

	/**
	 * returns the names of the columns that aren't null. ignored primary keys, generated and OnUpdate
	 * columns.
	 */
	<T extends DatabaseEntry> String[] getNonNullKeys(SQLQueryable<T> instance, T data);

	<T extends DatabaseEntry> Map<String, Object> getNonNullValues(SQLQueryable<T> instance, T data);

	<T extends DatabaseEntry> String getPreparedDeleteSQL(AbstractDBTable<T> table, T data);

	/*
	 * data entry
	 */
	<T extends DatabaseEntry> String getPreparedInsertSQL(AbstractDBTable<T> table, T data);

	<T extends DatabaseEntry> String getPreparedSelectCountNotNullSQL(SQLQueryable<T> instance, String[] notNullKeys, T data);

	<T extends DatabaseEntry> String getPreparedSelectCountUniqueSQL(SQLQueryable<T> instance, String[][] uniqueKeys, T data);

	<T extends DatabaseEntry> String getPreparedSelectSQL(SQLQueryable<T> table, T data);

	<T extends DatabaseEntry> String getPreparedSelectUniqueSQL(SQLQueryable<T> instance, String[][] uniqueKeys, T data);

	<T extends DatabaseEntry> String getPreparedUpdateSQL(AbstractDBTable<T> table, T data);

	default String[] getPrimaryKeyNames(final SQLQueryableStructure structure) {
		return Arrays.stream(structure.getColumns()).filter(ColumnData::isPrimaryKey).map(ColumnData::getLocalName).toArray(String[]::new);
	}

	default <T extends DatabaseEntry> String[] getPrimaryKeyNames(final SQLQueryable<T> table) {
		return this.getPrimaryKeyNames(table.getStructure());
	}

	default ColumnData[] getPrimaryKeys(final SQLQueryableStructure structure) {
		return Arrays.stream(structure.getColumns()).filter(ColumnData::isPrimaryKey).toArray(ColumnData[]::new);
	}

	default <T extends DatabaseEntry> ColumnData[] getPrimaryKeys(final SQLQueryable<T> table) {
		return this.getPrimaryKeys(table.getStructure());
	}

	default String[] getForeignKeyNames(final SQLQueryableStructure structure) {
		return Arrays.stream(structure.getColumns()).filter(ColumnData::isForeignKey).map(ColumnData::getLocalName).toArray(String[]::new);
	}

	default <T extends DatabaseEntry> String[] getForeignKeyNames(final SQLQueryable<T> table) {
		return this.getForeignKeyNames(table.getStructure());
	}

	default ColumnData[] getForeignKeys(final SQLQueryableStructure structure) {
		return Arrays.stream(structure.getColumns()).filter(ColumnData::isForeignKey).toArray(ColumnData[]::new);
	}

	default <T extends DatabaseEntry> ColumnData[] getForeignKeys(final SQLQueryable<T> table) {
		return this.getForeignKeys(table.getStructure());
	}

	SQLStructureVisitor getStructureVisitor();

	<T extends DatabaseEntry> String getTruncateSQL(AbstractDBTable<T> queryable);

	default ColumnType getTypeFor(final AnnotatedType parameter) {
		return this.getColumnTypeProvider().getTypeFor(parameter, this.getHintScanner().computeTypeHints(parameter));
	}

	<T extends DatabaseEntry> String[][] getUniqueKeys(AbstractDBTable<T> table, T data);

	<T extends DatabaseEntry> Map<String, Object>[] getUniqueValues(final AbstractDBTable<T> table, final T data);

	<T extends DatabaseEntry> String[] getUpdateColumnsNames(final AbstractDBTable<T> table);

	<T extends DatabaseEntry> ColumnData[] getUpdateGeneratedColumns(final SQLQueryable<T> table);

	<T extends DatabaseEntry> String[] getUpdateGeneratedColumnsNames(final SQLQueryable<T> table);

	default boolean matchesDbmsQualifier(final String dbms) {
		final String trimmed = dbms.trim();
		if (trimmed.isEmpty()) {
			return true;
		}
		return this.getDbmsQualifierName().matches(PCUtils.globToRegex(trimmed));
	}

	default String parameterToColumnName(final Parameter p) {
		if (!p.isAnnotationPresent(Column.class)) {
			if (p.isNamePresent()) {
				return this.fieldToColumnName(p.getName());
			} else {
				throw new DBException("No name present on: " + p + ", add @Column or keep parameter names during compilation.");
			}
		} else {
			final Column colAnno = p.getAnnotation(Column.class);
			if (colAnno.name().isEmpty()) {
				if (p.isNamePresent()) {
					return this.fieldToColumnName(p.getName());
				} else {
					throw new DBException("No name present on: " + p + ", add @Column or keep parameter names during compilation.");
				}
			} else {
				return colAnno.name();
			}
		}
	}

	<T extends DatabaseEntry> void prepareDeleteSQL(PreparedStatement stmt, AbstractDBTable<T> instance, T data) throws SQLException;

	<T extends DatabaseEntry> void prepareInsertSQL(PreparedStatement stmt, AbstractDBTable<T> instance, T data) throws SQLException;

	<T extends DatabaseEntry> void
			prepareSelectCountNotNullSQL(PreparedStatement stmt, SQLQueryable<T> instance, String[] notNullKeys, T data)
					throws SQLException;

	<T extends DatabaseEntry> void
			prepareSelectCountUniqueSQL(PreparedStatement stmt, SQLQueryable<T> instance, String[][] uniqueKeys, T data)
					throws SQLException;

	<T extends DatabaseEntry> void prepareSelectSQL(PreparedStatement stmt, SQLQueryable<T> instance, T data) throws SQLException;

	<T extends DatabaseEntry> void prepareSelectUniqueSQL(PreparedStatement stmt, SQLQueryable<T> instance, String[][] uniqueKeys, T data)
			throws SQLException;

	<T extends DatabaseEntry> void prepareUpdateSQL(PreparedStatement stmt, AbstractDBTable<T> instance, T data) throws SQLException;

	default <T extends DatabaseEntry> String replaceSQLQualifiers(final SQLQueryable<T> table, final String value) {
		return this.replaceSQLQualifiers(table,
				value,
				PCUtils.hashMap(DatabaseEntryUtils.TABLE_NAME_KEY, table.getQualifiedName()),
				s -> Optional.empty());
	}

	<T extends DatabaseEntry> String replaceSQLQualifiers(
			final SQLQueryable<T> table,
			final String input,
			final Map<String, String> data,
			Function<String, Optional<String>> func);

	void setColumnTypeProvider(SQLColumnTypeProvider columnTypeProvider);

	void setEntryInstanceProvider(EntryInstanceProvider entryInstanceProvider);

	void setFunctionResolver(SQLFunctionResolver functionResolver);

	void setHintScanner(HintScanner hintScanner);

	void setStructureVisitor(SQLStructureVisitor structureVisitor);

	void appendTypes(final ColumnTypeRegistry addColumnTypeRegistry);

}
