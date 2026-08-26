package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.Year;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class YearColumnType implements ColumnType<Year, Long> {

	private final EncodingType<Long> encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, IntEncodingType::new);

	@Override
	public Year decode(final Long value, final Type type) {
		return Year.of(value.intValue());
	}

	@Override
	public Long encode(final Year value) {
		return (long) value.getValue();
	}

}
