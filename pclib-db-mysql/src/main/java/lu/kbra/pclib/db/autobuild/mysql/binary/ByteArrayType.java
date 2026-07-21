package lu.kbra.pclib.db.autobuild.mysql.binary;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.BinaryEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.VarbinaryEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;

@Getter
@RequiredArgsConstructor
public class ByteArrayType implements IdentityColumnType<byte[]> {

	private final EncodingType<byte[]> encodingType;

	public ByteArrayType(final int length, boolean max) {
		if (max) {
			this.encodingType = new BinaryEncodingType(length);
		} else {
			this.encodingType = new VarbinaryEncodingType(length);
		}
	}

	public ByteArrayType(final Object object, boolean max) {
		this(ColumnType.asInt(object), max);
	}

	public ByteArrayType() {
		this.encodingType = new BinaryEncodingType();
	}

}