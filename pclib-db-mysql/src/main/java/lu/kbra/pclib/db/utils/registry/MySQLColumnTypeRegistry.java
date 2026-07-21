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
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.tuple.Pairs;
import lu.kbra.pclib.datastructure.tuple.ReadOnlyPair;
import lu.kbra.pclib.db.autobuild.mysql.binary.ByteArrayColumnType;
import lu.kbra.pclib.db.autobuild.mysql.binary.ByteBufferColumnType;
import lu.kbra.pclib.db.autobuild.mysql.decimal.DoubleType;
import lu.kbra.pclib.db.autobuild.mysql.decimal.FloatType;
import lu.kbra.pclib.db.autobuild.mysql.decimal.NumberType;
import lu.kbra.pclib.db.autobuild.mysql.integer.BigIntegerType;
import lu.kbra.pclib.db.autobuild.mysql.integer.ByteType;
import lu.kbra.pclib.db.autobuild.mysql.integer.IntegerType;
import lu.kbra.pclib.db.autobuild.mysql.integer.LongType;
import lu.kbra.pclib.db.autobuild.mysql.integer.ShortType;
import lu.kbra.pclib.db.autobuild.mysql.misc.BooleanType;
import lu.kbra.pclib.db.autobuild.mysql.misc.EnumOrdinalColumnType;
import lu.kbra.pclib.db.autobuild.mysql.misc.EnumStringColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.DurationType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.InstantColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.LocalDateColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.LocalDateTimeColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.LocalTimeColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.MonthDayPackedColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.MonthDayStringColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.OffsetDateTimeType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.OffsetTimeType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.PeriodType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.SqlDateColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.SqlTimeColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.TimestampColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.UtilDateColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.UtilDateTimeColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.YearColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.YearMonthPackedColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.YearMonthStringColumnType;
import lu.kbra.pclib.db.autobuild.mysql.temporal.ZonedDateTimeType;
import lu.kbra.pclib.db.autobuild.mysql.text.CharArrayType;
import lu.kbra.pclib.db.autobuild.mysql.text.CharType;
import lu.kbra.pclib.db.autobuild.mysql.text.JsonArrayColumnType;
import lu.kbra.pclib.db.autobuild.mysql.text.JsonObjectColumnType;
import lu.kbra.pclib.db.autobuild.mysql.text.StringType;
import lu.kbra.pclib.db.autobuild.mysql.text.UUIDType;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.domain.column.type.SizeClass;

public class MySQLColumnTypeRegistry implements ColumnTypeRegistry {

	public static final Map<ReadOnlyPair<Class<? extends EncodingType<?>>, Object>, EncodingType<?>> FIXED_ENCODING_TYPES = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <Tjdbc, Tec extends EncodingType<Tjdbc>> Tec
			getFixedEncodingType(final Class<? extends Tec> clazz, final Supplier<? extends Tec> supplier) {
		return (Tec) MySQLColumnTypeRegistry.FIXED_ENCODING_TYPES.computeIfAbsent(Pairs.readOnly(clazz, null), c -> supplier.get());
	}

	@SuppressWarnings("unchecked")
	public static <Tjdbc, Tec extends EncodingType<Tjdbc>, Tparam> Tec
			getFixedEncodingType(final Class<? extends Tec> clazz, final Tparam param, final Function<Tparam, ? extends Tec> supplier) {
		return (Tec) MySQLColumnTypeRegistry.FIXED_ENCODING_TYPES.computeIfAbsent(Pairs.readOnly(clazz, param), c -> supplier.apply(param));
	}

