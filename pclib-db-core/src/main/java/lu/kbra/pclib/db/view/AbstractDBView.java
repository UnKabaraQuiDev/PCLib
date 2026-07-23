package lu.kbra.pclib.db.view;

import java.util.Map;

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

	T load(T data) throws DBException;

	void setViewStructure(ViewStructure viewStructure);

}
