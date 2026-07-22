package lu.kbra.pclib.db.autobuild.mysql.column.temporal;

import java.lang.reflect.Type;
import java.sql.Time;
import java.time.LocalTime;

import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.TimeEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LocalTimeColumnType implements ColumnType<LocalTime, Time> {

	private final EncodingType<Time> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimeEncodingType.class,
			TimeEncodingType::new);

	@Override
	public @NonNull LocalTime decode(@NonNull Time value, Type type) {
		return value.toLocalTime();
	}

	@Override
	public @NonNull Time encode(@NonNull LocalTime value) {
		return Time.valueOf(value);
	}

}
