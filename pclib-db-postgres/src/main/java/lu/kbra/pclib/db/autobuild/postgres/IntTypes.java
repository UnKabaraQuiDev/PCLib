package lu.kbra.pclib.db.autobuild.postgres;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType.FixedColumnType;

public final class IntTypes {

	public static class BigIntType implements FixedColumnType {
		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final long longValue = value instanceof BigInteger ? ((BigInteger) value).longValueExact() : ((Number) value).longValue();
			if (type == Long.class || type == long.class) {
				return longValue;
			} else if (type == BigInteger.class) {
				return BigInteger.valueOf(longValue);
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof BigInteger) {
				return ((BigInteger) value).longValueExact();
			} else if (value instanceof Number) {
				return ((Number) value).longValue();
			}
			return ColumnType.unsupported(value);
		}

		@Override
		public Long getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getLong(columnIndex);
		}

		@Override
		public Long getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getLong(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.BIGINT;
		}

		@Override
		public String getTypeName() {
			return "BIGINT";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setLong(index, ((Number) value).longValue());
		}
	}

	public static class IntegerType implements FixedColumnType {
		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final int intValue = ((Number) value).intValue();
			if (type == Long.class || type == long.class) {
				return (long) intValue;
			} else if (type == Integer.class || type == int.class) {
				return intValue;
			} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
				return PCUtils.valueOfOrdinal((Class<? extends Enum>) ((Class<?>) type).asSubclass(Enum.class), intValue);
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Number) {
				return ((Number) value).intValue();
			} else if (value instanceof Enum<?>) {
				return ((Enum<?>) value).ordinal();
			}
			return ColumnType.unsupported(value);
		}

		@Override
		public Integer getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getInt(columnIndex);
		}

		@Override
		public Integer getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getInt(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.INTEGER;
		}

		@Override
		public String getTypeName() {
			return "INTEGER";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setInt(index, ((Number) value).intValue());
		}
	}

	public static class SmallIntType implements FixedColumnType {
		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final short shortValue = ((Number) value).shortValue();
			if (type == Long.class || type == long.class) {
				return (long) shortValue;
			} else if (type == Integer.class || type == int.class) {
				return (int) shortValue;
			} else if (type == Short.class || type == short.class) {
				return shortValue;
			} else if (type == Byte.class || type == byte.class) {
				return (byte) shortValue;
			} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
				return PCUtils.valueOfOrdinal((Class<? extends Enum>) ((Class<?>) type).asSubclass(Enum.class), shortValue);
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Number) {
				return ((Number) value).shortValue();
			} else if (value instanceof Enum<?>) {
				return (short) ((Enum<?>) value).ordinal();
			}
			return ColumnType.unsupported(value);
		}

		@Override
		public Short getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getShort(columnIndex);
		}

		@Override
		public Short getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getShort(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.SMALLINT;
		}

		@Override
		public String getTypeName() {
			return "SMALLINT";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setShort(index, ((Number) value).shortValue());
		}
	}

	private IntTypes() {
	}
}
