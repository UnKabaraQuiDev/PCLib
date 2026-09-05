package lu.kbra.pclib.db.autobuild.mysql.column.binary;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.BinaryEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.BlobEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.encoding.binary.VarbinaryEncodingType;
import lu.kbra.pclib.db.autobuild.mysql.meta.SizeClass;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ByteBufferColumnType implements ColumnType<ByteBuffer, byte[]> {

	private final EncodingType<byte[]> encodingType;

	public ByteBufferColumnType(final int length, final boolean max) {
		if (max) {
			this.encodingType = new BinaryEncodingType(length);
		} else {
			this.encodingType = new VarbinaryEncodingType(length);
		}
	}

	public ByteBufferColumnType(final Object object, final boolean max) {
		this(ColumnType.asInt(object), max);
	}

	public ByteBufferColumnType(final SizeClass sizeClass) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(BlobEncodingType.class, sizeClass, BlobEncodingType::new);
	}

	public ByteBufferColumnType() {
		this(SizeClass.NORMAL);
	}

	@Override
	public ByteBuffer decode(final byte[] value, final Type type) {
		return ByteBuffer.wrap(value);
	}

	@Override
	public byte[] encode(final ByteBuffer value) {
		return PCUtils.toByteArray(value);
	}

}
