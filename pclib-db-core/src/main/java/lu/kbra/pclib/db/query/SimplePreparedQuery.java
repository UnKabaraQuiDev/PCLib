package lu.kbra.pclib.db.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode
public abstract class SimplePreparedQuery<T extends DatabaseEntry> implements PreparedQuery<T> {

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class ArraySimplePreparedQuery<T extends DatabaseEntry> extends SimplePreparedQuery<T> {

		private final String sql;
		private final Object[] values;

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public void updateQuerySQL(final SQLQueryable<T> instance, final PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < this.values.length; i++) {
				stmt.setObject(i + 1, this.values[i]);
			}
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class ListSimplePreparedQuery<T extends DatabaseEntry> extends SimplePreparedQuery<T> {

		private final String sql;
		private final List<Object> values;

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public void updateQuerySQL(final SQLQueryable<T> instance, final PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < this.values.size(); i++) {
				stmt.setObject(i + 1, this.values.get(i));
			}
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class MapSimplePreparedQuery<T extends DatabaseEntry> extends SimplePreparedQuery<T> {

		private final String sql;
		private final String[] cols;
		private final Map<String, Object> values;

//		if (!Arrays.stream(cols).allMatch(values::containsKey)) {
//			throw new IllegalArgumentException(
//					"Missing values for some columns (expecting: " + Arrays.toString(cols) + ", but got: " + values.keySet() + ")");
//		}

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public void updateQuerySQL(final SQLQueryable<T> instance, final PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < this.cols.length; i++) {
				stmt.setObject(i + 1, this.values.get(this.cols[i]));
			}
		}

	}

}
