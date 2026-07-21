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

import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public final class TimeTypes {

	public static class DateType implements FixedColumnType<java.sql.Date> {

		@Override
		public Object decode(final java.sql.Date value, final Type type) {
			if (value == null) {
				return null;
			}

			final LocalDate date = value.toLocalDate();

			if (type == java.sql.Date.class) {
				return value;
			} else if (type == LocalDate.class) {
				return date;
			} else if (type == java.util.Date.class) {
				return new java.util.Date(date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli());
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public java.sql.Date encode(final Object value) {
			if (value instanceof java.sql.Date) {
				return (java.sql.Date) value;
			} else if (value instanceof LocalDate) {
				return java.sql.Date.valueOf((LocalDate) value);
			} else if (value instanceof java.util.Date) {
				final Instant instant = ((java.util.Date) value).toInstant();
				return java.sql.Date.valueOf(instant.atZone(ZoneOffset.UTC).toLocalDate());
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
		public void setObject(final PreparedStatement stmt, final int index, final java.sql.Date value) throws SQLException {
			stmt.setDate(index, value);
		}

	}

	public static class DurationType implements FixedColumnType<Long> {

		@Override
		public Object decode(final Long value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Duration.class) {
				return Duration.ofNanos(value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Long encode(final Object value) {
			if (value instanceof Duration) {
				return ((Duration) value).toNanos();
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

	public static class EpochMicrosType implements FixedColumnType<Long> {

		@Override
		public Object decode(final Long value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Instant.class) {
				return TimeTypes.instantFromEpochMicros(value);
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Long encode(final Object value) {
			if (value instanceof Instant) {
				return TimeTypes.instantToEpochMicros((Instant) value);
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

	public static class InstantType extends TimestampType {

		@Override
		public Object decode(final Timestamp value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Instant.class) {
				return value.toInstant();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Timestamp encode(final Object value) {
			if (value instanceof Instant) {
				return Timestamp.from((Instant) value);
			}

			return ColumnType.unsupported(value);
		}

	}

	public static class LocalDateTimeType extends TimestampType {

		@Override
		public Object decode(final Timestamp value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == LocalDateTime.class) {
				return value.toLocalDateTime();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Timestamp encode(final Object value) {
			if (value instanceof LocalDateTime) {
				return Timestamp.valueOf((LocalDateTime) value);
			}

			return ColumnType.unsupported(value);
		}

		@Override
		public String getTypeName() {
			return "DATETIME";
		}

	}

	public static class LocalDateType extends DateType {
	}

	public static class LocalTimeType implements FixedColumnType<Time> {

		public Object decode(Time value, Type type) {

			if (value == null) {
				return null;
			}

			if (type == Time.class) {
				return value;
			} else if (type == LocalTime.class) {
				return value.toLocalTime();
			}

			return ColumnType.unsupported(type);
		}

		public Time encode(Object value) {

			if (value instanceof Time) {
				return (Time) value;
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
		public void setObject(final PreparedStatement stmt, final int index, final Time value) throws SQLException {
			stmt.setTime(index, value);
		}

	}

	public static class MonthDayType implements FixedColumnType<Integer> {

		@Override
		public Object decode(final Integer value, final Type type) {
			if (value == null) {
				return null;
			}

			final int encoded = value;
			if (type == MonthDay.class) {
				return MonthDay.of(encoded / 100, encoded % 100);
			} else if (type == Integer.class || type == int.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Integer encode(final Object value) {
			if (value instanceof MonthDay) {
				final MonthDay monthDay = (MonthDay) value;
				return monthDay.getMonthValue() * 100 + monthDay.getDayOfMonth();
			} else if (value instanceof Integer) {
				return (Integer) value;
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
		public void setObject(final PreparedStatement stmt, final int index, final Integer value) throws SQLException {
			stmt.setInt(index, value);
		}

	}

	public static class OffsetDateTimeType extends EpochMicrosType {

		private final ZoneOffset offset;

		public OffsetDateTimeType() {
			this(ZoneOffset.UTC);
		}

		public OffsetDateTimeType(final Object object) {
			this(object instanceof ZoneOffset ? (ZoneOffset) object : ZoneOffset.of(object.toString()));
		}

		public OffsetDateTimeType(final String offset) {
			this(ZoneOffset.of(offset));
		}

		public OffsetDateTimeType(final ZoneOffset offset) {
			this.offset = offset;
		}

		@Override
		public Object decode(final Long value, final Type type) {
			if (value == null) {
				return null;
			}

			final Instant instant = TimeTypes.instantFromEpochMicros(value);
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
		public Long encode(final Object value) {
			if (value instanceof OffsetDateTime) {
				return TimeTypes.instantToEpochMicros(((OffsetDateTime) value).toInstant());
			}

			return super.encode(value);
		}

	}

	public static class PeriodType implements FixedColumnType<Long> {

		@Override
		public Object decode(final Long value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Period.class) {
				return TimeTypes.unpackPeriod(value);
			} else if (type == Long.class || type == long.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Long encode(final Object value) {
			if (value instanceof Period) {
				return TimeTypes.packPeriod((Period) value);
			} else if (value instanceof Long) {
				return (Long) value;
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

	public static class TimestampType implements FixedColumnType<Timestamp> {

		@Override
		public Object decode(final Timestamp value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Timestamp.class) {
				return value;
			} else if (type == LocalDateTime.class) {
				return value.toLocalDateTime();
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Timestamp encode(final Object value) {
			if (value instanceof Timestamp) {
				return (Timestamp) value;
			} else if (value instanceof LocalDateTime) {
				return Timestamp.valueOf((LocalDateTime) value);
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
			return "TIMESTAMP";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Timestamp value) throws SQLException {
			stmt.setTimestamp(index, value);
		}

	}

	public static class YearMonthType implements FixedColumnType<Integer> {

		@Override
		public Object decode(final Integer value, final Type type) {
			if (value == null) {
				return null;
			}

			final int encoded = value;
			if (type == YearMonth.class) {
				return YearMonth.of(Math.floorDiv(encoded, 100), Math.floorMod(encoded, 100));
			} else if (type == Integer.class || type == int.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Integer encode(final Object value) {
			if (value instanceof YearMonth) {
				final YearMonth yearMonth = (YearMonth) value;
				return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
			} else if (value instanceof Integer) {
				return (Integer) value;
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

	public static class YearType implements FixedColumnType<Integer> {

		@Override
		public Object decode(final Integer value, final Type type) {
			if (value == null) {
				return null;
			}

			if (type == Year.class) {
				return Year.of(value);
			} else if (type == Integer.class || type == int.class) {
				return value;
			}

			return ColumnType.unsupported(type);
		}

		@Override
		public Integer encode(final Object value) {
			if (value instanceof Year) {
				return ((Year) value).getValue();
			} else if (value instanceof Integer) {
				return (Integer) value;
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
		public void setObject(final PreparedStatement stmt, final int index, final Integer value) throws SQLException {
			stmt.setInt(index, value);
		}

	}

	public static class ZonedDateTimeType extends EpochMicrosType {

		private final ZoneId zoneId;

		public ZonedDateTimeType() {
			this(ZoneOffset.UTC);
		}

		public ZonedDateTimeType(final Object object) {
			this(object instanceof ZoneId ? (ZoneId) object : ZoneId.of(object.toString()));
		}

		public ZonedDateTimeType(final String zoneId) {
			this(ZoneId.of(zoneId));
		}

		public ZonedDateTimeType(final ZoneId zoneId) {
			this.zoneId = zoneId;
		}

		@Override
		public Object decode(final Long value, final Type type) {
			if (value == null) {
				return null;
			}

			final Instant instant = TimeTypes.instantFromEpochMicros(value);
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
		public Long encode(final Object value) {
			if (value instanceof ZonedDateTime) {
				return TimeTypes.instantToEpochMicros(((ZonedDateTime) value).toInstant());
			}

			return super.encode(value);
		}

	}

	private static final long MICROS_PER_SECOND = 1_000_000L;
	private static final long NANOS_PER_MICRO = 1_000L;
	private static final int PERIOD_PART_BITS = 21;
	private static final long PERIOD_PART_MASK = (1L << TimeTypes.PERIOD_PART_BITS) - 1L;
	private static final int PERIOD_PART_MIN = -(1 << TimeTypes.PERIOD_PART_BITS - 1);
	private static final int PERIOD_PART_MAX = (1 << TimeTypes.PERIOD_PART_BITS - 1) - 1;

	private static Instant instantFromEpochMicros(final long epochMicros) {
		final long seconds = Math.floorDiv(epochMicros, TimeTypes.MICROS_PER_SECOND);
		final long micros = Math.floorMod(epochMicros, TimeTypes.MICROS_PER_SECOND);
		return Instant.ofEpochSecond(seconds, micros * TimeTypes.NANOS_PER_MICRO);
	}

	private static long instantToEpochMicros(final Instant instant) {
		return Math.addExact(Math.multiplyExact(instant.getEpochSecond(), TimeTypes.MICROS_PER_SECOND),
				instant.getNano() / TimeTypes.NANOS_PER_MICRO);
	}

	private static long packPeriod(final Period period) {
		final long years = TimeTypes.packPeriodPart(period.getYears()) & TimeTypes.PERIOD_PART_MASK;
		final long months = TimeTypes.packPeriodPart(period.getMonths()) & TimeTypes.PERIOD_PART_MASK;
		final long days = TimeTypes.packPeriodPart(period.getDays()) & TimeTypes.PERIOD_PART_MASK;
		return years << TimeTypes.PERIOD_PART_BITS * 2 | months << TimeTypes.PERIOD_PART_BITS | days;
	}

	private static int packPeriodPart(final int value) {
		if (value < TimeTypes.PERIOD_PART_MIN || value > TimeTypes.PERIOD_PART_MAX) {
			throw new IllegalArgumentException("Period part out of supported range: " + value);
		}
		return value;
	}

	private static Period unpackPeriod(final long value) {
		return Period.of(TimeTypes.unpackPeriodPart(value, TimeTypes.PERIOD_PART_BITS * 2),
				TimeTypes.unpackPeriodPart(value, TimeTypes.PERIOD_PART_BITS),
				TimeTypes.unpackPeriodPart(value, 0));
	}

	private static int unpackPeriodPart(final long value, final int shift) {
		final long part = value >> shift & TimeTypes.PERIOD_PART_MASK;
		final long signBit = 1L << TimeTypes.PERIOD_PART_BITS - 1;
		return (int) ((part & signBit) == 0 ? part : part | ~TimeTypes.PERIOD_PART_MASK);
	}

}
