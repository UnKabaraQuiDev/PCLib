package lu.pcy113.pclib.db;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.DataBaseView.DataBaseViewStatus;
import lu.pcy113.pclib.db.impl.DataBaseEntry;
import lu.pcy113.pclib.db.impl.SQLHookable;
import lu.pcy113.pclib.db.impl.SQLQueryable;

public interface AbstractDBView<T extends DataBaseEntry> extends SQLQueryable<T>, SQLHookable {

	NextTask<Void, ?, Boolean> exists();

	NextTask<Void, ?, DataBaseViewStatus<T>> create();

	NextTask<Void, ?, DataBaseView<T>> drop();

	NextTask<Void, ?, T> load(T data);

	NextTask<Void, ?, Integer> count();

	DataBase getDataBase();

	String getCreateSQL();

}
