package lu.pcy113.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.pcy113.jbcodec.CodecManager;

public class ClassEncoder extends DefaultObjectEncoder<Class<?>> {

	public ClassEncoder() {
		super(Class.class);
	}

	@Override
	public ByteBuffer encode(boolean head, Class<?> obj) {
		ByteBuffer bb = ByteBuffer.allocate(estimateSize(head, obj));

		if (head) {
			bb.putShort(header());
		}

		bb.put(cm.encode(false, (String) obj.getName()));

		bb.flip();

		return bb;
	}

	@Override
	public int estimateSize(boolean head, Class<?> obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + cm.estimateSize(false, (String) obj.getName());
	}

}
