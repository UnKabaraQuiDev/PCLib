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

		public MapSimplePreparedQuery(final String sql, final String[] cols, final Map<String, Object> values) {
			this.sql = sql;
			this.cols = cols;
			this.values = values;

			if (!Arrays.stream(cols).allMatch(values::containsKey)) {
				throw new IllegalArgumentException(
						"Missing values for some columns (expecting: " + Arrays.toString(cols) + ", but got: " + values.keySet() + ")");
			}
		}

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public void updateQuerySQL(final PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < this.cols.length; i++) {
				stmt.setObject(i + 1, this.values.get(this.cols[i]));
			}
		}

		@Override
		public String toString() {
			return "MapPreparedSimpleSQLQuery [sql=" + this.sql + ", cols=" + Arrays.toString(this.cols) + ", values=" + this.values + "]";
		}

	}

	public static class ListSimplePreparedQuery<T extends DataBaseEntry> extends SimplePreparedQuery<T> {

		private final String sql;
		private final List<Object> values;

		public ListSimplePreparedQuery(final String sql, final List<Object> values) {
			this.sql = sql;
			this.values = values;
		}

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public void updateQuerySQL(final PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < this.values.size(); i++) {
				stmt.setObject(i + 1, this.values.get(i));
			}
		}

		@Override
		public String toString() {
			return "ListPreparedSimpleQuery [sql=" + this.sql + ", values=" + this.values + "]";
		}

	}

	public static class ArraySimplePreparedQuery<T extends DataBaseEntry> extends SimplePreparedQuery<T> {

		private final String sql;
		private final Object[] values;

		public ArraySimplePreparedQuery(final String sql, final Object[] values) {
			this.sql = sql;
			this.values = values;
		}

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public void updateQuerySQL(final PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < this.values.length; i++) {
				stmt.setObject(i + 1, this.values[i]);
			}
		}

		@Override
		public String toString() {
			return "ArrayPreparedSimpleQuery [sql=" + this.sql + ", values=" + Arrays.toString(this.values) + "]";
		}

	}

}
