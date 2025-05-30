package lu.pcy113.pclib.db.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lu.pcy113.pclib.db.autobuild.query.Query;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;

public abstract class SimpleSQLQuery<T extends DataBaseEntry, B> implements TransformingQuery<T, B> {

	public static <T extends DataBaseEntry, B> B transform(List<T> data, Query.Type type) throws SQLException {
		switch (type) {
		case FIRST_THROW:
			if (data.isEmpty()) {
				throw new SQLException("Expected at least one result, but got none.");
			}
			return (B) data.get(0);

		case FIRST_NULL:
			return (B) (data.isEmpty() ? null : data.get(0));

		case SINGLE_THROW:
			if (data.size() != 1) {
				throw new SQLException("Expected exactly one result, but got " + data.size() + ".");
			}
			return (B) data.get(0);

		case SINGLE_NULL:
			if (data.isEmpty())
				return null;
			if (data.size() > 1) {
				throw new SQLException("Expected at most one result, but got " + data.size() + ".");
			}
			return (B) data.get(0);

		case LIST_NULL:
			return (B) (data.isEmpty() ? null : data);

		case LIST_THROW:
			if (data.isEmpty()) {
				throw new SQLException("Expected a non-empty list, but got none.");
			}
			return (B) data;

		case LIST_EMPTY:
			return (B) data;

		default:
			throw new SQLException("Unknown result transformation type: " + type);
		}
	}

	public static class MapSimpleSQLQuery<T extends DataBaseEntry, B> extends SimpleSQLQuery<T, B> {

		private final String sql;
		private final String[] cols;
		private final Map<String, Object> values;
		private final Query.Type type;

		public MapSimpleSQLQuery(String sql, String[] cols, Map<String, Object> values, Query.Type type) {
			this.sql = sql;
			this.cols = cols;
			this.values = values;
			this.type = type;

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

		@Override
		public B transform(List<T> data) throws SQLException {
			return SimpleSQLQuery.transform(data, type);
		}

	}

	public static class ListSimpleSQLQuery<T extends DataBaseEntry, B> extends SimpleSQLQuery<T, B> {

		private final String sql;
		private final List<Object> values;
		private final Query.Type type;

		public ListSimpleSQLQuery(String sql, List<Object> values, Query.Type type) {
			this.sql = sql;
			this.values = values;
			this.type = type;
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

		@Override
		public B transform(List<T> data) throws SQLException {
			return SimpleSQLQuery.transform(data, type);
		}

	}

	public static class ArraySimpleSQLQuery<T extends DataBaseEntry, B> extends SimpleSQLQuery<T, B> {

		private final String sql;
		private final Object[] values;
		private final Query.Type type;

		public ArraySimpleSQLQuery(String sql, Object[] values, Query.Type type) {
			this.sql = sql;
			this.values = values;
			this.type = type;
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
			return "ListSimpleSQLQuery [sql=" + sql + ", values=" + values + "]";
		}

		@Override
		public B transform(List<T> data) throws SQLException {
			return SimpleSQLQuery.transform(data, type);
		}

	}

}
