package lu.kbra.pclib.db.autobuild.postgres.time.misc;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.MonthDay;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class MonthDayType implements FixedColumnType<Integer> {

	@Override
	public Object decode(final Integer value, final Type type) {
		if (value == null) {
			return null;
		}

		final int encoded = value;
		if (type == MonthDay.class) {
			return MonthDay.of(encoded / 100, encoded % 100);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Integer encode(final Object value) {
		if (value instanceof MonthDay) {
			final MonthDay monthDay = (MonthDay) value;
			return monthDay.getMonthValue() * 100 + monthDay.getDayOfMonth();
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Integer getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getInt(columnIndex);
	}

	@Override
	public Integer getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getInt(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.INTEGER;
	}

	@Override
	public String getTypeName() {
		return "SMALLINT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Integer value) throws SQLException {
		stmt.setInt(index, value);
	}

}