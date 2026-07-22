package lu.kbra.pclib.db.autobuild.postgres.column.integer;

import lu.kbra.pclib.db.autobuild.postgres.encoding.integer.SmallIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ShortColumnType implements IdentityColumnType<Short> {

	private final EncodingType<Short> encodingType;

	public ShortColumnType(final boolean unsigned) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(SmallIntEncodingType.class, unsigned, SmallIntEncodingType::new);
	}

	public ShortColumnType() {
		this(false);
	}

}
