package lu.kbra.pclib.db.autobuild.sqlite;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

	public static class DurationType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final long nanos = TimeTypes.asLong(value);
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
				final Duration duration = (Duration) value;
				return Math.addExact(Math.multiplyExact(duration.getSeconds(), TimeTypes.NANOS_PER_SECOND), duration.getNano());
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
			return Types.INTEGER;
		}

		@Override
		public String getTypeName() {
			return "INTEGER";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setLong(index, TimeTypes.asLong(value));
		}
	}

	public static class InstantType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final long epochMicros = TimeTypes.asLong(value);
			if (type == Instant.class) {
				return TimeTypes.instantFromEpochMicros(epochMicros);
			} else if (type == Long.class || type == long.class) {
				return epochMicros;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof Instant) {
				return TimeTypes.instantToEpochMicros((Instant) value);
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
			return Types.INTEGER;
		}

		@Override
		public String getTypeName() {
			return "INTEGER";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setLong(index, TimeTypes.asLong(value));
		}
	}

	public static class LocalDateTimeType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final long epochMicros = TimeTypes.asLong(value);
			if (type == LocalDateTime.class) {
				return TimeTypes.localDateTimeFromEpochMicros(epochMicros);
			} else if (type == Long.class || type == long.class) {
				return epochMicros;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof LocalDateTime) {
				return TimeTypes.localDateTimeToEpochMicros((LocalDateTime) value);
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
			return Types.INTEGER;
		}

		@Override
		public String getTypeName() {
			return "INTEGER";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setLong(index, TimeTypes.asLong(value));
		}
	}

	public static class LocalDateType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final LocalDate date = LocalDate.ofEpochDay(TimeTypes.asLong(value));
			if (type == LocalDate.class) {
				return date;
			} else if (type == Long.class || type == long.class) {
				return TimeTypes.asLong(value);
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof LocalDate) {
				return ((LocalDate) value).toEpochDay();
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
			return Types.INTEGER;
		}

		@Override
		public String getTypeName() {
			return "INTEGER";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setLong(index, TimeTypes.asLong(value));
		}
	}

	public static class LocalTimeType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final long nanos = TimeTypes.asLong(value);
			if (type == LocalTime.class) {
				return LocalTime.ofNanoOfDay(nanos);
			} else if (type == Long.class || type == long.class) {
				return nanos;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof LocalTime) {
				return ((LocalTime) value).toNanoOfDay();
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
			return Types.INTEGER;
		}

		@Override
		public String getTypeName() {
			return "INTEGER";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setLong(index, TimeTypes.asLong(value));
		}
	}

	public static class MonthDayType extends YearType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final int encoded = TimeTypes.asInt(value);
			if (type == MonthDay.class) {
				return MonthDay.of(Math.floorDiv(encoded, 100), Math.floorMod(encoded, 100));
			} else if (type == Integer.class || type == int.class) {
				return encoded;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof MonthDay) {
				final MonthDay monthDay = (MonthDay) value;
				return monthDay.getMonthValue() * 100 + monthDay.getDayOfMonth();
			}
			return super.encode(value);
		}
	}

	public static class OffsetDateTimeType extends InstantType {
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
			final long epochMicros = TimeTypes.asLong(value);
			final Instant instant = TimeTypes.instantFromEpochMicros(epochMicros);
			if (type == OffsetDateTime.class) {
				return instant.atOffset(this.offset);
			} else if (type == Instant.class) {
				return instant;
			} else if (type == Long.class || type == long.class) {
				return epochMicros;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof OffsetDateTime) {
				return TimeTypes.instantToEpochMicros(((OffsetDateTime) value).toInstant());
			}
			return super.encode(value);
		}
	}

	public static class PeriodType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final long encoded = TimeTypes.asLong(value);
			if (type == Period.class) {
				return TimeTypes.unpackPeriod(encoded);
			} else if (type == Long.class || type == long.class) {
				return encoded;
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
			return Types.INTEGER;
		}

		@Override
		public String getTypeName() {
			return "INTEGER";
		}

		@Override
		public void setObject(final PreparedStatement stmt, final int index, final Object value) throws SQLException {
			stmt.setLong(index, TimeTypes.asLong(value));
		}
	}

	public static class YearMonthType extends YearType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final int encoded = TimeTypes.asInt(value);
			if (type == YearMonth.class) {
				return YearMonth.of(Math.floorDiv(encoded, 100), Math.floorMod(encoded, 100));
			} else if (type == Integer.class || type == int.class) {
				return encoded;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof YearMonth) {
				final YearMonth yearMonth = (YearMonth) value;
				return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
			}
			return super.encode(value);
		}
	}

	public static class YearType implements FixedColumnType {

		@Override
		public Object decode(final Object value, final Type type) {
			if (value == null) {
				return null;
			}
			final int year = TimeTypes.asInt(value);
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
			stmt.setInt(index, TimeTypes.asInt(value));
		}
	}

	public static class ZonedDateTimeType extends InstantType {
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
			final long epochMicros = TimeTypes.asLong(value);
			final Instant instant = TimeTypes.instantFromEpochMicros(epochMicros);
			if (type == ZonedDateTime.class) {
				return instant.atZone(this.zoneId);
			} else if (type == Instant.class) {
				return instant;
			} else if (type == Long.class || type == long.class) {
				return epochMicros;
			}
			return ColumnType.unsupported(type);
		}

		@Override
		public Object encode(final Object value) {
			if (value instanceof ZonedDateTime) {
				return TimeTypes.instantToEpochMicros(((ZonedDateTime) value).toInstant());
			}
			return super.encode(value);
		}
	}

	private static final long MICROS_PER_SECOND = 1_000_000L;

	private static final long NANOS_PER_MICRO = 1_000L;

	private static final long NANOS_PER_SECOND = 1_000_000_000L;

	private static final int PERIOD_PART_BITS = 21;

	private static final long PERIOD_PART_MASK = (1L << TimeTypes.PERIOD_PART_BITS) - 1L;

	private static final int PERIOD_PART_MIN = -(1 << TimeTypes.PERIOD_PART_BITS - 1);

	private static final int PERIOD_PART_MAX = (1 << TimeTypes.PERIOD_PART_BITS - 1) - 1;

	private static int asInt(final Object value) {
		return value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(value.toString());
	}

	private static long asLong(final Object value) {
		return value instanceof Number ? ((Number) value).longValue() : Long.parseLong(value.toString());
	}

	private static Instant instantFromEpochMicros(final long epochMicros) {
		final long seconds = Math.floorDiv(epochMicros, TimeTypes.MICROS_PER_SECOND);
		final long micros = Math.floorMod(epochMicros, TimeTypes.MICROS_PER_SECOND);
		return Instant.ofEpochSecond(seconds, micros * TimeTypes.NANOS_PER_MICRO);
	}

	private static long instantToEpochMicros(final Instant instant) {
		return Math.addExact(Math.multiplyExact(instant.getEpochSecond(), TimeTypes.MICROS_PER_SECOND),
				instant.getNano() / TimeTypes.NANOS_PER_MICRO);
	}

	private static LocalDateTime localDateTimeFromEpochMicros(final long epochMicros) {
		final long seconds = Math.floorDiv(epochMicros, TimeTypes.MICROS_PER_SECOND);
		final long micros = Math.floorMod(epochMicros, TimeTypes.MICROS_PER_SECOND);
		return LocalDateTime.ofEpochSecond(seconds, (int) (micros * TimeTypes.NANOS_PER_MICRO), ZoneOffset.UTC);
	}

	private static long localDateTimeToEpochMicros(final LocalDateTime localDateTime) {
		return Math.addExact(Math.multiplyExact(localDateTime.toEpochSecond(ZoneOffset.UTC), TimeTypes.MICROS_PER_SECOND),
				localDateTime.getNano() / TimeTypes.NANOS_PER_MICRO);
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

	private TimeTypes() {
	}
}
