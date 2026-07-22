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

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.db.autobuild.sqlite.BlobType;
import lu.kbra.pclib.db.autobuild.sqlite.BooleanType;
import lu.kbra.pclib.db.autobuild.sqlite.DateType;
import lu.kbra.pclib.db.autobuild.sqlite.IntegerType;
import lu.kbra.pclib.db.autobuild.sqlite.JsonType;
import lu.kbra.pclib.db.autobuild.sqlite.NumericType;
import lu.kbra.pclib.db.autobuild.sqlite.RealType;
import lu.kbra.pclib.db.autobuild.sqlite.TextType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.DurationType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.InstantType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.LocalDateTimeType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.LocalDateType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.LocalTimeType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.MonthDayType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.OffsetDateTimeType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.PeriodType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.YearMonthType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.YearType;
import lu.kbra.pclib.db.autobuild.sqlite.TimeTypes.ZonedDateTimeType;
import lu.kbra.pclib.db.autobuild.sqlite.TimestampType;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;

public class SQLiteColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public void registerColumnTypes(final List<ColumnTypeFactory> typeMap) {
		typeMap.add(new DelegatingColumnTypeFactory(TextType.class,
				(clazz, map) -> clazz.isEnum() ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TextType()));

		ColumnTypeRegistry.registerType(TextType.class,
				(clazz, map) -> clazz == String.class || clazz == CharSequence.class || clazz == char[].class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TextType(),
				typeMap);

		ColumnTypeRegistry.registerType(BlobType.class,
				(clazz, map) -> clazz == byte[].class || clazz == ByteBuffer.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BlobType(),
				typeMap);

		ColumnTypeRegistry.registerType(IntegerType.class,
				(
						clazz,
						map) -> clazz == Byte.class || clazz == byte.class || clazz == Short.class || clazz == short.class
								|| clazz == Integer.class || clazz == int.class || clazz == Long.class || clazz == long.class
								|| clazz == BigInteger.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new IntegerType(),
				typeMap);

		ColumnTypeRegistry.registerType(RealType.class,
				(clazz, map) -> clazz == Double.class || clazz == double.class || clazz == Float.class || clazz == float.class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new RealType(),
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

		typeMap.add(new DelegatingColumnTypeFactory(NumericType.class,
				(clazz, map) -> clazz == NumericType.class ? ColumnTypeRegistry.PERFECT_MATCH_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new NumericType()));
	}
}
