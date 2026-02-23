package lu.kbra.pclib.db.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.annotations.view.OrderBy.Type;
import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLNamed;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.SinglePreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.loader.BufferedResultSetEnumeration;
import lu.kbra.pclib.db.loader.DirectResultSetEnumeration;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLThrowingFunction;
import lu.kbra.pclib.db.utils.SimpleTransformingQuery;

public class SelectQueryBuilder<V extends DataBaseEntry> extends QueryBuilder<V, SelectQueryBuilder<V>> {

	protected int offset = 0;
	protected final List<String> orderBy = new ArrayList<>();
	protected final List<String> explicitColumns = new ArrayList<String>();

	public SelectQueryBuilder<V> offset(int offset) {
		if (offset < 0)
			throw new IllegalArgumentException("Offset cannot be negative.");
		this.offset = offset;
		return this;
	}

	public SelectQueryBuilder<V> orderByAsc(String column) {
		orderBy.add(PCUtils.sqlEscapeIdentifier(column) + " ASC");
		return this;
	}

	public SelectQueryBuilder<V> orderByDesc(String column) {
		orderBy.add(PCUtils.sqlEscapeIdentifier(column) + " DESC");
		return this;
	}

	@Deprecated
	public SelectQueryBuilder<V> orderBy(String column, String dir) {
		orderBy.add(PCUtils.sqlEscapeIdentifier(column) + " " + dir);
		return this;
	}

	public SelectQueryBuilder<V> orderByFunc(String func) {
		orderBy.add(func);
		return this;
	}

	public SelectQueryBuilder<V> orderBy(String column, OrderBy.Type dir) {
		orderBy.add(PCUtils.sqlEscapeIdentifier(column) + " " + dir.name());
		return this;
	}

	public SelectQueryBuilder<V> orderBy(String[] primaryKeysNames, Type dir) {
		Arrays.stream(primaryKeysNames).forEach(column -> orderBy.add(PCUtils.sqlEscapeIdentifier(column) + " " + dir.name()));
		return this;
	}

	public SelectQueryBuilder<V> select(String explicitColumn) {
		explicitColumns.add(explicitColumn);
		return this;
	}

	@Override
	protected String getPreparedQuerySQL(SQLNamed table) {
		final StringBuilder sql = new StringBuilder("SELECT ")
				.append(explicitColumns.isEmpty() ? "*" : explicitColumns.stream().collect(Collectors.joining(", ")))
				.append(" FROM ")
				.append(table.getQualifiedName());
		if (root != null)
			sql.append(" WHERE ").append(root.toSQL());
		if (!orderBy.isEmpty()) {
			sql.append(" ORDER BY ").append(orderBy.stream().collect(Collectors.joining(", ")));
		}
		if (limit > 0)
			sql.append(" LIMIT ").append(limit);
		if (offset > 0)
			sql.append(" OFFSET ").append(offset);

		sql.append(";");
		return sql.toString();
	}

	public PreparedQuery<V> list() {
		if (!explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + explicitColumns);
		}
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
		if (!explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + explicitColumns);
		}
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

	public <K, B> TransformingQuery<V, Map<K, B>> map(Supplier<Map<K, B>> mapSupplier, Function<V, K> key, Function<V, B> value) {
		Objects.requireNonNull(key, "Key transformer function cannot be null.");
		Objects.requireNonNull(value, "Value transformer function cannot be null.");
		if (!explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + explicitColumns);
		}
		return new TransformingQuery<V, Map<K, B>>() {

			SQLQueryable<V> table;

			@Override
			public Map<K, B> transform(List<V> data) throws SQLException {
				final Map<K, B> map = mapSupplier.get();
				data.forEach(c -> map.put(key.apply(c), value.apply(c)));
				return map;
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

	public <K, B> TransformingQuery<V, Map<K, B>> map(Function<V, K> key, Function<V, B> value) {
		return map(HashMap::new, key, value);
	}

	public <K> TransformingQuery<V, Map<K, V>> map(Supplier<Map<K, V>> mapSupplier, Function<V, K> key) {
		return map(mapSupplier, key, Function.identity());
	}

	public <K> TransformingQuery<V, Map<K, V>> map(Function<V, K> key) {
		return map(HashMap::new, key, Function.identity());
	}

	public <B> RawTransformingQuery<V, B> rawTransform(SQLThrowingFunction<B> transformer) {
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

	public <B> RawTransformingQuery<V, DirectResultSetEnumeration<B>> rawDirectEnumeration(SQLThrowingFunction<B> transformer) {
		Objects.requireNonNull(transformer, "Transformer function cannot be null.");

		return rawTransform((rs) -> new DirectResultSetEnumeration<>(rs, transformer));
	}

	public <B> RawTransformingQuery<V, BufferedResultSetEnumeration<B>> rawBufferedEnumeration(SQLThrowingFunction<B> transformer) {
		Objects.requireNonNull(transformer, "Transformer function cannot be null.");

		return rawTransform((rs) -> new BufferedResultSetEnumeration<>(rs, transformer));
	}

	public TransformingQuery<V, Optional<V>> firstOptional() {
		if (!explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + explicitColumns);
		}
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
		if (!explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + explicitColumns);
		}
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
		if (!explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + explicitColumns);
		}
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

	public TransformingQuery<V, Optional<V>> singleOptional() {
		if (!explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + explicitColumns);
		}
		return new TransformingQuery<V, Optional<V>>() {

			SQLQueryable<V> table;

			@Override
			public Optional<V> transform(List<V> data) throws SQLException {
				return Optional.ofNullable(SimpleTransformingQuery.<V, V>transform(data, Query.Type.SINGLE_NULL));
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

	public SinglePreparedQuery<V> singleThrow() {
		if (!explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + explicitColumns);
		}
		return new SinglePreparedQuery<V>() {

			SQLQueryable<V> table;

			@Override
			public V transform(List<V> data) throws SQLException {
				return SimpleTransformingQuery.<V, V>transform(data, Query.Type.SINGLE_THROW);
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

	public SinglePreparedQuery<V> singleNull() {
		if (!explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + explicitColumns);
		}
		return new SinglePreparedQuery<V>() {

			SQLQueryable<V> table;

			@Override
			public V transform(List<V> data) throws SQLException {
				return SimpleTransformingQuery.<V, V>transform(data, Query.Type.SINGLE_NULL);
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

	public RawTransformingQuery<V, Integer> count() {
		select("COUNT(*) AS `count`");
		return rawTransform((rs) -> {
			rs.next();
			return rs.getInt(1);
		});
	}

	@Override
	public String toString() {
		return getPreparedQuerySQL(SQLNamed.MOCK);
	}

}