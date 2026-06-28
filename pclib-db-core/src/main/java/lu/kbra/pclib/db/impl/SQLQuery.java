package lu.kbra.pclib.db.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.query.SimpleTransformingQuery;

public interface SQLQuery<T extends DataBaseEntry, B> {

	public interface PreparedQuery<T extends DataBaseEntry> extends SQLQuery<T, List<T>> {

	}

	public interface RawTransformingQuery<T extends DataBaseEntry, B> extends SQLQuery<T, B> {

		B transform(ResultSet rs) throws SQLException;

	}

	public interface SinglePreparedQuery<T extends DataBaseEntry> extends TransformingQuery<T, T> {

		@Override
		default T transform(final List<T> data) throws SQLException {
			return SimpleTransformingQuery.<T, T>transform(data, Query.Type.FIRST_THROW);
		}

	}

	public interface TransformingQuery<T extends DataBaseEntry, B> extends SQLQuery<T, B> {

		B transform(List<T> data) throws SQLException;

	}

	String getPreparedQuerySQL(SQLQueryable<T> instance);

	void updateQuerySQL(SQLQueryable<T> instance, PreparedStatement stmt) throws SQLException;

}
