package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.math.BigDecimal;
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

import lu.kbra.pclib.datastructure.pair.Pairs;
import lu.kbra.pclib.datastructure.pair.ReadOnlyPair;
import lu.kbra.pclib.db.autobuild.column.type.meta.DefaultTypeHints;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.BlobType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BooleanType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BigIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.CharType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.ByteAType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.DateType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.DoublePrecisionType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.DurationType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.InstantType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.LocalDateTimeType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.LocalDateType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.LocalTimeType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.MonthDayType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.NumericType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.OffsetDateTimeType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.PeriodType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.RealType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.SmallIntType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.TextType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.TimestampType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.UUIDType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.VarcharType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.YearMonthType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.YearType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.ZonedDateTimeType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.IntegerType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.JsonType;

public class PostgreSQLColumnTypeRegistry implements ColumnTypeRegistry {

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

		this.registerType(ByteAType.class,
				(clazz, map) -> clazz == byte[].class || clazz == ByteBuffer.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteAType(),
				typeMap);

		this.registerType(BlobType.class,
				(clazz, map) -> clazz == byte[].class || clazz == ByteBuffer.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteAType(),
				typeMap);

		this.registerType(ByteAType.class,
				(clazz, map) -> clazz == ByteBuffer.class || clazz == byte[].class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new ByteAType(),
				typeMap);

		this.registerType(SmallIntType.class,
				(clazz, map) -> clazz == Byte.class || clazz == byte.class || clazz == Short.class || clazz == short.class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new SmallIntType(),
				typeMap);

		this.registerType(IntegerType.class,
				(clazz, map) -> clazz == Integer.class || clazz == int.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new IntegerType(),
				typeMap);

		this.registerType(BigIntType.class,
				(clazz, map) -> clazz == Long.class || clazz == long.class || clazz == BigInteger.class
						? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new BigIntType(),
				typeMap);

		this.registerType(DoublePrecisionType.class,
				(clazz, map) -> clazz == Double.class || clazz == double.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DoublePrecisionType(),
				typeMap);

		this.registerType(RealType.class,
				(clazz, map) -> clazz == Float.class || clazz == float.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new RealType(),
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
				(clazz, map) -> clazz == LocalDate.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new LocalDateType(),
				typeMap);

		this.registerType(LocalTimeType.class,
				(clazz, map) -> clazz == LocalTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
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

		this.registerType(NumericType.class,
				(clazz, map) -> clazz == BigDecimal.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new NumericType(),
				typeMap);
	}

}