	@Override
	public void registerTypes(final List<ColumnTypeFactory<?>> typeMap) {
		// ENUM
		typeMap.add(new DelegatingColumnTypeFactory<>(EnumStringColumnType.class,
				(clazz, map) -> clazz.isEnum() && map.hasHint(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new EnumStringColumnType(map.getHint(DefaultTypeHints.MAX_LENGTH), true)));
		typeMap.add(new DelegatingColumnTypeFactory<>(EnumStringColumnType.class,
				(clazz, map) -> clazz.isEnum() && map.hasHint(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new EnumStringColumnType(map.getHint(DefaultTypeHints.FIXED_LENGTH), false)));
		typeMap.add(new DelegatingColumnTypeFactory<>(EnumStringColumnType.class,
				(clazz, map) -> clazz.isEnum() ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new EnumStringColumnType()));

		// STRING
		ColumnTypeRegistry.registerType(StringType.class,
				(clazz, map) -> (clazz == String.class || clazz == CharSequence.class) && map.hasHint(DefaultTypeHints.MAX_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new StringType(map.getIntHint(DefaultTypeHints.MAX_LENGTH), true),
				typeMap);
		ColumnTypeRegistry.registerType(StringType.class,
				(clazz, map) -> (clazz == String.class || clazz == CharSequence.class) && map.hasHint(DefaultTypeHints.FIXED_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new StringType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH), false),
				typeMap);
		ColumnTypeRegistry.registerType(StringType.class,
				(clazz, map) -> (clazz == String.class || clazz == CharSequence.class) && map.hasHint(DefaultTypeHints.SIZE_CLASS)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new StringType(map.<SizeClass>getHint(DefaultTypeHints.SIZE_CLASS)),
				typeMap);
		ColumnTypeRegistry.registerType(StringType.class,
				(clazz, map) -> clazz == String.class || clazz == CharSequence.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new StringType(),
				typeMap);

		// CHAR ARRAY
		ColumnTypeRegistry.registerType(CharArrayType.class,
				(clazz, map) -> clazz == char[].class && map.hasHint(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharArrayType(map.getIntHint(DefaultTypeHints.MAX_LENGTH), true),
				typeMap);
		ColumnTypeRegistry.registerType(CharArrayType.class,
				(clazz, map) -> clazz == char[].class && map.hasHint(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharArrayType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH), false),
				typeMap);
		ColumnTypeRegistry.registerType(CharArrayType.class,
				(clazz, map) -> clazz == char[].class && map.hasHint(DefaultTypeHints.SIZE_CLASS) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharArrayType(map.<SizeClass>getHint(DefaultTypeHints.SIZE_CLASS)),
				typeMap);
		ColumnTypeRegistry.registerType(CharArrayType.class,
				(clazz, map) -> clazz == char[].class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharArrayType(),
				typeMap);

		// CHAR
		ColumnTypeRegistry.registerType(CharType.class,
				(clazz, map) -> clazz == Character.class || clazz == char.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharType(),
				typeMap);

		// UUID
		ColumnTypeRegistry.registerType(UUIDType.class,
				(clazz, map) -> clazz == UUID.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new UUIDType(),
				typeMap);

		// BYTE ARRAY
		ColumnTypeRegistry.registerType(ByteArrayColumnType.class,
				(clazz, map) -> clazz == byte[].class && map.hasHint(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteArrayColumnType(map.getIntHint(DefaultTypeHints.MAX_LENGTH), true),
				typeMap);
		ColumnTypeRegistry.registerType(ByteArrayColumnType.class,
				(clazz, map) -> clazz == byte[].class && map.hasHint(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteArrayColumnType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH), false),
				typeMap);
		ColumnTypeRegistry.registerType(ByteArrayColumnType.class,
				(clazz, map) -> clazz == byte[].class && map.hasHint(DefaultTypeHints.SIZE_CLASS) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteArrayColumnType(map.<SizeClass>getHint(DefaultTypeHints.SIZE_CLASS)),
				typeMap);
		ColumnTypeRegistry.registerType(ByteArrayColumnType.class,
				(clazz, map) -> clazz == byte[].class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteArrayColumnType(),
				typeMap);

		// BYTE BUFFER
		ColumnTypeRegistry.registerType(ByteBufferColumnType.class,
				(clazz, map) -> clazz == ByteBuffer.class && map.hasHint(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteBufferColumnType(map.getHint(DefaultTypeHints.MAX_LENGTH), true),
				typeMap);
		ColumnTypeRegistry.registerType(ByteBufferColumnType.class,
				(clazz, map) -> clazz == ByteBuffer.class && map.hasHint(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteBufferColumnType(map.getHint(DefaultTypeHints.FIXED_LENGTH), false),
				typeMap);
		ColumnTypeRegistry.registerType(ByteBufferColumnType.class,
				(clazz, map) -> clazz == ByteBuffer.class && map.hasHint(DefaultTypeHints.SIZE_CLASS) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteBufferColumnType((SizeClass) map.getHint(DefaultTypeHints.SIZE_CLASS)),
				typeMap);
		ColumnTypeRegistry.registerType(ByteBufferColumnType.class,
				(clazz, map) -> clazz == ByteBuffer.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteBufferColumnType(),
				typeMap);

		// NUMBERS
		// BYTE
		ColumnTypeRegistry.registerType(ByteType.class,
				(clazz, map) -> (clazz == Byte.class || clazz == byte.class) && map.hasHint(DefaultTypeHints.UNSIGNED)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);
		ColumnTypeRegistry.registerType(ByteType.class,
				(clazz, map) -> clazz == Byte.class || clazz == byte.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteType(),
				typeMap);

		// SHORT
		ColumnTypeRegistry.registerType(ShortType.class,
				(clazz, map) -> (clazz == Short.class || clazz == short.class) && map.hasHint(DefaultTypeHints.UNSIGNED)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ShortType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);
		ColumnTypeRegistry.registerType(ShortType.class,
				(clazz, map) -> clazz == Short.class || clazz == short.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ShortType(),
				typeMap);

		// INTEGER
		ColumnTypeRegistry.registerType(IntegerType.class,
				(clazz, map) -> (clazz == Integer.class || clazz == int.class) && map.hasHint(DefaultTypeHints.UNSIGNED)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new IntegerType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);
		ColumnTypeRegistry.registerType(IntegerType.class,
				(clazz, map) -> clazz == Integer.class || clazz == int.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new IntegerType(),
				typeMap);

		// LONG
		ColumnTypeRegistry.registerType(LongType.class,
				(clazz, map) -> (clazz == Long.class || clazz == long.class) && map.hasHint(DefaultTypeHints.UNSIGNED)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LongType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);
		ColumnTypeRegistry.registerType(LongType.class,
				(clazz, map) -> clazz == Long.class || clazz == long.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LongType(),
				typeMap);

		// BIG INTEGER
		ColumnTypeRegistry.registerType(BigIntegerType.class,
				(clazz, map) -> clazz == BigInteger.class && map.hasHint(DefaultTypeHints.UNSIGNED) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BigIntegerType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);
		ColumnTypeRegistry.registerType(BigIntegerType.class,
				(clazz, map) -> clazz == BigInteger.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BigIntegerType(),
				typeMap);

		// NUMBER with precision & scale
		ColumnTypeRegistry.registerType(NumberType.class,
				(clazz, map) -> PCUtils.isNumber(clazz) && (map.hasHint(DefaultTypeHints.PRECISION) || map.hasHint(DefaultTypeHints.SCALE))
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new NumberType(map.getIntHint(DefaultTypeHints.PRECISION, 10), map.getIntHint(DefaultTypeHints.SCALE, 0)),
				typeMap);

		// DOUBLE
		ColumnTypeRegistry.registerType(DoubleType.class,
				(clazz, map) -> clazz == Double.class || clazz == double.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DoubleType(),
				typeMap);

		// FLOAT
		ColumnTypeRegistry.registerType(FloatType.class,
				(clazz, map) -> clazz == Float.class || clazz == float.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new FloatType(),
				typeMap);

		// BOOLEAN
		ColumnTypeRegistry.registerType(BooleanType.class,
				(clazz, map) -> clazz == Boolean.class || clazz == boolean.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BooleanType(),
				typeMap);

		// TEMPORAL
		ColumnTypeRegistry.registerType(TimestampColumnType.class,
				(clazz, map) -> clazz == Timestamp.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TimestampColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(InstantColumnType.class,
				(clazz, map) -> clazz == Instant.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new InstantColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(OffsetTimeType.class,
				(clazz, map) -> clazz == OffsetTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new OffsetTimeType(),
				typeMap);
		ColumnTypeRegistry.registerType(OffsetDateTimeType.class,
				(clazz, map) -> clazz == OffsetDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new OffsetDateTimeType(),
				typeMap);
		ColumnTypeRegistry.registerType(ZonedDateTimeType.class,
				(clazz, map) -> clazz == ZonedDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ZonedDateTimeType(),
				typeMap);
		ColumnTypeRegistry.registerType(SqlDateColumnType.class,
				(clazz, map) -> clazz == java.sql.Date.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new SqlDateColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(SqlTimeColumnType.class,
				(clazz, map) -> clazz == java.sql.Time.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new SqlTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(UtilDateColumnType.class,
				(clazz, map) -> clazz == java.util.Date.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new UtilDateColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(UtilDateTimeColumnType.class,
				(clazz, map) -> clazz == java.util.Date.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new UtilDateTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(PeriodType.class,
				(clazz, map) -> clazz == Period.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new PeriodType(),
				typeMap);
		ColumnTypeRegistry.registerType(DurationType.class,
				(clazz, map) -> clazz == Duration.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DurationType(),
				typeMap);
		ColumnTypeRegistry.registerType(LocalDateTimeColumnType.class,
				(clazz, map) -> clazz == LocalDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalDateTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(LocalDateColumnType.class,
				(clazz, map) -> clazz == LocalDate.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalDateColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(LocalTimeColumnType.class,
				(clazz, map) -> clazz == LocalTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalTimeColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(MonthDayStringColumnType.class,
				(clazz, map) -> clazz == MonthDay.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new MonthDayStringColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(YearColumnType.class,
				(clazz, map) -> clazz == Year.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new YearColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(YearMonthStringColumnType.class,
				(clazz, map) -> clazz == YearMonth.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new YearMonthStringColumnType(),
				typeMap);

		// JSON
		ColumnTypeRegistry.registerType(JsonObjectColumnType.class,
				(clazz, map) -> clazz == JSONObject.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new JsonObjectColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(JsonArrayColumnType.class,
				(clazz, map) -> clazz == JSONArray.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new JsonArrayColumnType(),
				typeMap);

		// OTHERS
		ColumnTypeRegistry.registerTypeSimple(MonthDayPackedColumnType.class, (type, map) -> new MonthDayPackedColumnType(), typeMap);
		ColumnTypeRegistry.registerTypeSimple(YearMonthPackedColumnType.class, (type, map) -> new YearMonthPackedColumnType(), typeMap);
		ColumnTypeRegistry.registerTypeSimple(EnumOrdinalColumnType.class, (type, map) -> new EnumOrdinalColumnType(), typeMap);
	}

}
