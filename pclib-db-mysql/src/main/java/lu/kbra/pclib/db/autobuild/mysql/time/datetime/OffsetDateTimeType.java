package lu.kbra.pclib.db.autobuild.mysql.time.datetime;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

// As ISO-8601 String
public class OffsetDateTimeType implements FixedColumnType<String> {

	@Override
	public Object decode(final String value, final Type type) {
		if (value == null) {
			return null;
		}

		if (type == OffsetDateTime.class) {
			return OffsetDateTime.parse(value);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public String encode(final Object value) {
		if (value instanceof OffsetDateTime) {
			return ((OffsetDateTime) value).toString();
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public String getObject(ResultSet rs, int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(ResultSet rs, String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	@Override
	public String getTypeName() {
		return "TIMESTAMPZ";
	}

	@Override
	public int getSQLType() {
		return Types.TIMESTAMP_WITH_TIMEZONE;
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, String value) throws SQLException {
		stmt.setString(index, value);
	}

}