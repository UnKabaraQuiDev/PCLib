package lu.kbra.pclib.db.autobuild.mysql.column.temporal;

import java.lang.reflect.Type;
import java.time.MonthDay;

import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.SmallIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MonthDayPackedColumnType implements ColumnType<MonthDay, Short> {

	private final EncodingType<Short> encodingType = EncodingTypeRegistry
			.getFixedEncodingType(SmallIntEncodingType.class, true, SmallIntEncodingType::new);

	@Override
	public @NonNull MonthDay decode(@NonNull final Short value, final Type type) {
		final int month = value >>> 8 & 0xFF;
		final int day = value & 0xFF;

		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("Invalid encoded MonthDay: month=" + month);
		}

		return MonthDay.of(month, day);
	}

	@Override
	public @NonNull Short encode(@NonNull final MonthDay value) {
		return (short) (value.getMonthValue() << 8 | value.getDayOfMonth());
	}

}
