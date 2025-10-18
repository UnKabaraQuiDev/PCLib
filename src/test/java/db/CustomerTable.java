package db;

import java.util.List;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.datastructure.pair.Pair;
import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.SQLRequestType;
import lu.pcy113.pclib.db.autobuild.query.Query;

public class CustomerTable extends DataBaseTable<CustomerData> {

	public CustomerTable(DataBase dataBase) {
		super(dataBase);
	}

	@Query(columns = "name")
	public NextTask<String, ?, List<CustomerData>> BY_NAME;

	@Query(columns = "name")
	public NextTask<String, ?, CustomerData> BY_NAME_UNIQUE;

	@Query(columns = { "name", "email" })
	public NextTask<Pair<String, String>, ?, CustomerData> BY_NAME_AND_EMAIL;

	@Query("SELECT * FROM customer WHERE id > ?;")
	public NextTask<Integer, ?, List<CustomerData>> ID_SUP;

	@Query
	public List<CustomerData> all() {
		return null;
	}

	@Query(columns = "name", strategy = Query.Type.SINGLE_THROW)
	public CustomerData byName(String name) {
		return null;
	}

	@Query("SELECT * FROM customer WHERE id > ?;")
	public CustomerData idSup(int id) {
		return null;
	}

	@Override
	public void requestHook(SQLRequestType type, Object query) {
		// System.out.println(type + " = " + (query instanceof ClientPreparedStatement ?
		// ((PreparedQuery) ((ClientPreparedStatement) query).getQuery()).asSql() :
		// query.toString()));
	}

}
