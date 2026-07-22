package lu.kbra.pclib.db.autobuild.postgres.column.temporal;

import java.time.OffsetTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.TimeZEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

// As ISO-8601 String
@Getter
@RequiredArgsConstructor
public class OffsetTimeColumnType implements IdentityColumnType<OffsetTime> {

	private final EncodingType<OffsetTime> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimeZEncodingType.class,
			TimeZEncodingType::new);

}
