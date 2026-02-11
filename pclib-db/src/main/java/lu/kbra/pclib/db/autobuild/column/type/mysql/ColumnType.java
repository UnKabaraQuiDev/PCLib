package lu.kbra.pclib.db.autobuild.column.type.mysql;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.autobuild.SQLBuildable;
import lu.kbra.pclib.db.connector.impl.DataBaseConnector;

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

	default Object load(ResultSet rs, int columnIndex, Type type) throws SQLException {
		return decode(getObject(rs, columnIndex), type);
	}

	default Object load(ResultSet rs, String columnName, Type type) throws SQLException {
		return decode(getObject(rs, columnName), type);
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

	default Object decode(Object value, Type type) {
		if (type instanceof Class<?>)
			((Class<?>) type).cast(value);
		return unsupported(type);
	}

	@Override
	default String build(DataBaseConnector connector) {
		return getTypeName() + (isVariable() ? "(" + variableValue() + ")" : "");
	}

	public static Object unsupported(Object value) throws IllegalArgumentException {
		throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
	}

	public static Object unsupported(Class<?> clazz) throws IllegalArgumentException {
		throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
	}

	public static Object unsupported(Type type) throws IllegalArgumentException {
		throw new IllegalArgumentException("Unsupported type: " + type);
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
