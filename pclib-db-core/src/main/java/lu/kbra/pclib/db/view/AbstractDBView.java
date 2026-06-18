package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLHookable;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface AbstractDBView<T extends DataBaseEntry> extends SQLQueryable<T>, SQLHookable {

	DataBaseViewStatus<T, ? extends AbstractDBView<T>> create() throws DBException;

	AbstractDBView<T> drop() throws DBException;

	boolean exists() throws DBException;

	String getCreateSQL();

	DataBase getDataBase();

	T load(T data) throws DBException;

}
