package lu.kbra.pclib.db.autobuild.sqlite.column.binary;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;

import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.sqlite.encoding.binary.BlobEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ByteBufferColumnType implements ColumnType<ByteBuffer, byte[]> {

	private final EncodingType<byte[]> encodingType;

	public ByteBufferColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(BlobEncodingType.class, BlobEncodingType::new);
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
