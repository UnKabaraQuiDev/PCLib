package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class IntegerEncoder extends DefaultObjectEncoder<Integer> {

	@Override
	public ByteBuffer encode(final boolean head, final Integer obj) {
		final ByteBuffer bb = ByteBuffer.allocate(4 + (head ? 2 : 0));
		if (head) {
			bb.putShort(this.header);
		}
		bb.putInt(obj);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Integer obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Integer.BYTES;
	}

}
