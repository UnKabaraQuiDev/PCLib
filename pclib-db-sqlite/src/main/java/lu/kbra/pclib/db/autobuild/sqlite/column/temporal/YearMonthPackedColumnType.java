package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.YearMonth;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class YearMonthPackedColumnType implements ColumnType<YearMonth, Long> {

	private final EncodingType<Long> encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, IntEncodingType::new);

	@Override
	public @NonNull YearMonth decode(@NonNull final Long value, final Type type) {
		final int month = (int) (value & 0xF);
		final int year = (int) (value >> 4); // arithmetic shift preserves sign

		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("Invalid encoded YearMonth: month=" + month);
		}

		return YearMonth.of(year, month);
	}

	@Override
	public @NonNull Long encode(@NonNull final YearMonth value) {
		return (long) ((value.getYear() << 4) | value.getMonthValue());
	}

}
