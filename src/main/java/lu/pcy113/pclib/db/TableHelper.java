package lu.pcy113.pclib.db;

import java.util.function.Supplier;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;

public final class TableHelper {

	public static <T extends SQLEntry> NextTask<Void, T> insertOrLoad(DataBaseTable<T> table, T entry, Supplier<SQLQuery<T>> query) {
		return table
				.exists(entry)
				// throws the exception to the parent / the child if a catcher is registered
				// .thenApply((c) -> c.ifError(e -> e.printStackTrace()))
				.thenCompose((c) -> c != null && (c.getData() == null || c.getData()) ?
						table.query(query.get())
									.thenApply(e -> e.mapOk(v -> v.get(0))) : 
						table.insertAndReload(entry)
									.thenApply((e) -> e.getData()));
	}

}
