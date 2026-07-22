package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.Instant;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.DateEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class UtilDateColumnType implements ColumnType<java.util.Date, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(DateEncodingType.class,
			DateEncodingType::new);

	@Override
	public java.util.@NonNull Date decode(@NonNull String value, Type type) {
		return java.util.Date.from(Instant.parse(value));
	}

	@Override
	public @NonNull String encode(java.util.@NonNull Date value) {
		return value.toInstant().toString();
	}

}
