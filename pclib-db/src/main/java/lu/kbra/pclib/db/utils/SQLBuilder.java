package lu.kbra.pclib.db.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.query.SQLQueryVisitor;
import lu.kbra.pclib.db.query.SQLQueryVisitors;

public class SQLBuilder {

	public static int ENTRY_LIMIT = 500;

	@FunctionalInterface
	public interface SQLStatement {
		String build(SQLQueryVisitor visitor);
	}

	public static <T extends DataBaseEntry> String safeInsert(final SQLQueryable<T> table, final String[] columns) {
		return SQLBuilder.safeInsertStatement(table, columns).build(SQLQueryVisitors.forNamed(table));
	}

	public static <T extends DataBaseEntry> String
			safeInsert(final SQLQueryVisitor visitor, final SQLQueryable<T> table, final String[] columns) {
		return SQLBuilder.safeInsertStatement(table, columns).build(visitor);
	}

	public static <T extends DataBaseEntry> SQLStatement safeInsertStatement(final SQLQueryable<T> table, final String[] columns) {
		return visitor -> "INSERT INTO " + visitor.qualifiedName(table) + " ("
				+ Arrays.stream(columns).filter(Objects::nonNull).map(visitor::quoteIdentifier).collect(Collectors.joining(", "))
				+ ") VALUES (" + Arrays.stream(columns).map(i -> "?").collect(Collectors.joining(", ")) + ");";
	}

	public static <T extends DataBaseEntry> String
			safeUpdate(final SQLQueryable<T> table, final String[] columns, final String[] whereColumns) {
		return SQLBuilder.safeUpdateStatement(table, columns, whereColumns).build(SQLQueryVisitors.forNamed(table));
	}

	public static <T extends DataBaseEntry> String
			safeUpdate(final SQLQueryVisitor visitor, final SQLQueryable<T> table, final String[] columns, final String[] whereColumns) {
		return SQLBuilder.safeUpdateStatement(table, columns, whereColumns).build(visitor);
	}

	public static <T extends DataBaseEntry> SQLStatement
			safeUpdateStatement(final SQLQueryable<T> table, final String[] columns, final String[] whereColumns) {
		return visitor -> "UPDATE " + visitor.qualifiedName(table) + " SET "
				+ Arrays.stream(columns)
						.filter(Objects::nonNull)
						.map(i -> visitor.quoteIdentifier(i) + " = ?")
						.collect(Collectors.joining(", "))
				+ " WHERE "
				+ Arrays.stream(whereColumns)
						.filter(Objects::nonNull)
						.map(i -> visitor.quoteIdentifier(i) + " = ?")
						.collect(Collectors.joining(" AND "))
				+ ";";
	}

	public static <T extends DataBaseEntry> String safeDelete(final SQLQueryable<T> table, final String[] columns) {
		return SQLBuilder.safeDeleteStatement(table, columns).build(SQLQueryVisitors.forNamed(table));
	}

	public static <T extends DataBaseEntry> String
			safeDelete(final SQLQueryVisitor visitor, final SQLQueryable<T> table, final String[] columns) {
		return SQLBuilder.safeDeleteStatement(table, columns).build(visitor);
	}

	public static <T extends DataBaseEntry> SQLStatement safeDeleteStatement(final SQLQueryable<T> table, final String[] columns) {
		return visitor -> "DELETE FROM " + visitor.qualifiedName(table) + " WHERE "
				+ Arrays.stream(columns)
						.filter(Objects::nonNull)
						.map(i -> visitor.quoteIdentifier(i) + " = ?")
						.collect(Collectors.joining(" AND "))
				+ ";";
	}

	public static <T extends DataBaseEntry> String safeSelect(final SQLQueryable<T> table, final String[] whereColumns) {
		return SQLBuilder.safeSelect(table, whereColumns, false);
	}

	public static <T extends DataBaseEntry> String
			safeSelect(final SQLQueryable<T> table, final String[] whereColumns, final boolean limit) {
		return SQLBuilder.safeSelect(table, whereColumns, limit, false);
	}

	public static <T extends DataBaseEntry> String
			safeSelect(final SQLQueryable<T> table, final String[] whereColumns, final boolean limit, final boolean offset) {
		return SQLBuilder.safeSelectStatement(table, whereColumns, limit, offset).build(SQLQueryVisitors.forNamed(table));
	}

	public static <T extends DataBaseEntry> String safeSelect(
			final SQLQueryVisitor visitor,
			final SQLQueryable<T> table,
			final String[] whereColumns,
			final boolean limit,
			final boolean offset) {
		return SQLBuilder.safeSelectStatement(table, whereColumns, limit, offset).build(visitor);
	}

	public static <T extends DataBaseEntry> SQLStatement
			safeSelectStatement(final SQLQueryable<T> table, final String[] whereColumns, final boolean limit, final boolean offset) {
		return visitor -> SQLBuilder.buildSafeSelect(visitor, visitor.qualifiedName(table), whereColumns, limit, offset);
	}

