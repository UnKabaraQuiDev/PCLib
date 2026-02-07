package lu.kbra.pclib.db.view;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLHookable;
import lu.kbra.pclib.db.view.DataBaseView.DataBaseViewStatus;
import lu.kbra.pclib.db.impl.NTSQLQueryable;

public interface NTAbstractDBView<T extends DataBaseEntry> extends NTSQLQueryable<T>, SQLHookable {

	NextTask<Void, ?, Boolean> ntExists();

	NextTask<Void, ?, DataBaseViewStatus<T, ? extends NTAbstractDBView<T>>> ntCreate();

	NextTask<Void, ?, NTAbstractDBView<T>> ntDrop();

	NextTask<Void, ?, T> ntLoad(T data);

	NextTask<Void, ?, Integer> ntCount();

}
