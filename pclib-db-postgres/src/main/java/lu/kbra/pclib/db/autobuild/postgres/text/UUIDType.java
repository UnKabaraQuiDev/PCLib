package lu.kbra.pclib.db.autobuild.postgres.text;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class UUIDType implements FixedColumnType<String> {

	@Override
	public Object decode(final String value, final Type type) {
		if (value == null) {
			return null;
		} else if (type == UUID.class) {
			return UUID.fromString(value);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public String encode(final Object value) {
		if (value == null) {
			return null;
		} else if (value instanceof UUID) {
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
		return Types.CHAR;
	}

	@Override
	public String getTypeName() {
		return "CHAR(36)";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final String value) throws SQLException {
		stmt.setString(index, value);
	}

}
