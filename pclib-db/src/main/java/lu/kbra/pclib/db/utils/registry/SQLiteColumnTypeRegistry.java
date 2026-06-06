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
import java.util.function.BiFunction;

import org.json.JSONArray;
import org.json.JSONObject;

import lu.kbra.pclib.db.autobuild.column.type.meta.DefaultTypeHints;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.CharType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.VarcharType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.BlobType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.BooleanType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.DateType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.IntegerType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.JsonType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.NumericType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.RealType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.TextType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.TimestampType;

public class SQLiteColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public void registerTypes(
			final Map<BiFunction<Class<?>, Map<String, Object>, Integer>, BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType>> typeMap) {
		typeMap.put((clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.MAX_LENGTH) ? MAP_MATCH_SCORE : EXCLUDE,
				(type, map) -> new VarcharType(map.get(DefaultTypeHints.MAX_LENGTH)));
		typeMap.put((clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.FIXED_LENGTH) ? MAP_MATCH_SCORE : EXCLUDE,
				(type, map) -> new CharType(map.get(DefaultTypeHints.FIXED_LENGTH)));
		typeMap.put((clazz, map) -> clazz.isEnum() ? TYPE_CATCH_ALL_SCORE : EXCLUDE, (type, map) -> new TextType());

		registerType(TextType.class,
				(clazz, map) -> clazz == String.class || clazz == CharSequence.class || clazz == char[].class ? TYPE_CATCH_ALL_SCORE
						: EXCLUDE,
				(type, map) -> new TextType(),
				typeMap);

		registerType(BlobType.class,
				(clazz, map) -> clazz == byte[].class || clazz == ByteBuffer.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new BlobType(),
				typeMap);

		registerType(IntegerType.class,
				(
						clazz,
						map) -> clazz == Byte.class || clazz == byte.class || clazz == Short.class || clazz == short.class
								|| clazz == Integer.class || clazz == int.class || clazz == Long.class || clazz == long.class
								|| clazz == BigInteger.class ? TYPE_CATCH_ALL_SCORE : EXCLUDE,
				(type, map) -> new IntegerType(),
				typeMap);

		registerType(RealType.class,
				(clazz, map) -> clazz == Double.class || clazz == double.class || clazz == Float.class || clazz == float.class
						? TYPE_CATCH_ALL_SCORE
						: EXCLUDE,
				(type, map) -> new RealType(),
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

		typeMap.put((clazz, map) -> clazz == NumericType.class ? PERFECT_MATCH_SCORE : EXCLUDE, (type, map) -> new NumericType());
	}
}
