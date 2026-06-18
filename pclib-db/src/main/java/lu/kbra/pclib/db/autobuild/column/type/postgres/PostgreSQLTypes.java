package lu.kbra.pclib.db.autobuild.column.type.postgres;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.time.YearMonth;
import java.time.Year;
import java.time.Period;
import java.time.OffsetDateTime;
import java.time.MonthDay;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.Instant;
import java.time.Duration;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType.FixedColumnType;

public final class PostgreSQLTypes {

	public static class BigIntType extends lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BigIntType {
		@Override
		public String getTypeName() {
			return "BIGINT";
		}
	}

	public static class BooleanType extends lu.kbra.pclib.db.autobuild.column.type.mysql.BooleanType {
		@Override
		public String getTypeName() {
			return "BOOLEAN";
		}
	}

	public static class ByteAType implements FixedColumnType {

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
		public Object encode(final Object value) {
			if (value instanceof byte[]) {
				return value;
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
			return "BYTEA";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setBytes(index, (byte[]) value);
		}
	}

	public static class DateType extends lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.DateType {
		@Override
		public String getTypeName() {
			return "DATE";
		}
	}

	public static class DoublePrecisionType extends lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.DoubleType {
		@Override
		public String getTypeName() {
			return "DOUBLE PRECISION";
		}
	}

	public static class IntegerType extends lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.IntType {
		@Override
		public String getTypeName() {
			return "INTEGER";
		}
	}

	public static class JsonType implements FixedColumnType {
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
		public int getSQLType() {
			return Types.OTHER;
		}

		@Override
		public String getTypeName() {
			return "JSONB";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setObject(index, value, Types.OTHER);
		}
	}

	public static class NumericType extends lu.kbra.pclib.db.autobuild.column.type.sqlite.NumericType {
		@Override
		public String getTypeName() {
			return "NUMERIC";
		}
	}

	public static class RealType extends lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.FloatType {
		@Override
		public String getTypeName() {
			return "REAL";
		}
	}

	public static class SmallIntType extends lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.SmallIntType {
		@Override
		public String getTypeName() {
			return "SMALLINT";
		}
	}

	public static class TextType implements FixedColumnType {

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
		public String getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}

		@Override
		public String getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getString(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.VARCHAR;
		}

		@Override
		public String getTypeName() {
			return "TEXT";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setString(index, value == null ? null : value.toString());
		}
	}

	public static class TimestampType extends lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.TimestampType {
		@Override
		public String getTypeName() {
			return "TIMESTAMP";
		}
	}


	public static class LocalDateType extends DateType {
	}

	public static class LocalTimeType extends lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.LocalTimeType {
		@Override
		public String getTypeName() {
			return "TIME";
		}
	}

	public static class LocalDateTimeType extends TimestampType {
	}

	public static class TimestampWithTimeZoneType implements FixedColumnType {
		private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.UTC;

		private static OffsetDateTime normalize(final Object value) {
			if (value instanceof OffsetDateTime) {
				return (OffsetDateTime) value;
			} else if (value instanceof ZonedDateTime) {
				return ((ZonedDateTime) value).toOffsetDateTime();
			} else if (value instanceof Instant) {
				return ((Instant) value).atOffset(DEFAULT_OFFSET);
			}
			return OffsetDateTime.parse(value.toString());
		}

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final OffsetDateTime dateTime = normalize(value);
			if (type == OffsetDateTime.class) {
				return dateTime;
			} else if (type == ZonedDateTime.class) {
				return dateTime.toZonedDateTime();
			} else if (type == Instant.class) {
				return dateTime.toInstant();
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof OffsetDateTime || value instanceof ZonedDateTime || value instanceof Instant) {
				return normalize(value);
			}
			return ColumnType.unsupported(value);
		}

		@Override
		public OffsetDateTime getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getObject(columnIndex, OffsetDateTime.class);
		}

		@Override
		public OffsetDateTime getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getObject(columnName, OffsetDateTime.class);
		}

		@Override
		public int getSQLType() {
			return Types.TIMESTAMP_WITH_TIMEZONE;
		}

		@Override
		public String getTypeName() {
			return "TIMESTAMP WITH TIME ZONE";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setObject(index, value, Types.TIMESTAMP_WITH_TIMEZONE);
		}
	}

	public static class InstantType extends TimestampWithTimeZoneType {
	}

	public static class ZonedDateTimeType extends TimestampWithTimeZoneType {
		private final ZoneId zoneId;

		public ZonedDateTimeType() {
			this(ZoneOffset.UTC);
		}

		public ZonedDateTimeType(final ZoneId zoneId) {
			this.zoneId = zoneId;
		}

		public ZonedDateTimeType(final String zoneId) {
			this(ZoneId.of(zoneId));
		}

		public ZonedDateTimeType(final Object object) {
			this(object instanceof ZoneId ? (ZoneId) object : ZoneId.of(object.toString()));
		}

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final Instant instant = ((OffsetDateTime) super.decode(value, OffsetDateTime.class)).toInstant();
			if (type == ZonedDateTime.class) {
				return instant.atZone(this.zoneId);
			} else if (type == Instant.class) {
				return instant;
			} else if (type == OffsetDateTime.class) {
				return instant.atOffset(ZoneOffset.UTC);
			}
			return ColumnType.unsupported(type);
		}
	}

	public static class OffsetDateTimeType extends TimestampWithTimeZoneType {
		private final ZoneOffset offset;

		public OffsetDateTimeType() {
			this(ZoneOffset.UTC);
		}

		public OffsetDateTimeType(final ZoneOffset offset) {
			this.offset = offset;
		}

		public OffsetDateTimeType(final String offset) {
			this(ZoneOffset.of(offset));
		}

		public OffsetDateTimeType(final Object object) {
			this(object instanceof ZoneOffset ? (ZoneOffset) object : ZoneOffset.of(object.toString()));
		}

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final Instant instant = ((OffsetDateTime) super.decode(value, OffsetDateTime.class)).toInstant();
			if (type == OffsetDateTime.class) {
				return instant.atOffset(this.offset);
			} else if (type == Instant.class) {
				return instant;
			} else if (type == ZonedDateTime.class) {
				return instant.atZone(this.offset);
			}
			return ColumnType.unsupported(type);
		}
	}

	public static class DurationType extends lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.DurationType {
		@Override
		public String getTypeName() {
			return "BIGINT";
		}
	}

	public static class PeriodType extends lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.PeriodType {
		@Override
		public String getTypeName() {
			return "BIGINT";
		}
	}

	public static class YearType extends lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.YearType {
		@Override
		public String getTypeName() {
			return "INTEGER";
		}
	}

	public static class YearMonthType extends lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.YearMonthType {
		@Override
		public String getTypeName() {
			return "INTEGER";
		}
	}

	public static class MonthDayType extends lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.MonthDayType {
		@Override
		public String getTypeName() {
			return "SMALLINT";
		}
	}

	public static class UUIDType implements FixedColumnType {

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
		public int getSQLType() {
			return Types.OTHER;
		}

		@Override
		public String getTypeName() {
			return "UUID";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setObject(index, value, Types.OTHER);
		}
	}

	public static class VarcharType extends TextType implements ColumnType {

		private final int length;

		public VarcharType(final int length) {
			this.length = length;
		}

		public VarcharType(final Object object) {
			this.length = ColumnType.asInt(object);
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

	private PostgreSQLTypes() {
	}
}
