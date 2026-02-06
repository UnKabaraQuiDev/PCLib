package lu.kbra.pclib.db;

import lu.kbra.pclib.async.NextTask;
import lu.kbra.pclib.db.DataBaseView.DataBaseViewStatus;
import lu.kbra.pclib.db.impl.DataBaseEntry;
import lu.kbra.pclib.db.impl.SQLHookable;
import lu.kbra.pclib.db.impl.SQLQueryable;

public interface AbstractDBView<T extends DataBaseEntry> extends SQLQueryable<T>, SQLHookable {

	NextTask<Void, ?, Boolean> exists();

	NextTask<Void, ?, DataBaseViewStatus<T>> create();

	NextTask<Void, ?, DataBaseView<T>> drop();

	NextTask<Void, ?, T> load(T data);

	NextTask<Void, ?, Integer> count();

	DataBase getDataBase();

	String getCreateSQL();

}
