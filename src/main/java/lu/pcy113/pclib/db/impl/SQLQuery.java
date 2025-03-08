package lu.pcy113.pclib.db.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface SQLQuery<T> extends Cloneable {

	T clone();

	public interface SafeSQLQuery<T extends SQLEntry> extends SQLQuery<T> {

		// String getPreparedSelectSQL(DataBaseTable<T> table);

		String getPreparedQuerySQL(SQLQueryable<T> table);

		// void updateSelectSQL(PreparedStatement stmt) throws SQLException;

		void updateQuerySQL(PreparedStatement stmt) throws SQLException;

	}

	public interface UnsafeSQLQuery<T extends SQLEntry> extends SQLQuery<T> {

		// String getSelectSQL(DataBaseTable<T> table);

		String getQuerySQL(SQLQueryable<T> table);

	}

}
