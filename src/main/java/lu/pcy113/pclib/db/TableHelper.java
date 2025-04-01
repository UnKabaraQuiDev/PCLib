package lu.pcy113.pclib.db;

import java.util.function.Supplier;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.impl.SQLEntry;
import lu.pcy113.pclib.db.impl.SQLQuery;

public final class TableHelper {

	public static <T extends SQLEntry> NextTask<Void, T> insertOrLoad(DataBaseTable<T> table, T entry, Supplier<SQLQuery<T>> query) {
		return table.exists(entry)
				// throws the exception to the parent / the child if a catcher is registered
				.thenCompose((c) ->
					c != null ?
						table.query(query.get())
							.thenApply(e -> e.get(0)) :
						table.insertAndReload(entry)
				);
	}

}
