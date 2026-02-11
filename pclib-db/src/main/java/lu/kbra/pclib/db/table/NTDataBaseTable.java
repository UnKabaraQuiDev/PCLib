package lu.kbra.pclib.db.table;

import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLQuery;
import lu.kbra.pclib.db.utils.DataBaseEntryUtils;

public class NTDataBaseTable<T extends DataBaseEntry> extends DataBaseTable<T> implements NTAbstractDBTable<T> {

	public NTDataBaseTable(DataBase dataBase) {
		super(dataBase);
	}

	public NTDataBaseTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils) {
		super(dataBase, dbEntryUtils);
	}

	public NTDataBaseTable(DataBase dataBase, DataBaseEntryUtils dbEntryUtils, Class<? extends AbstractDBTable<T>> tableClass) {
		super(dataBase, dbEntryUtils, tableClass);
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
	public NextTask<Void, ?, DataBaseTableStatus<T, ? extends NTAbstractDBTable<T>>> ntCreate() {
		return NextTask.create(() -> (DataBaseTableStatus<T, ? extends NTDataBaseTable<T>>) create());
	}

	@Override
	public NextTask<Void, ?, NTAbstractDBTable<T>> ntDrop() {
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
	public NextTask<Void, ?, Integer> ntTruncate() {
		return NextTask.create(() -> truncate());
	}

	@Override
	public NextTask<Void, ?, Integer> ntClear() {
		return NextTask.create(() -> clear());
	}

	@Override
	public NextTask<Void, ?, T> ntUpdateAndReload(T data) {
		return NextTask.create(() -> updateAndReload(data));
	}

	@Override
	public NextTask<Void, ?, T> ntUpdate(T data) {
		return NextTask.create(() -> update(data));
	}

	@Override
	public NextTask<Void, Void, Optional<T>> ntDeleteIfExists(T data) {
		return NextTask.create(() -> deleteIfExists(data));
	}

	@Override
	public NextTask<Void, ?, Optional<T>> ntDeleteUnique(T data) {
		return NextTask.create(() -> deleteUnique(data));
	}

	@Override
	public NextTask<Void, ?, List<T>> ntDeleteUniques(T data) {
		return NextTask.create(() -> deleteUniques(data));
	}

	@Override
	public NextTask<Void, ?, T> ntDelete(T data) {
		return NextTask.create(() -> delete(data));
	}

	@Override
	public NextTask<Void, ?, T> ntInsertAndReload(T data) {
		return NextTask.create(() -> insertAndReload(data));
	}

	@Override
	public NextTask<Void, ?, T> ntInsert(T data) {
		return NextTask.create(() -> insert(data));
	}

	@Override
	public NextTask<Void, ?, List<T>> ntLoadByUnique(T data) {
		return NextTask.create(() -> loadByUnique(data));
	}

	@Override
	public NextTask<Void, ?, T> ntLoadUnique(T data) {
		return NextTask.create(() -> loadUnique(data));
	}

	@Override
	public NextTask<Void, ?, T> ntLoadIfExistsElseInsert(T data) {
		return NextTask.create(() -> loadUniqueIfExistsElseInsert(data));
	}

	@Override
	public NextTask<Void, ?, Optional<T>> ntLoadIfExists(T data) {
		return NextTask.create(() -> loadUniqueIfExists(data));
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExists(T data) {
		return NextTask.create(() -> exists(data));
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExistsUniques(T data) {
		return NextTask.create(() -> existsUniques(data));
	}

	@Override
	public NextTask<Void, ?, Boolean> ntExistsUnique(T data) {
		return NextTask.create(() -> existsUnique(data));
	}

	@Override
	public NextTask<Void, ?, Integer> ntCountUniques(T data) {
		return NextTask.create(() -> countUniques(data));
	}

	@Override
	public NextTask<Void, ?, Integer> ntCountNotNull(T data) {
		return NextTask.create(() -> countNotNull(data));
	}

	@Override
	public Class<? extends NTSQLQueryable<T>> getTargetClass() {
		return (Class<? extends NTSQLQueryable<T>>) super.getTargetClass();
	}

	@Override
	public String toString() {
		return "NTDataBaseTable@" + System.identityHashCode(this) + " [dataBase=" + dataBase + ", dbEntryUtils=" + dbEntryUtils
				+ ", structure=" + structure + ", tableClass=" + tableClass + "]";
	}

}