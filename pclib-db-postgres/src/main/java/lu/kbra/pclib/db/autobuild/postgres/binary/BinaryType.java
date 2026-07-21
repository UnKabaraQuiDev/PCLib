package lu.kbra.pclib.db.autobuild.postgres.binary;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;

public class BinaryType implements ColumnType<byte[]> {

	private final int length;

	public BinaryType(final int length) {
		this.length = length;
	}

	public BinaryType(final Object object) {
		this.length = ColumnType.asInt(object);
	}

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
	public int getSQLType() {
		return Types.BINARY;
	}

	@Override
	public String getTypeName() {
		return "BINARY";
	}

	@Override
	public boolean isVariable() {
		return true;
	}

	@Override
	public void setObject(final PreparedStatement stmt, final int index, final byte[] value) throws SQLException {
		stmt.setBytes(index, value);
	}

	@Override
	public Object variableValue() {
		return this.length;
	}
}