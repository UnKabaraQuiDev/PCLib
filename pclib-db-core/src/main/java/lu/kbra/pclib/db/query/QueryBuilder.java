package lu.kbra.pclib.db.query;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.query.ConditionBuilder.Node;
import lu.kbra.pclib.db.utils.impl.DataBaseEntryUtils;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public abstract class QueryBuilder<V extends DataBaseEntry, S extends QueryBuilder<V, S>> {

	public static final String DEFAULT_ENTRY_LIMIT_PROPERTY = QueryBuilder.class.getSimpleName() + ".default_entry_limit";
	public static int DEFAULT_ENTRY_LIMIT = PCUtils.getInteger(DEFAULT_ENTRY_LIMIT_PROPERTY, 250);

	public static <V extends DataBaseEntry> SelectQueryBuilder<V> select() {
		return new SelectQueryBuilder<>();
	}

	protected Node conditionRoot;
	protected final List<Object> params = new ArrayList<>();
	protected final List<String> paramColumns = new ArrayList<>();
	protected int limit = DEFAULT_ENTRY_LIMIT;

	protected abstract <B extends SQLQueryable<T>, T extends DataBaseEntry> String getPreparedQuerySQL(final B table);

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

	protected void updateQuerySQL(final PreparedStatement stmt, final SQLQueryable<V> table) throws SQLException {
		final DataBaseEntryUtils dbEntryUtils = table.getDataBaseEntryUtils();
		final Class<? extends SQLQueryable<V>> tableClass = table.getTargetClass();
		final Class<? extends DataBaseEntry> entryType = dbEntryUtils.getEntryType(tableClass);

		for (int i = 0; i < this.params.size(); i++) {
			final Field field = dbEntryUtils.getFieldFor(entryType, this.paramColumns.get(i));
			final ColumnType columnType = dbEntryUtils.getTypeFor(field);
			columnType.store(stmt, i + 1, this.params.get(i));
		}
	}

}
