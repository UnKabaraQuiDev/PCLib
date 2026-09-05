package lu.kbra.pclib.db.autobuild.mysql.column.temporal;

import java.lang.reflect.Type;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import lu.kbra.pclib.db.autobuild.mysql.encoding.text.VarcharEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// As ISO-8601 String

@Getter
@RequiredArgsConstructor
public class ZonedDateTimeType implements ColumnType<ZonedDateTime, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry
			.getFixedEncodingType(VarcharEncodingType.class, 64, VarcharEncodingType::new);

	@Override
	public ZonedDateTime decode(final String value, final Type type) {
		return ZonedDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

	@Override
	public String encode(final ZonedDateTime value) {
		return value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

}
