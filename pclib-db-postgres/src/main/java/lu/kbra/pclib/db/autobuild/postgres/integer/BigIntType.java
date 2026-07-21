package lu.kbra.pclib.db.autobuild.postgres.integer;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class BigIntType implements FixedColumnType<Long> {

	@Override
	public Object decode(final Long value, final Type type) {
		if (type == Long.class || type == long.class) {
			return value.longValue();
		} else if (type == BigInteger.class) {
			return BigInteger.valueOf(value);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Long encode(final Object value) {
		if (value instanceof Long) {
			return (long) value;
		} else if (value instanceof BigInteger) {
			return ((BigInteger) value).longValueExact();
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
		return Types.BIGINT;
	}

	@Override
	public String getTypeName() {
		return "BIGINT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Long value) throws SQLException {
		stmt.setLong(index, value);
	}

}
