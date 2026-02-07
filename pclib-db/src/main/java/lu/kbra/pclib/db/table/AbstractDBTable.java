package lu.kbra.pclib.db.table;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import lu.kbra.pclib.db.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLHookable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.DataBaseTable.DataBaseTableStatus;

public interface AbstractDBTable<T extends DataBaseEntry> extends SQLQueryable<T>, SQLHookable {

	int truncate() throws SQLException;

	int clear() throws SQLException;

	T load(T data) throws SQLException;

	T updateAndReload(T data) throws SQLException;

	T update(T data) throws SQLException;

	Optional<T> deleteIfExists(T data) throws SQLException;

	Optional<T> deleteUnique(T data) throws SQLException;

	List<T> deleteUniques(T data) throws SQLException;

	T delete(T data) throws SQLException;

	T insertAndReload(T data) throws SQLException;

	T insert(T data) throws SQLException;

	/**
	 * Returns a list of all the possible entries matching with the unique values of the input.
	 */
	List<T> loadByUnique(T data) throws SQLException;

	/**
	 * Loads the first unique result, or throws an exception if none is found.
	 */
	T loadUnique(T data) throws SQLException;

	/**
	 * Loads the first unique result, returns a the newly inserted instance if none is found and throws
	 * an exception if too many are available.
	 */
	T loadIfExistsElseInsert(T data) throws SQLException;

	/**
	 * Loads the first unique result, returns null if none is found and throws an exception if too many
	 * are available.
	 */
	Optional<T> loadIfExists(T data) throws SQLException;

	boolean exists(T data) throws SQLException;

	boolean existsUniques(T data) throws SQLException;

	boolean existsUnique(T data) throws SQLException;

	int countUniques(T data) throws SQLException;

	int countNotNull(T data) throws SQLException;

	AbstractDBTable<T> drop() throws SQLException;

	DataBaseTableStatus<T, ? extends AbstractDBTable<T>> create() throws SQLException;

	boolean exists() throws SQLException;

	DataBase getDataBase();

	String getCreateSQL();

	String[] getPrimaryKeysNames();

}
