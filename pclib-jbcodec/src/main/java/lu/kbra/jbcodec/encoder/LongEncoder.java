package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class LongEncoder extends DefaultObjectEncoder<Long> {

	@Override
	public ByteBuffer encode(final boolean head, final Long obj) {
		final ByteBuffer bb = ByteBuffer.allocate(8 + (head ? 2 : 0));
		if (head) {
			bb.putShort(this.header);
		}
		bb.putLong(obj);

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Long obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Long.BYTES;
	}

}
