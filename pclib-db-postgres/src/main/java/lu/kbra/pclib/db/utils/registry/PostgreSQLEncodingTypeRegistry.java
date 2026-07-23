package lu.kbra.pclib.db.utils.registry;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.List;

import lu.kbra.pclib.db.autobuild.postgres.encoding.array.GenericArrayEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.binary.BinaryEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.binary.ByteAEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.binary.VarbinaryEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.bool.BitEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.bool.BooleanEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.decimal.DoubleEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.decimal.NumericEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.decimal.RealEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.integer.BigIntEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.integer.SmallIntEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.integer.TinyIntEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.misc.JsonBEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.misc.JsonEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.misc.UUIDEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.misc.XmlEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.DateEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.IntervalEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.TimeEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.TimeZEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.TimestampZEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;

public class PostgreSQLEncodingTypeRegistry implements EncodingTypeRegistry {

	@Override
	public void registerEncodingTypes(List<EncodingTypeFactory<?, ?>> typeMap) {
		// INTEGER
		registerByte(typeMap);
		registerShort(typeMap);
		registerInt(typeMap);
		registerLong(typeMap);

		// DECIMALS
		registerFloat(typeMap);
		registerDouble(typeMap);
		registerBigDecimal(typeMap);

		// BOOL
		EncodingTypeRegistry.registerType(BitEncodingType.class,
				boolean[].class,
				(clazz, map) -> clazz == boolean[].class && map.hasHint(DefaultTypeHints.FIXED_LENGTH)
						? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new BitEncodingType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		EncodingTypeRegistry.registerType(BooleanEncodingType.class,
				Boolean.class,
				(clazz, map) -> clazz == boolean.class || clazz == Boolean.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new BooleanEncodingType(),
				typeMap);

		// BINARY
		EncodingTypeRegistry.registerType(BinaryEncodingType.class,
				byte[].class,
				(clazz, map) -> clazz == byte[].class && map.hasHint(DefaultTypeHints.FIXED_LENGTH) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new BinaryEncodingType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		EncodingTypeRegistry.registerType(VarbinaryEncodingType.class,
				byte[].class,
				(clazz, map) -> clazz == byte[].class && map.hasHint(DefaultTypeHints.MAX_LENGTH) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new VarbinaryEncodingType(map.getIntHint(DefaultTypeHints.MAX_LENGTH)),
				typeMap);
		EncodingTypeRegistry.registerType(ByteAEncodingType.class,
				byte[].class,
				(clazz, map) -> clazz == byte[].class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new ByteAEncodingType(),
				typeMap);

		// MISC
		EncodingTypeRegistry.registerType(GenericArrayEncodingType.class,
				Array.class,
				(clazz, map) -> clazz == Array.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new GenericArrayEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(JsonEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.getBooleanHint(DefaultTypeHints.JSON) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new JsonEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(JsonBEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.getBooleanHint(DefaultTypeHints.JSONB) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new JsonBEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(UUIDEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.getBooleanHint(DefaultTypeHints.UUID) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new UUIDEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(XmlEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.getBooleanHint(DefaultTypeHints.XML) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new XmlEncodingType(),
				typeMap);

		// TEMPORAL
		EncodingTypeRegistry.registerType(DateEncodingType.class,
				Date.class,
				(clazz, map) -> clazz == Date.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new DateEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(TimestampEncodingType.class,
				Timestamp.class,
				(clazz, map) -> clazz == Timestamp.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new TimestampEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(TimeEncodingType.class,
				Time.class,
				(clazz, map) -> clazz == Time.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new TimeEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(TimestampZEncodingType.class,
				OffsetDateTime.class,
				(clazz, map) -> clazz == OffsetDateTime.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new TimestampZEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(TimeZEncodingType.class,
				OffsetTime.class,
				(clazz, map) -> clazz == OffsetTime.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new TimeZEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(IntervalEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.getBooleanHint(DefaultTypeHints.INTERVAL)
						? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new IntervalEncodingType(),
				typeMap);

		// TEXT
		EncodingTypeRegistry.registerType(CharEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.hasHint(DefaultTypeHints.FIXED_LENGTH) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new CharEncodingType(map.getIntHint(DefaultTypeHints.FIXED_LENGTH)),
				typeMap);
		EncodingTypeRegistry.registerType(VarcharEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.hasHint(DefaultTypeHints.MAX_LENGTH) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new VarcharEncodingType(map.getIntHint(DefaultTypeHints.MAX_LENGTH)),
				typeMap);
		EncodingTypeRegistry.registerType(TextEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new TextEncodingType(),
				typeMap);

		// ARRAYS
//		EncodingTypeRegistry.registerType(ObjectArrayEncodingType.class,
//				String.class,
//				(clazz, map) -> clazz.isArray() && map.hasHint(DefaultTypeHints.FIXED_LENGTH) ? EncodingTypeRegistry.MAP_MATCH_SCORE
//						: EncodingTypeRegistry.EXCLUDE,
//				map -> new ObjectArrayEncodingType(map.getStringHint(DefaultTypeHints.TYPE) map.getIntHint(DefaultTypeHints.FIXED_LENGTH)),
//				typeMap);
	}

	private void registerBigDecimal(List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(NumericEncodingType.class,
				BigDecimal.class,
				(clazz, map) -> clazz == BigDecimal.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new NumericEncodingType(map.getIntHint(DefaultTypeHints.PRECISION), map.getIntHint(DefaultTypeHints.SCALE)),
				typeMap);
	}

	private void registerDouble(List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(DoubleEncodingType.class,
				Double.class,
				(clazz, map) -> clazz == double.class || clazz == Double.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new DoubleEncodingType(),
				typeMap);
	}

	private void registerFloat(List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(RealEncodingType.class,
				Float.class,
				(clazz, map) -> clazz == float.class || clazz == Float.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new RealEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(DoubleEncodingType.class,
				Double.class,
				(clazz, map) -> clazz == float.class || clazz == Float.class ? EncodingTypeRegistry.typeCatchAll(1)
						: EncodingTypeRegistry.EXCLUDE,
				map -> new DoubleEncodingType(),
				typeMap);
	}

	private void registerLong(List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(BigIntEncodingType.class,
				Long.class,
				(clazz, map) -> clazz == Long.class || clazz == long.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new BigIntEncodingType(),
				typeMap);
	}

	private void registerInt(List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(IntEncodingType.class,
				Integer.class,
				(clazz, map) -> clazz == int.class || clazz == Integer.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new IntEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(BigIntEncodingType.class,
				Long.class,
				(clazz, map) -> clazz == int.class || clazz == Integer.class ? EncodingTypeRegistry.typeCatchAll(1)
						: EncodingTypeRegistry.EXCLUDE,
				map -> new BigIntEncodingType(),
				typeMap);
	}

	private void registerShort(List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(SmallIntEncodingType.class,
				Short.class,
				(clazz, map) -> clazz == short.class || clazz == Short.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new SmallIntEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(IntEncodingType.class,
				Integer.class,
				(clazz, map) -> clazz == short.class || clazz == Short.class ? EncodingTypeRegistry.typeCatchAll(2)
						: EncodingTypeRegistry.EXCLUDE,
				map -> new IntEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(BigIntEncodingType.class,
				Long.class,
				(clazz, map) -> clazz == short.class || clazz == Short.class ? EncodingTypeRegistry.typeCatchAll(3)
						: EncodingTypeRegistry.EXCLUDE,
				map -> new BigIntEncodingType(),
				typeMap);
	}

	private void registerByte(List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(TinyIntEncodingType.class,
				Byte.class,
				(clazz, map) -> clazz == byte.class || clazz == Byte.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new TinyIntEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(SmallIntEncodingType.class,
				Short.class,
				(clazz, map) -> clazz == byte.class || clazz == Byte.class ? EncodingTypeRegistry.typeCatchAll(1)
						: EncodingTypeRegistry.EXCLUDE,
				map -> new SmallIntEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(IntEncodingType.class,
				Integer.class,
				(clazz, map) -> clazz == byte.class || clazz == Byte.class ? EncodingTypeRegistry.typeCatchAll(3)
						: EncodingTypeRegistry.EXCLUDE,
				map -> new IntEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(BigIntEncodingType.class,
				Long.class,
				(clazz, map) -> clazz == byte.class || clazz == Byte.class ? EncodingTypeRegistry.typeCatchAll(4)
						: EncodingTypeRegistry.EXCLUDE,
				map -> new BigIntEncodingType(),
				typeMap);
	}

}
