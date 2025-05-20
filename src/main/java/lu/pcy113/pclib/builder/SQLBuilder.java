package lu.pcy113.pclib.builder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLQueryable;

public class SQLBuilder {

	public static int ENTRY_LIMIT = 500;

	public static <T extends SQLEntry> String safeInsert(SQLQueryable<T> table, String[] columns) {
		return "INSERT INTO " + table.getQualifiedName() + " (" + Arrays.stream(columns).filter(Objects::nonNull).map(i -> "`" + i + "`").collect(Collectors.joining(", ")) + ") VALUES ("
				+ Arrays.stream(columns).map((i) -> "?").collect(Collectors.joining(", ")) + ");";
	}

	public static <T extends SQLEntry> String safeUpdate(SQLQueryable<T> table, String[] columns, String[] whereColumns) {
		return "UPDATE " + table.getQualifiedName() + " SET " + Arrays.stream(columns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(", ")) + " WHERE "
				+ Arrays.stream(whereColumns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND ")) + ";";
	}

	public static <T extends SQLEntry> String safeDelete(SQLQueryable<T> table, String[] columns) {
		return "DELETE FROM " + table.getQualifiedName() + " WHERE " + Arrays.stream(columns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND ")) + ";";
	}

	public static <T extends SQLEntry> String safeSelect(SQLQueryable<T> table, String[] whereColumns) {
		return safeSelect(table, whereColumns, -1);
	}

	public static <T extends SQLEntry> String safeSelect(SQLQueryable<T> table, String[] whereColumns, int limit) {
		return "SELECT * FROM " + table.getQualifiedName()
				+ (whereColumns == null || whereColumns.length == 0 ? "" : " WHERE " + Arrays.stream(whereColumns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND ")))
				+ (limit > -1 ? " LIMIT " + limit : " LIMIT " + ENTRY_LIMIT) + ";";
	}

	public static <T extends SQLEntry> String safeSelectUniqueCollision(SQLQueryable<T> table, List<Set<String>> whereColumns) {
		return "SELECT count(*) as `count` FROM " + table.getQualifiedName() + " WHERE "
				+ whereColumns.stream().filter(Objects::nonNull).map(l -> l.stream().map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND ", "(", ")"))).collect(Collectors.joining(" OR ")) + ";";
	}

	public static <T extends SQLEntry> String count(SQLQueryable<T> table) {
		return "SELECT count(*) as `count` FROM " + table.getQualifiedName() + ";";
	}
	
	public static <T extends SQLEntry> String count(SQLQueryable<T> table, String[] whereColumns) {
		return "SELECT count(*) as `count` FROM " + table.getQualifiedName() + " WHERE " + Arrays.stream(whereColumns).filter(Objects::nonNull).map(i -> "'"+i+"' = ?").collect(Collectors.joining(" AND ")) + ";";
	}
}
