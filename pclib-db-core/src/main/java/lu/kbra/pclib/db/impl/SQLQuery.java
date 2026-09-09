package lu.kbra.pclib.db.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import lu.kbra.pclib.db.annotations.query.Query;
import lu.kbra.pclib.db.exception.DBException;
import lu.kbra.pclib.db.exception.NoMatchingRowException;
import lu.kbra.pclib.db.exception.TooManyMatchingRowsException;
import lu.kbra.pclib.db.exception.UnsupportedQueryTypeException;
import lu.kbra.pclib.impl.supplier.ThrowingSupplier;

public interface SQLQuery<T extends DatabaseEntry, B> {

	public interface PreparedQuery<T extends DatabaseEntry> extends SQLQuery<T, List<T>> {

	}

	public interface RawTransformingQuery<T extends DatabaseEntry, B> extends SQLQuery<T, B> {

		B transform(SQLQueryable<T> table, ResultSet rs) throws SQLException;

	}

	public interface SinglePreparedQuery<T extends DatabaseEntry> extends TransformingQuery<T, T> {

		@Override
		default T transform(final SQLQueryable<T> table, final List<T> data) throws SQLException {
			return TransformingQuery.<T, T>transform(data, Query.Type.FIRST_THROW);
		}

	}

	public interface TransformingQuery<T extends DatabaseEntry, B> extends SQLQuery<T, B> {

		B transform(SQLQueryable<T> table, List<T> data) throws SQLException;

		static <T, B> B transform(final List<T> data, final Query.Type type) throws DBException {
			switch (type) {
			case FIRST_THROW:
				if (data.isEmpty()) {
					throw new NoMatchingRowException("Expected at least one result, but got none.");
				}
				return (B) data.get(0);

			case FIRST_NULL:
				return (B) (data.isEmpty() ? null : data.get(0));

			case SINGLE_THROW:
				if (data.size() != 1) {
					throw new NoMatchingRowException("Expected exactly one result, but got " + data.size() + ".");
				}
				return (B) data.get(0);

			case SINGLE_NULL:
				if (data.isEmpty()) {
					return null;
				}
				if (data.size() > 1) {
					throw new TooManyMatchingRowsException("Expected at most one result, but got " + data.size() + ".");
				}
				return (B) data.get(0);

			case LIST_NULL:
				return (B) (data.isEmpty() ? null : data);

			case LIST_THROW:
				if (data.isEmpty()) {
					throw new NoMatchingRowException("Expected a non-empty list, but got none.");
				}
				return (B) data;

			case LIST_EMPTY:
				return (B) data;

			default:
				throw new UnsupportedQueryTypeException("Unknown result transformation type: " + type);
			}
		}

		static <T> void transformRow(final List<T> data, final Query.Type type, final ThrowingSupplier<T, SQLException> function)
				throws SQLException {
			if (type.isSingle() && data.size() >= 1) {
				throw new TooManyMatchingRowsException("Expected at most one result, but got at least " + (data.size() + 1) + ".");
			}

			data.add(function.get());
		}

	}

	String getPreparedQuerySQL(SQLQueryable<T> instance);

	void updateQuerySQL(SQLQueryable<T> instance, PreparedStatement stmt) throws SQLException;

}
