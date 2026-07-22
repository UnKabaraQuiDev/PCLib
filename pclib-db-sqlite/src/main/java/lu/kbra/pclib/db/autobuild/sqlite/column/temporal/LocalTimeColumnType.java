package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.time.LocalTime;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.TimeEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class LocalTimeColumnType implements ColumnType<LocalTime, String> {

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimeEncodingType.class,
			TimeEncodingType::new);

	@Override
	public @NonNull LocalTime decode(@NonNull String value, Type type) {
		return LocalTime.parse(value);
	}

	@Override
	public @NonNull String encode(@NonNull LocalTime value) {
		return value.toString();
	}

}
