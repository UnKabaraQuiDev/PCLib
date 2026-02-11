package lu.kbra.pclib.db.query;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLNamed;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.query.ConditionBuilder.Node;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.db.utils.SQLBuilder;

public abstract class QueryBuilder<V extends DataBaseEntry, S extends QueryBuilder<V, S>> {

	protected Node root;
	protected final List<Object> params = new ArrayList<>();
	protected final List<String> paramColumns = new ArrayList<>();
	protected int limit = SQLBuilder.ENTRY_LIMIT;

	@SuppressWarnings("unchecked")
	public S where(Consumer<ConditionBuilder> builderFn) {
		ConditionBuilder cb = new ConditionBuilder();
		builderFn.accept(cb);
		root = cb.build();
		params.clear();
		params.addAll(cb.getParams());
		paramColumns.clear();
		paramColumns.addAll(cb.getColumns());

		return (S) this;
	}

	@SuppressWarnings("unchecked")
	public S limit(int limit) {
		this.limit = limit;
		return (S) this;
	}

	protected abstract String getPreparedQuerySQL(SQLNamed table);

	protected void updateQuerySQL(PreparedStatement stmt, SQLQueryable<V> table) throws SQLException {
		final DataBaseEntryUtils dbEntryUtils = table.getDbEntryUtils();
		final Class<? extends SQLQueryable<V>> tableClass = table.getTargetClass();
		final Class<? extends DataBaseEntry> entryType = dbEntryUtils.getEntryType(tableClass);

		for (int i = 0; i < params.size(); i++) {
			final Field field = dbEntryUtils.getFieldFor(entryType, paramColumns.get(i));
			final ColumnType columnType = dbEntryUtils.getTypeFor(field);
			columnType.store(stmt, i + 1, params.get(i));
		}
	}

	public static <V extends DataBaseEntry> SelectQueryBuilder<V> select() {
		return new SelectQueryBuilder<>();
	}

	@Override
	public String toString() {
		return getPreparedQuerySQL(SQLNamed.MOCK);
	}

}
