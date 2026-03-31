package lu.kbra.pclib.db.table;

import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.NTSQLQueryable;
import lu.kbra.pclib.db.impl.SQLHookable;

@Deprecated
public interface NTAbstractDBTable<T extends DataBaseEntry> extends NTSQLQueryable<T>, SQLHookable {

	@Deprecated
	NextTask<Void, ?, Integer> ntTruncate();

	@Deprecated
	NextTask<Void, ?, Integer> ntClear();

	@Deprecated
	NextTask<Void, ?, T> ntLoad(T data);

	@Deprecated
	NextTask<Void, ?, T> ntUpdateAndReload(T data);

	@Deprecated
	NextTask<Void, ?, T> ntUpdate(T data);

	@Deprecated
	NextTask<Void, ?, Optional<T>> ntDeleteIfExists(T data);

	@Deprecated
	NextTask<Void, ?, Optional<T>> ntDeleteUnique(T data);

	@Deprecated
	NextTask<Void, ?, List<T>> ntDeleteUniques(T data);

	@Deprecated
	NextTask<Void, ?, T> ntDelete(T data);

	@Deprecated
	NextTask<Void, ?, T> ntInsertAndReload(T data);

	@Deprecated
	NextTask<Void, ?, T> ntInsert(T data);

	/**
	 * Returns a list of all the possible entries matching with the unique values of the input.
	 */
	@Deprecated
	NextTask<Void, ?, List<T>> ntLoadByUnique(T data);

	/**
	 * Loads the first unique result, or throws an exception if none is found.
	 */
	@Deprecated
	NextTask<Void, ?, T> ntLoadUnique(T data);

	/**
	 * Loads the first unique result, returns a the newly inserted instance if none is found and throws
	 * an exception if too many are available.
	 */
	@Deprecated
	NextTask<Void, ?, T> ntLoadIfExistsElseInsert(T data);

	/**
	 * Loads the first unique result, returns null if none is found and throws an exception if too many
	 * are available.
	 */
	@Deprecated
	NextTask<Void, ?, Optional<T>> ntLoadIfExists(T data);

	@Deprecated
	NextTask<Void, ?, Boolean> ntExists(T data);

	@Deprecated
	NextTask<Void, ?, Boolean> ntExistsUniques(T data);

	@Deprecated
	NextTask<Void, ?, Boolean> ntExistsUnique(T data);

	@Deprecated
	NextTask<Void, ?, Integer> ntCountUniques(T data);

	@Deprecated
	NextTask<Void, ?, Integer> ntCountNotNull(T data);

	@Deprecated
	NextTask<Void, ?, NTAbstractDBTable<T>> ntDrop();

	@Deprecated
	NextTask<Void, ?, DataBaseTableStatus<T, ? extends NTAbstractDBTable<T>>> ntCreate();

	@Deprecated
	NextTask<Void, ?, Boolean> ntExists();

}
