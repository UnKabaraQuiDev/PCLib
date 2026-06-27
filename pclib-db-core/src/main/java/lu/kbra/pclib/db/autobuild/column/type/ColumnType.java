package lu.kbra.pclib.db.autobuild.column.type;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.db.autobuild.dialect.SQLStructureVisitor;

public interface ColumnType {

	@FunctionalInterface
	public interface FixedColumnType extends ColumnType {

		@Override
		default boolean isVariable() {
			return false;
		}

		@Override
		default Object variableValue() {
			return null;
		}

	}

	static int asInt(final Object object) {
		if (object instanceof String) {
			return Integer.parseInt((String) object);
		} else if (object.getClass() == int.class) {
			return (int) object;
		} else if (object.getClass() == Integer.class) {
			return (Integer) object;
		} else {
			throw new IllegalArgumentException("Unsupported type: " + object.getClass() + " for: " + object);
		}
	}

	static Object unsupported(final Class<?> clazz) throws IllegalArgumentException {
		throw new IllegalArgumentException("Unsupported type: " + clazz.getName());
	}

	static Object unsupported(final Object value) throws IllegalArgumentException {
		throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
	}

	static Object unsupported(final Type type) throws IllegalArgumentException {
		throw new IllegalArgumentException("Unsupported type: " + type);
	}

	default String build(final SQLStructureVisitor structureVisitor) {
		return this.getTypeName() + (this.isVariable() ? "(" + this.variableValue() + ")" : "");
	}

	default Object decode(final Object value, final Type type) {
		if (type instanceof Class<?>) {
			((Class<?>) type).cast(value);
		}
		return ColumnType.unsupported(type);
	}

	default Object encode(final Object value) {
		return value;
	}

	default Object getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getObject(columnIndex);
	}

	default Object getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getObject(columnName);
	}

	default int getSQLType() {
		return -1;
	}

	String getTypeName();

	boolean isVariable();

	default Object load(final ResultSet rs, final int columnIndex, final Type type) throws SQLException {
		return this.decode(this.getObject(rs, columnIndex), type);
	}

	default Object load(final ResultSet rs, final String columnName, final Type type) throws SQLException {
		return this.decode(this.getObject(rs, columnName), type);
	}

	default void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setObject(index, value);
	}

	default void store(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		if (value == null) {
			if (this.getSQLType() == -1) {
				stmt.setObject(index, null);
			} else {
				stmt.setNull(index, this.getSQLType());
			}
		} else {
			this.setObject(stmt, index, this.encode(value));
		}
	}

	Object variableValue();

}
