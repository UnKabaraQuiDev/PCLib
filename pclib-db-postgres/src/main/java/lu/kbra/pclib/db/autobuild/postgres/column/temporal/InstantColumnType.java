package lu.kbra.pclib.db.autobuild.postgres.column.temporal;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.TimestampZEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class InstantColumnType implements ColumnType<Instant, OffsetDateTime> {

	private final EncodingType<OffsetDateTime> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimestampZEncodingType.class,
			TimestampZEncodingType::new);

	@Override
	public Instant decode(final OffsetDateTime value, final Type type) {
		return value.toInstant();
	}

	@Override
	public OffsetDateTime encode(final Instant value) {
		return value.atOffset(ZoneOffset.UTC);
	}

}
