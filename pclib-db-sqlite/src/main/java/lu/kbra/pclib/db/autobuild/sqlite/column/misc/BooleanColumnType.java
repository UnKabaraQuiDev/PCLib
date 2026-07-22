package lu.kbra.pclib.db.autobuild.sqlite.column.misc;

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
public class BooleanColumnType implements ColumnType<Boolean, Long> {

	private final EncodingType<Long> encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, IntEncodingType::new);

	@Override
	public @NonNull Boolean decode(@NonNull Long value, Type type) {
		return value > 0;
	}

	@Override
	public @NonNull Long encode(@NonNull Boolean value) {
		return value ? 1L : 0L;
	}

}
