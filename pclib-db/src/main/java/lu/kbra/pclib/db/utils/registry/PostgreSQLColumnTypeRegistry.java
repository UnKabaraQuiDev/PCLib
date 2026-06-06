package lu.kbra.pclib.db.utils.registry;

import java.lang.reflect.AnnotatedType;
import java.math.BigDecimal;
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

import lu.kbra.pclib.db.autobuild.column.type.meta.DefaultTypeHints;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BinaryTypes.BlobType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.BooleanType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.ColumnType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.IntTypes.BigIntType;
import lu.kbra.pclib.db.autobuild.column.type.mysql.TextTypes.CharType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.ByteAType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.DateType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.DoublePrecisionType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.NumericType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.RealType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.SmallIntType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.TextType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.UUIDType;
import lu.kbra.pclib.db.autobuild.column.type.postgres.PostgreSQLTypes.VarcharType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.IntegerType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.JsonType;
import lu.kbra.pclib.db.autobuild.column.type.sqlite.TimestampType;

public class PostgreSQLColumnTypeRegistry implements ColumnTypeRegistry {

	@Override
	public void registerTypes(
			final Map<BiFunction<Class<?>, Map<String, Object>, Integer>, BiFunction<Optional<AnnotatedType>, Map<String, Object>, ColumnType>> typeMap) {
		typeMap.put((clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.MAX_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
				: ColumnTypeRegistry.EXCLUDE, (type, map) -> new VarcharType(map.get(DefaultTypeHints.MAX_LENGTH)));
		typeMap.put((clazz, map) -> clazz.isEnum() && map.containsKey(DefaultTypeHints.FIXED_LENGTH) ? ColumnTypeRegistry.MAP_MATCH_SCORE
				: ColumnTypeRegistry.EXCLUDE, (type, map) -> new CharType(map.get(DefaultTypeHints.FIXED_LENGTH)));
		typeMap.put((clazz, map) -> clazz.isEnum() ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE : ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TextType());

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
				(clazz, map) -> clazz == Timestamp.class || clazz == LocalDateTime.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new TimestampType(),
				typeMap);

		this.registerType(DateType.class,
				(clazz, map) -> clazz == Date.class || clazz == LocalDate.class ? ColumnTypeRegistry.TYPE_CATCH_ALL_SCORE
						: ColumnTypeRegistry.EXCLUDE,
				(type, map) -> new DateType(),
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
