package lu.kbra.pclib.db.loader;

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

	public BufferedPagedEnumeration(final int pageSize, final AbstractDBTable<B> table) {
		super(pageSize, table.count());
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		this.conditionBuilder = BufferedPagedEnumeration.EMPTY;
	}

	public BufferedPagedEnumeration(final int pageSize, final int total, final AbstractDBTable<B> table) {
		super(pageSize, total);
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		this.conditionBuilder = BufferedPagedEnumeration.EMPTY;
	}

	public BufferedPagedEnumeration(final int pageSize, final AbstractDBTable<B> table, final Consumer<ConditionBuilder> conditionBuilder) {
		super(pageSize, table.count());
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	public BufferedPagedEnumeration(
			final int pageSize,
			final int total,
			final AbstractDBTable<B> table,
			final Consumer<ConditionBuilder> conditionBuilder) {
		super(pageSize, total);
		this.pks = table.getPrimaryKeysNames();
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	public BufferedPagedEnumeration(final int pageSize, final AbstractDBView<B> table, final String... pks) {
		super(pageSize, table.count());
		this.pks = pks;
		this.queryable = table;
		this.conditionBuilder = BufferedPagedEnumeration.EMPTY;
	}

	public BufferedPagedEnumeration(final int pageSize, final int total, final AbstractDBView<B> table, final String... pks) {
		super(pageSize, total);
		this.pks = pks;
		this.queryable = table;
		this.conditionBuilder = BufferedPagedEnumeration.EMPTY;
	}

	public BufferedPagedEnumeration(
			final int pageSize,
			final AbstractDBView<B> table,
			final Consumer<ConditionBuilder> conditionBuilder,
			final String... pks) {
		super(pageSize, table.count());
		this.pks = pks;
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	public BufferedPagedEnumeration(
			final int pageSize,
			final int total,
			final AbstractDBView<B> table,
			final Consumer<ConditionBuilder> conditionBuilder,
			final String... pks) {
		super(pageSize, total);
		this.pks = pks;
		this.queryable = table;
		this.conditionBuilder = conditionBuilder;
	}

	@Override
	protected Iterator<B> fetchPage(final int page, final int size) {
		return this.queryable.query(
				QueryBuilder.<B>select().orderBy(this.pks, Type.ASC).where(this.conditionBuilder).limit(size).offset(page * size).list())
				.iterator();
	}

	public Stream<B> stream() {
		return StreamSupport.stream(Spliterators.spliterator(this.asIterator(), this.total, Spliterator.ORDERED), false);
	}

	public Stream<B> parallelStream() {
		return StreamSupport.stream(Spliterators.spliterator(this.asIterator(), this.total, Spliterator.ORDERED), true);
	}

}
