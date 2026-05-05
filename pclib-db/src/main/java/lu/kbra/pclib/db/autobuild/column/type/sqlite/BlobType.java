package lu.kbra.pclib.db.autobuild.column.type.sqlite;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public class BlobType implements FixedColumnType {

	@Override
	public String getTypeName() {
		return "BLOB";
	}

	@Override
	public int getSQLType() {
		return Types.BLOB;
	}

	@Override
	public Object encode(final Object value) {
		if (value instanceof byte[]) {
			return value;
		} else if (value instanceof ByteBuffer) {
			return PCUtils.toByteArray((ByteBuffer) value);
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public Object decode(final Object value, final Type type) {
		if (value == null) {
			return null;
		}
		if (type == byte[].class) {
			return value;
		} else if (type == ByteBuffer.class) {
			return ByteBuffer.wrap((byte[]) value);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
		stmt.setBytes(index, (byte[]) value);
	}

	@Override
	public byte[] getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getBytes(columnIndex);
	}

	@Override
	public byte[] getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getBytes(columnName);
	}

}
