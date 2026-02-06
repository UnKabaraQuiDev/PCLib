package lu.pcy113.pclib.db.loader;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.DataBaseView;
import lu.pcy113.pclib.db.annotations.view.OrderBy.Type;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQueryable;
import lu.pcy113.pclib.db.query.ConditionBuilder;
import lu.pcy113.pclib.db.query.QueryBuilder;

public class BufferedPagedEnumeration<B extends DataBaseEntry> extends PagedEnumeration<B> {

	private static final Consumer<ConditionBuilder> EMPTY = db -> {
	};
	private final SQLQueryable<B> queryable;
	private final Consumer<ConditionBuilder> conditionBuilder;
	private final String[] pks;

	public BufferedPagedEnumeration(int pageSize, DataBaseTable<B> table) {
		super(pageSize, table.count().run());
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		this.conditionBuilder = EMPTY;
	}

	public BufferedPagedEnumeration(int pageSize, int total, DataBaseTable<B> table) {
		super(pageSize, total);
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		conditionBuilder = EMPTY;
	}

	public BufferedPagedEnumeration(int pageSize, DataBaseTable<B> table, Consumer<ConditionBuilder> conditionBuilder) {
		super(pageSize, table.count().run());
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	public BufferedPagedEnumeration(int pageSize, int total, DataBaseTable<B> table, Consumer<ConditionBuilder> conditionBuilder) {
		super(pageSize, total);
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	public BufferedPagedEnumeration(int pageSize, DataBaseView<B> table, String... pks) {
		super(pageSize, table.count().run());
		this.pks = pks;
		this.queryable = table;
		this.conditionBuilder = EMPTY;
	}

	public BufferedPagedEnumeration(int pageSize, int total, DataBaseView<B> table, String... pks) {
		super(pageSize, total);
		this.pks = pks;
		this.queryable = table;
		conditionBuilder = EMPTY;
	}

	public BufferedPagedEnumeration(int pageSize, DataBaseView<B> table, Consumer<ConditionBuilder> conditionBuilder, String... pks) {
		super(pageSize, table.count().run());
		this.pks = pks;
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	public BufferedPagedEnumeration(
			int pageSize,
			int total,
			DataBaseView<B> table,
			Consumer<ConditionBuilder> conditionBuilder,
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
				.run()
				.iterator();
	}

	public Stream<B> stream() {
		return StreamSupport.stream(Spliterators.spliterator(asIterator(), total, Spliterator.ORDERED), false);
	}

	public Stream<B> parallelStream() {
		return StreamSupport.stream(Spliterators.spliterator(asIterator(), total, Spliterator.ORDERED), true);
	}

}
