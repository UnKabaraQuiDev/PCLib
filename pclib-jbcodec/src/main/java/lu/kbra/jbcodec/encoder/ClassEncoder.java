package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class ClassEncoder extends DefaultObjectEncoder<Class<?>> {

	public ClassEncoder() {
		super(Class.class);
	}

	@Override
	public ByteBuffer encode(final boolean head, final Class<?> obj) {
		final ByteBuffer bb = ByteBuffer.allocate(this.estimateSize(head, obj));

		if (head) {
			bb.putShort(this.header());
		}

		bb.put(this.cm.encode(false, obj.getName()));

		bb.flip();

		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Class<?> obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + this.cm.estimateSize(false, obj.getName());
	}

}
