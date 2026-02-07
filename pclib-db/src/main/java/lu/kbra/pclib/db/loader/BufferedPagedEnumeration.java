package lu.kbra.pclib.db.loader;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lu.kbra.pclib.db.annotations.view.OrderBy.Type;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.query.ConditionBuilder;
import lu.kbra.pclib.db.query.QueryBuilder;
import lu.kbra.pclib.db.table.AbstractDBTable;
import lu.kbra.pclib.db.view.AbstractDBView;

public class BufferedPagedEnumeration<B extends DataBaseEntry> extends PagedEnumeration<B> {

	private static final Consumer<ConditionBuilder> EMPTY = db -> {
	};
	private final SQLQueryable<B> queryable;
	private final Consumer<ConditionBuilder> conditionBuilder;
	private final String[] pks;

	public BufferedPagedEnumeration(int pageSize, AbstractDBTable<B> table) throws SQLException {
		super(pageSize, table.count());
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		this.conditionBuilder = EMPTY;
	}

	public BufferedPagedEnumeration(int pageSize, int total, AbstractDBTable<B> table) {
		super(pageSize, total);
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		conditionBuilder = EMPTY;
	}

	public BufferedPagedEnumeration(int pageSize, AbstractDBTable<B> table, Consumer<ConditionBuilder> conditionBuilder)
			throws SQLException {
		super(pageSize, table.count());
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	public BufferedPagedEnumeration(int pageSize, int total, AbstractDBTable<B> table, Consumer<ConditionBuilder> conditionBuilder) {
		super(pageSize, total);
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	public BufferedPagedEnumeration(int pageSize, AbstractDBView<B> table, String... pks) throws SQLException {
		super(pageSize, table.count());
		this.pks = pks;
		this.queryable = table;
		this.conditionBuilder = EMPTY;
	}

	public BufferedPagedEnumeration(int pageSize, int total, AbstractDBView<B> table, String... pks) {
		super(pageSize, total);
		this.pks = pks;
		this.queryable = table;
		conditionBuilder = EMPTY;
	}

	public BufferedPagedEnumeration(int pageSize, AbstractDBView<B> table, Consumer<ConditionBuilder> conditionBuilder, String... pks)
			throws SQLException {
		super(pageSize, table.count());
		this.pks = pks;
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	public BufferedPagedEnumeration(int pageSize, int total, AbstractDBView<B> table, Consumer<ConditionBuilder> conditionBuilder,
			String... pks) {
		super(pageSize, total);
		this.pks = pks;
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	@Override
	protected Iterator<B> fetchPage(int page, int size) {
		return queryable
				.query(QueryBuilder.<B>select().orderBy(pks, Type.ASC).where(conditionBuilder).limit(size).offset(page * size).list())
				.iterator();
	}

	public Stream<B> stream() {
		return StreamSupport.stream(Spliterators.spliterator(asIterator(), total, Spliterator.ORDERED), false);
	}

	public Stream<B> parallelStream() {
		return StreamSupport.stream(Spliterators.spliterator(asIterator(), total, Spliterator.ORDERED), true);
	}

}
