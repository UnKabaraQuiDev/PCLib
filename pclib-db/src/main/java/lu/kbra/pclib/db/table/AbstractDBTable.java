package lu.kbra.pclib.db.table;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLHookable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.DataBaseTable.DataBaseTableStatus;

public interface AbstractDBTable<T extends DataBaseEntry> extends SQLQueryable<T>, SQLHookable {

	int truncate() throws DBException;

	int clear() throws DBException;

	T load(T data) throws DBException;

	T updateAndReload(T data) throws DBException;

	T update(T data) throws DBException;

	Optional<T> deleteIfExists(T data) throws DBException;

	Optional<T> deleteUnique(T data) throws DBException;

	List<T> deleteUniques(T data) throws DBException;

	T delete(T data) throws DBException;

	T insertAndReload(T data) throws DBException;

	T insert(T data) throws DBException;

	/**
	 * Returns a list of all the possible entries matching with the unique values of the input.
	 */
	List<T> loadByUnique(T data) throws DBException;

	/**
	 * Loads the first unique result, or throws an exception if none is found.
	 */
	T loadUnique(T data) throws DBException;

	/**
	 * Loads the first unique result, returns a the newly inserted instance if none is found and throws
	 * an exception if too many are available.
	 */
	T loadUniqueIfExistsElseInsert(T data) throws DBException;

	/**
	 * Loads the first unique result, returns null if none is found and throws an exception if too many
	 * are available.
	 */
	Optional<T> loadUniqueIfExists(T data) throws DBException;

	boolean exists(T data) throws DBException;

	boolean existsUniques(T data) throws DBException;

	boolean existsUnique(T data) throws DBException;

	int countUniques(T data) throws DBException;

	int countNotNull(T data) throws DBException;

	AbstractDBTable<T> drop() throws DBException;

	DataBaseTableStatus<T, ? extends AbstractDBTable<T>> create() throws DBException;

	boolean exists() throws DBException;

	DataBase getDataBase();

	String getCreateSQL();

	String[] getPrimaryKeysNames();

}
