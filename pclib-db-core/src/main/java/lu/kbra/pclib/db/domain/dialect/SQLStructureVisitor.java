package lu.kbra.pclib.db.domain.dialect;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.table.DataBaseStructure;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLNamed;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.AbstractDBTable;

public interface SQLStructureVisitor {

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String count(B queryable);

	String create(DataBaseStructure db);

	String create(TableStructure table);

	String create(TableStructure table, ColumnData column);

	String create(ViewStructure view);

	String drop(DataBaseStructure dataBaseStructure);

	String drop(TableStructure tableStructure);

	String drop(ViewStructure tableStructure);

	default String fieldToColumnName(final String name) {
		return PCUtils.camelCaseToSnakeCase(name);
	}

	@Deprecated
	default String qualifiedName(final SQLNamed named) {
		Objects.requireNonNull(named, "SQLNamed cannot be null.");
		if (named instanceof SQLQueryable) {
			return this.qualifiedName((SQLQueryable<?>) named);
		}
		return this.qualifiedName(named.getName());
	}

	default <T extends DataBaseEntry> String qualifiedName(final SQLQueryable<T> table) {
		Objects.requireNonNull(table, "SQLQueryable cannot be null.");
		return this.qualifiedName(table.getName());
	}

	String qualifiedName(final String name);

	default String qualifiedName(final String... names) {
		return Arrays.stream(names).map(this::qualifiedName).collect(Collectors.joining("."));
	}

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> String safeDelete(B table, String[] whereColumns);

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> String safeInsert(B table, String[] columns);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String safeSelect(B table, String[] whereColumns);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String safeSelect(B table, String[] columns, String[] whereColumns);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String
			safeSelect(SQLQueryable<T> instance, String[] cols, boolean limit, boolean offset);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String
			safeSelect(SQLQueryable<T> instance, String[] columns, String[] whereColumns, boolean limit, boolean offset);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String safeSelectCountUniqueCollision(B instance, String[][] strings);

	<B extends SQLQueryable<T>, T extends DataBaseEntry> String safeSelectUniqueCollision(B instance, String[][] uniqueKeys);

	<B extends AbstractDBTable<T>, T extends DataBaseEntry> String safeUpdate(B table, String[] setColumns, String[] whereColumns);

}
