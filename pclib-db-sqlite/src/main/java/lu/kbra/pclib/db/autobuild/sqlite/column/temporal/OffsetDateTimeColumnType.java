package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// As ISO-8601 String
@Getter
@RequiredArgsConstructor
public class OffsetDateTimeColumnType implements ColumnType<OffsetDateTime, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimestampEncodingType.class,
			TimestampEncodingType::new);

	@Override
	public OffsetDateTime decode(final String value, final Type type) {
		return OffsetDateTime.parse(value);
	}

	@Override
	public String encode(final OffsetDateTime value) {
		return value.toString();
	}

}
