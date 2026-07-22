package lu.kbra.pclib.db.utils.registry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;
import java.util.List;

import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.BinaryEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.BlobEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.VarbinaryEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.bool.BitEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.bool.BooleanEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.decimal.DecimalEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.decimal.DoubleEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.decimal.FloatEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.BigIntEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.MediumIntEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.SmallIntEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.TinyIntEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.misc.EnumEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.misc.JsonEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.misc.SetEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.misc.XmlEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.DateEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.DatetimeEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.TimeEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.YearEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.meta.MySQLTypeHints;
import lu.kbra.pclib.db.autobuild.mysql.meta.SizeClass;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;

public class MySQLEncodingTypeRegistry implements EncodingTypeRegistry {

	@Override
	public void registerEncodingTypes(final List<EncodingTypeFactory<?, ?>> typeMap) {
		// INTEGER
		EncodingTypeRegistry.registerType(TinyIntEncodingType.class,
				Byte.class,
				(clazz, map) -> clazz == byte.class || clazz == Byte.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new TinyIntEncodingType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);
		EncodingTypeRegistry.registerType(SmallIntEncodingType.class,
				Short.class,
				(clazz, map) -> clazz == short.class || clazz == Short.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new SmallIntEncodingType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);
		EncodingTypeRegistry.registerType(MediumIntEncodingType.class,
				Integer.class,
				(clazz, map) -> clazz == int.class || clazz == Integer.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new MediumIntEncodingType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);
		EncodingTypeRegistry.registerType(IntEncodingType.class,
				Integer.class,
				(clazz, map) -> clazz == int.class || clazz == Integer.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new IntEncodingType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);
		EncodingTypeRegistry.registerType(BigIntEncodingType.class,
				Long.class,
				(clazz, map) -> clazz == Long.class || clazz == long.class || clazz == BigInteger.class
						? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new BigIntEncodingType(map.getBooleanHint(DefaultTypeHints.UNSIGNED)),
				typeMap);

		// DECIMALS
		EncodingTypeRegistry.registerType(FloatEncodingType.class,
				Float.class,
				(clazz, map) -> clazz == Float.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new FloatEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(DoubleEncodingType.class,
				Double.class,
				(clazz, map) -> clazz == Float.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new DoubleEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(DecimalEncodingType.class,
				BigDecimal.class,
				(clazz, map) -> clazz == Float.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new DecimalEncodingType(map.getIntHint(DefaultTypeHints.PRECISION), map.getIntHint(DefaultTypeHints.SCALE)),
				typeMap);

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
		EncodingTypeRegistry.registerType(BlobEncodingType.class,
				byte[].class,
				(clazz, map) -> clazz == byte[].class && map.hasHint(MySQLTypeHints.SIZE_CLASS) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new BlobEncodingType(map.<SizeClass>getHint(MySQLTypeHints.SIZE_CLASS)),
				typeMap);
		EncodingTypeRegistry.registerType(BlobEncodingType.class,
				byte[].class,
				(clazz, map) -> clazz == byte[].class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new BlobEncodingType(map.<SizeClass>getHint(MySQLTypeHints.SIZE_CLASS)),
				typeMap);

		// MISC
		EncodingTypeRegistry.registerType(EnumEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.hasHint(DefaultTypeHints.ENUM_VALUES) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new EnumEncodingType(map.<String[]>getHint(DefaultTypeHints.ENUM_VALUES)),
				typeMap);
		EncodingTypeRegistry.registerType(JsonEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.getBooleanHint(DefaultTypeHints.JSON) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new JsonEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(SetEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.hasHint(DefaultTypeHints.SET_VALUES) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new SetEncodingType(map.<String[]>getHint(DefaultTypeHints.SET_VALUES)),
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
		EncodingTypeRegistry.registerType(DatetimeEncodingType.class,
				Timestamp.class,
				(clazz, map) -> clazz == Date.class && map.getBooleanHint(DefaultTypeHints.DATETIME) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new DatetimeEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(YearEncodingType.class,
				Integer.class,
				(clazz, map) -> clazz == Year.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new YearEncodingType(),
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
				(clazz, map) -> clazz == String.class && map.hasHint(MySQLTypeHints.SIZE_CLASS) ? EncodingTypeRegistry.MAP_MATCH_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new TextEncodingType(map.<SizeClass>getHint(MySQLTypeHints.SIZE_CLASS)),
				typeMap);
		EncodingTypeRegistry.registerType(TextEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new TextEncodingType(SizeClass.NORMAL),
				typeMap);
	}

}
