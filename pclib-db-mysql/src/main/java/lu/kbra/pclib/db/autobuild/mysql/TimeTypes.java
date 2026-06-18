package lu.kbra.pclib.db.autobuild.mysql;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.ColumnType.FixedColumnType;

public final class TimeTypes {

	private static final long MICROS_PER_SECOND = 1_000_000L;
	private static final long NANOS_PER_MICRO = 1_000L;
	private static final int PERIOD_PART_BITS = 21;
	private static final long PERIOD_PART_MASK = (1L << PERIOD_PART_BITS) - 1L;
	private static final int PERIOD_PART_MIN = -(1 << (PERIOD_PART_BITS - 1));
	private static final int PERIOD_PART_MAX = (1 << (PERIOD_PART_BITS - 1)) - 1;

	private static Instant instantFromEpochMicros(final long epochMicros) {
		final long seconds = Math.floorDiv(epochMicros, MICROS_PER_SECOND);
		final long micros = Math.floorMod(epochMicros, MICROS_PER_SECOND);
		return Instant.ofEpochSecond(seconds, micros * NANOS_PER_MICRO);
	}

	private static long instantToEpochMicros(final Instant instant) {
		return Math.addExact(Math.multiplyExact(instant.getEpochSecond(), MICROS_PER_SECOND), instant.getNano() / NANOS_PER_MICRO);
	}

	private static int packPeriodPart(final int value) {
		if (value < PERIOD_PART_MIN || value > PERIOD_PART_MAX) {
			throw new IllegalArgumentException("Period part out of supported range: " + value);
		}
		return value;
	}

	private static long packPeriod(final Period period) {
		final long years = packPeriodPart(period.getYears()) & PERIOD_PART_MASK;
		final long months = packPeriodPart(period.getMonths()) & PERIOD_PART_MASK;
		final long days = packPeriodPart(period.getDays()) & PERIOD_PART_MASK;
		return years << (PERIOD_PART_BITS * 2) | months << PERIOD_PART_BITS | days;
	}

	private static int unpackPeriodPart(final long value, final int shift) {
		final long part = value >> shift & PERIOD_PART_MASK;
		final long signBit = 1L << (PERIOD_PART_BITS - 1);
		return (int) ((part & signBit) == 0 ? part : part | ~PERIOD_PART_MASK);
	}

	private static Period unpackPeriod(final long value) {
		return Period.of(unpackPeriodPart(value, PERIOD_PART_BITS * 2), unpackPeriodPart(value, PERIOD_PART_BITS),
				unpackPeriodPart(value, 0));
	}

