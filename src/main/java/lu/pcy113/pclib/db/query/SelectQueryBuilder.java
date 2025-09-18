package lu.pcy113.pclib.db.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import lu.pcy113.pclib.datastructure.pair.Pair;
import lu.pcy113.pclib.db.annotations.view.OrderBy;
import lu.pcy113.pclib.db.autobuild.query.Query;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLNamed;
import lu.pcy113.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.SinglePreparedQuery;
import lu.pcy113.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.pcy113.pclib.db.impl.SQLQueryable;
import lu.pcy113.pclib.db.utils.SimpleTransformingQuery;

public class SelectQueryBuilder<V extends DataBaseEntry> extends QueryBuilder<V, SelectQueryBuilder<V>> {

	protected int offset = 0;
	protected final List<Pair<String, String>> orderBy = new ArrayList<>();

	public SelectQueryBuilder<V> offset(int offset) {
		if (offset < 0)
			throw new IllegalArgumentException("Offset cannot be negative.");
		this.offset = offset;
		return this;
	}

	public SelectQueryBuilder<V> orderByAsc(String column) {
		orderBy.add(new Pair<>(column, "ASC"));
		return this;
	}

	public SelectQueryBuilder<V> orderByDesc(String column) {
		orderBy.add(new Pair<>(column, "DESC"));
		return this;
	}

	@Deprecated
	public SelectQueryBuilder<V> orderBy(String column, String dir) {
		orderBy.add(new Pair<>(column, dir));
		return this;
	}

	public SelectQueryBuilder<V> orderBy(String column, OrderBy.Type dir) {
		orderBy.add(new Pair<>(column, dir.name()));
		return this;
	}

	@Override
	protected String getPreparedQuerySQL(SQLNamed table) {
		StringBuilder sql = new StringBuilder("SELECT * FROM ").append(table.getQualifiedName());
		if (root != null)
			sql.append(" WHERE ").append(root.toSQL());
		if (!orderBy.isEmpty()) {
			sql.append(" ORDER BY ").append(orderBy.stream().map(p -> p.getKey() + " " + p.getValue()).collect(Collectors.joining(", ")));
		}
		if (limit > 0)
			sql.append(" LIMIT ").append(limit);
		if (offset > 0)
			sql.append(" OFFSET ").append(offset);
		
		sql.append(";");
		return sql.toString();
	}

	public PreparedQuery<V> list() {
		return new PreparedQuery<V>() {

			SQLQueryable<V> table;

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				this.table = table;
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public <B> TransformingQuery<V, B> transform(Function<List<V>, B> transformer) {
		Objects.requireNonNull(transformer, "Transformer function cannot be null.");

		return new TransformingQuery<V, B>() {

			SQLQueryable<V> table;

			@Override
			public B transform(List<V> data) throws SQLException {
				return transformer.apply(data);
			}

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				this.table = table;
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public <B> RawTransformingQuery<V, B> rawTransform(Function<ResultSet, B> transformer) {
		Objects.requireNonNull(transformer, "Transformer function cannot be null.");

		return new RawTransformingQuery<V, B>() {

			SQLQueryable<V> table;

			@Override
			public B transform(ResultSet rs) throws SQLException {
				return transformer.apply(rs);
			}

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				this.table = table;
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public TransformingQuery<V, Optional<V>> firstOptional() {
		return new TransformingQuery<V, Optional<V>>() {

			SQLQueryable<V> table;

			@Override
			public Optional<V> transform(List<V> data) throws SQLException {
				return Optional.ofNullable(SimpleTransformingQuery.<V, V>transform(data, Query.Type.FIRST_NULL));
			}

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				this.table = table;
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public SinglePreparedQuery<V> firstNull() {
		return new SinglePreparedQuery<V>() {

			SQLQueryable<V> table;

			@Override
			public V transform(List<V> data) throws SQLException {
				return SimpleTransformingQuery.<V, V>transform(data, Query.Type.FIRST_NULL);
			}

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				this.table = table;
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public SinglePreparedQuery<V> firstThrow() {
		return new SinglePreparedQuery<V>() {

			SQLQueryable<V> table;

			@Override
			public String getPreparedQuerySQL(SQLQueryable<V> table) {
				this.table = table;
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public void updateQuerySQL(PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

}