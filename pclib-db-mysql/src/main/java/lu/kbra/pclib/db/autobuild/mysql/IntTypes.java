package lu.kbra.pclib.db.autobuild.mysql;

import java.lang.reflect.Type;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public final class IntTypes {

	public static class BigIntType implements FixedColumnType<Long> {

		@Override
		public Object decode(final Long value, final Type type) {
			if (type == Long.class || type == long.class) {
				return value.longValue();
			} else if (type == BigInteger.class) {
				return BigInteger.valueOf(value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Long encode(final Object value) {
			if (value instanceof Long) {
				return (long) value;
			} else if (value instanceof BigInteger) {
				return ((BigInteger) value).longValueExact();
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
		public void setObject(final PreparedStatement stmt, final int index, final Long value) throws SQLException {
			stmt.setLong(index, value);
		}

	}

	public static class BitType implements FixedColumnType<Boolean> {

		@Override
		public Object decode(final Boolean value, final Type type) {
			if (type == Boolean.class || type == boolean.class) {
				return value.booleanValue();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Boolean encode(final Object value) {
			if (value instanceof Boolean) {
				return (boolean) value;
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Boolean getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getBoolean(columnIndex);
		}

		@Override
		public Boolean getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getBoolean(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.BIT;
		}

		@Override
		public String getTypeName() {
			return "BIT";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Boolean value) throws SQLException {
			stmt.setBoolean(index, value);
		}

	}

	public static class IntType implements FixedColumnType<Integer> {

		@SuppressWarnings("unchecked")
		@Override
		public Object decode(final Integer value, final Type type) {
			if (type == Integer.class || type == int.class) {
				return (int) value;
			} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
				return PCUtils.valueOfOrdinal(((Class<?>) type).asSubclass(Enum.class), value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Integer encode(final Object value) {
			if (value instanceof Integer) {
				return (int) value;
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
			return "INT";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Integer value) throws SQLException {
			stmt.setInt(index, value);
		}

	}

	public static class SmallIntType implements FixedColumnType<Short> {

		@Override
		@SuppressWarnings({})
		public Object decode(final Short value, final Type type) {
			if (type == Short.class || type == short.class) {
				return value.shortValue();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Short encode(final Object value) {
			if (value instanceof Short) {
				return (short) value;
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
		public void setObject(final PreparedStatement stmt, final int index, final Short value) throws SQLException {
			stmt.setShort(index, value);
		}

	}

	public static class TinyIntType implements FixedColumnType<Byte> {

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

}
