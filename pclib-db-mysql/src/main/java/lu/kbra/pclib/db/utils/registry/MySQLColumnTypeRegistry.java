package lu.kbra.pclib.db.utils.registry;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.Time;
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
import lu.kbra.pclib.db.autobuild.mysql.binary.ByteArrayType;
import lu.kbra.pclib.db.autobuild.mysql.binary.BlobType;
import lu.kbra.pclib.db.autobuild.mysql.binary.BytesType;
import lu.kbra.pclib.db.autobuild.mysql.binary.VarbinaryType;
import lu.kbra.pclib.db.autobuild.mysql.decimal.BigDecimalType;
import lu.kbra.pclib.db.autobuild.mysql.decimal.DoubleType;
import lu.kbra.pclib.db.autobuild.mysql.decimal.FloatType;
import lu.kbra.pclib.db.autobuild.mysql.integer.BigIntType;
import lu.kbra.pclib.db.autobuild.mysql.integer.IntType;
import lu.kbra.pclib.db.autobuild.mysql.integer.SmallIntType;
import lu.kbra.pclib.db.autobuild.mysql.integer.TinyIntType;
import lu.kbra.pclib.db.autobuild.mysql.misc.BooleanType;
import lu.kbra.pclib.db.autobuild.mysql.text.CharType;
import lu.kbra.pclib.db.autobuild.mysql.text.JsonType;
import lu.kbra.pclib.db.autobuild.mysql.text.StringColumnType;
import lu.kbra.pclib.db.autobuild.mysql.text.UUIDType;
import lu.kbra.pclib.db.autobuild.mysql.text.StringType;
import lu.kbra.pclib.db.autobuild.mysql.time.date.DateType;
import lu.kbra.pclib.db.autobuild.mysql.time.datetime.ForcedOffsetDateTimeType;
import lu.kbra.pclib.db.autobuild.mysql.time.datetime.ForcedZonedDateTimeType;
import lu.kbra.pclib.db.autobuild.mysql.time.datetime.LocalDateTimeType;
import lu.kbra.pclib.db.autobuild.mysql.time.datetime.OffsetDateTimeType;
import lu.kbra.pclib.db.autobuild.mysql.time.datetime.TimestampType;
import lu.kbra.pclib.db.autobuild.mysql.time.datetime.ZonedDateTimeType;
import lu.kbra.pclib.db.autobuild.mysql.time.misc.DurationType;
import lu.kbra.pclib.db.autobuild.mysql.time.misc.MonthDayType;
import lu.kbra.pclib.db.autobuild.mysql.time.misc.PeriodType;
import lu.kbra.pclib.db.autobuild.mysql.time.misc.YearMonthType;
import lu.kbra.pclib.db.autobuild.mysql.time.misc.YearType;
import lu.kbra.pclib.db.autobuild.mysql.time.time.OffsetTimeType;
import lu.kbra.pclib.db.autobuild.mysql.time.time.TimeType;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

public class MySQLColumnTypeRegistry implements ColumnTypeRegistry {

	public static final Map<ReadOnlyPair<Class<? extends EncodingType<?>>, Object>, EncodingType<?>> FIXED_ENCODING_TYPES = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <Tjdbc, Tec extends EncodingType<Tjdbc>> Tec
			getFixedEncodingType(Class<? extends Tec> clazz, Supplier<? extends Tec> supplier) {
		return (Tec) FIXED_ENCODING_TYPES.computeIfAbsent(Pairs.readOnly(clazz, null), c -> supplier.get());
	}

	@SuppressWarnings("unchecked")
	public static <Tjdbc, Tec extends EncodingType<Tjdbc>, Tparam> Tec
			getFixedEncodingType(Class<? extends Tec> clazz, Tparam param, Function<Tparam, ? extends Tec> supplier) {
		return (Tec) FIXED_ENCODING_TYPES.computeIfAbsent(Pairs.readOnly(clazz, param), c -> supplier.apply(param));
	}

