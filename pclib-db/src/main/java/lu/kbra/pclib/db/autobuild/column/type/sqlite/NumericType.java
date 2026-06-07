package lu.kbra.pclib.db.autobuild.column.type.sqlite;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public class NumericType implements FixedColumnType {

	@Override
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}
		final BigDecimal decimal = value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(value.toString());
		if (type == BigDecimal.class) {
			return decimal;
		} else if (type == BigInteger.class) {
			return decimal.toBigInteger();
		} else if (type == Double.class || type == double.class) {
			return decimal.doubleValue();
		} else if (type == Float.class || type == float.class) {
			return decimal.floatValue();
		} else if (type == Long.class || type == long.class) {
			return decimal.longValue();
		} else if (type == Integer.class || type == int.class) {
			return decimal.intValue();
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof BigDecimal) {
			return value;
		} else if (value instanceof BigInteger) {
			return new BigDecimal((BigInteger) value);
		} else if (value instanceof Number) {
			return new BigDecimal(value.toString());
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public BigDecimal getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getBigDecimal(columnIndex);
	}

	@Override
	public BigDecimal getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getBigDecimal(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.NUMERIC;
	}

	@Override
	public String getTypeName() {
		return "NUMERIC";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setBigDecimal(index, (BigDecimal) value);
	}

}
