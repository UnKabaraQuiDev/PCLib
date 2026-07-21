package lu.kbra.pclib.db.autobuild.postgres.time.misc;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class DurationType implements FixedColumnType<String> {

	@Override
	public Object decode(final String value, final Type type) {
		if (value == null) {
			return null;
		}

		if (type == Duration.class) {
			return Duration.parse(value);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public String encode(final Object value) {
		if (value instanceof Duration) {
			return ((Duration) value).toString();
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
		return "INTERVAL";
	}

	@Override
	public void setObject(PreparedStatement stmt, int index, String value) throws SQLException {
		stmt.setString(index, value);
	}

}
