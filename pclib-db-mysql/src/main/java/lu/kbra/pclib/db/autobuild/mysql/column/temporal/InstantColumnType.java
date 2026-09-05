package lu.kbra.pclib.db.autobuild.mysql.column.temporal;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.Instant;

import lu.kbra.pclib.db.autobuild.mysql.encoding.temporal.TimestampEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InstantColumnType implements ColumnType<Instant, Timestamp> {

	private final EncodingType<Timestamp> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimestampEncodingType.class,
			TimestampEncodingType::new);

	@Override
	public Instant decode(final Timestamp value, final Type type) {
		return value.toInstant();
	}

	@Override
	public Timestamp encode(final Instant value) {
		return Timestamp.from(value);
	}

}
