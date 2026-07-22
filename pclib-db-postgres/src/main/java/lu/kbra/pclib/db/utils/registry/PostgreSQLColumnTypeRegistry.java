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
import lu.kbra.pclib.db.autobuild.postgres.column.binary.ByteArrayColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.binary.ByteBufferColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.decimal.DoubleType;
import lu.kbra.pclib.db.autobuild.postgres.column.decimal.FloatType;
import lu.kbra.pclib.db.autobuild.postgres.column.decimal.NumberType;
import lu.kbra.pclib.db.autobuild.postgres.column.integer.BigIntegerType;
import lu.kbra.pclib.db.autobuild.postgres.column.integer.ByteType;
import lu.kbra.pclib.db.autobuild.postgres.column.integer.IntegerType;
import lu.kbra.pclib.db.autobuild.postgres.column.integer.LongType;
import lu.kbra.pclib.db.autobuild.postgres.column.integer.ShortType;
import lu.kbra.pclib.db.autobuild.postgres.column.misc.BooleanType;
import lu.kbra.pclib.db.autobuild.postgres.column.misc.EnumOrdinalColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.misc.EnumStringColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.DurationType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.InstantColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.LocalDateColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.LocalDateTimeColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.LocalTimeColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.MonthDayPackedColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.MonthDayStringColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.OffsetDateTimeType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.OffsetTimeType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.PeriodType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.SqlDateColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.SqlTimeColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.TimestampColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.UtilDateColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.UtilDateTimeColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.YearMonthPackedColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.YearMonthStringColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.temporal.ZonedDateTimeType;
import lu.kbra.pclib.db.autobuild.postgres.column.text.CharArrayType;
import lu.kbra.pclib.db.autobuild.postgres.column.text.CharType;
import lu.kbra.pclib.db.autobuild.postgres.column.text.JsonArrayColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.text.JsonObjectColumnType;
import lu.kbra.pclib.db.autobuild.postgres.column.text.StringType;
import lu.kbra.pclib.db.autobuild.postgres.column.text.UUIDType;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;

public class PostgreSQLColumnTypeRegistry implements ColumnTypeRegistry {

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
		ColumnTypeRegistry.registerType(StringType.class,
				(clazz, map, etp) -> (clazz == String.class || clazz == CharSequence.class) && map.hasHint(DefaultTypeHints.MAX_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new StringType(map.getIntHint(DefaultTypeHints.MAX_LENGTH), true),
				typeMap);
		ColumnTypeRegistry.registerType(StringType.class,
				(clazz, map, etp) -> (clazz == String.class || clazz == CharSequence.class) && map.hasHint(DefaultTypeHints.FIXED_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new StringType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH), false),
				typeMap);
		ColumnTypeRegistry.registerType(StringType.class,
				(clazz, map, etp) -> clazz == String.class || clazz == CharSequence.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new StringType(),
				typeMap);

		// CHAR ARRAY
		ColumnTypeRegistry.registerType(CharArrayType.class,
				(clazz, map, etp) -> clazz == char[].class && map.hasHint(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new CharArrayType(map.getIntHint(DefaultTypeHints.MAX_LENGTH), true),
				typeMap);
		ColumnTypeRegistry.registerType(CharArrayType.class,
				(clazz, map, etp) -> clazz == char[].class && map.hasHint(DefaultTypeHints.FIXED_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new CharArrayType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH), false),
				typeMap);
		ColumnTypeRegistry.registerType(CharArrayType.class,
				(clazz, map, etp) -> clazz == char[].class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new CharArrayType(),
				typeMap);

		// CHAR
		ColumnTypeRegistry.registerType(CharType.class,
				(clazz, map, etp) -> clazz == Character.class || clazz == char.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new CharType(),
				typeMap);

		// UUID
		ColumnTypeRegistry.registerType(UUIDType.class,
				(clazz, map, etp) -> clazz == UUID.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new UUIDType(),
				typeMap);

