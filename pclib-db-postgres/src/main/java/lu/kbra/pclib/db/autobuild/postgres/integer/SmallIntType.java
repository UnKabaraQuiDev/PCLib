package lu.kbra.pclib.db.autobuild.postgres.integer;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class SmallIntType implements FixedColumnType<Short> {

	@Override
	@SuppressWarnings({})
	public Object decode(final Short value, final Type type) {
		if (type == Short.class || type == short.class) {
			return value.shortValue();
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Short encode(final Object value) {
		if (value instanceof Short) {
			return (short) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Short getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getShort(columnIndex);
	}

	@Override
	public Short getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getShort(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.SMALLINT;
	}

	@Override
	public String getTypeName() {
		return "SMALLINT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Short value) throws SQLException {
		stmt.setShort(index, value);
	}

}