package lu.kbra.pclib.db.utils.registry;

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
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.mysql.BinaryTypes.BinaryType;
import lu.kbra.pclib.db.autobuild.mysql.BinaryTypes.BlobType;
import lu.kbra.pclib.db.autobuild.mysql.BinaryTypes.VarbinaryType;
import lu.kbra.pclib.db.autobuild.mysql.BooleanType;
import lu.kbra.pclib.db.autobuild.mysql.DecimalTypes.DecimalType;
import lu.kbra.pclib.db.autobuild.mysql.DecimalTypes.DoubleType;
import lu.kbra.pclib.db.autobuild.mysql.DecimalTypes.FloatType;
import lu.kbra.pclib.db.autobuild.mysql.IntTypes.BigIntType;
import lu.kbra.pclib.db.autobuild.mysql.IntTypes.IntType;
import lu.kbra.pclib.db.autobuild.mysql.IntTypes.SmallIntType;
import lu.kbra.pclib.db.autobuild.mysql.IntTypes.TinyIntType;
import lu.kbra.pclib.db.autobuild.mysql.TextTypes.CharType;
import lu.kbra.pclib.db.autobuild.mysql.TextTypes.JsonType;
import lu.kbra.pclib.db.autobuild.mysql.TextTypes.TextType;
import lu.kbra.pclib.db.autobuild.mysql.TextTypes.UUIDType;
import lu.kbra.pclib.db.autobuild.mysql.TextTypes.VarcharType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.DateType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.DurationType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.InstantType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.LocalDateTimeType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.LocalDateType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.LocalTimeType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.MonthDayType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.OffsetDateTimeType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.PeriodType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.TimestampType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.YearMonthType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.YearType;
import lu.kbra.pclib.db.autobuild.mysql.TimeTypes.ZonedDateTimeType;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;

public class MySQLColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public void registerTypes(final List<ColumnTypeFactory> typeMap) {
		typeMap.add(new DelegatingColumnTypeFactory(VarcharType.class,
				(clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new VarcharType(map.get(DefaultTypeHints.MAX_LENGTH))));
		typeMap.add(new DelegatingColumnTypeFactory(CharType.class,
				(clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharType(map.get(DefaultTypeHints.FIXED_LENGTH))));
		typeMap.add(new DelegatingColumnTypeFactory(TextType.class,
				(clazz, map) -> clazz.isEnum() ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TextType()));

		ColumnTypeRegistry.registerType(VarcharType.class,
				(clazz, map) -> (clazz == String.class || clazz == CharSequence.class || clazz == char[].class)
						&& map.containsKey(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new VarcharType(map.get(DefaultTypeHints.MAX_LENGTH)),
				typeMap);
		ColumnTypeRegistry.registerType(CharType.class,
				(clazz, map) -> (clazz == String.class || clazz == CharSequence.class || clazz == char[].class)
						&& map.containsKey(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new CharType(map.get(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		ColumnTypeRegistry.registerType(TextType.class,
				(clazz, map) -> clazz == String.class || clazz == CharSequence.class || clazz == char[].class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TextType(),
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
		ColumnTypeRegistry.registerType(BinaryType.class,
				(clazz, map) -> (clazz == ByteBuffer.class || clazz == byte[].class) && map.containsKey(DefaultTypeHints.FIXED_LENGTH)
						? ColumnTypeRegistry.MAP_MATCH_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BinaryType(map.get(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		ColumnTypeRegistry.registerType(BlobType.class,
				(clazz, map) -> clazz == ByteBuffer.class || clazz == byte[].class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
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

		ColumnTypeRegistry.registerType(DecimalType.class,
				(clazz, map) -> PCUtils.isNumber(clazz) && map.containsKey(DefaultTypeHints.PRECISION)
						&& map.containsKey(DefaultTypeHints.SCALE) ? ColumnTypeRegistry.MAP_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DecimalType(map.get(DefaultTypeHints.PRECISION), map.get(DefaultTypeHints.SCALE)),
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
				(clazz, map) -> clazz == Timestamp.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TimestampType(),
				typeMap);

		ColumnTypeRegistry.registerType(InstantType.class,
				(clazz, map) -> clazz == Instant.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new InstantType(),
				typeMap);

		ColumnTypeRegistry.registerType(LocalDateTimeType.class,
				(clazz, map) -> clazz == LocalDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalDateTimeType(),
				typeMap);

		ColumnTypeRegistry.registerType(DateType.class,
				(clazz, map) -> clazz == Date.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DateType(),
				typeMap);

		ColumnTypeRegistry.registerType(LocalDateType.class,
				(clazz, map) -> clazz == LocalDate.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalDateType(),
				typeMap);

		ColumnTypeRegistry.registerType(LocalTimeType.class,
				(clazz, map) -> clazz == LocalTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalTimeType(),
				typeMap);

		ColumnTypeRegistry.registerType(ZonedDateTimeType.class,
				(
						clazz,
						map) -> clazz == ZonedDateTime.class && map.containsKey(DefaultTypeHints.ZONE_ID)
								? ColumnTypeRegistry.MAP_MATCH_SCORE
								: clazz == ZonedDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
								: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> map.containsKey(DefaultTypeHints.ZONE_ID) ? new ZonedDateTimeType(map.get(DefaultTypeHints.ZONE_ID))
						: new ZonedDateTimeType(),
				typeMap);

		ColumnTypeRegistry
				.registerType(OffsetDateTimeType.class,
						(clazz, map) -> clazz == OffsetDateTime.class
								&& map.containsKey(DefaultTypeHints.OFFSET_ID) ? ColumnTypeRegistry.MAP_MATCH_SCORE
								: clazz == OffsetDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
								: ColumnTypeRegistry.EXCLUDE,
						(type, map) -> map.containsKey(DefaultTypeHints.OFFSET_ID)
								? new OffsetDateTimeType(map.get(DefaultTypeHints.OFFSET_ID))
								: new OffsetDateTimeType(),
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
