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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ByteBufferColumnType implements ColumnType<ByteBuffer, byte[]> {

	private final EncodingType<byte[]> encodingType;

	public ByteBufferColumnType(final int length, boolean max) {
		if (max) {
			this.encodingType = new BinaryEncodingType(length);
		} else {
			this.encodingType = new VarbinaryEncodingType(length);
		}
	}

	public ByteBufferColumnType(final Object object, boolean max) {
		this(ColumnType.asInt(object), max);
	}

	public ByteBufferColumnType(SizeClass sizeClass) {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(BlobEncodingType.class, sizeClass, BlobEncodingType::new);
	}

	public ByteBufferColumnType() {
		this(SizeClass.NORMAL);
	}

	@Override
	public @NonNull ByteBuffer decode(byte @NonNull [] value, Type type) {
		return ByteBuffer.wrap(value);
	}

	@Override
	public byte @NonNull [] encode(@NonNull ByteBuffer value) {
		return PCUtils.toByteArray(value);
	}

}
