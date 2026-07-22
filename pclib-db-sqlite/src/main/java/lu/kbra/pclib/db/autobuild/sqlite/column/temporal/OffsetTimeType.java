package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.OffsetTime;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.TimeEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

// As ISO-8601 String
@Getter
@RequiredArgsConstructor
public class OffsetTimeType implements ColumnType<OffsetTime, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimeEncodingType.class,
			TimeEncodingType::new);

	@Override
	public @NonNull OffsetTime decode(@NonNull String value, Type type) {
		return OffsetTime.parse(value);
	}

	@Override
	public @NonNull String encode(@NonNull OffsetTime value) {
		return value.toString();
	}

}
