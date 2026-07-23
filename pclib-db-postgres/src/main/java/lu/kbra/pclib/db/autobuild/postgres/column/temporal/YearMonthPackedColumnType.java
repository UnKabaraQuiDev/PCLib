package lu.kbra.pclib.db.autobuild.postgres.column.temporal;

import java.lang.reflect.Type;
import java.time.YearMonth;

import lu.kbra.pclib.db.autobuild.postgres.encoding.integer.SmallIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class YearMonthPackedColumnType implements ColumnType<YearMonth, Short> {

	private final EncodingType<Short> encodingType = EncodingTypeRegistry.getFixedEncodingType(SmallIntEncodingType.class,
			SmallIntEncodingType::new);

	@Override
	public @NonNull YearMonth decode(@NonNull final Short value, final Type type) {
		final int month = value & 0xF;
		final int year = value >> 4; // arithmetic shift preserves sign

		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("Invalid encoded YearMonth: month=" + month);
		}

		return YearMonth.of(year, month);
	}

	@Override
	public @NonNull Short encode(@NonNull final YearMonth value) {
		return (short) ((value.getYear() << 4) | value.getMonthValue());
	}

}
