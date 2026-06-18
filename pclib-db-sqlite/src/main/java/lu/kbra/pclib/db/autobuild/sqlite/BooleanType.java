package lu.kbra.pclib.db.autobuild.sqlite;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.autobuild.column.type.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType.FixedColumnType;

public class BooleanType implements FixedColumnType {

	@Override
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}
		if (type == Boolean.class || type == boolean.class) {
			return ((Number) value).longValue() != 0L;
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof Boolean) {
			return (Boolean) value ? 1L : 0L;
		} else if (value instanceof Number) {
			return ((Number) value).longValue() == 0L ? 0L : 1L;
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
