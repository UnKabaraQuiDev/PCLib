package lu.kbra.pclib.db.query;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.ColumnData;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.query.ConditionBuilder.Node;
import lu.kbra.pclib.db.utils.impl.DatabaseEntryUtils;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public abstract class QueryBuilder<V extends DatabaseEntry, S extends QueryBuilder<V, S>> {

	public static final String DEFAULT_ENTRY_LIMIT_PROPERTY = QueryBuilder.class.getSimpleName() + ".default_entry_limit";
	public static int DEFAULT_ENTRY_LIMIT = PCUtils.getInteger(QueryBuilder.DEFAULT_ENTRY_LIMIT_PROPERTY, 250);

	public static <V extends DatabaseEntry> SelectQueryBuilder<V> select() {
		return new SelectQueryBuilder<>();
	}

	protected Node conditionRoot;
	protected final List<Object> params = new ArrayList<>();
	protected final List<String> paramColumns = new ArrayList<>();
	protected int limit = QueryBuilder.DEFAULT_ENTRY_LIMIT;

	@SuppressWarnings("unchecked")
	public S limit(final int limit) {
		this.limit = limit;
		return (S) this;
	}

	@SuppressWarnings("unchecked")
	public S where(final Consumer<ConditionBuilder> builderFn) {
		final ConditionBuilder cb = new ConditionBuilder();
		builderFn.accept(cb);
		this.conditionRoot = cb.getRoot();
		this.params.clear();
		this.params.addAll(cb.getParams());
		this.paramColumns.clear();
		this.paramColumns.addAll(cb.getColumns());

		return (S) this;
	}

	protected abstract <B extends SQLQueryable<T>, T extends DatabaseEntry> String getPreparedQuerySQL(final B table);

	protected void updateQuerySQL(final PreparedStatement stmt, final SQLQueryable<V> table) throws SQLException {
		final DatabaseEntryUtils dbEntryUtils = table.getDatabaseEntryUtils();

		for (int i = 0; i < this.params.size(); i++) {
			final String columnName = this.paramColumns.get(i);
			final ColumnData column = dbEntryUtils.getColumnFor(table, columnName);
			final ColumnType columnType = column.getType();
			columnType.store(stmt, i + 1, this.params.get(i));
		}
	}

}
