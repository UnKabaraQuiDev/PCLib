package lu.kbra.pclib.db.view;

import java.util.Map;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.domain.view.ViewStructure;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLHookable;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface AbstractDBView<T extends DataBaseEntry> extends SQLQueryable<T>, SQLHookable {

	DataBaseViewStatus<T, ? extends AbstractDBView<T>> create() throws DBException;

	AbstractDBView<T> drop() throws DBException;

	boolean exists() throws DBException;

	String[] getCreateSQL();

	Map<String, Object> getCustomHints();

	DataBase getDatabase();

	ViewStructure getStructure();

	Class<? extends AbstractDBView<T>> getViewClass();

	T load(T data) throws DBException;

	void setViewStructure(ViewStructure viewStructure);

}
