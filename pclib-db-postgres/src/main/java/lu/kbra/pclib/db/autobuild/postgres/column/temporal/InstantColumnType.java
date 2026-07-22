package lu.kbra.pclib.db.autobuild.postgres.column.temporal;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.TimestampZEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class InstantColumnType implements ColumnType<Instant, OffsetDateTime> {

	private final EncodingType<OffsetDateTime> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimestampZEncodingType.class,
			TimestampZEncodingType::new);

	@Override
	public @NonNull Instant decode(@NonNull OffsetDateTime value, Type type) {
		return value.toInstant();
	}

	@Override
	public @NonNull OffsetDateTime encode(@NonNull Instant value) {
		return value.atOffset(ZoneOffset.UTC);
	}

}