	@Override
	public void registerTypes(final List<ColumnTypeFactory> typeMap) {
		typeMap.add(new DelegatingColumnTypeFactory<>(StringType.class,
				(clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new StringType(map.get(DefaultTypeHints.MAX_LENGTH))));
		typeMap.add(new DelegatingColumnTypeFactory<>(CharType.class,
				(clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharType(map.get(DefaultTypeHints.FIXED_LENGTH))));
		typeMap.add(new DelegatingColumnTypeFactory<>(StringColumnType.class,
				(clazz, map) -> clazz.isEnum() ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new StringColumnType()));

		ColumnTypeRegistry.registerType(StringType.class,
				(clazz, map) -> (clazz == String.class || clazz == CharSequence.class || clazz == char[].class)
						&& map.containsKey(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new StringType(map.get(DefaultTypeHints.MAX_LENGTH)),
				typeMap);
		ColumnTypeRegistry.registerType(CharType.class,
				(clazz, map) -> (clazz == String.class || clazz == CharSequence.class || clazz == char[].class)
						&& map.containsKey(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharType(map.get(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		ColumnTypeRegistry.registerType(StringColumnType.class,
				(clazz, map) -> clazz == String.class || clazz == CharSequence.class || clazz == char[].class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new StringColumnType(),
				typeMap);
		ColumnTypeRegistry.registerType(UUIDType.class,
				(clazz, map) -> clazz == UUID.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new UUIDType(),
				typeMap);

		ColumnTypeRegistry.registerType(VarbinaryType.class,
				(clazz, map) -> (clazz == ByteBuffer.class || clazz == byte[].class) && map.containsKey(DefaultTypeHints.MAX_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new VarbinaryType(map.get(DefaultTypeHints.MAX_LENGTH)),
				typeMap);
		ColumnTypeRegistry.registerType(ByteArrayType.class,
				(clazz, map) -> (clazz == ByteBuffer.class || clazz == byte[].class) && map.containsKey(DefaultTypeHints.FIXED_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteArrayType(map.get(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		ColumnTypeRegistry.registerType(BytesType.class,
				(clazz, map) -> clazz == ByteBuffer.class || clazz == byte[].class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BytesType(),
				typeMap);
		ColumnTypeRegistry.registerType(BlobType.class,
				(clazz, map) -> clazz == Blob.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BlobType(),
				typeMap);

		ColumnTypeRegistry.registerType(TinyIntType.class,
				(clazz, map) -> clazz == Byte.class || clazz == byte.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TinyIntType(),
				typeMap);
		ColumnTypeRegistry.registerType(SmallIntType.class,
				(clazz, map) -> clazz == Short.class || clazz == short.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new SmallIntType(),
				typeMap);
		ColumnTypeRegistry.registerType(IntType.class,
				(clazz, map) -> clazz == Integer.class || clazz == int.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new IntType(),
				typeMap);
		ColumnTypeRegistry.registerType(BigIntType.class,
				(clazz, map) -> clazz == Long.class || clazz == long.class || clazz == BigInteger.class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BigIntType(),
				typeMap);

		ColumnTypeRegistry.registerType(BigDecimalType.class,
				(clazz, map) -> PCUtils.isNumber(clazz) && map.containsKey(DefaultTypeHints.PRECISION)
						&& map.containsKey(DefaultTypeHints.SCALE) ? ColumnTypeRegistry.MAP_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BigDecimalType(map.get(DefaultTypeHints.PRECISION), map.get(DefaultTypeHints.SCALE)),
				typeMap);
		ColumnTypeRegistry.registerType(DoubleType.class,
				(clazz, map) -> clazz == Double.class || clazz == double.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DoubleType(),
				typeMap);
		ColumnTypeRegistry.registerType(FloatType.class,
				(clazz, map) -> clazz == Float.class || clazz == float.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new FloatType(),
				typeMap);

		ColumnTypeRegistry.registerType(BooleanType.class,
				(clazz, map) -> clazz == Boolean.class || clazz == boolean.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BooleanType(),
				typeMap);

		ColumnTypeRegistry.registerType(TimestampType.class,
				(clazz, map) -> clazz == Instant.class || clazz == Timestamp.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TimestampType(),
				typeMap);
		ColumnTypeRegistry.registerType(OffsetTimeType.class,
				(clazz, map) -> clazz == OffsetTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new OffsetTimeType(),
				typeMap);

		ColumnTypeRegistry.registerType(DateType.class,
				(clazz, map) -> clazz == LocalDate.class || clazz == java.sql.Date.class || clazz == java.util.Date.class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DateType(),
				typeMap);

		ColumnTypeRegistry.registerType(LocalDateTimeType.class,
				(clazz, map) -> clazz == LocalDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalDateTimeType(),
				typeMap);
		ColumnTypeRegistry.registerType(ZonedDateTimeType.class,
				(clazz, map) -> clazz == ZonedDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ZonedDateTimeType(),
				typeMap);
		ColumnTypeRegistry.registerType(OffsetDateTimeType.class,
				(clazz, map) -> clazz == OffsetDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new OffsetDateTimeType(),
				typeMap);
		ColumnTypeRegistry.registerType(ForcedZonedDateTimeType.class,
				(clazz, map) -> clazz == ZonedDateTime.class && map.containsKey(DefaultTypeHints.ZONE_ID)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ForcedZonedDateTimeType(map.get(DefaultTypeHints.ZONE_ID)),
				typeMap);
		ColumnTypeRegistry.registerType(ForcedOffsetDateTimeType.class,
				(clazz, map) -> clazz == OffsetDateTime.class && map.containsKey(DefaultTypeHints.OFFSET_ID)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ForcedOffsetDateTimeType(map.get(DefaultTypeHints.OFFSET_ID)),
				typeMap);

		ColumnTypeRegistry.registerType(TimeType.class,
				(clazz, map) -> clazz == Time.class || clazz == LocalTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TimeType(),
				typeMap);

		ColumnTypeRegistry.registerType(DurationType.class,
				(clazz, map) -> clazz == Duration.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DurationType(),
				typeMap);
		ColumnTypeRegistry.registerType(PeriodType.class,
				(clazz, map) -> clazz == Period.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new PeriodType(),
				typeMap);
		ColumnTypeRegistry.registerType(YearType.class,
				(clazz, map) -> clazz == Year.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new YearType(),
				typeMap);
		ColumnTypeRegistry.registerType(YearMonthType.class,
				(clazz, map) -> clazz == YearMonth.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new YearMonthType(),
				typeMap);
		ColumnTypeRegistry.registerType(MonthDayType.class,
				(clazz, map) -> clazz == MonthDay.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new MonthDayType(),
				typeMap);

		ColumnTypeRegistry.registerType(JsonType.class,
				(clazz, map) -> clazz == JSONObject.class || clazz == JSONArray.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new JsonType(),
				typeMap);
	}

}
