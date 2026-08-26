package lu.kbra.pclib.db.autobuild.sqlite.column.misc;

import java.lang.reflect.Type;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EnumOrdinalColumnType implements ColumnType<Enum<?>, Long> {

	private final EncodingType<Long> encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, IntEncodingType::new);

	@Override
	public Enum<?> decode(final Long value, final Type type) {
		if (!(type instanceof Class<?>) || !((Class<?>) type).isEnum()) {
			throw new IllegalArgumentException("Not an enum: " + type);
		}
		return PCUtils.valueOfOrdinal(((Class<?>) type).asSubclass(Enum.class), value.intValue());
	}

	@Override
	public Long encode(final Enum<?> value) {
		return (long) value.ordinal();
	}

}
