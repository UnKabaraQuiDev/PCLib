package lu.kbra.pclib.db.domain.dialect;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.DatabaseStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;

public interface SQLStructureVisitor extends SQLStructureVisitorOptionsOwner {

	<B extends SQLQueryable<T>, T extends DatabaseEntry> String count(B queryable);

	String create(DatabaseStructure db);

	String[] create(TableStructure table);

	String create(TableStructure table, ColumnData column);

	String[] create(ViewStructure view);

	String drop(DatabaseStructure databaseStructure);

	String drop(TableStructure tableStructure);

	String drop(ViewStructure tableStructure);

	default String fieldToColumnName(final String name) {
		return PCUtils.camelCaseToSnakeCase(name);
	}

	String getQueryableName(Class<? extends SQLQueryable<?>> tableClass, Map<String, Object> queryableHints);

	String[] getQueryableNameParts(Class<? extends SQLQueryable<?>> tableClazz, Map<String, Object> queryableHints);

	<T extends DatabaseEntry> String getTruncateSQL(AbstractDBTable<T> queryable);

	String qualifiedName(Class<? extends SQLQueryable<?>> clazz, Map<String, Object> queryableHints);

	String qualifiedName(final String name);

	default String qualifiedName(final String... names) {
		return Arrays.stream(names).map(this::qualifiedName).collect(Collectors.joining("."));
	}

	<B extends AbstractDBTable<T>, T extends DatabaseEntry> String safeDelete(B table, String[] whereColumns);

	<B extends AbstractDBTable<T>, T extends DatabaseEntry> String safeInsert(B table, String[] columns);

	<B extends SQLQueryable<T>, T extends DatabaseEntry> String safeSelect(B table, String[] whereColumns);

	<B extends SQLQueryable<T>, T extends DatabaseEntry> String safeSelect(B table, String[] columns, String[] whereColumns);

	<B extends SQLQueryable<T>, T extends DatabaseEntry> String
			safeSelect(SQLQueryable<T> instance, String[] cols, boolean limit, boolean offset);

	<B extends SQLQueryable<T>, T extends DatabaseEntry> String
			safeSelect(SQLQueryable<T> instance, String[] columns, String[] whereColumns, boolean limit, boolean offset);

	<B extends SQLQueryable<T>, T extends DatabaseEntry> String safeSelectCountUniqueCollision(B instance, String[][] strings);

	<B extends SQLQueryable<T>, T extends DatabaseEntry> String safeSelectUniqueCollision(B instance, String[][] uniqueKeys);

	<B extends AbstractDBTable<T>, T extends DatabaseEntry> String safeUpdate(B table, String[] setColumns, String[] whereColumns);

	<B extends AbstractDBTable<T>, T extends DatabaseEntry> String safeUpdateExpr(B table, String[] setColumns, String[] whereColumns);

	default <B extends SQLQueryable<T>, T extends DatabaseEntry> String schemaName(final B table) {
		return null;
	}

}
