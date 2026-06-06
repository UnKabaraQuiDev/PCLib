package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.PCUtils;
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
import lu.kbra.pclib.db.autobuild.column.type.mysql.TimeTypes.TimestampType;

public class MySQLColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public void registerTypes(
			final Map<BiFunction<Class<?>, Map<String, Object>, Integer>, BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType>> typeMap) {
		typeMap.put((clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.MAX_LENGTH) ? MAP_MATCH_SCORE : EXCLUDE,
				(type, map) -> new VarcharType(map.get(DefaultTypeHints.MAX_LENGTH)));
		typeMap.put((clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.FIXED_LENGTH) ? MAP_MATCH_SCORE : EXCLUDE,
				(type, map) -> new CharType(map.get(DefaultTypeHints.FIXED_LENGTH)));
		typeMap.put((clazz, map) -> clazz.isEnum() ? TYPE_CATCH_ALL_SCORE : EXCLUDE, (type, map) -> new TextType());

		registerType(VarcharType.class,
				(
						clazz,
						map) -> (clazz == String.class || clazz == CharSequence.class || clazz == char[].class)
								&& map.containsKey(DefaultTypeHints.MAX_LENGTH) ? MAP_MATCH_SCORE : EXCLUDE,
				(type, map) -> new VarcharType(map.get(DefaultTypeHints.MAX_LENGTH)),
				typeMap);
		registerType(CharType.class,
				(
						clazz,
						map) -> (clazz == String.class || clazz == CharSequence.class || clazz == char[].class)
								&& map.containsKey(DefaultTypeHints.FIXED_LENGTH) ? MAP_MATCH_SCORE : EXCLUDE,
				(type, map) -> new CharType(map.get(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		registerType(TextType.class,
				(clazz, map) -> (clazz == String.class || clazz == CharSequence.class || clazz == char[].class) ? TYPE_CATCH_ALL_SCORE
						: EXCLUDE,
				(type, map) -> new TextType(),
				typeMap);
		registerType(UUIDType.class,
				(clazz, map) -> clazz == UUID.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new UUIDType(),
				typeMap);

		registerType(VarbinaryType.class,
				(clazz, map) -> (clazz == ByteBuffer.class || clazz == byte[].class) && map.containsKey(DefaultTypeHints.MAX_LENGTH)
						? MAP_MATCH_SCORE
						: EXCLUDE,
				(type, map) -> new VarbinaryType(map.get(DefaultTypeHints.MAX_LENGTH)),
				typeMap);
		registerType(BinaryType.class,
				(clazz, map) -> (clazz == ByteBuffer.class || clazz == byte[].class) && map.containsKey(DefaultTypeHints.FIXED_LENGTH)
						? MAP_MATCH_SCORE
						: EXCLUDE,
				(type, map) -> new BinaryType(map.get(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		registerType(BlobType.class,
				(clazz, map) -> (clazz == ByteBuffer.class || clazz == byte[].class) ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new BlobType(),
				typeMap);

		registerType(TinyIntType.class,
				(clazz, map) -> clazz == Byte.class || clazz == byte.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new TinyIntType(),
				typeMap);

		registerType(SmallIntType.class,
				(clazz, map) -> clazz == Short.class || clazz == short.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new SmallIntType(),
				typeMap);

		registerType(IntType.class,
				(clazz, map) -> clazz == Integer.class || clazz == int.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new IntType(),
				typeMap);

		registerType(BigIntType.class,
				(clazz, map) -> clazz == Long.class || clazz == long.class || clazz == BigInteger.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new BigIntType(),
				typeMap);

		registerType(DecimalType.class,
				(
						clazz,
						map) -> PCUtils.isNumber(clazz) && map.containsKey(DefaultTypeHints.PRECISION)
								&& map.containsKey(DefaultTypeHints.SCALE) ? MAP_MATCH_SCORE : EXCLUDE,
				(type, map) -> new DecimalType(map.get(DefaultTypeHints.PRECISION), map.get(DefaultTypeHints.SCALE)),
				typeMap);

		registerType(DoubleType.class,
				(clazz, map) -> clazz == Double.class || clazz == double.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new DoubleType(),
				typeMap);

		registerType(FloatType.class,
				(clazz, map) -> clazz == Float.class || clazz == float.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new FloatType(),
				typeMap);

		registerType(BooleanType.class,
				(clazz, map) -> clazz == Boolean.class || clazz == boolean.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new BooleanType(),
				typeMap);

		registerType(TimestampType.class,
				(clazz, map) -> clazz == Timestamp.class || clazz == LocalDateTime.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new TimestampType(),
				typeMap);

		registerType(DateType.class,
				(clazz, map) -> clazz == Date.class || clazz == LocalDate.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new DateType(),
				typeMap);

		registerType(JsonType.class,
				(clazz, map) -> clazz == JSONObject.class || clazz == JSONArray.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new JsonType(),
				typeMap);
	}

}
