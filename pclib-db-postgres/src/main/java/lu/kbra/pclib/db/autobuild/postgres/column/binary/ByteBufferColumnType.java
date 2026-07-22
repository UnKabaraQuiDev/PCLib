package lu.kbra.pclib.db.autobuild.postgres.column.binary;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lu.kbra.pclib.PCUtils;
import lu.kbra.pclib.db.autobuild.postgres.encoding.binary.BinaryEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.binary.ByteAEncodingType;
import lu.kbra.pclib.db.autobuild.postgres.encoding.binary.VarbinaryEncodingType;
import lu.kbra.pclib.db.domain.column.type.ColumnType;
import lu.kbra.pclib.db.domain.column.type.EncodingType;
import lu.kbra.pclib.db.utils.registry.EncodingTypeRegistry;

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

	public ByteBufferColumnType() {
		this.encodingType = EncodingTypeRegistry.getFixedEncodingType(ByteAEncodingType.class, ByteAEncodingType::new);
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
