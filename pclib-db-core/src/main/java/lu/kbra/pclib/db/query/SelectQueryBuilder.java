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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.annotations.view.OrderBy;
import lu.kbra.pclib.db.annotations.view.OrderBy.Type;
import lu.kbra.pclib.db.domain.dialect.SQLStructureVisitor;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQuery.PreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.RawTransformingQuery;
import lu.kbra.pclib.db.impl.SQLQuery.SinglePreparedQuery;
import lu.kbra.pclib.db.impl.SQLQuery.TransformingQuery;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.impl.SQLThrowingFunction;
import lu.kbra.pclib.db.loader.BufferedResultSetEnumeration;
import lu.kbra.pclib.db.loader.DirectResultSetEnumeration;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SelectQueryBuilder<V extends DataBaseEntry> extends QueryBuilder<V, SelectQueryBuilder<V>> {

	@Getter
	@AllArgsConstructor
	@ToString
	@EqualsAndHashCode
	public static final class OrderByPart {

		private static OrderByPart column(final String column, final String direction) {
			return new OrderByPart(column, direction, false);
		}

		private static OrderByPart raw(final String sql) {
			return new OrderByPart(sql, null, true);
		}

		private final String value;
		private final String direction;
		private final boolean raw;

		public <B extends SQLQueryable<T>, T extends DataBaseEntry> String build(SQLStructureVisitor visitor, B instance) {
			return isRaw() ? value : (visitor.qualifiedName(value) + " " + direction);
		}

	}

	protected int offset = 0;
	protected final List<OrderByPart> orderBy = new ArrayList<>();
	protected final List<String> explicitColumns = new ArrayList<>();
	protected final List<Join> joins = new ArrayList<>();

	public <B extends SQLQueryable<T>, T extends DataBaseEntry> String build(final SQLStructureVisitor visitor, final B instance) {
		final StringBuilder sql = new StringBuilder("SELECT ");

		if (this.explicitColumns.isEmpty()) {
			sql.append("*");
		} else {
			sql.append(this.explicitColumns.stream().map(visitor::qualifiedName).collect(Collectors.joining(", ")));
		}

		sql.append(" FROM ").append(visitor.qualifiedName(instance));

		for (final Join join : this.joins) {
			sql.append(" ").append(join.getType().name()).append(" JOIN ");

			final SQLQueryable<?> joinQueryable = join.getQueryable();

			sql.append(visitor.qualifiedName(joinQueryable));

			if (join.getAlias() != null && !join.getAlias().isEmpty()) {
				sql.append(" AS ").append(visitor.qualifiedName(join.getAlias()));
			}

			sql.append(" ON ").append(join.getOn());
		}

		if (this.conditionRoot != null) {
			sql.append(" WHERE ").append(this.conditionRoot.build(visitor, instance));
		}

		if (!this.orderBy.isEmpty()) {
			sql.append(" ORDER BY ")
					.append(this.orderBy.stream()
							.map(order -> order.build(visitor, instance))
							.collect(java.util.stream.Collectors.joining(", ")));
		}

		if (this.limit > 0) {
			sql.append(" LIMIT ").append(this.limit);
		}

		if (this.offset > 0) {
			sql.append(" OFFSET ").append(this.offset);
		}

		sql.append(";");
		return sql.toString();
	}

	public RawTransformingQuery<V, Integer> count() {
		this.select("COUNT(*) AS `count`");
		return this.rawTransform(rs -> {
			rs.next();
			return rs.getInt(1);
		});
	}

	public SinglePreparedQuery<V> firstNull() {
		if (!this.explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + this.explicitColumns);
		}
		return new SinglePreparedQuery<V>() {

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<V> table) {
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public V transform(final List<V> data) throws SQLException {
				return SimpleTransformingQuery.<V, V>transform(data, Query.Type.FIRST_NULL);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<V> table, final PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public TransformingQuery<V, Optional<V>> firstOptional() {
		if (!this.explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + this.explicitColumns);
		}
		return new TransformingQuery<V, Optional<V>>() {

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<V> table) {
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public Optional<V> transform(final List<V> data) throws SQLException {
				return Optional.ofNullable(SimpleTransformingQuery.<V, V>transform(data, Query.Type.FIRST_NULL));
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<V> table, final PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public SinglePreparedQuery<V> firstThrow() {
		if (!this.explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + this.explicitColumns);
		}
		return new SinglePreparedQuery<V>() {

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<V> table) {
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<V> table, final PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public <T extends DataBaseEntry> SelectQueryBuilder<V> join(final Join.Type type, final SQLQueryable<T> queryable, final String on) {
		return this.join(type, queryable, on, (String[]) null);
	}

	public <T extends DataBaseEntry> SelectQueryBuilder<V>
			join(final Join.Type type, final SQLQueryable<T> queryable, final String asName, final String on) {
		this.joins.add(new Join(type, queryable, asName, on, null));
		return this;
	}

	public <T extends DataBaseEntry> SelectQueryBuilder<V>
			join(final Join.Type type, final SQLQueryable<T> queryable, final String on, final String[] columns) {
		this.joins.add(new Join(type, queryable, null, on, columns));
		return this;
	}

	public PreparedQuery<V> list() {
		if (!this.explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + this.explicitColumns);
		}
		return new PreparedQuery<V>() {

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<V> table) {
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<V> table, final PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public <K> TransformingQuery<V, Map<K, V>> map(final Function<V, K> key) {
		return this.map(HashMap::new, key, Function.identity());
	}

	public <K, B> TransformingQuery<V, Map<K, B>> map(final Function<V, K> key, final Function<V, B> value) {
		return this.map(HashMap::new, key, value);
	}

	public <K, B> TransformingQuery<V, Map<K, B>>
			map(final Supplier<Map<K, B>> mapSupplier, final Function<V, K> key, final Function<V, B> value) {
		Objects.requireNonNull(key, "Key transformer function cannot be null.");
		Objects.requireNonNull(value, "Value transformer function cannot be null.");
		if (!this.explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + this.explicitColumns);
		}
		return new TransformingQuery<V, Map<K, B>>() {

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<V> table) {
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public Map<K, B> transform(final List<V> data) throws SQLException {
				final Map<K, B> map = mapSupplier.get();
				data.forEach(c -> map.put(key.apply(c), value.apply(c)));
				return map;
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<V> table, final PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public <K> TransformingQuery<V, Map<K, V>> map(final Supplier<Map<K, V>> mapSupplier, final Function<V, K> key) {
		return this.map(mapSupplier, key, Function.identity());
	}

	public SelectQueryBuilder<V> offset(final int offset) {
		if (offset < 0) {
			throw new IllegalArgumentException("Offset cannot be negative.");
		}
		this.offset = offset;
		return this;
	}

	public SelectQueryBuilder<V> orderBy(final String column, final OrderBy.Type dir) {
		this.orderBy.add(OrderByPart.column(column, dir.name()));
		return this;
	}

	@Deprecated
	public SelectQueryBuilder<V> orderBy(final String column, final String dir) {
		this.orderBy.add(OrderByPart.column(column, dir));
		return this;
	}

	public SelectQueryBuilder<V> orderBy(final String[] primaryKeysNames, final Type dir) {
		Arrays.stream(primaryKeysNames).forEach(column -> this.orderBy.add(OrderByPart.column(column, dir.name())));
		return this;
	}

	public SelectQueryBuilder<V> orderByAsc(final String column) {
		this.orderBy.add(OrderByPart.column(column, Type.ASC.name()));
		return this;
	}

	public SelectQueryBuilder<V> orderByDesc(final String column) {
		this.orderBy.add(OrderByPart.column(column, Type.DESC.name()));
		return this;
	}

	public SelectQueryBuilder<V> orderByFunc(final String func) {
		this.orderBy.add(OrderByPart.raw(func));
		return this;
	}

	public <B> RawTransformingQuery<V, BufferedResultSetEnumeration<B>> rawBufferedEnumeration(final SQLThrowingFunction<B> transformer) {
		Objects.requireNonNull(transformer, "Transformer function cannot be null.");

		return this.rawTransform(rs -> new BufferedResultSetEnumeration<>(rs, transformer));
	}

	public <B> RawTransformingQuery<V, DirectResultSetEnumeration<B>> rawDirectEnumeration(final SQLThrowingFunction<B> transformer) {
		Objects.requireNonNull(transformer, "Transformer function cannot be null.");

		return this.rawTransform(rs -> new DirectResultSetEnumeration<>(rs, transformer));
	}

	public <B> RawTransformingQuery<V, B> rawTransform(final SQLThrowingFunction<B> transformer) {
		Objects.requireNonNull(transformer, "Transformer function cannot be null.");

		return new RawTransformingQuery<V, B>() {

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<V> table) {
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public B transform(final ResultSet rs) throws SQLException {
				return transformer.apply(rs);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<V> table, final PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public SelectQueryBuilder<V> select(final String explicitColumn) {
		this.explicitColumns.add(explicitColumn);
		return this;
	}

	public SinglePreparedQuery<V> singleNull() {
		if (!this.explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + this.explicitColumns);
		}
		return new SinglePreparedQuery<V>() {

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<V> table) {
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public V transform(final List<V> data) throws SQLException {
				return SimpleTransformingQuery.<V, V>transform(data, Query.Type.SINGLE_NULL);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<V> table, final PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public TransformingQuery<V, Optional<V>> singleOptional() {
		if (!this.explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + this.explicitColumns);
		}
		return new TransformingQuery<V, Optional<V>>() {

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<V> table) {
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public Optional<V> transform(final List<V> data) throws SQLException {
				return Optional.ofNullable(SimpleTransformingQuery.<V, V>transform(data, Query.Type.SINGLE_NULL));
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<V> table, final PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public SinglePreparedQuery<V> singleThrow() {
		if (!this.explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + this.explicitColumns);
		}
		return new SinglePreparedQuery<V>() {

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<V> table) {
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public V transform(final List<V> data) throws SQLException {
				return SimpleTransformingQuery.<V, V>transform(data, Query.Type.SINGLE_THROW);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<V> table, final PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	public <B> TransformingQuery<V, B> transform(final Function<List<V>, B> transformer) {
		Objects.requireNonNull(transformer, "Transformer function cannot be null.");
		if (!this.explicitColumns.isEmpty()) {
			throw new IllegalArgumentException("You specified the following explicit rows: " + this.explicitColumns);
		}
		return new TransformingQuery<V, B>() {

			@Override
			public String getPreparedQuerySQL(final SQLQueryable<V> table) {
				return SelectQueryBuilder.this.getPreparedQuerySQL(table);
			}

			@Override
			public B transform(final List<V> data) throws SQLException {
				return transformer.apply(data);
			}

			@Override
			public void updateQuerySQL(final SQLQueryable<V> table, final PreparedStatement stmt) throws SQLException {
				SelectQueryBuilder.this.updateQuerySQL(stmt, table);
			}

		};
	}

	@Override
	protected <B extends SQLQueryable<T>, T extends DataBaseEntry> String getPreparedQuerySQL(final B table) {
		return this.build(table.getDataBaseEntryUtils().getStructureVisitor(), table);
	}

}
