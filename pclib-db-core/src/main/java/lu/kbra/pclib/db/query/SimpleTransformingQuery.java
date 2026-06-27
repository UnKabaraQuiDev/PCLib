package lu.kbra.pclib.db.query;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;

public abstract class SimpleTransformingQuery<T extends DataBaseEntry, B> implements TransformingQuery<T, B> {

	public static class ArraySimpleTransformingQuery<T extends DataBaseEntry, B> extends SimpleTransformingQuery<T, B> {

		private final String sql;
		private final Object[] values;
		private final ColumnType[] types;
		private final Query.Type type;

		public ArraySimpleTransformingQuery(final String sql, final Object[] values, final ColumnType[] types, final Query.Type type) {
			this.sql = sql;
			this.values = values;
			this.types = types;
			this.type = type;
		}

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public String toString() {
			return "ArraySimpleTransformingQuery [sql=" + this.sql + ", values=" + Arrays.toString(this.values) + ", types="
					+ Arrays.toString(this.types) + ", type=" + this.type + "]";
		}

		@Override
		public B transform(final List<T> data) throws SQLException {
			return SimpleTransformingQuery.transform(data, this.type);
		}

		@Override
		public void updateQuerySQL(final PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < this.values.length; i++) {
				this.types[i].store(stmt, i + 1, this.values[i]);
			}
		}

	}

	public static class ListSimpleTransformingQuery<T extends DataBaseEntry, B> extends SimpleTransformingQuery<T, B> {

		private final String sql;
		private final List<Object> values;
		private final List<ColumnType> types;
		private final Query.Type type;

		public ListSimpleTransformingQuery(
				final String sql,
				final List<Object> values,
				final List<ColumnType> types,
				final Query.Type type) {
			this.sql = sql;
			this.values = values;
			this.types = types;
			this.type = type;
		}

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public String toString() {
			return "ListSimpleTransformingQuery [sql=" + this.sql + ", values=" + this.values + ", types=" + this.types + ", type="
					+ this.type + "]";
		}

		@Override
		public B transform(final List<T> data) throws SQLException {
			return SimpleTransformingQuery.transform(data, this.type);
		}

		@Override
		public void updateQuerySQL(final PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < this.values.size(); i++) {
				this.types.get(i).store(stmt, i + 1, this.values.get(i));
			}
		}

	}

	public static class MapSimpleTransformingQuery<T extends DataBaseEntry, B> extends SimpleTransformingQuery<T, B> {

		private final String sql;
		private final String[] cols;
		private final Map<String, Object> values;
		private final Map<String, ColumnType> types;
		private final Query.Type type;

		public MapSimpleTransformingQuery(
				final String sql,
				final String[] cols,
				final Map<String, Object> values,
				final Map<String, ColumnType> types,
				final Query.Type type) {
			this.sql = sql;
			this.cols = cols;
			this.values = values;
			this.types = types;
			this.type = type;

			if (!Arrays.stream(cols).allMatch(values::containsKey)) {
				throw new IllegalArgumentException(
						"Missing values for some columns (expecting: " + Arrays.toString(cols) + ", but got: " + values.keySet() + ")");
			}

			if (!Arrays.stream(cols).allMatch(types::containsKey)) {
				throw new IllegalArgumentException("Missing column types for some columns (expecting: " + Arrays.toString(cols)
						+ ", but got: " + values.keySet() + ")");
			}
		}

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public String toString() {
			return "MapSimpleTransformingQuery [sql=" + this.sql + ", cols=" + Arrays.toString(this.cols) + ", values=" + this.values
					+ ", types=" + this.types + ", type=" + this.type + "]";
		}

		@Override
		public B transform(final List<T> data) throws SQLException {
			return SimpleTransformingQuery.transform(data, this.type);
		}

		@Override
		public void updateQuerySQL(final PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < this.cols.length; i++) {
				this.types.get(this.cols[i]).store(stmt, i + 1, this.values.get(this.cols[i]));
			}
		}

	}

	public static class ScalarListTransformingQuery<T extends DataBaseEntry, B> implements RawTransformingQuery<T, B> {

		private final String sql;
		private final List<Object> values;
		private final List<ColumnType> types;
		private final Query.Type type;
		private final ColumnType returnColumnType;
		private final Type returnType;

		public ScalarListTransformingQuery(
				final String sql,
				final List<Object> values,
				final List<ColumnType> types,
				final Query.Type type,
				final ColumnType returnColumnType,
				final Type returnType) {
			this.sql = sql;
			this.values = values;
			this.types = types;
			this.type = type;
			this.returnColumnType = returnColumnType;
			this.returnType = returnType;
		}

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public String toString() {
			return "ScalarListTransformingQuery [sql=" + this.sql + ", values=" + this.values + ", types=" + this.types + ", type="
					+ this.type + ", returnColumnType=" + this.returnColumnType + ", returnType=" + this.returnType + "]";
		}

		@Override
		public B transform(final ResultSet rs) throws SQLException {
			final List<Object> data = new ArrayList<>();
			while (rs.next()) {
				data.add(this.returnColumnType.load(rs, 1, this.returnType));
			}
			return SimpleTransformingQuery.transform(data, this.type);
		}

		@Override
		public void updateQuerySQL(final PreparedStatement stmt) throws SQLException {
			for (int i = 0; i < this.values.size(); i++) {
				this.types.get(i).store(stmt, i + 1, this.values.get(i));
			}
		}

	}

	public static <T, B> B transform(final List<T> data, final Query.Type type) throws SQLException {
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
			if (data.isEmpty()) {
				return null;
			}
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

}
