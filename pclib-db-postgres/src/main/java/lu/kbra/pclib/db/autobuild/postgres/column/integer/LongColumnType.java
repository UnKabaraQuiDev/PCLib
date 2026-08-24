package lu.kbra.pclib.db.autobuild.postgres.column.integer;

import lu.kbra.pclib.db.autobuild.postgres.encoding.integer.BigIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LongColumnType implements IdentityColumnType<Long> {

	private final EncodingType<Long> encodingType;

	public LongColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(BigIntEncodingType.class, BigIntEncodingType::new);
	}

}
