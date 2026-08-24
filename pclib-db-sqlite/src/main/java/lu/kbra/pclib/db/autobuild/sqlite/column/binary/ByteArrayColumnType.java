package lu.kbra.pclib.db.autobuild.sqlite.column.binary;

import lu.kbra.pclib.db.autobuild.sqlite.encoding.binary.BlobEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ByteArrayColumnType implements IdentityColumnType<byte[]> {

	private final EncodingType<byte[]> encodingType;

	public ByteArrayColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(BlobEncodingType.class, BlobEncodingType::new);
	}

}
