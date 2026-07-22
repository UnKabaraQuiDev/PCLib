package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

// As ISO-8601 String
@Getter
@RequiredArgsConstructor
public class OffsetDateTimeType implements ColumnType<OffsetDateTime, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimestampEncodingType.class,
			TimestampEncodingType::new);

	@Override
	public @NonNull OffsetDateTime decode(@NonNull String value, Type type) {
		return OffsetDateTime.parse(value);
	}

	@Override
	public @NonNull String encode(@NonNull OffsetDateTime value) {
		return value.toString();
	}

}
