package lu.kbra.pclib.db.autobuild.postgres.column.temporal;

import java.lang.reflect.Type;
import java.time.Year;

import lu.kbra.pclib.db.autobuild.postgres.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class YearColumnType implements ColumnType<Year, Integer> {

	private final EncodingType<Integer> encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class,
			IntEncodingType::new);

	@Override
	public @NonNull Year decode(@NonNull Integer value, Type type) {
		return Year.of(value);
	}

	@Override
	public @NonNull Integer encode(@NonNull Year value) {
		return value.getValue();
	}

}
