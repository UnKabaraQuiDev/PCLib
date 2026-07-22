package lu.kbra.pclib.db.autobuild.mysql.column.integer;

import lu.kbra.pclib.db.autobuild.mysql.encoding.integer.TinyIntEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ByteColumnType implements IdentityColumnType<Byte> {

	private final EncodingType<Byte> encodingType;

	public ByteColumnType(final boolean unsigned) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(TinyIntEncodingType.class, unsigned, TinyIntEncodingType::new);
	}

	public ByteColumnType() {
		this(false);
	}

}
