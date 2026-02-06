package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;
import lu.pcy113.jbcodec.other.ObjectSerializable;

public class ObjectSerializableEncoder extends DefaultObjectEncoder<ObjectSerializable> {

	@Override
	public ByteBuffer encode(boolean head, ObjectSerializable obj) {
		ByteBuffer bb = ByteBuffer.allocate(estimateSize(head, obj));

		if (head) {
			bb.putShort(header());
		}

		bb.put(cm.encode(false, obj.getClass()));

		bb.flip();

		return bb;
	}

	@Override
	public int estimateSize(boolean head, ObjectSerializable obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + cm.estimateSize(false, obj.getClass());
	}

	@Override
	public boolean confirmClassType(Class<?> clazz) {
		return ObjectSerializableEncoder.class.isAssignableFrom(clazz);
	}

	@Override
	public boolean confirmType(Object obj) {
		return obj instanceof ObjectSerializable;
	}

}
