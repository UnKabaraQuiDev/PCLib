package lu.kbra.pclib.db.autobuild.postgres.column.temporal;

import java.time.OffsetDateTime;

import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.TimestampZEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// As ISO-8601 String
@Getter
@RequiredArgsConstructor
public class OffsetDateTimeColumnType implements IdentityColumnType<OffsetDateTime> {

	private final EncodingType<OffsetDateTime> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimestampZEncodingType.class,
			TimestampZEncodingType::new);

}
