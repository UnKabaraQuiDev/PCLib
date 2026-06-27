package lu.kbra.pclib.db.autobuild.sqlite;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class IntegerType implements FixedColumnType {

	@Override
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}

		final long number = value instanceof Number ? ((Number) value).longValue() : Long.parseLong(value.toString());

		if (type == Long.class || type == long.class) {
			return number;
		} else if (type == Integer.class || type == int.class) {
			return (int) number;
		} else if (type == Short.class || type == short.class) {
			return (short) number;
		} else if (type == Character.class || type == char.class) {
			return (char) number;
		} else if (type == Byte.class || type == byte.class) {
			return (byte) number;
		} else if (type == BigInteger.class) {
			return BigInteger.valueOf(number);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof BigInteger) {
			return ((BigInteger) value).longValue();
		} else if (value instanceof Number) {
			return ((Number) value).longValue();
		} else if (value instanceof Character) {
			return (long) (Character) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Long getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getLong(columnIndex);
	}

	@Override
	public Long getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getLong(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.INTEGER;
	}

	@Override
	public String getTypeName() {
		return "INTEGER";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setLong(index, ((Number) value).longValue());
	}

}
