package lu.pcy113.pclib.db.autobuild.column;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import lu.pcy113.pclib.db.autobuild.SQLBuildable;

public interface ColumnType extends SQLBuildable {

	String getTypeName();

	boolean isVariable();

	Object variableValue();

	default void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
		if (value == null) {
			if (getSQLType() == -1) {
				stmt.setObject(index++, null);
			} else {
				stmt.setNull(index++, getSQLType());
			}
		} else {
			stmt.setObject(index++, encode(value));
		}
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
