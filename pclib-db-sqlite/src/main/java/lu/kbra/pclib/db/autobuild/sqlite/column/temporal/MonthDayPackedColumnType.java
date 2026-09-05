package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.MonthDay;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.integer.IntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MonthDayPackedColumnType implements ColumnType<MonthDay, Long> {

	private final EncodingType<Long> encodingType = EncodingTypeRegistry.getFixedEncodingType(IntEncodingType.class, IntEncodingType::new);

	@Override
	public MonthDay decode(final Long value, final Type type) {
		final int month = (int) (value >>> 8 & 0xFF);
		final int day = (int) (value & 0xFF);

		if (month < 1 || month > 12) {
			throw new IllegalArgumentException("Invalid encoded MonthDay: month=" + month);
		}

		return MonthDay.of(month, day);
	}

	@Override
	public Long encode(final MonthDay value) {
		return (long) (value.getMonthValue() << 8 | value.getDayOfMonth());
	}

}
