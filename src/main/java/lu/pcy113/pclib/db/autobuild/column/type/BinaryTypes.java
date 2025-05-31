package lu.pcy113.pclib.db.autobuild.column.type;

import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.pcy113.pclib.PCUtils;
import lu.pcy113.pclib.db.autobuild.column.ColumnType;
import lu.pcy113.pclib.db.autobuild.column.ColumnType.FixedColumnType;

public final class BinaryTypes {

	public static class BinaryType implements ColumnType {

		private final int length;

		public BinaryType(int length) {
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
			return length;
		}

		@Override
		public int getSQLType() {
			return Types.BINARY;
		}

		@Override
		public Object encode(Object value) {
			byte[] bytes = null;
			if (value instanceof byte[]) {
				bytes = (byte[]) value;
			} else if (value instanceof ByteBuffer) {
				bytes = PCUtils.toByteArray((ByteBuffer) value);
			} else {
				return ColumnType.unsupported(value);
			}

			return bytes;
			// trim/expand the array if its too long/short
			// but we assume that the table/dbms returns the correct length
			/*
			 * if (bytes.length == length) { return bytes; } else { return
			 * Arrays.copyOf(bytes, length); }
			 */
		}

		@Override
		public Object decode(Object value, Class<?> type) {
			if (type == byte[].class) {
				return (byte[]) value;
			} else if (type == ByteBuffer.class) {
				return ByteBuffer.wrap((byte[]) value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setBytes(index, (byte[]) value);
		}

		@Override
		public Object getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getBytes(columnIndex);
		}

		@Override
		public Object getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getBytes(columnName);
		}
	}

	public static class VarbinaryType implements ColumnType {

		private final int length;

		public VarbinaryType(int length) {
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
			return length;
		}

		@Override
		public int getSQLType() {
			return Types.VARBINARY;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof byte[]) {
				return (byte[]) value;
			} else if (value instanceof ByteBuffer) {
				return PCUtils.toByteArray((ByteBuffer) value);
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Class<?> type) {
			if (type == byte[].class) {
				return (byte[]) value;
			} else if (type == ByteBuffer.class) {
				return ByteBuffer.wrap((byte[]) value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setBytes(index, (byte[]) value);
		}

		@Override
		public byte[] getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getBytes(columnIndex);
		}

		@Override
		public byte[] getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getBytes(columnName);
		}

	}

	public static class BlobType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "BLOB";
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof byte[]) {
				return (byte[]) value;
			} else if (value instanceof ByteBuffer) {
				return PCUtils.toByteArray((ByteBuffer) value);
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Class<?> type) {
			if (type == byte[].class) {
				return (byte[]) value;
			} else if (type == ByteBuffer.class) {
				return ByteBuffer.wrap((byte[]) value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setBytes(index, (byte[]) value);
		}

		@Override
		public byte[] getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getBytes(columnIndex);
		}

		@Override
		public byte[] getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getBytes(columnName);
		}

	}
}
