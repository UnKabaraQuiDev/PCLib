package lu.kbra.pclib.db.view;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;
import lu.kbra.pclib.impl.ThrowingSupplier;

@Deprecated
public class NTDataBaseView<T extends DataBaseEntry> extends DataBaseView<T> implements NTAbstractDBView<T> {

	@Deprecated
	public NTDataBaseView(final DataBase dataBase) {
		super(dataBase);
	}

	@Deprecated
	public NTDataBaseView(final DataBase dataBase, final DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	@Deprecated
	public NTDataBaseView(
			final DataBase dataBase,
			final DataBaseEntryUtils dbEntryUtils,
			final Class<? extends NTAbstractDBView<T>> viewClass) {
		super(dataBase, dbEntryUtils, (Class<? extends AbstractDBView<T>>) viewClass);
	}

	@Deprecated
	@Override
	public <B> NextTask<Void, ?, B> ntQuery(final SQLQuery<T, B> query) {
		return NextTask.create(() -> this.query(query));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Boolean> ntExists() {
		return NextTask.create((ThrowingSupplier<Boolean, Throwable>) this::exists);
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, DataBaseViewStatus<T, ? extends NTAbstractDBView<T>>> ntCreate() {
		// cast if safe because this implements NTDataBaseView<T>
		return NextTask.create(() -> (DataBaseViewStatus<T, ? extends NTDataBaseView<T>>) this.create());
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, NTAbstractDBView<T>> ntDrop() {
		return NextTask.create(() -> {
			this.drop();
			return this;
		});
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, T> ntLoad(final T data) {
		return NextTask.create(() -> this.load(data));
	}

	@Deprecated
	@Override
	public NextTask<Void, ?, Integer> ntCount() {
		return NextTask.create((ThrowingSupplier<Integer, Throwable>) this::count);
	}

	@Deprecated
	@Override
	public Class<? extends NTSQLQueryable<T>> getTargetClass() {
		return (Class<? extends NTSQLQueryable<T>>) super.getTargetClass();
	}

	@Deprecated
	@Override
	public String toString() {
		return "NTDataBaseView@" + System.identityHashCode(this) + " [dataBase=" + this.dataBase + ", dbEntryUtils=" + this.dbEntryUtils
				+ ", viewClass=" + this.viewClass + "]";
	}

}
