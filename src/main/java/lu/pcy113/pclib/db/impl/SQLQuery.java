package lu.pcy113.pclib.db.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lu.pcy113.pclib.db.autobuild.query.Query;
import lu.pcy113.pclib.db.utils.SimpleSQLQuery;

public interface SQLQuery<T, B> {

	public interface PreparedQuery<T extends DataBaseEntry> extends SQLQuery<T, List<T>> {

		String getPreparedQuerySQL(SQLQueryable<T> table);

		void updateQuerySQL(PreparedStatement stmt) throws SQLException;

	}

	public interface TransformingQuery<T extends DataBaseEntry, B> extends SQLQuery<T, B> {

		B transform(List<T> data) throws SQLException;

		String getPreparedQuerySQL(SQLQueryable<T> table);

		void updateQuerySQL(PreparedStatement stmt) throws SQLException;

	}

	public interface SinglePreparedQuery<T extends DataBaseEntry> extends TransformingQuery<T, T> {

		default T transform(List<T> data) throws SQLException {
			return SimpleSQLQuery.<T, T>transform(data, Query.Type.FIRST_THROW);
		}

	}

	public interface RawTransformingQuery<T extends DataBaseEntry, B> extends SQLQuery<T, List<B>> {

		B transform(ResultSet rs) throws SQLException;

		String getPreparedQuerySQL(SQLQueryable<T> table);

		void updateQuerySQL(PreparedStatement stmt) throws SQLException;

	}

}
