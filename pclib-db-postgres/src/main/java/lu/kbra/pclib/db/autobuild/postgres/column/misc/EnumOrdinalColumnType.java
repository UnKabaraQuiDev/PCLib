package lu.kbra.pclib.db.autobuild.postgres.column.misc;

import java.lang.reflect.Type;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.postgres.encoding.integer.SmallIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnumOrdinalColumnType implements ColumnType<Enum<?>, Short> {

	private final EncodingType<Short> encodingType = EncodingTypeRegistry.getFixedEncodingType(SmallIntEncodingType.class,
			SmallIntEncodingType::new);

	@Override
	public @NonNull Enum<?> decode(@NonNull Short value, Type type) {
		if (!(type instanceof Class<?>) || !((Class<?>) type).isEnum()) {
			throw new IllegalArgumentException("Not an enum: " + type);
		}
		return PCUtils.valueOfOrdinal(((Class<?>) type).asSubclass(Enum.class), value);
	}

	@Override
	public @NonNull Short encode(@NonNull Enum<?> value) {
		return (short) value.ordinal();
	}

}