	public static <T extends DataBaseEntry> String
			safeSelect(final String name, final String[] whereColumns, final boolean limit, final boolean offset) {
		return SQLBuilder.safeSelectStatement(name, whereColumns, limit, offset).build(SQLQueryVisitors.defaultVisitor());
	}

	public static String safeSelect(
			final SQLQueryVisitor visitor,
			final String name,
			final String[] whereColumns,
			final boolean limit,
			final boolean offset) {
		return SQLBuilder.safeSelectStatement(name, whereColumns, limit, offset).build(visitor);
	}

	public static SQLStatement
			safeSelectStatement(final String name, final String[] whereColumns, final boolean limit, final boolean offset) {
		return visitor -> SQLBuilder.buildSafeSelect(visitor, visitor.qualifiedName(name), whereColumns, limit, offset);
	}

	private static String buildSafeSelect(
			final SQLQueryVisitor visitor,
			final String name,
			final String[] whereColumns,
			final boolean limit,
			final boolean offset) {
		//@formatter:off
		return "SELECT * FROM " + name +
				(whereColumns == null || whereColumns.length == 0 ? "" : " WHERE " + Arrays.stream(whereColumns).filter(Objects::nonNull).map(i -> visitor.quoteIdentifier(i) + " = ?").collect(Collectors.joining(" AND "))) +
				(limit ? " LIMIT ?" : "") +
				(offset ? " OFFSET ?" : "") +
				";";
		//@formatter:on
	}

	public static <T extends DataBaseEntry> String
			safeSelectUniqueCollision(final SQLQueryable<T> table, final List<List<String>> whereColumns) {
		return SQLBuilder.safeSelectUniqueCollisionStatement(table, whereColumns).build(SQLQueryVisitors.forNamed(table));
	}

	public static <T extends DataBaseEntry> SQLStatement
			safeSelectUniqueCollisionStatement(final SQLQueryable<T> table, final List<List<String>> whereColumns) {
		return visitor -> "SELECT * FROM " + visitor.qualifiedName(table) + " WHERE "
				+ whereColumns.stream()
						.filter(Objects::nonNull)
						.map(l -> l.stream().map(i -> visitor.quoteIdentifier(i) + " = ?").collect(Collectors.joining(" AND ", "(", ")")))
						.collect(Collectors.joining(" OR "))
				+ ";";
	}

	public static <T extends DataBaseEntry> String
			safeSelectCountUniqueCollision(final SQLQueryable<T> table, final List<List<String>> whereColumns) {
		return SQLBuilder.safeSelectCountUniqueCollisionStatement(table, whereColumns).build(SQLQueryVisitors.forNamed(table));
	}

	public static <T extends DataBaseEntry> SQLStatement
			safeSelectCountUniqueCollisionStatement(final SQLQueryable<T> table, final List<List<String>> whereColumns) {
		return visitor -> "SELECT count(*) as " + visitor.quoteIdentifier("count") + " FROM " + visitor.qualifiedName(table) + " WHERE "
				+ whereColumns.stream()
						.filter(Objects::nonNull)
						.map(l -> l.stream().map(i -> visitor.quoteIdentifier(i) + " = ?").collect(Collectors.joining(" AND ", "(", ")")))
						.collect(Collectors.joining(" OR "))
				+ ";";
	}

	public static <T extends DataBaseEntry> String count(final SQLQueryable<T> table) {
		return SQLBuilder.countStatement(table).build(SQLQueryVisitors.forNamed(table));
	}

	public static <T extends DataBaseEntry> SQLStatement countStatement(final SQLQueryable<T> table) {
		return visitor -> "SELECT count(*) as " + visitor.quoteIdentifier("count") + " FROM " + visitor.qualifiedName(table) + ";";
	}

	public static <T extends DataBaseEntry> String count(final SQLQueryable<T> table, final String[] whereColumns) {
		return SQLBuilder.countStatement(table, whereColumns).build(SQLQueryVisitors.forNamed(table));
	}

	public static <T extends DataBaseEntry> SQLStatement countStatement(final SQLQueryable<T> table, final String[] whereColumns) {
		return visitor -> "SELECT count(*) as " + visitor.quoteIdentifier("count") + " FROM " + visitor.qualifiedName(table) + " WHERE "
				+ Arrays.stream(whereColumns)
						.filter(Objects::nonNull)
						.map(i -> visitor.quoteIdentifier(i) + " = ?")
						.collect(Collectors.joining(" AND "))
				+ ";";
	}

	@Deprecated
	private static <T extends DataBaseEntry> String escapeIdentifier(final SQLQueryable<T> table, final String identifier) {
		return SQLQueryVisitors.forNamed(table).quoteIdentifier(identifier);
	}

}
