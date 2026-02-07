package lu.kbra.pclib.db.table;

import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLHookable;
import lu.kbra.pclib.db.table.DataBaseTable.DataBaseTableStatus;

public interface NTAbstractDBTable<T extends DataBaseEntry> extends NTSQLQueryable<T>, SQLHookable {

	NextTask<Void, ?, Integer> ntTruncate();

	NextTask<Void, ?, Integer> ntClear();

	NextTask<Void, ?, T> ntLoad(T data);

	NextTask<Void, ?, T> ntUpdateAndReload(T data);

	NextTask<Void, ?, T> ntUpdate(T data);

	NextTask<Void, ?, Optional<T>> ntDeleteIfExists(T data);

	NextTask<Void, ?, Optional<T>> ntDeleteUnique(T data);

	NextTask<Void, ?, List<T>> ntDeleteUniques(T data);

	NextTask<Void, ?, T> ntDelete(T data);

	NextTask<Void, ?, T> ntInsertAndReload(T data);

	NextTask<Void, ?, T> ntInsert(T data);

	/**
	 * Returns a list of all the possible entries matching with the unique values of
	 * the input.
	 */
	NextTask<Void, ?, List<T>> ntLoadByUnique(T data);

	/**
	 * Loads the first unique result, or throws an exception if none is found.
	 */
	NextTask<Void, ?, T> ntLoadUnique(T data);

	/**
	 * Loads the first unique result, returns a the newly inserted instance if none
	 * is found and throws an exception if too many are available.
	 */
	NextTask<Void, ?, T> ntLoadIfExistsElseInsert(T data);

	/**
	 * Loads the first unique result, returns null if none is found and throws an
	 * exception if too many are available.
	 */
	NextTask<Void, ?, Optional<T>> ntLoadIfExists(T data);

	NextTask<Void, ?, Boolean> ntExists(T data);

	NextTask<Void, ?, Boolean> ntExistsUniques(T data);

	NextTask<Void, ?, Boolean> ntExistsUnique(T data);

	NextTask<Void, ?, Integer> ntCountUniques(T data);

	NextTask<Void, ?, Integer> ntCountNotNull(T data);

	NextTask<Void, ?, NTAbstractDBTable<T>> ntDrop();

	NextTask<Void, ?, DataBaseTableStatus<T, ? extends NTAbstractDBTable<T>>> ntCreate();

	NextTask<Void, ?, Boolean> ntExists();

}