		// BYTE ARRAY
		ColumnTypeRegistry.registerType(ByteArrayColumnType.class,
				(clazz, map, etp) -> clazz == byte[].class && map.hasHint(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ByteArrayColumnType(map.getIntHint(DefaultTypeHints.MAX_LENGTH), true),
				typeMap);
		ColumnTypeRegistry.registerType(ByteArrayColumnType.class,
				(clazz, map, etp) -> clazz == byte[].class && map.hasHint(DefaultTypeHints.FIXED_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ByteArrayColumnType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH), false),
				typeMap);
		ColumnTypeRegistry.registerType(ByteArrayColumnType.class,
				(clazz, map, etp) -> clazz == byte[].class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ByteArrayColumnType(),
				typeMap);

		// BYTE BUFFER
		ColumnTypeRegistry.registerType(ByteBufferColumnType.class,
				(clazz, map, etp) -> clazz == ByteBuffer.class && map.hasHint(DefaultTypeHints.MAX_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ByteBufferColumnType(map.getHint(DefaultTypeHints.MAX_LENGTH), true),
				typeMap);
		ColumnTypeRegistry.registerType(ByteBufferColumnType.class,
				(clazz, map, etp) -> clazz == ByteBuffer.class && map.hasHint(DefaultTypeHints.FIXED_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ByteBufferColumnType(map.getHint(DefaultTypeHints.FIXED_LENGTH), false),
				typeMap);
		ColumnTypeRegistry.registerType(ByteBufferColumnType.class,
				(clazz, map, etp) -> clazz == ByteBuffer.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ByteBufferColumnType(),
				typeMap);

		// NUMBERS
		// BYTE
		ColumnTypeRegistry.registerType(ByteType.class,
				(clazz, map, etp) -> (clazz == Byte.class || clazz == byte.class) ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ByteType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);

		// SHORT
		ColumnTypeRegistry.registerType(ShortType.class,
				(clazz, map, etp) -> (clazz == Short.class || clazz == short.class) ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ShortType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);

		// INTEGER
		ColumnTypeRegistry.registerType(IntegerType.class,
				(clazz, map, etp) -> (clazz == Integer.class || clazz == int.class) ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new IntegerType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);

		// LONG
		ColumnTypeRegistry.registerType(LongType.class,
				(clazz, map, etp) -> (clazz == Long.class || clazz == long.class) ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new LongType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);

		// BIG INTEGER
		ColumnTypeRegistry.registerType(BigIntegerType.class,
				(clazz, map, etp) -> clazz == BigInteger.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new BigIntegerType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);

		// NUMBER with precision & scale
		ColumnTypeRegistry.registerType(NumberType.class,
				(clazz, map, etp) -> PCUtils.isNumber(clazz)
						&& (map.hasHint(DefaultTypeHints.PRECISION) || map.hasHint(DefaultTypeHints.SCALE))
								? ColumnTypeRegistry.MAP_MATCH_SCORE
								: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new NumberType(map.getIntHint(DefaultTypeHints.PRECISION, 10),
						map.getIntHint(DefaultTypeHints.SCALE, 0)),
				typeMap);

		// DOUBLE
		ColumnTypeRegistry.registerType(DoubleType.class,
				(clazz, map, etp) -> clazz == Double.class || clazz == double.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new DoubleType(),
				typeMap);

		// FLOAT
		ColumnTypeRegistry.registerType(FloatType.class,
				(clazz, map, etp) -> clazz == Float.class || clazz == float.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new FloatType(),
				typeMap);

		// BOOLEAN
		ColumnTypeRegistry.registerType(BooleanType.class,
				(clazz, map, etp) -> clazz == Boolean.class || clazz == boolean.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new BooleanType(),
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
		ColumnTypeRegistry.registerType(OffsetTimeType.class,
				(clazz, map, etp) -> clazz == OffsetTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new OffsetTimeType(),
				typeMap);
		ColumnTypeRegistry.registerType(OffsetDateTimeType.class,
				(clazz, map, etp) -> clazz == OffsetDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new OffsetDateTimeType(),
				typeMap);
		ColumnTypeRegistry.registerType(ZonedDateTimeType.class,
				(clazz, map, etp) -> clazz == ZonedDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new ZonedDateTimeType(),
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
		ColumnTypeRegistry.registerType(PeriodType.class,
				(clazz, map, etp) -> clazz == Period.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new PeriodType(),
				typeMap);
		ColumnTypeRegistry.registerType(DurationType.class,
				(clazz, map, etp) -> clazz == Duration.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map, etp) -> new DurationType(),
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
