package lu.kbra.pclib.db.autobuild.sqlite;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class TimestampType implements FixedColumnType {

	@Override
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}

		if (type == Timestamp.class) {
			return Timestamp.valueOf(value.toString());
		} else if (type == LocalDateTime.class) {
			return LocalDateTime.parse(value.toString());
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof Timestamp) {
			return ((Timestamp) value).toLocalDateTime().toString();
		} else if (value instanceof LocalDateTime) {
			return value.toString();
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getString(columnIndex);
	}

	@Override
	public String getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getString(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.VARCHAR;
	}

	@Override
	public String getTypeName() {
		return "TEXT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setString(index, (String) value);
	}

}
