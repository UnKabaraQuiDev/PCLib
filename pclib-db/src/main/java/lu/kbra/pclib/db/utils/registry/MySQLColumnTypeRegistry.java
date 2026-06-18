package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;
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
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.datastructure.pair.Pairs;
import lu.kbra.pclib.datastructure.pair.ReadOnlyPair;
import lu.kbra.pclib.db.autobuild.column.type.meta.DefaultTypeHints;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.BinaryType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.BlobType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.VarbinaryType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BooleanType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.DecimalType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.DoubleType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.DecimalTypes.FloatType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BigIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.IntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.SmallIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.TinyIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.CharType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.JsonType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.TextType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.UUIDType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.VarcharType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.DateType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.DurationType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.InstantType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.LocalDateTimeType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.LocalDateType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.LocalTimeType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.MonthDayType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.OffsetDateTimeType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.PeriodType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.TimestampType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.YearMonthType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.YearType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.ZonedDateTimeType;

public class MySQLColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public void registerTypes(
			final List<ReadOnlyPair<BiFunction<Class<?>, Map<String, Object>, Integer>, BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType>>> typeMap) {
		typeMap.add(Pairs.readOnly(
				(clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new VarcharType(map.get(DefaultTypeHints.MAX_LENGTH))));
		typeMap.add(Pairs.readOnly(
				(clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharType(map.get(DefaultTypeHints.FIXED_LENGTH))));
		typeMap.add(Pairs.readOnly((clazz, map) -> clazz.isEnum() ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TextType()));

		this.registerType(VarcharType.class,
				(clazz, map) -> (clazz == String.class || clazz == CharSequence.class || clazz == char[].class)
						&& map.containsKey(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new VarcharType(map.get(DefaultTypeHints.MAX_LENGTH)),
				typeMap);
		this.registerType(CharType.class,
				(clazz, map) -> (clazz == String.class || clazz == CharSequence.class || clazz == char[].class)
						&& map.containsKey(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharType(map.get(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		this.registerType(TextType.class,
				(clazz, map) -> clazz == String.class || clazz == CharSequence.class || clazz == char[].class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TextType(),
				typeMap);
		this.registerType(UUIDType.class,
				(clazz, map) -> clazz == UUID.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new UUIDType(),
				typeMap);

		this.registerType(VarbinaryType.class,
				(clazz, map) -> (clazz == ByteBuffer.class || clazz == byte[].class) && map.containsKey(DefaultTypeHints.MAX_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new VarbinaryType(map.get(DefaultTypeHints.MAX_LENGTH)),
				typeMap);
		this.registerType(BinaryType.class,
				(clazz, map) -> (clazz == ByteBuffer.class || clazz == byte[].class) && map.containsKey(DefaultTypeHints.FIXED_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BinaryType(map.get(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		this.registerType(BlobType.class,
				(clazz, map) -> clazz == ByteBuffer.class || clazz == byte[].class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BlobType(),
				typeMap);

		this.registerType(TinyIntType.class,
				(clazz, map) -> clazz == Byte.class || clazz == byte.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TinyIntType(),
				typeMap);

		this.registerType(SmallIntType.class,
				(clazz, map) -> clazz == Short.class || clazz == short.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new SmallIntType(),
				typeMap);

		this.registerType(IntType.class,
				(clazz, map) -> clazz == Integer.class || clazz == int.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new IntType(),
				typeMap);

		this.registerType(BigIntType.class,
				(clazz, map) -> clazz == Long.class || clazz == long.class || clazz == BigInteger.class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BigIntType(),
				typeMap);

		this.registerType(DecimalType.class,
				(clazz, map) -> PCUtils.isNumber(clazz) && map.containsKey(DefaultTypeHints.PRECISION)
						&& map.containsKey(DefaultTypeHints.SCALE) ? ColumnTypeRegistry.MAP_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DecimalType(map.get(DefaultTypeHints.PRECISION), map.get(DefaultTypeHints.SCALE)),
				typeMap);

		this.registerType(DoubleType.class,
				(clazz, map) -> clazz == Double.class || clazz == double.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DoubleType(),
				typeMap);

		this.registerType(FloatType.class,
				(clazz, map) -> clazz == Float.class || clazz == float.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new FloatType(),
				typeMap);

		this.registerType(BooleanType.class,
				(clazz, map) -> clazz == Boolean.class || clazz == boolean.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BooleanType(),
				typeMap);

		this.registerType(TimestampType.class,
				(clazz, map) -> clazz == Timestamp.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TimestampType(),
				typeMap);

		this.registerType(InstantType.class,
				(clazz, map) -> clazz == Instant.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new InstantType(),
				typeMap);

		this.registerType(LocalDateTimeType.class,
				(clazz, map) -> clazz == LocalDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalDateTimeType(),
				typeMap);

		this.registerType(DateType.class,
				(clazz, map) -> clazz == Date.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DateType(),
				typeMap);

		this.registerType(LocalDateType.class,
				(clazz, map) -> clazz == LocalDate.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalDateType(),
				typeMap);

		this.registerType(LocalTimeType.class,
				(clazz, map) -> clazz == LocalTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalTimeType(),
				typeMap);

		this.registerType(ZonedDateTimeType.class,
				(clazz, map) -> clazz == ZonedDateTime.class && map.containsKey(DefaultTypeHints.ZONE_ID)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: clazz == ZonedDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> map.containsKey(DefaultTypeHints.ZONE_ID) ? new ZonedDateTimeType(map.get(DefaultTypeHints.ZONE_ID))
						: new ZonedDateTimeType(),
				typeMap);

		this.registerType(OffsetDateTimeType.class,
				(clazz, map) -> clazz == OffsetDateTime.class && map.containsKey(DefaultTypeHints.OFFSET_ID)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: clazz == OffsetDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> map.containsKey(DefaultTypeHints.OFFSET_ID) ? new OffsetDateTimeType(map.get(DefaultTypeHints.OFFSET_ID))
						: new OffsetDateTimeType(),
				typeMap);

		this.registerType(DurationType.class,
				(clazz, map) -> clazz == Duration.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DurationType(),
				typeMap);

		this.registerType(PeriodType.class,
				(clazz, map) -> clazz == Period.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new PeriodType(),
				typeMap);

		this.registerType(YearType.class,
				(clazz, map) -> clazz == Year.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new YearType(),
				typeMap);

		this.registerType(YearMonthType.class,
				(clazz, map) -> clazz == YearMonth.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new YearMonthType(),
				typeMap);

		this.registerType(MonthDayType.class,
				(clazz, map) -> clazz == MonthDay.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new MonthDayType(),
				typeMap);

		this.registerType(JsonType.class,
				(clazz, map) -> clazz == JSONObject.class || clazz == JSONArray.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new JsonType(),
				typeMap);
	}

}
