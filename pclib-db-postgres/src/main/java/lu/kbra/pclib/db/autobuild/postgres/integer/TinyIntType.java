package lu.kbra.pclib.db.autobuild.postgres.integer;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class TinyIntType implements FixedColumnType<Byte> {

	@Override
	@SuppressWarnings({})
	public Object decode(final Byte value, final Type type) {
		if (type == Byte.class || type == byte.class) {
			return (byte) value.byteValue();
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public Byte encode(final Object value) {
		if (value instanceof Byte) {
			return (Byte) value;
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Byte getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getByte(columnIndex);
	}

	@Override
	public Byte getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getByte(columnName);
	}

	@Override
	public int getSQLType() {
		return Types.TINYINT;
	}

	@Override
	public String getTypeName() {
		return "TINYINT";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Byte value) throws SQLException {
		stmt.setByte(index, value);
	}

}
