package lu.kbra.pclib.db.autobuild.sqlite.column.integer;

import java.lang.reflect.Type;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ByteColumnType implements ColumnType<Byte, Long> {

	private final EncodingType<Long> encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, IntEncodingType::new);

	@Override
	public Byte decode(final Long value, final Type type) {
		return value.byteValue();
	}

	@Override
	public Long encode(final Byte value) {
		return value.longValue();
	}

}
