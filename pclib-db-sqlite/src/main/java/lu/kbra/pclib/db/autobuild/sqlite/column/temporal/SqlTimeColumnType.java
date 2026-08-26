package lu.kbra.pclib.db.autobuild.sqlite.column.temporal;

import java.lang.reflect.Type;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.temporal.TimeEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SqlTimeColumnType implements ColumnType<Time, String> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

	private final EncodingType<String> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimeEncodingType.class,
			TimeEncodingType::new);

	@Override
	public @NonNull Time decode(@NonNull String value, Type type) {
		return Time.valueOf(LocalTime.parse(value, FORMATTER));
	}

	@Override
	public @NonNull String encode(@NonNull Time value) {
		return value.toLocalTime().format(FORMATTER);
	}

}
