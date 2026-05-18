package lu.kbra.pclib.db.autobuild.column.type.postgres;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public final class PostgreSQLTypes {

	private PostgreSQLTypes() {
	}

	public static class ByteAType implements FixedColumnType {
		@Override
		public String getTypeName() {
			return "BYTEA";
		}

		@Override
		public int getSQLType() {
			return Types.BINARY;
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

	public static class TextType implements FixedColumnType {
		@Override
		public String getTypeName() {
			return "TEXT";
		}

		@Override
		public int getSQLType() {
			return Types.VARCHAR;
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof String) {
				return value;
			} else if (value instanceof CharSequence) {
				return value.toString();
			} else if (value instanceof char[]) {
				return new String((char[]) value);
			} else if (value instanceof byte[]) {
				return new String((byte[]) value);
			} else if (value instanceof Character) {
				return Character.toString((Character) value);
			} else if (value instanceof Enum<?>) {
				return ((Enum<?>) value).name();
			}
			return ColumnType.unsupported(value);
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final String text = value.toString();
			if (type == String.class || type == CharSequence.class) {
				return text;
			} else if (type == char[].class) {
				return text.toCharArray();
			} else if (type == byte[].class) {
				return text.getBytes();
			} else if (type == Character.class || type == char.class) {
				return text.isEmpty() ? null : text.charAt(0);
			} else if (type instanceof Class && ((Class<?>) type).isEnum()) {
				return Enum.valueOf((Class<? extends Enum>) ((Class<?>) type).asSubclass(Enum.class), text);
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setString(index, value == null ? null : value.toString());
		}

		@Override
		public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public String getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getString(columnName);
		}
	}

	public static class VarcharType extends TextType implements ColumnType {
		private final int length;

		public VarcharType(final int length) {
			this.length = length;
		}

		@Override
		public String getTypeName() {
			return "VARCHAR";
		}

		@Override
		public boolean isVariable() {
			return true;
		}

		@Override
		public Object variableValue() {
			return this.length;
		}
	}

	public static class UUIDType implements FixedColumnType {
		@Override
		public String getTypeName() {
			return "UUID";
		}

		@Override
		public int getSQLType() {
			return Types.OTHER;
		}

		@Override
		public Object encode(final Object value) {
			if (value == null) {
				return null;
			}
			if (value instanceof UUID) {
				return value;
			}
			if (value instanceof String) {
				return UUID.fromString((String) value);
			}
			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			if (type == UUID.class && value instanceof UUID) {
				return value;
			}
			if (type == UUID.class) {
				return UUID.fromString(value.toString());
			}
			if (type == String.class) {
				return value.toString();
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setObject(index, value, Types.OTHER);
		}
	}

	public static class SmallIntType extends lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.SmallIntType {
		@Override
		public String getTypeName() {
			return "SMALLINT";
		}
	}

	public static class IntegerType extends lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.IntType {
		@Override
		public String getTypeName() {
			return "INTEGER";
		}
	}

	public static class BigIntType extends lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BigIntType {
		@Override
		public String getTypeName() {
			return "BIGINT";
		}
	}

	public static class RealType extends lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.FloatType {
		@Override
		public String getTypeName() {
			return "REAL";
		}
	}

	public static class DoublePrecisionType extends lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.DoubleType {
		@Override
		public String getTypeName() {
			return "DOUBLE PRECISION";
		}
	}

	public static class NumericType extends lu.kbra.pclib.db.autobuild.column.type.sqlite.NumericType {
		@Override
		public String getTypeName() {
			return "NUMERIC";
		}
	}

	public static class BooleanType extends lu.kbra.pclib.db.autobuild.column.type.mysql.BooleanType {
		@Override
		public String getTypeName() {
			return "BOOLEAN";
		}
	}

	public static class DateType extends lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.DateType {
		@Override
		public String getTypeName() {
			return "DATE";
		}
	}

	public static class TimestampType extends lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.TimestampType {
		@Override
		public String getTypeName() {
			return "TIMESTAMP";
		}
	}

	public static class JsonType implements FixedColumnType {
		@Override
		public String getTypeName() {
			return "JSONB";
		}

		@Override
		public int getSQLType() {
			return Types.OTHER;
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof JSONObject || value instanceof JSONArray) {
				return value.toString();
			} else if (value instanceof String) {
				final String text = (String) value;
				try {
					return new JSONObject(text).toString();
				} catch (final RuntimeException objectException) {
					return new JSONArray(text).toString();
				}
			}
			return ColumnType.unsupported(value);
		}

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final String text = value.toString();
			if (type == JSONObject.class) {
				return new JSONObject(text);
			} else if (type == JSONArray.class) {
				return new JSONArray(text);
			} else if (type == String.class) {
				return text;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setObject(index, value, Types.OTHER);
		}
	}

	public static BigInteger normalizeBigInteger(final Object value) {
		if (value instanceof BigInteger) {
			return (BigInteger) value;
		}
		if (value instanceof BigDecimal) {
			return ((BigDecimal) value).toBigInteger();
		}
		if (value instanceof Number) {
			return BigInteger.valueOf(((Number) value).longValue());
		}
		return new BigInteger(value.toString());
	}
}