	public static class DateType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Date.class) {
				return value;
			} else if (type == Timestamp.class) {
				return PCUtils.toTimestamp((Date) value);
			} else if (type == LocalDate.class) {
				return ((Date) value).toLocalDate();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Date) {
				return value;
			} else if (value instanceof LocalDate) {
				return Date.valueOf((LocalDate) value);
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Date getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getDate(columnIndex);
		}

		@Override
		public Date getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getDate(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.DATE;
		}

		@Override
		public String getTypeName() {
			return "DATE";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setDate(index, (Date) value);
		}

	}

	public static class LocalDateType extends DateType {
	}

	public static class LocalTimeType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Time.class) {
				return value;
			} else if (type == LocalTime.class) {
				return ((Time) value).toLocalTime();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Time) {
				return value;
			} else if (value instanceof LocalTime) {
				return Time.valueOf((LocalTime) value);
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Time getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getTime(columnIndex);
		}

		@Override
		public Time getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getTime(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.TIME;
		}

		@Override
		public String getTypeName() {
			return "TIME";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setTime(index, (Time) value);
		}

	}

	public static class LocalDateTimeType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Timestamp.class) {
				return value;
			} else if (type == LocalDateTime.class) {
				return ((Timestamp) value).toLocalDateTime();
			} else if (type == Instant.class) {
				return ((Timestamp) value).toInstant();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Timestamp) {
				return value;
			} else if (value instanceof LocalDateTime) {
				return Timestamp.valueOf((LocalDateTime) value);
			} else if (value instanceof Instant) {
				return Timestamp.from((Instant) value);
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public Timestamp getObject(final ResultSet rs, final int columnIndex) throws SQLException {
			return rs.getTimestamp(columnIndex);
		}

		@Override
		public Timestamp getObject(final ResultSet rs, final String columnName) throws SQLException {
			return rs.getTimestamp(columnName);
		}

		@Override
		public int getSQLType() {
			return Types.TIMESTAMP;
		}

		@Override
		public String getTypeName() {
			return "DATETIME";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setTimestamp(index, (Timestamp) value);
		}

	}

	public static class TimestampType extends LocalDateTimeType {

		@Override
		public String getTypeName() {
			return "TIMESTAMP";
		}

	}

	public static class InstantType extends TimestampType {
	}

	public static class EpochMicrosType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}

			final Instant instant = instantFromEpochMicros((long) value);
			if (type == Instant.class) {
				return instant;
			} else if (type == Long.class || type == long.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Long) {
				return value;
			} else if (value instanceof Instant) {
				return instantToEpochMicros((Instant) value);
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
			stmt.setLong(index, (long) value);
		}

	}

	public static class ZonedDateTimeType extends EpochMicrosType {

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

			final Instant instant = instantFromEpochMicros((long) value);
			if (type == ZonedDateTime.class) {
				return instant.atZone(this.zoneId);
			} else if (type == Instant.class) {
				return instant;
			} else if (type == Long.class || type == long.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof ZonedDateTime) {
				return instantToEpochMicros(((ZonedDateTime) value).toInstant());
			}

			return super.encode(value);
		}

	}

	public static class OffsetDateTimeType extends EpochMicrosType {

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

			final Instant instant = instantFromEpochMicros((long) value);
			if (type == OffsetDateTime.class) {
				return instant.atOffset(this.offset);
			} else if (type == Instant.class) {
				return instant;
			} else if (type == Long.class || type == long.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof OffsetDateTime) {
				return instantToEpochMicros(((OffsetDateTime) value).toInstant());
			}

			return super.encode(value);
		}

	}

	public static class DurationType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Duration.class) {
				return Duration.ofNanos((long) value);
			} else if (type == Long.class || type == long.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Duration) {
				return ((Duration) value).toNanos();
			} else if (value instanceof Long) {
				return value;
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
			stmt.setLong(index, (long) value);
		}

	}

	public static class PeriodType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Period.class) {
				return unpackPeriod((long) value);
			} else if (type == Long.class || type == long.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Period) {
				return packPeriod((Period) value);
			} else if (value instanceof Long) {
				return value;
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
			stmt.setLong(index, (long) value);
		}

	}

	public static class YearType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Year.class) {
				return Year.of((int) value);
			} else if (type == Integer.class || type == int.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Year) {
				return ((Year) value).getValue();
			} else if (value instanceof Integer) {
				return value;
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
			return "YEAR";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setInt(index, (int) value);
		}

	}

	public static class YearMonthType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}

			final int encoded = (int) value;
			if (type == YearMonth.class) {
				return YearMonth.of(Math.floorDiv(encoded, 100), Math.floorMod(encoded, 100));
			} else if (type == Integer.class || type == int.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof YearMonth) {
				final YearMonth yearMonth = (YearMonth) value;
				return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
			} else if (value instanceof Integer) {
				return value;
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
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setInt(index, (int) value);
		}

	}

	public static class MonthDayType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}

			final int encoded = (int) value;
			if (type == MonthDay.class) {
				return MonthDay.of(encoded / 100, encoded % 100);
			} else if (type == Integer.class || type == int.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof MonthDay) {
				final MonthDay monthDay = (MonthDay) value;
				return monthDay.getMonthValue() * 100 + monthDay.getDayOfMonth();
			} else if (value instanceof Integer) {
				return value;
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
			return "SMALLINT";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setInt(index, (int) value);
		}

	}

}
