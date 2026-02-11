package lu.kbra.pclib.db.autobuild.column.type.sqlite;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public class IntegerType implements FixedColumnType {

	@Override
	public String getTypeName() {
		return "INTEGER";
	}

	@Override
	public int getSQLType() {
		return Types.INTEGER;
	}

	@Override
	public Object encode(Object value) {
		if (value instanceof Long) {
			return (long) value;
		} else if (value instanceof Integer) {
			return (long) (Integer) value;
		} else if (value instanceof Short) {
			return (long) (Short) value;
		} else if (value instanceof Character) {
			return (long) (Character) value;
		} else if (value instanceof Byte) {
			return (long) (Byte) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Object decode(Object value, Type type) {
		if (type == Long.class || type == long.class) {
			return (long) value;
		} else if (type == Integer.class || type == int.class) {
			return (int) value;
		} else if (type == Short.class || type == short.class) {
			return (short) value;
		} else if (type == Character.class || type == char.class) {
			return (char) value;
		} else if (type == Byte.class || type == byte.class) {
			return (byte) value;
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
		stmt.setLong(index, (long) value);
	}

	@Override
	public Integer getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getInt(columnIndex);
	}

	@Override
	public Integer getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

}
