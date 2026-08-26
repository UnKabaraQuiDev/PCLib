package lu.kbra.pclib.db.autobuild.postgres.column.temporal;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LocalDateTimeColumnType implements ColumnType<LocalDateTime, Timestamp> {

	private final EncodingType<Timestamp> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimestampEncodingType.class,
			TimestampEncodingType::new);

	@Override
	public LocalDateTime decode(final Timestamp value, final Type type) {
		return value.toLocalDateTime();
	}

	@Override
	public Timestamp encode(final LocalDateTime value) {
		return Timestamp.valueOf(value);
	}

}
