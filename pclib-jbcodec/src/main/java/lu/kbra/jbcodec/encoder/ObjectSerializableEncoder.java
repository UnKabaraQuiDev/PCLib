package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;
import lu.kbra.jbcodec.other.ObjectSerializable;

public class ObjectSerializableEncoder extends DefaultObjectEncoder<ObjectSerializable> {

	@Override
	public ByteBuffer encode(final boolean head, final ObjectSerializable obj) {
		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));

		if (head) {
			bb.putShort(this.header());
		}

		bb.put(this.cm.encode(false, obj.getClass()));

		bb.flip();

		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final ObjectSerializable obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + this.cm.estimateSize(false, obj.getClass());
	}

	@Override
	public boolean confirmClassType(final Class<?> clazz) {
		return ObjectSerializableEncoder.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean confirmType(final Object obj) {
		return obj instanceof ObjectSerializable;
	}

}
