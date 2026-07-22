package lu.kbra.pclib.db.autobuild.sqlite.column.integer;

import java.lang.reflect.Type;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class ShortType implements ColumnType<Short, Long> {

	private final EncodingType<Long> encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, IntEncodingType::new);

	@Override
	public @NonNull Short decode(@NonNull Long value, Type type) {
		return value.shortValue();
	}

	@Override
	public @NonNull Long encode(@NonNull Short value) {
		return value.longValue();
	}

}
