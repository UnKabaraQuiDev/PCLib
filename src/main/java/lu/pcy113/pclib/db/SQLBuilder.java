package lu.pcy113.pclib.db;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lu.pcy113.pclib.db.impl.SQLEntry;

public class SQLBuilder {

	public static int ENTRY_LIMIT = 500;
	
	public static <T extends SQLEntry> String safeInsert(DataBaseTable<T> table, String[] columns) {
		return "INSERT INTO `" + table.getTableName() + "` (" + Arrays.stream(columns).filter(Objects::nonNull).map(i -> "`" + i + "`").collect(Collectors.joining(", ")) + ") VALUES (" + Arrays.stream(columns).map((i) -> "?").collect(Collectors.joining(", ")) + ");";
	}

	public static <T extends SQLEntry> String safeUpdate(DataBaseTable<T> table, String[] columns, String[] whereColumns) {
		return "UPDATE `" + table.getTableName() + "` SET " + Arrays.stream(columns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(", ")) + " WHERE "
				+ Arrays.stream(whereColumns).map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND ")) + ";";
	}

	public static <T extends SQLEntry> String safeDelete(DataBaseTable<T> table, String[] columns) {
		return "DELETE FROM `" + table.getTableName() + "` WHERE " + Arrays.stream(columns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND ")) + ";";
	}

	public static  <T extends SQLEntry> String safeSelect(DataBaseTable<T> table, String[] whereColumns) {
		return safeSelect(table, whereColumns, -1);
	}
	
	public static  <T extends SQLEntry> String safeSelect(DataBaseTable<T> table, String[] whereColumns, int limit) {
		return "SELECT * FROM `" + table.getTableName() + "`" + (whereColumns == null || whereColumns.length == 0 ? "" : " WHERE " + Arrays.stream(whereColumns).filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(" AND ")))
														+ (limit > -1 ? " LIMIT " + limit : " LIMIT "+ENTRY_LIMIT)
														+ ";";
	}

	public static <T extends SQLEntry> String safeSelectUniqueCollision(DataBaseTable<T> table, Stream<String> whereColumns) {
		return "SELECT count(*) as `count` FROM `" + table.getTableName() + "` WHERE " + whereColumns.filter(Objects::nonNull).map(i -> "`" + i + "` = ?").collect(Collectors.joining(" OR ")) + ";";
	}

	public static <T extends SQLEntry> String count(DataBaseTable<T> table) {
		return "SELECT count(*) as `count` FROM `" + table.getTableName() + "`;";
	}

}
