package lu.pcy113.pclib.db.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery.SafeSQLQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;

public abstract class SimpleSQLQuery<T extends DataBaseEntry> implements SafeSQLQuery<T> {

	public static class MapSimpleSQLQuery<T extends DataBaseEntry> extends SimpleSQLQuery<T> {

		private final String sql;
		private final String[] cols;
		private final Map<String, Object> values;

		public MapSimpleSQLQuery(String sql, String[] cols, Map<String, Object> values) {
			this.sql = sql;
			this.cols = cols;
			this.values = values;
			
			System.err.println(sql);
			System.err.println(Arrays.toString(cols));
			System.err.println(values);

			if (!Arrays.stream(cols).allMatch(values::containsKey)) {
				throw new IllegalArgumentException("Missing values for some columns (expecting: " + Arrays.toString(cols) + ", but got: " + values.keySet() + ")");
			}
		}

		@Override
		public String getPreparedQuerySQL(SQLQueryable<T> table) {
			return sql;
		}

		@Override
		public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < cols.length; i++) {
				stmt.setObject(i + 1, values.get(cols[i]));
			}
		}

		@Override
		public String toString() {
			return "MapSimpleSQLQuery [sql=" + sql + ", cols=" + Arrays.toString(cols) + ", values=" + values + "]";
		}

	}

	public static class ListSimpleSQLQuery<T extends DataBaseEntry> extends SimpleSQLQuery<T> {

		private final String sql;
		private final List<Object> values;

		public ListSimpleSQLQuery(String sql, List<Object> values) {
			this.sql = sql;
			this.values = values;
		}

		@Override
		public String getPreparedQuerySQL(SQLQueryable<T> table) {
			return sql;
		}

		@Override
		public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < values.size(); i++) {
				stmt.setObject(i + 1, values.get(i));
			}
		}

		@Override
		public String toString() {
			return "ListSimpleSQLQuery [sql=" + sql + ", values=" + values + "]";
		}

	}

}
