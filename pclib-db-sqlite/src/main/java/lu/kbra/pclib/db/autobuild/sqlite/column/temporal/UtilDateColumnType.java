package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.Instant;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.DateEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UtilDateColumnType implements ColumnType<java.util.Date, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(DateEncodingType.class,
			DateEncodingType::new);

	@Override
	public java.util.Date decode(final String value, final Type type) {
		return java.util.Date.from(Instant.parse(value));
	}

	@Override
	public String encode(final java.util.Date value) {
		return value.toInstant().toString();
	}

}
