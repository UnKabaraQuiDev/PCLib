package lu.kbra.pclib.db.view;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLHookable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.table.DBException;
import lu.kbra.pclib.db.view.DataBaseView.DataBaseViewStatus;

public interface AbstractDBView<T extends DataBaseEntry> extends SQLQueryable<T>, SQLHookable {

	boolean exists() throws DBException;

	DataBaseViewStatus<T, ? extends AbstractDBView<T>> create() throws DBException;

	AbstractDBView<T> drop() throws DBException;

	T load(T data) throws DBException;

	DataBase getDataBase();

	String getCreateSQL();

}
