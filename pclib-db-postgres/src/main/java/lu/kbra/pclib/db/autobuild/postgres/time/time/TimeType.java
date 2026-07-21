package lu.kbra.pclib.db.autobuild.postgres.time.time;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.LocalTime;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class TimeType implements FixedColumnType<Time> {

	@Override
	public Object decode(final Time value, final Type type) {
		if (value == null) {
			return null;
		}

		if (type == Time.class) {
			return value;
		} else if (type == LocalTime.class) {
			return value.toLocalTime();
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Time encode(final Object value) {
		if (value instanceof Time) {
			return (Time) value;
		} else if (value instanceof LocalTime) {
			return Time.valueOf((LocalTime) value);
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Time getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getTime(columnIndex);
	}

	@Override
	public Time getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getTime(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.TIME;
	}

	@Override
	public String getTypeName() {
		return "TIME";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Time value) throws SQLException {
		stmt.setTime(index, value);
	}

}