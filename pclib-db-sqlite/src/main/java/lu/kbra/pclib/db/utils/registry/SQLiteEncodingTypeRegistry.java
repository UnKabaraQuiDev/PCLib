package lu.kbra.pclib.db.utils.registry;

import java.math.BigDecimal;
import java.util.List;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.binary.BlobEncodingType;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.decimal.NumericEncodingType;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.decimal.RealEncodingType;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.DateEncodingType;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.DatetimeEncodingType;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.TimeEncodingType;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;

public class SQLiteEncodingTypeRegistry implements EncodingTypeRegistry {

	@Override
	public void registerEncodingTypes(final List<EncodingTypeFactory<?, ?>> typeMap) {
		// INTEGER
		this.registerByte(typeMap);
		this.registerShort(typeMap);
		this.registerInt(typeMap);
		this.registerLong(typeMap);

		// DECIMALS
		this.registerFloat(typeMap);
		this.registerDouble(typeMap);
		this.registerBigDecimal(typeMap);

		// BINARY
		EncodingTypeRegistry.registerType(BlobEncodingType.class,
				byte[].class,
				(clazz, map) -> clazz == byte[].class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new BlobEncodingType(),
				typeMap);

		// TEMPORAL
		EncodingTypeRegistry.registerType(DateEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.getBooleanHint(DefaultTypeHints.DATE)
						? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new DateEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(DatetimeEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.getBooleanHint(DefaultTypeHints.DATETIME)
						? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new DatetimeEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(TimeEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.getBooleanHint(DefaultTypeHints.TIME)
						? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new TimeEncodingType(),
				typeMap);
		EncodingTypeRegistry.registerType(TimestampEncodingType.class,
				String.class,
				(clazz, map) -> clazz == String.class && map.getBooleanHint(DefaultTypeHints.TIMESTAMP)
						? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new TimestampEncodingType(),
				typeMap);

		// TEXT
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
	}

	private void registerBigDecimal(final List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(NumericEncodingType.class,
				BigDecimal.class,
				(clazz, map) -> clazz == BigDecimal.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new NumericEncodingType(map.getIntHint(DefaultTypeHints.PRECISION), map.getIntHint(DefaultTypeHints.SCALE)),
				typeMap);
	}

	private void registerDouble(final List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(RealEncodingType.class,
				Double.class,
				(clazz, map) -> clazz == double.class || clazz == Double.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new RealEncodingType(),
				typeMap);
	}

	private void registerFloat(final List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(RealEncodingType.class,
				Double.class,
				(clazz, map) -> clazz == float.class || clazz == Float.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new RealEncodingType(),
				typeMap);
	}

	private void registerLong(final List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(IntEncodingType.class,
				Long.class,
				(clazz, map) -> clazz == long.class || clazz == Long.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new IntEncodingType(),
				typeMap);
	}

	private void registerInt(final List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(IntEncodingType.class,
				Long.class,
				(clazz, map) -> clazz == int.class || clazz == Integer.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new IntEncodingType(),
				typeMap);
	}

	private void registerShort(final List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(IntEncodingType.class,
				Long.class,
				(clazz, map) -> clazz == short.class || clazz == Short.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new IntEncodingType(),
				typeMap);
	}

	private void registerByte(final List<EncodingTypeFactory<?, ?>> typeMap) {
		EncodingTypeRegistry.registerType(IntEncodingType.class,
				Long.class,
				(clazz, map) -> clazz == byte.class || clazz == Byte.class ? EncodingTypeRegistry.TYPE_CATCH_ALL_SCORE
						: EncodingTypeRegistry.EXCLUDE,
				map -> new IntEncodingType(),
				typeMap);
	}

}
