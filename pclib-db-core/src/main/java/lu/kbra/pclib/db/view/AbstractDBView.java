package lu.kbra.pclib.db.view;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import lu.kbra.pclib.db.base.Database;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DatabaseEntry;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface AbstractDBView<T extends DatabaseEntry> extends SQLQueryable<T> {

	DatabaseViewStatus<T, ? extends AbstractDBView<T>> create() throws DBException;

	AbstractDBView<T> drop() throws DBException;

	boolean exists() throws DBException;

	String[] getCreateSQL();

	Map<String, Object> getCustomHints();

	@Override
	Database getDatabase();

	@Override
	ViewStructure getStructure();

	int countNotNull(T data) throws DBException;

	int countUniques(T data) throws DBException;

	T load(T data) throws DBException;

	Optional<T> loadIfExists(T data) throws DBException;

	T loadUnique(T data) throws DBException;

	List<T> loadByUnique(T data) throws DBException;

	Optional<T> loadUniqueIfExists(T data) throws DBException;

	boolean exists(T data) throws DBException;

	boolean existsUnique(T data) throws DBException;

	boolean existsUniques(T data) throws DBException;

	void setViewStructure(ViewStructure viewStructure);

	<C extends Collection<T>> C loadAll(final C data) throws DBException;

	<C extends Collection<T>, D extends Collection<T>> D loadIfExists(C datas, Supplier<D> supplier) throws DBException;

	<C extends Collection<T>, D extends Collection<T>> D filterExists(C datas, Supplier<D> supplier) throws DBException;

	<C extends Collection<T>, D extends Collection<T>> D filterExistsUnique(C datas, Supplier<D> supplier) throws DBException;

}
