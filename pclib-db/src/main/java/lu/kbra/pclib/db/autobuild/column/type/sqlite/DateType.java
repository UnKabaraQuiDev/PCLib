package lu.kbra.pclib.db.autobuild.column.type.sqlite;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public class DateType implements FixedColumnType {

	@Override
	public String getTypeName() {
		return "TEXT";
	}

	@Override
	public int getSQLType() {
		return Types.VARCHAR;
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof Date) {
			return ((Date) value).toLocalDate().toString();
		} else if (value instanceof LocalDate) {
			return value.toString();
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}
		final LocalDate localDate = LocalDate.parse(value.toString());
		if (type == Date.class) {
			return Date.valueOf(localDate);
		} else if (type == Timestamp.class) {
			return PCUtils.toTimestamp(Date.valueOf(localDate));
		} else if (type == LocalDate.class) {
			return localDate;
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setString(index, (String) value);
	}

	@Override
	public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getString(columnName);
	}

}
