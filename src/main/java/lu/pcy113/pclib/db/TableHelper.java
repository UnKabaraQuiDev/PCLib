package lu.pcy113.pclib.db;

import java.util.List;
import java.util.function.Supplier;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;

/**
 * @deprecated use {@link DataBaseTable} integrated methods; tbr in 1.0
 */
@Deprecated
public final class TableHelper {

	public static <T extends DataBaseEntry> NextTask<Void, T> insertOrLoad(DataBaseTable<T> table, T entry, Supplier<SQLQuery<T, List<T>>> query) {
		return table.exists(entry)
				// throws the exception to the parent / the child if a catcher is registered
				.thenCompose((c) -> c ? table.query(query.get()).thenApply(e -> e.get(0)) : table.insertAndReload(entry));
	}

	public static <T extends DataBaseEntry> NextTask<Void, T> insertOrLoad(DataBaseTable<T> table, T entry, SQLQuery<T, List<T>> query) {
		return table.exists(entry)
				// throws the exception to the parent / the child if a catcher is registered
				.thenCompose((c) -> c ? table.query(query).thenApply(e -> e.get(0)) : table.insertAndReload(entry));
	}

}
