package lu.kbra.pclib.db.autobuild.postgres.column.misc;

import java.lang.reflect.Type;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.CharEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.TextEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class EnumStringColumnType implements ColumnType<Enum<?>, String> {

	private final EncodingType<String> encodingType;

	public EnumStringColumnType(final int length, final boolean max) {
		if (max) {
			this.encodingType = new VarcharEncodingType(length);
		} else {
			this.encodingType = new CharEncodingType(length);
		}
	}

	public EnumStringColumnType(final Object object, final boolean max) {
		this(ColumnType.asInt(object), max);
	}

	public EnumStringColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(TextEncodingType.class, TextEncodingType::new);
	}

	@Override
	public @NonNull Enum<?> decode(@NonNull String value, Type type) {
		if (!(type instanceof Class<?>) || !((Class<?>) type).isEnum()) {
			throw new IllegalArgumentException("Not an enum: " + type);
		}
		return PCUtils.enumValuetoEnum(((Class<?>) type).asSubclass(Enum.class), value);
	}

	@Override
	public @NonNull String encode(@NonNull Enum<?> value) {
		return value.name();
	}

}
