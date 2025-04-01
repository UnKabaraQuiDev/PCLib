package lu.pcy113.pclib.db.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface SQLQuery<T> extends Cloneable {

	T clone();

	public interface SafeSQLQuery<T extends SQLEntry> extends SQLQuery<T> {

		String getPreparedQuerySQL(SQLQueryable<T> table);

		void updateQuerySQL(PreparedStatement stmt) throws SQLException;

	}

	public interface UnsafeSQLQuery<T extends SQLEntry> extends SQLQuery<T> {

		String getQuerySQL(SQLQueryable<T> table);

	}

	public interface TransformativeSQLQuery<T extends SQLEntry> extends SQLQuery<T> {

		List<T> transform(ResultSet rs) throws SQLException;

		public interface SafeTransformativeSQLQuery<T extends SQLEntry> extends TransformativeSQLQuery<T> {

			String getPreparedQuerySQL(SQLQueryable<T> table);

			void updateQuerySQL(PreparedStatement stmt) throws SQLException;

		}

		public interface UnsafeTransformativeSQLQuery<T extends SQLEntry> extends TransformativeSQLQuery<T> {

			String getQuerySQL(SQLQueryable<T> table);

		}

	}

}
