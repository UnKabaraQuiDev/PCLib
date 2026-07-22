package lu.kbra.pclib.db.utils.registry;

import java.util.List;

import lu.kbra.pclib.db.autobuild.mysql.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.meta.MySQLTypeHints;
import lu.kbra.pclib.db.autobuild.mysql.meta.SizeClass;
import lu.kbra.pclib.db.domain.column.meta.DefaultTypeHints;

public class MySQLEncodingTypeRegistry implements EncodingTypeRegistry {

	@Override
	public void registerEncodingTypes(final List<EncodingTypeFactory<?, ?>> typeMap) {
		// STRING
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
				(clazz, map) -> clazz == String.class ? EncodingTypeRegistry.STORED_TYPE_MATCH_SCORE : EncodingTypeRegistry.EXCLUDE,
				map -> new TextEncodingType(SizeClass.NORMAL),
				typeMap);

		// TODO
	}

}
