package lu.kbra.pclib.db.autobuild.postgres.time.datetime;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class InstantType implements FixedColumnType<Timestamp> {

	@Override
	public Object decode(final Timestamp value, final Type type) {
		if (value == null) {
			return null;
		}

		if (type == Timestamp.class) {
			return value;
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Timestamp encode(final Object value) {
		if (value instanceof Timestamp) {
			return (Timestamp) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Timestamp getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getTimestamp(columnIndex);
	}

	@Override
	public Timestamp getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getTimestamp(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.TIMESTAMP_WITH_TIMEZONE;
	}

	@Override
	public String getTypeName() {
		return "TIMESTAMPZ";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Timestamp value) throws SQLException {
		stmt.setTimestamp(index, value);
	}

}
