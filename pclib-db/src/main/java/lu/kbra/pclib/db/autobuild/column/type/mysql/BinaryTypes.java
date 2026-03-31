package lu.kbra.pclib.db.autobuild.column.type.mysql;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public final class BinaryTypes {

	public static class BinaryType implements ColumnType {

		private final int length;

		public BinaryType(final int length) {
			this.length = length;
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
		public Object variableValue() {
			return this.length;
		}

		@Override
		public int getSQLType() {
			return Types.BINARY;
		}

		@Override
		public Object encode(final Object value) {
			byte[] bytes = null;
			if (value instanceof byte[]) {
				bytes = (byte[]) value;
			} else if (value instanceof ByteBuffer) {
				bytes = PCUtils.toByteArray((ByteBuffer) value);
			} else {
				return ColumnType.unsupported(value);
			}

			return bytes;
		}

		@Override
		public Object decode(final Object value, final Type type) {
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
		public Object getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getBytes(columnIndex);
		}

		@Override
		public Object getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getBytes(columnName);
		}
	}

	public static class VarbinaryType implements ColumnType {

		private final int length;

		public VarbinaryType(final int length) {
			this.length = length;
		}

		@Override
		public String getTypeName() {
			return "VARBINARY";
		}

		@Override
		public boolean isVariable() {
			return true;
		}

		@Override
		public Object variableValue() {
			return this.length;
		}

		@Override
		public int getSQLType() {
			return Types.VARBINARY;
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

	public static class BlobType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "BLOB";
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
}
