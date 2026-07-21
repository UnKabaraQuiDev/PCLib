package lu.kbra.pclib.db.autobuild.mysql.binary;

import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.BinaryEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.BlobEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.VarbinaryEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.ColumnType.IdentityColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.domain.column.type.SizeClass;
import lu.kbra.pclib.db.utils.registry.MySQLColumnTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

	public ByteArrayColumnType(SizeClass sizeClass) {
		this.encodingType = MySQLColumnTypeRegistry.getFixedEncodingType(BlobEncodingType.class, sizeClass, BlobEncodingType::new);
	}

	public ByteArrayColumnType() {
		this(SizeClass.NORMAL);
	}

}
