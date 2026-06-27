package lu.kbra.pclib.db.autobuild.postgres;

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
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.FixedColumnType;

public final class TimeTypes {

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

	public static class DurationType implements FixedColumnType {
		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final long nanos = ((Number) value).longValue();
			if (type == Duration.class) {
				return Duration.ofNanos(nanos);
			} else if (type == Long.class || type == long.class) {
				return nanos;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Duration) {
				return ((Duration) value).toNanos();
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

	public static class InstantType extends TimestampWithTimeZoneType {
	}

	public static class LocalDateTimeType extends TimestampType {
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

	public static class MonthDayType implements FixedColumnType {
		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final int packed = ((Number) value).intValue();
			if (type == MonthDay.class) {
				return MonthDay.of(packed / 100, packed % 100);
			} else if (type == Integer.class || type == int.class) {
				return packed;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof MonthDay) {
				final MonthDay monthDay = (MonthDay) value;
				return (short) (monthDay.getMonthValue() * 100 + monthDay.getDayOfMonth());
			} else if (value instanceof Number) {
				return ((Number) value).shortValue();
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

	public static class OffsetDateTimeType extends TimestampWithTimeZoneType {
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

	public static class PeriodType implements FixedColumnType {
		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final long packed = ((Number) value).longValue();
			if (type == Period.class) {
				return TimeTypes.unpackPeriod(packed);
			} else if (type == Long.class || type == long.class) {
				return packed;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Period) {
				return TimeTypes.packPeriod((Period) value);
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

	public static class TimestampType implements FixedColumnType {
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
			return "TIMESTAMP";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setTimestamp(index, (Timestamp) value);
		}
	}

	public static class TimestampWithTimeZoneType implements FixedColumnType {
		private static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.UTC;

		private static OffsetDateTime normalize(final Object value) {
			if (value instanceof OffsetDateTime) {
				return (OffsetDateTime) value;
			} else if (value instanceof ZonedDateTime) {
				return ((ZonedDateTime) value).toOffsetDateTime();
			} else if (value instanceof Instant) {
				return ((Instant) value).atOffset(TimestampWithTimeZoneType.DEFAULT_OFFSET);
			}
			return OffsetDateTime.parse(value.toString());
		}

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final OffsetDateTime dateTime = TimestampWithTimeZoneType.normalize(value);
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
				return TimestampWithTimeZoneType.normalize(value);
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

	public static class YearMonthType implements FixedColumnType {
		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final int packed = ((Number) value).intValue();
			if (type == YearMonth.class) {
				return YearMonth.of(packed / 100, packed % 100);
			} else if (type == Integer.class || type == int.class) {
				return packed;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof YearMonth) {
				final YearMonth yearMonth = (YearMonth) value;
				return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
			} else if (value instanceof Number) {
				return ((Number) value).intValue();
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

	public static class YearType implements FixedColumnType {
		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final int year = ((Number) value).intValue();
			if (type == Year.class) {
				return Year.of(year);
			} else if (type == Integer.class || type == int.class) {
				return year;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Year) {
				return ((Year) value).getValue();
			} else if (value instanceof Number) {
				return ((Number) value).intValue();
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

	public static class ZonedDateTimeType extends TimestampWithTimeZoneType {
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

	private static final int PERIOD_PART_BITS = 21;

	private static final long PERIOD_PART_MASK = (1L << TimeTypes.PERIOD_PART_BITS) - 1L;

	private static final int PERIOD_PART_MIN = -(1 << TimeTypes.PERIOD_PART_BITS - 1);

	private static final int PERIOD_PART_MAX = (1 << TimeTypes.PERIOD_PART_BITS - 1) - 1;

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

	private TimeTypes() {
	}
}
