package lu.kbra.pclib.db.autobuild.mysql.column.integer;

import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.BigIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LongColumnType implements IdentityColumnType<Long> {

	private final EncodingType<Long> encodingType;

	public LongColumnType(final boolean unsigned) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(BigIntEncodingType.class, unsigned, BigIntEncodingType::new);
	}

	public LongColumnType() {
		this(false);
	}

}
