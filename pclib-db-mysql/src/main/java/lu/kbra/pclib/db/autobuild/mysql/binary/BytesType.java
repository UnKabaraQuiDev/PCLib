package lu.kbra.pclib.db.autobuild.mysql.binary;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public class BytesType implements FixedColumnType<byte[]> {

	@Override
	public Object decode(final byte[] value, final Type type) {
		if (type == byte[].class) {
			return value;
		} else if (type == ByteBuffer.class) {
			return ByteBuffer.wrap(value);
		}

		return ColumnType.unsupported(type);
	}

	@Override
	public byte[] encode(final Object value) {
		if (value instanceof byte[]) {
			return (byte[]) value;
		} else if (value instanceof ByteBuffer) {
			return PCUtils.toByteArray((ByteBuffer) value);
		}

		return ColumnType.unsupported(value);
	}

	@Override
	public byte[] getObject(final ResultSet rs, final int columnIndex) throws SQLException {
		return rs.getBytes(columnIndex);
	}

	@Override
	public byte[] getObject(final ResultSet rs, final String columnName) throws SQLException {
		return rs.getBytes(columnName);
	}

	@Override
	public String getTypeName() {
		return "BLOB";
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final byte[] value) throws SQLException {
		stmt.setBytes(index, value);
	}

}