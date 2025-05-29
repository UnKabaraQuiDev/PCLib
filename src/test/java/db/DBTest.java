package db;

import com.mysql.cj.PreparedQuery;
import com.mysql.cj.jdbc.ClientPreparedStatement;

import lu.pcy113.pclib.db.DataBase;
import lu.pcy113.pclib.db.DataBaseConnector;
import lu.pcy113.pclib.db.SQLRequestType;
import lu.pcy113.pclib.db.annotations.base.DB_Base;

@DB_Base(name = "database")
public class DBTest extends DataBase {
	
	public DBTest(DataBaseConnector connector) {
		super(connector);

		System.out.println(connector);
	}
	
	@Override
	public void requestHook(SQLRequestType type, Object query) {
		System.out.println(type + " = " + (query instanceof ClientPreparedStatement ? ((PreparedQuery) ((ClientPreparedStatement) query).getQuery()).asSql() : query.toString()));
	}

}
