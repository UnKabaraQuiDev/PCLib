package lu.kbra.jbcodec.encoder;

import java.nio.ByteBuffer;

import lu.kbra.jbcodec.CodecManager;

public class BooleanEncoder extends DefaultObjectEncoder<Boolean> {

	@Override
	public ByteBuffer encode(final boolean head, final Boolean obj) {
		final ByteBuffer bb = ByteBuffer.allocate(1 + (head ? 2 : 0));
		if (head) {
			bb.putShort(this.header);
		}
		bb.put((byte) (obj ? 1 : 0));

		bb.flip();
		return bb;
	}

	@Override
	public int estimateSize(final boolean head, final Boolean obj) {
		return (head ? CodecManager.HEAD_SIZE : 0) + Byte.BYTES;
	}

}
