package lu.kbra.pclib.db.autobuild.postgres.column.temporal;

import java.time.OffsetTime;

import lu.kbra.pclib.db.autobuild.postgres.encoding.temporal.TimeZEncodingType;
import lu.kbra.pclib.db.dbms.registry.EncodingTypeRegistry;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// As ISO-8601 String
@Getter
@RequiredArgsConstructor
public class OffsetTimeColumnType implements IdentityColumnType<OffsetTime> {

	private final EncodingType<OffsetTime> encodingType = EncodingTypeRegistry.getFixedEncodingType(TimeZEncodingType.class,
			TimeZEncodingType::new);

	@Override
	public Class<OffsetTime> getJavaType() {
		return OffsetTime.class;
	}

	@Override
	public Class<OffsetTime> getJdbcType() {
		return OffsetTime.class;
	}

}
