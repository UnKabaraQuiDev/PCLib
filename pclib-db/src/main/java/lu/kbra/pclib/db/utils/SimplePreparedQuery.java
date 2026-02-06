package lu.kbra.pclib.db.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;

public abstract class SimplePreparedQuery<T extends DataBaseEntry> implements PreparedQuery<T> {

	public static class MapSimplePreparedQuery<T extends DataBaseEntry> extends SimplePreparedQuery<T> {

		private final String sql;
		private final String[] cols;
		private final Map<String, Object> values;

		public MapSimplePreparedQuery(String sql, String[] cols, Map<String, Object> values) {
			this.sql = sql;
			this.cols = cols;
			this.values = values;

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
			return "MapPreparedSimpleSQLQuery [sql=" + sql + ", cols=" + Arrays.toString(cols) + ", values=" + values + "]";
		}

	}

	public static class ListSimplePreparedQuery<T extends DataBaseEntry> extends SimplePreparedQuery<T> {

		private final String sql;
		private final List<Object> values;

		public ListSimplePreparedQuery(String sql, List<Object> values) {
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
			return "ListPreparedSimpleQuery [sql=" + sql + ", values=" + values + "]";
		}

	}

	public static class ArraySimplePreparedQuery<T extends DataBaseEntry> extends SimplePreparedQuery<T> {

		private final String sql;
		private final Object[] values;

		public ArraySimplePreparedQuery(String sql, Object[] values) {
			this.sql = sql;
			this.values = values;
		}

		@Override
		public String getPreparedQuerySQL(SQLQueryable<T> table) {
			return sql;
		}

		@Override
		public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < values.length; i++) {
				stmt.setObject(i + 1, values[i]);
			}
		}

		@Override
		public String toString() {
			return "ArrayPreparedSimpleQuery [sql=" + sql + ", values=" + Arrays.toString(values) + "]";
		}

	}

}
