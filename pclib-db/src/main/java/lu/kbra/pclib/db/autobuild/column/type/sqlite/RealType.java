package lu.kbra.pclib.db.autobuild.column.type.sqlite;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public class RealType implements FixedColumnType {

	@Override
	public String getTypeName() {
		return "REAL";
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public int getSQLType() {
		return Types.REAL;
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof Double) {
			return (double) value;
		} else if (value instanceof Float) {
			return (double) (Float) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Object decode(final Object value, final Type type) {
		if (type == Double.class || type == double.class) {
			return (double) value;
		} else if (type == Float.class || type == float.class) {
			return (float) (double) value;
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		if (value instanceof Double) {
			stmt.setDouble(index, (double) value);
		}
	}

	@Override
	public Double getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getDouble(columnIndex);
	}

	@Override
	public Double getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getDouble(columnName);
	}

}
