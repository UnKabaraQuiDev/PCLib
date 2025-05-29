package db;

import com.mysql.cj.PreparedQuery;
import com.mysql.cj.jdbc.ClientPreparedStatement;

import lu.pcy113.pclib.async.NextTask;
import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseTable;
import lu.pcy113.pclib.db.SQLRequestType;

public class CustomerTable extends DataBaseTable<CustomerData> {

	public CustomerTable(DataBase dataBase) {
		super(dataBase);
	}

	// @Query(columns = "name")
	public NextTask<String, CustomerData> BY_NAME;

	@Override
	public void requestHook(SQLRequestType type, Object query) {
		System.out.println(type + " = " + (query instanceof ClientPreparedStatement ? ((PreparedQuery) ((ClientPreparedStatement) query).getQuery()).asSql() : query.toString()));
	}

}
