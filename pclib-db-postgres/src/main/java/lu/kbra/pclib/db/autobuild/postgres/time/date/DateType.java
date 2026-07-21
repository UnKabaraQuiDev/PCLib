package lu.kbra.pclib.db.autobuild.postgres.time.date;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.ZoneOffset;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class DateType implements FixedColumnType<java.sql.Date> {

	@Override
	public Object decode(final java.sql.Date value, final Type type) {
		if (value == null) {
			return null;
		}

		if (type == LocalDate.class) {
			return value.toLocalDate();
		} else if (type == java.sql.Date.class) {
			return value;
		} else if (type == java.util.Date.class) {
			return new java.util.Date(value.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public java.sql.Date encode(final Object value) {
		if (value instanceof LocalDate) {
			return java.sql.Date.valueOf((LocalDate) value);
		} else if (value instanceof java.sql.Date) {
			return (java.sql.Date) value;
		} else if (value instanceof java.util.Date) {
			return java.sql.Date.valueOf(((java.util.Date) value).toInstant().atZone(ZoneOffset.UTC).toLocalDate());
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Date getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getDate(columnIndex);
	}

	@Override
	public Date getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getDate(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.DATE;
	}

	@Override
	public String getTypeName() {
		return "DATE";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final java.sql.Date value) throws SQLException {
		stmt.setDate(index, value);
	}

}
