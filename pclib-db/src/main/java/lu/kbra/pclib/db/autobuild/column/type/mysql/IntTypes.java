package lu.kbra.pclib.db.autobuild.column.type.mysql;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public final class IntTypes {

	public static class BitType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "BIT";
		}

		@Override
		public int getSQLType() {
			return Types.BIT;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Boolean) {
				return (boolean) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (type == Boolean.class || type == boolean.class) {
				return (boolean) value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setBoolean(index, (boolean) value);
		}

		@Override
		public Boolean getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getBoolean(columnIndex);
		}

		@Override
		public Boolean getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getBoolean(columnName);
		}

	}

	public static class TinyIntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "TINYINT";
		}

		@Override
		public int getSQLType() {
			return Types.TINYINT;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Byte) {
				return (short) (Byte) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (type == Long.class || type == long.class) {
				return (long) value;
			} else if (type == Integer.class || type == int.class) {
				return (int) value;
			} else if (type == Short.class || type == short.class) {
				return (short) value;
			} else if (type == Byte.class || type == byte.class) {
				return (byte) value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setByte(index, (byte) value);
		}

		@Override
		public Byte getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getByte(columnIndex);
		}

		@Override
		public Byte getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getByte(columnName);
		}

	}

	public static class SmallIntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "SMALLINT";
		}

		@Override
		public int getSQLType() {
			return Types.SMALLINT;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Short) {
				return (short) value;
			} else if (value instanceof Byte) {
				return (short) (Byte) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (type == Long.class || type == long.class) {
				return (long) value;
			} else if (type == Integer.class || type == int.class) {
				return (int) value;
			} else if (type == Short.class || type == short.class) {
				return (short) value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setShort(index, (short) value);
		}

		@Override
		public Short getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getShort(columnIndex);
		}

		@Override
		public Short getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getShort(columnName);
		}

	}

	public static class IntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "INT";
		}

		@Override
		public int getSQLType() {
			return Types.INTEGER;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Integer) {
				return (int) value;
			} else if (value instanceof Short) {
				return (int) (Short) value;
			} else if (value instanceof Byte) {
				return (int) (Byte) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (type == Long.class || type == long.class) {
				return (long) value;
			} else if (type == Integer.class || type == int.class) {
				return (int) value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setInt(index, (int) value);
		}

		@Override
		public Integer getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getInt(columnIndex);
		}

		@Override
		public Integer getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getInt(columnName);
		}

	}

	public static class BigIntType implements FixedColumnType {

		@Override
		public String getTypeName() {
			return "BIGINT";
		}

		@Override
		public int getSQLType() {
			return Types.BIGINT;
		}

		@Override
		public Object encode(Object value) {
			if (value instanceof Long) {
				return (long) value;
			} else if (value instanceof Integer) {
				return (long) (Integer) value;
			} else if (value instanceof Short) {
				return (long) (Short) value;
			} else if (value instanceof Byte) {
				return (long) (Byte) value;
			} else if (value instanceof BigInteger) {
				return ((BigInteger) value).longValueExact();
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(Object value, Type type) {
			if (type == Long.class || type == long.class) {
				return (long) value;
			} else if (type == BigInteger.class) {
				return BigInteger.valueOf((long) value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(PreparedStatement stmt, int index, Object value) throws SQLException {
			stmt.setLong(index, (long) value);
		}

		@Override
		public Long getObject(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getLong(columnIndex);
		}

		@Override
		public Long getObject(ResultSet rs, String columnName) throws SQLException {
			return rs.getLong(columnName);
		}

	}

}
