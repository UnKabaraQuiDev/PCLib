package lu.kbra.pclib.db.view;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class NTDataBaseView<T extends DataBaseEntry> extends DataBaseView<T> implements NTAbstractDBView<T> {

	public NTDataBaseView(DataBase dataBase) {
		super(dataBase);
	}

	public NTDataBaseView(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public NTDataBaseView(DataBase dataBase, DataBaseEntryUtils dbEntryUtils, Class<? extends NTAbstractDBView<T>> viewClass) {
		super(dataBase, dbEntryUtils, (Class<? extends AbstractDBView<T>>) viewClass);
	}

	@Override
	public <B> NextTask<Void, ?, B> ntQuery(SQLQuery<T, B> query) {
		return NextTask.create(() -> query(query));
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExists() {
		return NextTask.create(() -> exists());
	}

	@Override
	public NextTask<Void, ?, DataBaseViewStatus<T, ? extends NTAbstractDBView<T>>> ntCreate() {
		// cast if safe because this implements NTDataBaseView<T>
		return NextTask.create(() -> (DataBaseViewStatus<T, ? extends NTDataBaseView<T>>) create());
	}

	@Override
	public NextTask<Void, ?, NTAbstractDBView<T>> ntDrop() {
		return NextTask.create(() -> {
			drop();
			return this;
		});
	}

	@Override
	public NextTask<Void, ?, T> ntLoad(T data) {
		return NextTask.create(() -> load(data));
	}

	@Override
	public NextTask<Void, ?, Integer> ntCount() {
		return NextTask.create(() -> count());
	}

	@Override
	public Class<? extends NTSQLQueryable<T>> getTargetClass() {
		return (Class<? extends NTSQLQueryable<T>>) super.getTargetClass();
	}

	@Override
	public String toString() {
		return "NTDataBaseView@" + System.identityHashCode(this) + " [dataBase=" + dataBase + ", dbEntryUtils=" + dbEntryUtils
				+ ", viewClass=" + viewClass + "]";
	}

}
