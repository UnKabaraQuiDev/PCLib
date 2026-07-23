package lu.kbra.pclib.db.autobuild.mysql.column.temporal;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LocalDateTimeColumnType implements ColumnType<LocalDateTime, Timestamp> {

	private final EncodingType<Timestamp> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimestampEncodingType.class,
			TimestampEncodingType::new);

	@Override
	public @NonNull LocalDateTime decode(@NonNull Timestamp value, Type type) {
		return value.toLocalDateTime();
	}

	@Override
	public @NonNull Timestamp encode(@NonNull LocalDateTime value) {
		return Timestamp.valueOf(value);
	}

}
