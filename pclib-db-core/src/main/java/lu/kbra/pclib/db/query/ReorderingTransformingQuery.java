package lu.kbra.pclib.db.query;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public abstract class ReorderingTransformingQuery<T extends DatabaseEntry, B> implements TransformingQuery<T, B> {

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class ArrayReorderingTransformingQuery<T extends DatabaseEntry, B> extends ReorderingTransformingQuery<T, B> {

		private final String sql;
		private final Object[] values;
		private final ColumnType[] types;
		private final Query.Type type;
		private final int[] reordering;

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public B transform(final List<T> data) throws SQLException {
			return TransformingQuery.transform(data, this.type);
		}

		@Override
		public void updateQuerySQL(final SQLQueryable<T> table, final PreparedStatement stmt) throws SQLException {
			for (int i : reordering) {
				this.types[i].store(stmt, i + 1, this.values[i]);
			}
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class ListReorderingTransformingQuery<T extends DatabaseEntry, B> extends ReorderingTransformingQuery<T, B> {

		private final String sql;
		private final List<Object> values;
		private final List<ColumnType> types;
		private final Query.Type type;
		private final int[] reordering;

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public B transform(final List<T> data) throws SQLException {
			return TransformingQuery.transform(data, this.type);
		}

		@Override
		public void updateQuerySQL(final SQLQueryable<T> table, final PreparedStatement stmt) throws SQLException {
			for (int i : reordering) {
				this.types.get(i).store(stmt, i + 1, this.values.get(i));
			}
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class MapReorderingTransformingQuery<T extends DatabaseEntry, B> extends ReorderingTransformingQuery<T, B> {

		private final String sql;
		private final String[] cols;
		private final Map<String, Object> values;
		private final Map<String, ColumnType> types;
		private final Query.Type type;
		private final int[] reordering;

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public B transform(final List<T> data) throws SQLException {
			return TransformingQuery.transform(data, this.type);
		}

		@Override
		public void updateQuerySQL(final SQLQueryable<T> table, final PreparedStatement stmt) throws SQLException {
			for (int i : reordering) {
				this.types.get(this.cols[i]).store(stmt, i + 1, this.values.get(this.cols[i]));
			}
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode
	public static class ScalarListReorderingTransformingQuery<T extends DatabaseEntry, B> implements RawTransformingQuery<T, B> {

		private final String sql;
		private final List<Object> values;
		private final List<ColumnType> types;
		private final Query.Type type;
		private final ColumnType returnColumnType;
		private final Type returnType;
		private final int[] reordering;

		@Override
		public String getPreparedQuerySQL(final SQLQueryable<T> table) {
			return this.sql;
		}

		@Override
		public B transform(final ResultSet rs) throws SQLException {
			final List<Object> data = new ArrayList<>();
			while (rs.next()) {
				data.add(this.returnColumnType.load(rs, 1, this.returnType));
			}
			return TransformingQuery.transform(data, this.type);
		}

		@Override
		public void updateQuerySQL(final SQLQueryable<T> table, final PreparedStatement stmt) throws SQLException {
			for (int i : reordering) {
				this.types.get(i).store(stmt, i + 1, this.values.get(i));
			}
		}

	}

}
