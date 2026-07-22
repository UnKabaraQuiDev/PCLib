package lu.kbra.pclib.db.utils.registry;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.sqlite.column.binary.ByteArrayColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.binary.ByteBufferColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.decimal.DoubleColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.decimal.FloatColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.decimal.NumberColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.integer.BigIntegerColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.integer.ByteColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.integer.IntegerColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.integer.LongColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.integer.ShortColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.misc.BooleanColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.misc.EnumOrdinalColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.misc.EnumStringColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.DurationColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.InstantColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.LocalDateColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.LocalDateTimeColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.LocalTimeColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.MonthDayPackedColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.MonthDayStringColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.OffsetDateTimeColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.OffsetTimeColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.PeriodColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.SqlDateColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.SqlTimeColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.TimestampColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.UtilDateColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.UtilDateTimeColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.YearMonthPackedColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.YearMonthStringColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.temporal.ZonedDateTimeColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.text.CharArrayColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.text.CharColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.text.JsonArrayColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.text.JsonObjectColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.text.StringColumnType;
import lu.kbra.pclib.db.autobuild.sqlite.column.text.UUIDColumnType;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;

public class SQLiteColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public void registerColumnTypes(final List<ColumnTypeFactory<?>> typeMap) {
		// ENUM
		typeMap.add(new DelegatingColumnTypeFactory<>(EnumStringColumnType.class,
				(clazz, map, etp) -> clazz.isEnum() && map.hasHint(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new EnumStringColumnType(map.getHint(DefaultTypeHints.MAX_LENGTH), true)));
		typeMap.add(new DelegatingColumnTypeFactory<>(EnumStringColumnType.class,
				(clazz, map, etp) -> clazz.isEnum() && map.hasHint(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new EnumStringColumnType(map.getHint(DefaultTypeHints.FIXED_LENGTH), false)));
		typeMap.add(new DelegatingColumnTypeFactory<>(EnumStringColumnType.class,
				(clazz, map, etp) -> clazz.isEnum() ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new EnumStringColumnType()));

		// STRING
		ColumnTypeRegistry.registerType(StringColumnType.class,
				(clazz, map, etp) -> (clazz == String.class || clazz == CharSequence.class) && map.hasHint(DefaultTypeHints.MAX_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new StringColumnType(map.getIntHint(DefaultTypeHints.MAX_LENGTH), true),
				typeMap);
		ColumnTypeRegistry.registerType(StringColumnType.class,
				(clazz, map, etp) -> (clazz == String.class || clazz == CharSequence.class) && map.hasHint(DefaultTypeHints.FIXED_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new StringColumnType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH), false),
				typeMap);
		ColumnTypeRegistry.registerType(StringColumnType.class,
				(clazz, map, etp) -> clazz == String.class || clazz == CharSequence.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new StringColumnType(),
				typeMap);

		// CHAR ARRAY
		ColumnTypeRegistry.registerType(CharArrayColumnType.class,
				(clazz, map, etp) -> clazz == char[].class && map.hasHint(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new CharArrayColumnType(map.getIntHint(DefaultTypeHints.MAX_LENGTH), true),
				typeMap);
		ColumnTypeRegistry.registerType(CharArrayColumnType.class,
				(clazz, map, etp) -> clazz == char[].class && map.hasHint(DefaultTypeHints.FIXED_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new CharArrayColumnType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH), false),
				typeMap);
		ColumnTypeRegistry.registerType(CharArrayColumnType.class,
				(clazz, map, etp) -> clazz == char[].class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new CharArrayColumnType(),
				typeMap);

		// CHAR
		ColumnTypeRegistry.registerType(CharColumnType.class,
				(clazz, map, etp) -> clazz == Character.class || clazz == char.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new CharColumnType(),
				typeMap);

		// UUID
		ColumnTypeRegistry.registerType(UUIDColumnType.class,
				(clazz, map, etp) -> clazz == UUID.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new UUIDColumnType(),
				typeMap);

		// BYTE ARRAY
		ColumnTypeRegistry.registerType(ByteArrayColumnType.class,
				(clazz, map, etp) -> clazz == byte[].class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ByteArrayColumnType(),
				typeMap);

		// BYTE BUFFER
		ColumnTypeRegistry.registerType(ByteBufferColumnType.class,
				(clazz, map, etp) -> clazz == ByteBuffer.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ByteBufferColumnType(),
				typeMap);

		// NUMBERS
		// BYTE
		ColumnTypeRegistry.registerType(ByteColumnType.class,
				(clazz, map, etp) -> (clazz == Byte.class || clazz == byte.class) ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ByteColumnType(),
				typeMap);

		// SHORT
		ColumnTypeRegistry.registerType(ShortColumnType.class,
				(clazz, map, etp) -> (clazz == Short.class || clazz == short.class) ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ShortColumnType(),
				typeMap);

		// INTEGER
		ColumnTypeRegistry.registerType(IntegerColumnType.class,
				(clazz, map, etp) -> (clazz == Integer.class || clazz == int.class) ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new IntegerColumnType(),
				typeMap);

		// LONG
		ColumnTypeRegistry.registerType(LongColumnType.class,
				(clazz, map, etp) -> (clazz == Long.class || clazz == long.class) ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new LongColumnType(),
				typeMap);

		// BIG INTEGER
		ColumnTypeRegistry.registerType(BigIntegerColumnType.class,
				(clazz, map, etp) -> clazz == BigInteger.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new BigIntegerColumnType(),
				typeMap);

		// NUMBER with precision & scale
		ColumnTypeRegistry.registerType(NumberColumnType.class,
				(clazz, map, etp) -> PCUtils.isNumber(clazz)
						&& (map.hasHint(DefaultTypeHints.PRECISION) || map.hasHint(DefaultTypeHints.SCALE))
								? ColumnTypeRegistry.MAP_MATCH_SCORE
								: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new NumberColumnType(map.getIntHint(DefaultTypeHints.PRECISION, 10),
						map.getIntHint(DefaultTypeHints.SCALE, 0)),
				typeMap);

		// DOUBLE
		ColumnTypeRegistry.registerType(DoubleColumnType.class,
				(clazz, map, etp) -> clazz == Double.class || clazz == double.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new DoubleColumnType(),
				typeMap);

		// FLOAT
		ColumnTypeRegistry.registerType(FloatColumnType.class,
				(clazz, map, etp) -> clazz == Float.class || clazz == float.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new FloatColumnType(),
				typeMap);

		// BOOLEAN
		ColumnTypeRegistry.registerType(BooleanColumnType.class,
				(clazz, map, etp) -> clazz == Boolean.class || clazz == boolean.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new BooleanColumnType(),
				typeMap);

		// TEMPORAL
		ColumnTypeRegistry.registerType(TimestampColumnType.class,
				(clazz, map, etp) -> clazz == Timestamp.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new TimestampColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(InstantColumnType.class,
				(clazz, map, etp) -> clazz == Instant.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new InstantColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(OffsetTimeColumnType.class,
				(clazz, map, etp) -> clazz == OffsetTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new OffsetTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(OffsetDateTimeColumnType.class,
				(clazz, map, etp) -> clazz == OffsetDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new OffsetDateTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(ZonedDateTimeColumnType.class,
				(clazz, map, etp) -> clazz == ZonedDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ZonedDateTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(SqlDateColumnType.class,
				(clazz, map, etp) -> clazz == java.sql.Date.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new SqlDateColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(SqlTimeColumnType.class,
				(clazz, map, etp) -> clazz == java.sql.Time.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new SqlTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(UtilDateColumnType.class,
				(clazz, map, etp) -> clazz == java.util.Date.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new UtilDateColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(UtilDateTimeColumnType.class,
				(clazz, map, etp) -> clazz == java.util.Date.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new UtilDateTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(PeriodColumnType.class,
				(clazz, map, etp) -> clazz == Period.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new PeriodColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(DurationColumnType.class,
				(clazz, map, etp) -> clazz == Duration.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new DurationColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(LocalDateTimeColumnType.class,
				(clazz, map, etp) -> clazz == LocalDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new LocalDateTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(LocalDateColumnType.class,
				(clazz, map, etp) -> clazz == LocalDate.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new LocalDateColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(LocalTimeColumnType.class,
				(clazz, map, etp) -> clazz == LocalTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new LocalTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(MonthDayStringColumnType.class,
				(clazz, map, etp) -> clazz == MonthDay.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new MonthDayStringColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(YearMonthStringColumnType.class,
				(clazz, map, etp) -> clazz == YearMonth.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new YearMonthStringColumnType(),
				typeMap);

		// JSON
		ColumnTypeRegistry.registerType(JsonObjectColumnType.class,
				(clazz, map, etp) -> clazz == JSONObject.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new JsonObjectColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(JsonArrayColumnType.class,
				(clazz, map, etp) -> clazz == JSONArray.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new JsonArrayColumnType(),
				typeMap);

		// OTHERS
		ColumnTypeRegistry.registerTypeSimple(MonthDayPackedColumnType.class, (type, map, etp) -> new MonthDayPackedColumnType(), typeMap);
		ColumnTypeRegistry
				.registerTypeSimple(YearMonthPackedColumnType.class, (type, map, etp) -> new YearMonthPackedColumnType(), typeMap);
		ColumnTypeRegistry.registerTypeSimple(EnumOrdinalColumnType.class, (type, map, etp) -> new EnumOrdinalColumnType(), typeMap);
	}
}
