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

public abstract class SimpleTransformingQuery<T extends DatabaseEntry, B> implements TransformingQuery<T, B> {

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class ArraySimpleTransformingQuery<T extends DatabaseEntry, B> extends SimpleTransformingQuery<T, B> {

		private final String sql;
		private final QueryParameter<?>[] parameters;
		private final Query.Type type;

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
			for (int i = 0; i < this.parameters.length; i++) {
				parameters[i].store(stmt, i + 1);
			}
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class ListSimpleTransformingQuery<T extends DatabaseEntry, B> extends SimpleTransformingQuery<T, B> {

		private final String sql;
		private final List<QueryParameter<?>> parameters;
		private final Query.Type type;

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
			for (int i = 0; i < this.parameters.size(); i++) {
				parameters.get(i).store(stmt, i + 1);
			}
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode(callSuper = true)
	public static class MapSimpleTransformingQuery<T extends DatabaseEntry, B> extends SimpleTransformingQuery<T, B> {

		private final String sql;
		private final String[] cols;
		private final Map<String, QueryParameter<?>> parameters;
		private final Query.Type type;

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
			for (int i = 0; i < this.cols.length; i++) {
				this.parameters.get(this.cols[i]).store(stmt, i + 1);
			}
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode
	public static class ScalarArraySimpleTransformingQuery<T extends DatabaseEntry, B> implements RawTransformingQuery<T, B> {

		private final String sql;
		private final QueryParameter<?>[] parameters;
		private final Query.Type type;
		private final ColumnType<B, ?> returnColumnType;
		private final Type returnType;

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
			for (int i = 0; i < this.parameters.length; i++) {
				parameters[i].store(stmt, i + 1);
			}
		}

	}

	@Getter
	@ToString
	@AllArgsConstructor
	@EqualsAndHashCode
	public static class ScalarListSimpleTransformingQuery<T extends DatabaseEntry, B> implements RawTransformingQuery<T, B> {

		private final String sql;
		private final List<QueryParameter<?>> parameters;
		private final Query.Type type;
		private final ColumnType<B, ?> returnColumnType;
		private final Type returnType;

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
			for (int i = 0; i < this.parameters.size(); i++) {
				parameters.get(i).store(stmt, i + 1);
			}
		}

	}

}
