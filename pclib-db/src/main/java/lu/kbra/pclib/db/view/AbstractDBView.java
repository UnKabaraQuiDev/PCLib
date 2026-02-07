package lu.kbra.pclib.db.view;

import java.sql.SQLException;

import lu.kbra.pclib.db.base.DataBase;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLHookable;
import lu.kbra.pclib.db.impl.SQLQueryable;
import lu.kbra.pclib.db.view.DataBaseView.DataBaseViewStatus;

public interface AbstractDBView<T extends DataBaseEntry> extends SQLQueryable<T>, SQLHookable {

	boolean exists() throws SQLException;

	DataBaseViewStatus<T, ? extends AbstractDBView<T>> create() throws SQLException;

	AbstractDBView<T> drop() throws SQLException;

	T load(T data) throws SQLException;

	DataBase getDataBase();

	String getCreateSQL();

}
