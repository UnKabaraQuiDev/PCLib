package lu.pcy113.pclib.db.autobuild.column;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.pcy113.pclib.db.autobuild.SQLBuildable;

public interface ColumnType extends SQLBuildable {

	String getTypeName();

	boolean isVariable();

	Object variableValue();

	default void store(PreparedStatement stmt, int index, Object value) throws SQLException {
		if (value == null) {
			if (getSQLType() == -1) {
				stmt.setObject(index, null);
			} else {
				stmt.setNull(index, getSQLType());
			}
		} else {
			this.setObject(stmt, index, encode(value));
		}
	}

	default void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
		stmt.setObject(index, value);
	}

	default Object getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getObject(columnIndex);
	}

	default Object getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getObject(columnName);
	}

	default int getSQLType() {
		return -1;
	}

	default Object encode(Object value) {
		return value;
	}

	@Override
	default String build() {
		return getTypeName() + (isVariable() ? "(" + variableValue() + ")" : "");
	}

	public static Object unsupported(Object value) throws IllegalArgumentException {
		throw new IllegalArgumentException("Unsupported type: " + value.getClass());
	}
	
	@FunctionalInterface
	public interface FixedColumnType extends ColumnType {

		default boolean isVariable() {
			return false;
		}

		default Object variableValue() {
			return null;
		}

	}

}
