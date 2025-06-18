package lu.pcy113.pclib.db;

import java.util.List;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.DataBaseTable.DataBaseTableStatus;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLHookable;
import lu.pcy113.pclib.db.impl.SQLQueryable;

public interface AbstractDBTable<T extends DataBaseEntry> extends SQLQueryable<T>, SQLHookable {

	NextTask<Void, Integer> truncate();

	NextTask<Void, Integer> clear();

	NextTask<Void, Integer> count();

	NextTask<Void, T> load(T data);

	NextTask<Void, T> updateAndReload(T data);

	NextTask<Void, T> update(T data);

	NextTask<Void, T> deleteIfExists(T data);

	NextTask<Void, T> deleteUnique(T data);

	NextTask<Void, List<T>> deleteByUnique(T data);

	NextTask<Void, T> delete(T data);

	NextTask<Void, T> insertAndReload(T data);

	NextTask<Void, T> insert(T data);

	/**
	 * Returns a list of all the possible entries matching with the unique values of
	 * the input.
	 */
	NextTask<Void, List<T>> loadByUnique(T data);

	/**
	 * Loads the first unique result, or throws an exception if none is found.
	 */
	NextTask<Void, T> loadUnique(T data);

	/**
	 * Loads the first unique result, returns a the newly inserted instance if none
	 * is found and throws an exception if too many are available.
	 */
	NextTask<Void, T> loadIfExistsElseInsert(T data);

	/**
	 * Loads the first unique result, returns null if none is found and throws an
	 * exception if too many are available.
	 */
	NextTask<Void, T> loadIfExists(T data);

	NextTask<Void, Boolean> exists(T data);

	NextTask<Void, Boolean> existsUnique(T data);

	NextTask<Void, Integer> countUniques(T data);
	
	NextTask<Void, Integer> countNotNull(T data);

	NextTask<Void, DataBaseTable<T>> drop();

	NextTask<Void, DataBaseTableStatus<T>> create();

	NextTask<Void, Boolean> exists();

	DataBase getDataBase();

	String getCreateSQL();

}
