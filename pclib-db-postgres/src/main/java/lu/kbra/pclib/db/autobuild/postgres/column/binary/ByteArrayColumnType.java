package lu.kbra.pclib.db.autobuild.postgres.column.binary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.postgres.encoding.binary.BinaryEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.binary.ByteAEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.binary.VarbinaryEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

@Getter
@RequiredArgsConstructor
public class ByteArrayColumnType implements IdentityColumnType<byte[]> {

	private final EncodingType<byte[]> encodingType;

	public ByteArrayColumnType(final int length, boolean max) {
		if (max) {
			this.encodingType = new BinaryEncodingType(length);
		} else {
			this.encodingType = new VarbinaryEncodingType(length);
		}
	}

	public ByteArrayColumnType(final Object object, boolean max) {
		this(ColumnType.asInt(object), max);
	}

	public ByteArrayColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(ByteAEncodingType.class, ByteAEncodingType::new);
	}

}
