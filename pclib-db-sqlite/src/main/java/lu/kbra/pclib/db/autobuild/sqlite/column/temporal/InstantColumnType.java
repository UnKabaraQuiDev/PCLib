package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.Instant;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InstantColumnType implements ColumnType<Instant, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimestampEncodingType.class,
			TimestampEncodingType::new);

	@Override
	public Instant decode(final String value, final Type type) {
		return Instant.parse(value);
	}

	@Override
	public String encode(final Instant value) {
		return value.toString();
	}

}
