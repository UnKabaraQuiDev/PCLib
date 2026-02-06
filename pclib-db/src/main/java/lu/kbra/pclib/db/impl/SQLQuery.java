package lu.kbra.pclib.db.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lu.kbra.pclib.db.autobuild.query.Query;
import lu.kbra.pclib.db.utils.SimpleTransformingQuery;

public interface SQLQuery<T extends DataBaseEntry, B> {

	String getPreparedQuerySQL(SQLQueryable<T> table);

	void updateQuerySQL(PreparedStatement stmt) throws SQLException;

	public interface PreparedQuery<T extends DataBaseEntry> extends SQLQuery<T, List<T>> {

	}

	public interface TransformingQuery<T extends DataBaseEntry, B> extends SQLQuery<T, B> {

		B transform(List<T> data) throws SQLException;

	}

	public interface SinglePreparedQuery<T extends DataBaseEntry> extends TransformingQuery<T, T> {

		default T transform(List<T> data) throws SQLException {
			return SimpleTransformingQuery.<T, T>transform(data, Query.Type.FIRST_THROW);
		}

	}

	public interface RawTransformingQuery<T extends DataBaseEntry, B> extends SQLQuery<T, B> {

		B transform(ResultSet rs) throws SQLException;

	}

}
