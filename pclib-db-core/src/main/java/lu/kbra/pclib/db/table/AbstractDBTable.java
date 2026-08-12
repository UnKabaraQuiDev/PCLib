package lu.kbra.pclib.db.table;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.table.TableStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.utils.SQLQueryableHookManager;

public interface AbstractDBTable<T extends DatabaseEntry> extends SQLQueryable<T> {

	int clear() throws DBException;

	int countNotNull(T data) throws DBException;

	int countUniques(T data) throws DBException;

	DatabaseTableStatus<T, ? extends AbstractDBTable<T>> create() throws DBException;

	T delete(T data) throws DBException;

	Optional<T> deleteIfExists(T data) throws DBException;

	Optional<T> deleteUnique(T data) throws DBException;

	List<T> deleteUniques(T data) throws DBException;

	AbstractDBTable<T> drop() throws DBException;

	boolean exists() throws DBException;

	boolean exists(T data) throws DBException;

	boolean existsUnique(T data) throws DBException;

	boolean existsUniques(T data) throws DBException;

	String[] getCreateSQL();

	@Override
	Map<String, Object> getCustomHints();

	@Override
	Database getDatabase();

	@Override
	TableStructure getStructure();

	void setTableStructure(TableStructure tableStructure);

	@Override
	SQLQueryableHookManager getQueryableHookManager();

	void setQueryableHookManager(SQLQueryableHookManager queryableHookManager);

	T insert(T data) throws DBException;

	T insertAndReload(T data) throws DBException;

	T load(T data) throws DBException;

	/**
	 * Returns a list of all the possible entries matching with the unique values of the input.
	 */
	List<T> loadByUnique(T data) throws DBException;

	/**
	 * Loads the only unique result, or throws an exception if none is found.
	 */
	T loadUnique(T data) throws DBException;

	/**
	 * Loads the only unique result, returns null if none is found and throws an exception if too many
	 * are available.
	 */
	Optional<T> loadUniqueIfExists(T data) throws DBException;

	Optional<T> loadIfExists(T data) throws DBException;

	/**
	 * Loads the first unique result, returns a the newly inserted instance if none is found and throws
	 * an exception if too many are available.
	 */
	T loadUniqueIfExistsElseInsert(T data) throws DBException;

	int truncate() throws DBException;

	T update(T data) throws DBException;

	T updateAndReload(T data) throws DBException;

	<C extends Collection<T>> C insertAll(C datas) throws DBException;

	<C extends Collection<T>> C insertAndReloadAll(C datas) throws DBException;

	<C extends Collection<T>> C deleteAll(C datas) throws DBException;

	<C extends Collection<T>> C updateAll(C datas) throws DBException;

	<C extends Collection<T>> C updateAndReloadAll(C datas) throws DBException;

	<C extends Collection<T>> C loadAll(C datas) throws DBException;

	<C extends Collection<T>, D extends Collection<T>> D loadIfExists(C datas, Supplier<D> supplier) throws DBException;

	<C extends Collection<T>, D extends Collection<T>> D deleteIfExists(C datas, Supplier<D> supplier) throws DBException;

	<C extends Collection<T>, D extends Collection<T>> D filterExists(C datas, Supplier<D> supplier) throws DBException;

	<C extends Collection<T>, D extends Collection<T>> D filterExistsUnique(C datas, Supplier<D> supplier) throws DBException;

}
