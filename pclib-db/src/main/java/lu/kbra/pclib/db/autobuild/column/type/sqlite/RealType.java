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
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}

		final double number = value instanceof Number ? ((Number) value).doubleValue() : Double.parseDouble(value.toString());

		if (type == Double.class || type == double.class) {
			return number;
		} else if (type == Float.class || type == float.class) {
			return (float) number;
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Double getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getDouble(columnIndex);
	}

	@Override
	public Double getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getDouble(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.REAL;
	}

	@Override
	public String getTypeName() {
		return "REAL";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setDouble(index, ((Number) value).doubleValue());
	}

}
