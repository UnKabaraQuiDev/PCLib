package lu.kbra.pclib.db.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public class SQLBuilder {

	public static int ENTRY_LIMIT = 500;

	public static <T extends DataBaseEntry> String safeInsert(final SQLQueryable<T> table, final String[] columns) {
		return "INSERT INTO " + table.getQualifiedName() + " ("
				+ Arrays.stream(columns).filter(Objects::nonNull).map(i -> "`" + i + "`").collect(Collectors.joining(", ")) + ") VALUES ("
				+ Arrays.stream(columns).map(i -> "?").collect(Collectors.joining(", ")) + ");";
	}

	public static <T extends DataBaseEntry> String safeUpdate(
			final SQLQueryable<T> table,
			final String[] columns,
			final String[] whereColumns) {
		return "UPDATE " + table.getQualifiedName() + " SET "
				+ Arrays.stream(columns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(", ")) + " WHERE "
				+ Arrays.stream(whereColumns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND "))
				+ ";";
	}

	public static <T extends DataBaseEntry> String safeDelete(final SQLQueryable<T> table, final String[] columns) {
		return "DELETE FROM " + table.getQualifiedName() + " WHERE "
				+ Arrays.stream(columns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND ")) + ";";
	}

	public static <T extends DataBaseEntry> String safeSelect(final SQLQueryable<T> table, final String[] whereColumns) {
		return SQLBuilder.safeSelect(table, whereColumns);
	}

	public static <T extends DataBaseEntry> String safeSelect(
			final SQLQueryable<T> table,
			final String[] whereColumns,
			final boolean limit) {
		return SQLBuilder.safeSelect(table, whereColumns, limit, false);
	}

	public static <T extends DataBaseEntry> String safeSelect(
			final SQLQueryable<T> table,
			final String[] whereColumns,
			final boolean limit,
			final boolean offset) {
		return SQLBuilder.safeSelect(table.getQualifiedName(), whereColumns, limit, offset);
	}

	public static <T extends DataBaseEntry> String safeSelect(
			final String name,
			final String[] whereColumns,
			final boolean limit,
			final boolean offset) {
		//@formatter:off
		return "SELECT * FROM " + name +
				(whereColumns == null || whereColumns.length == 0 ? "" : " WHERE " + Arrays.stream(whereColumns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND "))) +
				(limit ? " LIMIT ?" : "") +
				(offset ? " OFFSET ?" : "") +
				";";
		//@formatter:on
	}

	public static <T extends DataBaseEntry> String safeSelectUniqueCollision(
			final SQLQueryable<T> table,
			final List<List<String>> whereColumns) {
		return "SELECT * FROM " + table.getQualifiedName() + " WHERE "
				+ whereColumns.stream()
						.filter(Objects::nonNull)
						.map(l -> l.stream().map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND ", "(", ")")))
						.collect(Collectors.joining(" OR "))
				+ ";";
	}

	public static <T extends DataBaseEntry> String safeSelectCountUniqueCollision(
			final SQLQueryable<T> table,
			final List<List<String>> whereColumns) {
		return "SELECT count(*) as `count` FROM " + table.getQualifiedName() + " WHERE "
				+ whereColumns.stream()
						.filter(Objects::nonNull)
						.map(l -> l.stream().map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND ", "(", ")")))
						.collect(Collectors.joining(" OR "))
				+ ";";
	}

	public static <T extends DataBaseEntry> String count(final SQLQueryable<T> table) {
		return "SELECT count(*) as `count` FROM " + table.getQualifiedName() + ";";
	}

	public static <T extends DataBaseEntry> String count(final SQLQueryable<T> table, final String[] whereColumns) {
		return "SELECT count(*) as `count` FROM " + table.getQualifiedName() + " WHERE "
				+ Arrays.stream(whereColumns).filter(Objects::nonNull).map(i -> "'" + i + "' = ?").collect(Collectors.joining(" AND "))
				+ ";";
	}
}
