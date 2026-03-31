package lu.kbra.pclib.db.view;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLHookable;

@Deprecated
public interface NTAbstractDBView<T extends DataBaseEntry> extends NTSQLQueryable<T>, SQLHookable {

	@Deprecated
	NextTask<Void, ?, Boolean> ntExists();

	@Deprecated
	NextTask<Void, ?, DataBaseViewStatus<T, ? extends NTAbstractDBView<T>>> ntCreate();

	@Deprecated
	NextTask<Void, ?, NTAbstractDBView<T>> ntDrop();

	@Deprecated
	NextTask<Void, ?, T> ntLoad(T data);

	@Deprecated
	@Override
	NextTask<Void, ?, Integer> ntCount();

}
